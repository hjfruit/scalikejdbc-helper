/*
 * Copyright (c) 2023 jxnu-liguobin
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

package bitlap.scalikejdbc.internal

import bitlap.scalikejdbc.ObjectType
import org.postgresql.util.PGobject
import scalikejdbc.ParameterBinderFactory

import java.sql.{ Array as _, * }
import scala.quoted.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
object DeriveParameterBinder:

  inline def array[A, T[X] <: Iterable[X]](
    inline objectType: ObjectType,
    f: T[A] => Array[Any]
  ): ParameterBinderFactory[T[A]] = ${ arrayImpl('{ f }, '{ objectType }) }

  inline def jsonb[T](inline f: T => String): ParameterBinderFactory[T] = ${ jsonImpl('{ f }, '{ ObjectType.Jsonb }) }

  inline def json[T](inline f: T => String): ParameterBinderFactory[T] = ${ jsonImpl('{ f }, '{ ObjectType.Json }) }

  private def jsonImpl[T: Type](f: Expr[T => String], objectType: Expr[ObjectType])(using
    quotes: Quotes
  ): Expr[ParameterBinderFactory[T]] =
    import quotes.reflect.*
    '{
      ParameterBinderFactory[T] { (value: T) => (stmt: PreparedStatement, idx: Int) =>
        val obj     = new PGobject()
        val jsonStr = $f(value)
        obj.setType($objectType.name)
        obj.setValue(jsonStr)
        stmt.setObject(idx, obj)
      }
    }

  private def arrayImpl[A, T[X]](
    f: Expr[T[A] => Array[Any]],
    objectType: Expr[ObjectType]
  )(using quotes: Quotes, tpa: Type[A], tpt: Type[T]): Expr[ParameterBinderFactory[T[A]]] =
    import quotes.reflect.*
    '{
      ParameterBinderFactory[T[A]] { (value: T[A]) => (stmt: PreparedStatement, idx: Int) =>
        stmt.setArray(idx, stmt.getConnection.createArrayOf($objectType.name, $f(value)))
      }
    }
