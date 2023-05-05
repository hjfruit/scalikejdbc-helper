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

package bitlap.scalikejdbc.binders.model

import bitlap.scalikejdbc.core.*
import scalikejdbc.*
import bitlap.scalikejdbc.core.internal.DeriveEnumTypeBinder
import bitlap.scalikejdbc.binders.AllBinders

final case class JsonEntity(
  name: Map[String, String]
)

object JsonTable extends SQLSyntaxSupport[JsonEntity], AllBinders:

  implicit val asJson: Map[String, String] => String = (mp: Map[String, String]) =>
    mp.map(kv => s"""\"${kv._1}\":\"${kv._2}\"""").mkString("{", ",", "}")

  implicit val fromJson: String => Map[String, String] = (jsonStr: String) =>
    val Array(k, v) = jsonStr
      .replace("{", "")
      .replace("}", "")
      .split(":")
    Map(k.trim.replace("\"", "") -> v.trim.replace("\"", ""))

  override def schemaName: Option[String] = Some("testdb")
  override val tableName                  = "t_json"

  def apply(up: ResultName[JsonEntity])(rs: WrappedResultSet): JsonEntity = autoConstruct(rs, up)

  val jsonColumn = JsonTable.column

  val j = JsonTable.syntax("j")

  def insertJson(
    j: JsonEntity
  )(using a: AutoSession = AutoSession): Int =
    withSQL {
      insert
        .into(JsonTable)
        .namedValues(
          jsonColumn.name -> j.name
        )
    }.update.apply()

  def queryJson()(using a: AutoSession = AutoSession): Option[JsonEntity] =
    withSQL {
      select.from(JsonTable as j)
    }.map(JsonTable(j.resultName)).single.apply()
