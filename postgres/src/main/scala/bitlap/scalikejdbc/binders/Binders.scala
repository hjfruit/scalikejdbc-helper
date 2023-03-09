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

import scalikejdbc.ParameterBinderFactory

import java.sql.Connection

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
trait Binders {

  // Iterable[String]
  given stringList2Array(using Connection): ParameterBinderFactory[List[String]] =
    DeriveParameterBinderFactory.arrayOf[String, List](OType.String, _.toArray)

  given stringSeq2Array(using Connection): ParameterBinderFactory[Seq[String]] =
    DeriveParameterBinderFactory.arrayOf[String, Seq](OType.String, _.toArray)

  given stringSet2Array(using Connection): ParameterBinderFactory[Set[String]] =
    DeriveParameterBinderFactory.arrayOf[String, Set](OType.String, _.toArray)

  given stringVector2Array(using Connection): ParameterBinderFactory[Vector[String]] =
    DeriveParameterBinderFactory.arrayOf[String, Vector](OType.String, _.toArray)
  // Iterable[String] end

  // Iterable[Int]
  given intList2Array(using Connection): ParameterBinderFactory[List[Int]] =
    DeriveParameterBinderFactory.arrayOf[Int, List](OType.Int, _.toArray)

  given intSeq2Array(using Connection): ParameterBinderFactory[Seq[Int]] =
    DeriveParameterBinderFactory.arrayOf[Int, Seq](OType.Int, _.toArray)

  given intSet2Array(using Connection): ParameterBinderFactory[Set[Int]] =
    DeriveParameterBinderFactory.arrayOf[Int, Set](OType.Int, _.toArray)

  given intVector2Array(using Connection): ParameterBinderFactory[Vector[Int]] =
    DeriveParameterBinderFactory.arrayOf[Int, Vector](OType.BigDecimal, _.toArray)
  // Iterable[Int] end

  // Iterable[BigDecimal]
  given bigDecimalList2Array(using Connection): ParameterBinderFactory[List[BigDecimal]] =
    DeriveParameterBinderFactory.arrayOf[BigDecimal, List](OType.BigDecimal, _.toArray)

  given bigDecimalSeq2Array(using Connection): ParameterBinderFactory[Seq[BigDecimal]] =
    DeriveParameterBinderFactory.arrayOf[BigDecimal, Seq](OType.BigDecimal, _.toArray)

  given bigDecimalSet2Array(using Connection): ParameterBinderFactory[Set[BigDecimal]] =
    DeriveParameterBinderFactory.arrayOf[BigDecimal, Set](OType.BigDecimal, _.toArray)

  given bigDecimalVector2Array(using Connection): ParameterBinderFactory[Vector[BigDecimal]] =
    DeriveParameterBinderFactory.arrayOf[BigDecimal, Vector](OType.BigDecimal, _.toArray)
  // Iterable[BigDecimal] end

  // json
  given map2Json[T](using toJsonString: T => String): ParameterBinderFactory[T] =
    DeriveParameterBinderFactory.json[T](toJsonString)

  given map2Jsonb[T](using toJsonString: T => String): ParameterBinderFactory[T] =
    DeriveParameterBinderFactory.jsonb[T](toJsonString)
  // json end
}
