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

import scalikejdbc.TypeBinder

import java.sql.ResultSet
import scala.quoted.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
object DeriveTypeBinder {

  inline def array[A, T[X] <: Iterable[X]](inline f: Array[Any] => T[A], default: T[A]): TypeBinder[T[A]] = 
    ${ arrayImpl('{ f }, '{ default }) }

  inline def string[T](inline f: String => T): TypeBinder[T] = ${ stringImpl('{ f }) }

  private def stringImpl[T](f: Expr[String => T])(using Quotes, Type[T]): Expr[TypeBinder[T]] =
    '{
      new TypeBinder[T]:
        def apply(rs: ResultSet, label: String): T =
          $f(rs.getString(label))

        def apply(rs: ResultSet, columnIndex: Int): T =
          $f(rs.getString(rs.getString(columnIndex)))
    }

  private def arrayImpl[A, T[X]](
    f: Expr[Array[Any] => T[A]],
    default: Expr[T[A]]
  )(using quotes: Quotes, tpa: Type[A], tpt: Type[T]): Expr[TypeBinder[T[A]]] =
    import quotes.reflect.*
    '{
      TypeBinder[T[A]] { (resultSet: ResultSet, idx: Int) =>
        val arr = resultSet.getArray(idx)
        if arr == null then $default
        else
          arr.getArray match
            case a: Array[Any] => $f(a)
            case _           => $default

      } { (resultSet: ResultSet, label: String) =>
        val arr = resultSet.getArray(label)
        if arr == null then $default
        else
          arr.getArray match
            case a: Array[Any] => $f(a)
            case _           => $default
      }
    }

}
