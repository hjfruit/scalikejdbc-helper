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

import bitlap.scalikejdbc.ObjectType
import bitlap.scalikejdbc.internal.*
import scalikejdbc.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
trait ArrayBinders:
  // Iterable[String]
  given stringList2Array: ParameterBinderFactory[List[String]] =
    DeriveParameterBinder.array[String, List](ObjectType.String, _.toArray)

  given stringSeq2Array: ParameterBinderFactory[Seq[String]] =
    DeriveParameterBinder.array[String, Seq](ObjectType.String, _.toArray)

  given stringSet2Array: ParameterBinderFactory[Set[String]] =
    DeriveParameterBinder.array[String, Set](ObjectType.String, _.toArray)

  given stringVector2Array: ParameterBinderFactory[Vector[String]] =
    DeriveParameterBinder.array[String, Vector](ObjectType.String, _.toArray)
  // Iterable[String] end

  // Iterable[Int]
  given intList2Array: ParameterBinderFactory[List[Int]] =
    DeriveParameterBinder.array[Int, List](ObjectType.Int, _.toArray)

  given intSeq2Array: ParameterBinderFactory[Seq[Int]] =
    DeriveParameterBinder.array[Int, Seq](ObjectType.Int, _.toArray)

  given intSet2Array: ParameterBinderFactory[Set[Int]] =
    DeriveParameterBinder.array[Int, Set](ObjectType.Int, _.toArray)

  given intVector2Array: ParameterBinderFactory[Vector[Int]] =
    DeriveParameterBinder.array[Int, Vector](ObjectType.BigDecimal, _.toArray)
  // Iterable[Int] end

  // Iterable[BigDecimal]
  given bigDecimalList2Array: ParameterBinderFactory[List[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, List](ObjectType.BigDecimal, _.toArray)

  given bigDecimalSeq2Array: ParameterBinderFactory[Seq[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, Seq](ObjectType.BigDecimal, _.toArray)

  given bigDecimalSet2Array: ParameterBinderFactory[Set[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, Set](ObjectType.BigDecimal, _.toArray)

  given bigDecimalVector2Array: ParameterBinderFactory[Vector[BigDecimal]] =
    DeriveParameterBinder.array[BigDecimal, Vector](ObjectType.BigDecimal, _.toArray)
  // Iterable[BigDecimal] end

  // Iterable[Long]
  given longList2Array: ParameterBinderFactory[List[Long]] =
    DeriveParameterBinder.array[Long, List](ObjectType.Long, _.toArray)

  given longSeq2Array: ParameterBinderFactory[Seq[Long]] =
    DeriveParameterBinder.array[Long, Seq](ObjectType.Long, _.toArray)

  given longSet2Array: ParameterBinderFactory[Set[Long]] =
    DeriveParameterBinder.array[Long, Set](ObjectType.Long, _.toArray)

  given longVector2Array: ParameterBinderFactory[Vector[Long]] =
    DeriveParameterBinder.array[Long, Vector](ObjectType.Long, _.toArray)
  // Iterable[Long] end

  // type binder
  given intArray2List: TypeBinder[List[Int]] =
    DeriveTypeBinder.array[Int, List](_.map(_.toString.toInt).toList, List.empty[Int])
  given stringArray2List: TypeBinder[List[String]] =
    DeriveTypeBinder.array[String, List](_.map(_.toString).toList, List.empty[String])
  given longArray2List: TypeBinder[List[Long]] =
    DeriveTypeBinder.array[Long, List](_.map(_.toString.toLong).toList, List.empty[Long])
  given bigDecimalArray2List: TypeBinder[List[BigDecimal]] =
    DeriveTypeBinder.array[BigDecimal, List](_.map(s => BigDecimal(s.toString)).toList, List.empty[BigDecimal])

  given intArray2Set: TypeBinder[Set[Int]] =
    DeriveTypeBinder.array[Int, Set](_.map(_.toString.toInt).toSet, Set.empty[Int])
  given stringArray2Set: TypeBinder[Set[String]] =
    DeriveTypeBinder.array[String, Set](_.map(_.toString).toSet, Set.empty[String])
  given longArray2Set: TypeBinder[Set[Long]] =
    DeriveTypeBinder.array[Long, Set](_.map(_.toString.toLong).toSet, Set.empty[Long])
  given bigDecimalArray2Set: TypeBinder[Set[BigDecimal]] =
    DeriveTypeBinder.array[BigDecimal, Set](_.map(s => BigDecimal(s.toString)).toSet, Set.empty[BigDecimal])

  given intArray2Vector: TypeBinder[Vector[Int]] =
    DeriveTypeBinder.array[Int, Vector](_.map(_.toString.toInt).toVector, Vector.empty[Int])
  given stringArray2Vector: TypeBinder[Vector[String]] =
    DeriveTypeBinder.array[String, Vector](_.map(_.toString).toVector, Vector.empty[String])
  given longArray2Vector: TypeBinder[Vector[Long]] =
    DeriveTypeBinder.array[Long, Vector](_.map(_.toString.toLong).toVector, Vector.empty[Long])
  given bigDecimalArray2Vector: TypeBinder[Vector[BigDecimal]] =
    DeriveTypeBinder.array[BigDecimal, Vector](_.map(s => BigDecimal(s.toString)).toVector, Vector.empty[BigDecimal])

  given intArray2Seq: TypeBinder[Seq[Int]] =
    DeriveTypeBinder.array[Int, Seq](_.map(_.toString.toInt).toSeq, Seq.empty[Int])
  given stringArray2Seq: TypeBinder[Seq[String]] =
    DeriveTypeBinder.array[String, Seq](_.map(_.toString).toSeq, Seq.empty[String])
  given longArray2Seq: TypeBinder[Seq[Long]] =
    DeriveTypeBinder.array[Long, Seq](_.map(_.toString.toLong).toSeq, Seq.empty[Long])
  given bigDecimalArray2Seq: TypeBinder[Seq[BigDecimal]] =
    DeriveTypeBinder.array[BigDecimal, Seq](_.map(s => BigDecimal(s.toString)).toSeq, Seq.empty[BigDecimal])

  // type binder end
