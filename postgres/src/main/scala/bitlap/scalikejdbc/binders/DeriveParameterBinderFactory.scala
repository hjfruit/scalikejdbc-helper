/*
 * Copyright (c) 2023 bitlap
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bitlap.scalikejdbc.binders

import org.postgresql.util.PGobject

import java.sql.{ Connection, PreparedStatement }
import scala.quoted.*
import org.postgresql.util.PGobject
import scalikejdbc.ParameterBinderFactory

import java.sql.{ Connection, PreparedStatement }
import scala.quoted.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
object DeriveParameterBinderFactory {

  inline def array[A, T[X] <: Iterable[X]](
    oType: OType,
    f: T[A] => Array[AnyRef]
  )(using conn: Connection): ParameterBinderFactory[T[A]] = ${ arrayImpl('{ f }, '{ oType }, '{ conn }) }

  inline def jsonb[T](inline f: T => String): ParameterBinderFactory[T] = ${ jsonImpl('{ f }, '{ "jsonb" }) }

  inline def json[T](inline f: T => String): ParameterBinderFactory[T] = ${ jsonImpl('{ f }, '{ "json" }) }

  private def jsonImpl[T](f: Expr[T => String], typeString: Expr[String])(using
    Quotes,
    Type[T]
  ): Expr[ParameterBinderFactory[T]] =
    '{
      ParameterBinderFactory[T] { (value: T) => (stmt: PreparedStatement, idx: Int) =>
        val obj     = new PGobject()
        val jsonStr = $f(value)
        obj.setType(${ typeString })
        obj.setValue(jsonStr)
        stmt.setObject(idx, obj)
      }
    }

  private def arrayImpl[A, T[_]](
    f: Expr[T[A] => Array[AnyRef]],
    arrType: Expr[OType],
    conn: Expr[Connection]
  )(using quotes: Quotes, tpa: Type[A], tpt: Type[T]): Expr[ParameterBinderFactory[T[A]]] =
    import quotes.reflect.*
    '{
      ParameterBinderFactory[T[A]] { (value: T[A]) => (stmt: PreparedStatement, idx: Int) =>
        stmt.setArray(idx, $conn.createArrayOf($arrType.name, $f(value)))
      }
    }
}
