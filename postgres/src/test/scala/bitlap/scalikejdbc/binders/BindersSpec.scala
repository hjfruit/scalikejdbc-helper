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

import bitlap.scalikejdbc.binders.Utils.*
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
class BindersSpec extends AnyFlatSpec with Matchers:

  def toJson(mp: Map[String, String]): String =
    mp.map(kv => s"""\"${kv._1}\":\"${kv._2}\"""").mkString("{", ",", "}")

  "DeriveParameterBinderFactory postgres json" should "ok" in {
    toJson(Map("key" -> "value")) shouldEqual "{\"key\":\"value\"}"

    val jsonMap = showCode_(DeriveParameterBinderFactory.json[Map[String, String]](toJson))
    println(s"jsonMap:\n$jsonMap")
    jsonMap shouldEqual """(ParameterBinderFactory.apply[Map[String, String]](((value: Map[String, String]) => ((stmt: PreparedStatement, idx: Int) => {
                          |  val obj: PGobject = new PGobject()
                          |  val jsonStr: String = BindersSpec.this.toJson(value)
                          |  obj.setType(OType.Json.name)
                          |  obj.setValue(jsonStr)
                          |  stmt.setObject(idx, obj)
                          |}))): ParameterBinderFactory[Map[String, String]])""".stripMargin

    val jsonbMap = showCode_(DeriveParameterBinderFactory.jsonb[Map[String, String]](toJson))
    println(s"jsonbMap:\n$jsonbMap")
    jsonbMap shouldEqual """(ParameterBinderFactory.apply[Map[String, String]](((value: Map[String, String]) => ((stmt: PreparedStatement, idx: Int) => {
                           |  val obj: PGobject = new PGobject()
                           |  val jsonStr: String = BindersSpec.this.toJson(value)
                           |  obj.setType(OType.Jsonb.name)
                           |  obj.setValue(jsonStr)
                           |  stmt.setObject(idx, obj)
                           |}))): ParameterBinderFactory[Map[String, String]])""".stripMargin

  }

  "DeriveParameterBinderFactory postgres array" should "ok" in {
    given Connection = ???
    val stringArr    = showCode_(DeriveParameterBinderFactory.arrayOf[String, List](OType.String, _.toArray))
    println(s"stringArr:\n$stringArr")
    stringArr shouldEqual
      """{
        |  val f$proxy3: Function1[List[String], Array[Any]] = ((_$1: List[String]) => _$1.toArray[Any](ClassTag.Any))
        |
        |  (ParameterBinderFactory.apply[List[String]](((value: List[String]) => ((stmt: PreparedStatement, idx: Int) => stmt.setArray(idx, given_Connection.createArrayOf(String.name, f$proxy3.apply(value)))))): ParameterBinderFactory[List[String]])
        |}""".stripMargin

    val intArr = showCode_(DeriveParameterBinderFactory.arrayOf[Int, Seq](OType.Int, _.toArray))
    println(s"intArr:\n$intArr")
    intArr shouldEqual
      """{
        |  val f$proxy4: Function1[Seq[Int], Array[Any]] = ((_$2: Seq[Int]) => _$2.toArray[Any](ClassTag.Any))
        |
        |  (ParameterBinderFactory.apply[Seq[Int]](((value: Seq[Int]) => ((stmt: PreparedStatement, idx: Int) => stmt.setArray(idx, given_Connection.createArrayOf(Int.name, f$proxy4.apply(value)))))): ParameterBinderFactory[Seq[Int]])
        |}""".stripMargin

    val parameterBinderWithValue = DeriveParameterBinderFactory.arrayOf[Int, Seq](OType.Int, _.toArray).apply(Seq(123))
    parameterBinderWithValue.toString shouldEqual "ParameterBinder(value=List(123))"
  }
