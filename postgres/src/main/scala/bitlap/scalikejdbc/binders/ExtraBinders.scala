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

import scalikejdbc.{ ParameterBinderFactory, TypeBinder }

import java.sql.Connection

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
trait ExtraBinders:
  // Iterable[String]
  given stringList2Array(using Connection): ParameterBinderFactory[List[String]] =
    DeriveParameterBinder.array[String, List](OType.String, _.toArray)

  given stringSeq2Array(using Connection): ParameterBinderFactory[Seq[String]] =
    DeriveParameterBinder.array[String, Seq](OType.String, _.toArray)

  given stringSet2Array(using Connection): ParameterBinderFactory[Set[String]] =
    DeriveParameterBinder.array[String, Set](OType.String, _.toArray)

  given stringVector2Array(using Connection): ParameterBinderFactory[Vector[String]] =
    DeriveParameterBinder.array[String, Vector](OType.String, _.toArray)
  // Iterable[String] end

  // Iterable[Int]
  given intList2Array(using Connection): ParameterBinderFactory[List[Int]] =
    DeriveParameterBinder.array[Int, List](OType.Int, _.toArray)

  given intSeq2Array(using Connection): ParameterBinderFactory[Seq[Int]] =
    DeriveParameterBinder.array[Int, Seq](OType.Int, _.toArray)

  given intSet2Array(using Connection): ParameterBinderFactory[Set[Int]] =
    DeriveParameterBinder.array[Int, Set](OType.Int, _.toArray)

  given intVector2Array(using Connection): ParameterBinderFactory[Vector[Int]] =
    DeriveParameterBinder.array[Int, Vector](OType.BigDecimal, _.toArray)
  // Iterable[Int] end

  // Iterable[BigDecimal]
  given bigDecimalList2Array(using Connection): ParameterBinderFactory[List[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, List](OType.BigDecimal, _.toArray)

  given bigDecimalSeq2Array(using Connection): ParameterBinderFactory[Seq[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, Seq](OType.BigDecimal, _.toArray)

  given bigDecimalSet2Array(using Connection): ParameterBinderFactory[Set[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, Set](OType.BigDecimal, _.toArray)

  given bigDecimalVector2Array(using Connection): ParameterBinderFactory[Vector[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, Vector](OType.BigDecimal, _.toArray)
  // Iterable[BigDecimal] end

  // json
  given type2Json[T](using map: T => String): ParameterBinderFactory[T] =
    DeriveParameterBinder.json[T](map)

  given type2Jsonb[T](using map: T => String): ParameterBinderFactory[T] =
    DeriveParameterBinder.jsonb[T](map)
  // json end

  // type binder
  given json2Type[T](using map: String => T): TypeBinder[T] =
    DeriveTypeBinder.string[T](map)

  given array2List[T](using map: Any => T): TypeBinder[List[T]] =
    DeriveTypeBinder.array[T, List](_.toList.map(map), List.empty[T])

  given array2Set[T](using map: Any => T): TypeBinder[Set[T]] =
    DeriveTypeBinder.array[T, Set](_.toSet.map(map), Set.empty[T])

  given array2Vector[T](using map: Any => T): TypeBinder[Vector[T]] =
    DeriveTypeBinder.array[T, Vector](_.toVector.map(map), Vector.empty[T])

  given array2Seq[T](using map: Any => T): TypeBinder[Seq[T]] =
    DeriveTypeBinder.array[T, Seq](_.toSeq.map(map), Seq.empty[T])
  // type binder end
