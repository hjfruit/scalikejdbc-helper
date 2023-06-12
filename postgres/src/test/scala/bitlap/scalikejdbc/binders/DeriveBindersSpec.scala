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
import bitlap.scalikejdbc.Utils.*
import bitlap.scalikejdbc.internal.{ DeriveParameterBinder, DeriveTypeBinder }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.ParameterBinderWithValue

import java.sql.Connection
import java.time.ZonedDateTime
import scala.collection.immutable.ListMap
import scala.concurrent.{ ExecutionContext, Future }

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
class DeriveBindersSpec extends AnyFlatSpec with Matchers with BaseSpec:

  def toJson(mp: Map[String, String]): String =
    mp.map(kv => s"""\"${kv._1}\":\"${kv._2}\"""").mkString("{", ",", "}")

  "DeriveParameterBinderFactory postgres json" should "ok" in {
    toJson(Map("key" -> "value")) shouldEqual "{\"key\":\"value\"}"

    val jsonMap = showCode_(DeriveParameterBinder.json[Map[String, String]](toJson))
    println(s"jsonMap:\n$jsonMap")
    jsonMap shouldEqual """(ParameterBinderFactory.apply[Map[String, String]](((value: Map[String, String]) => ((stmt: PreparedStatement, idx: Int) => {
                          |  val obj: PGobject = new PGobject()
                          |  val jsonStr: String = DeriveBindersSpec.this.toJson(value)
                          |  obj.setType(ObjectType.Json.name)
                          |  obj.setValue(jsonStr)
                          |  stmt.setObject(idx, obj)
                          |}))): ParameterBinderFactory[Map[String, String]])""".stripMargin

    val jsonbMap = showCode_(DeriveParameterBinder.jsonb[Map[String, String]](toJson))
    println(s"jsonbMap:\n$jsonbMap")
    jsonbMap shouldEqual """(ParameterBinderFactory.apply[Map[String, String]](((value: Map[String, String]) => ((stmt: PreparedStatement, idx: Int) => {
                           |  val obj: PGobject = new PGobject()
                           |  val jsonStr: String = DeriveBindersSpec.this.toJson(value)
                           |  obj.setType(ObjectType.Jsonb.name)
                           |  obj.setValue(jsonStr)
                           |  stmt.setObject(idx, obj)
                           |}))): ParameterBinderFactory[Map[String, String]])""".stripMargin

  }

  "DeriveParameterBinderFactory postgres array" should "ok" in {
    val stringArr = showCode_(DeriveParameterBinder.array[String, List](ObjectType.String, _.toArray))
    println(s"stringArr:\n$stringArr")
    stringArr shouldEqual
      """{
        |  val f$proxy3: Function1[List[String], Array[Any]] = ((_$1: List[String]) => _$1.toArray[Any](ClassTag.Any))
        |
        |  (ParameterBinderFactory.apply[List[String]](((value: List[String]) => ((stmt: PreparedStatement, idx: Int) => stmt.setArray(idx, stmt.getConnection().createArrayOf(String.name, f$proxy3.apply(value)))))): ParameterBinderFactory[List[String]])
        |}""".stripMargin

    val intArr = showCode_(DeriveParameterBinder.array[Int, Seq](ObjectType.Int, _.toArray))
    println(s"intArr:\n$intArr")
    intArr shouldEqual
      """{
        |  val f$proxy4: Function1[Seq[Int], Array[Any]] = ((_$2: Seq[Int]) => _$2.toArray[Any](ClassTag.Any))
        |
        |  (ParameterBinderFactory.apply[Seq[Int]](((value: Seq[Int]) => ((stmt: PreparedStatement, idx: Int) => stmt.setArray(idx, stmt.getConnection().createArrayOf(Int.name, f$proxy4.apply(value)))))): ParameterBinderFactory[Seq[Int]])
        |}""".stripMargin

    val parameterBinderWithValue = DeriveParameterBinder.array[Int, Seq](ObjectType.Int, _.toArray).apply(Seq(123))
    parameterBinderWithValue.toString shouldEqual "ParameterBinder(value=List(123))"
  }

  "DeriveTypeBinder postgres" should "ok" in {
    val string = showCode_(DeriveTypeBinder.json(_.trim))
    println(s"string:\n$string")
    string shouldEqual """({
        |  final class $anon() extends TypeBinder[String] {
        |    def apply(rs: ResultSet, label: String): String = {
        |      val _$4$proxy1: String = rs.getString(label)
        |      _$4$proxy1.trim()
        |    }
        |    def apply(`rs₂`: ResultSet, columnIndex: Int): String = {
        |      val _$4$proxy2: String = `rs₂`.getString(columnIndex)
        |      _$4$proxy2.trim()
        |    }
        |  }
        |
        |  (new $anon(): TypeBinder[String])
        |}: TypeBinder[String])""".stripMargin

    val array = showCode_(DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil))
    println(s"array:\n$array")
    array shouldEqual """(TypeBinder.apply[List[String]](((resultSet: ResultSet, idx: Int) => {
                         |  val arr: Array = resultSet.getArray(idx)
                         |  if (arr.==(null)) Nil else arr.getArray() match {
                         |    case a: Array[Any] =>
                         |      genericWrapArray[Any](a).toList.map[String](((_$6: Any) => _$6.toString()))
                         |    case _ =>
                         |      Nil
                         |  }
                         |}))(((`resultSet₂`: ResultSet, label: String) => {
                         |  val `arr₂`: Array = `resultSet₂`.getArray(label)
                         |  if (`arr₂`.==(null)) Nil else `arr₂`.getArray() match {
                         |    case a: Array[Any] =>
                         |      genericWrapArray[Any](`a₂`).toList.map[String](((`_$6₂`: Any) => `_$6₂`.toString()))
                         |    case _ =>
                         |      Nil
                         |  }
                         |})): TypeBinder[List[String]])""".stripMargin
  }
