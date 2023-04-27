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

import bitlap.scalikejdbc.core.*
import scalikejdbc.*
import bitlap.scalikejdbc.core.internal.DeriveEnumTypeBinder

final case class EnumEntity(
  id: TestEnum
)

enum TestEnum:
  case Enum1 extends TestEnum
  case Enum2 extends TestEnum

object TestEnum:
  implicit def enumFromInt: Int => TestEnum = TestEnum.fromOrdinal
end TestEnum

object EnumTable extends SQLSyntaxSupport[EnumEntity], AllBinders:

  override def schemaName: Option[String] = Some("testdb")
  override val tableName                  = "t_enum"

  def apply(up: ResultName[EnumEntity])(rs: WrappedResultSet): EnumEntity = EnumEntity(rs.get(1))

  val enumColumn = EnumTable.column

  val e = EnumTable.syntax("e")

  def insertEnum(
    e: EnumEntity
  ): SQLUpdate =
    withSQL {
      insert
        .into(EnumTable)
        .namedValues(
          enumColumn.id -> e.id
        )
    }.update

  def queryEnum()(using a: AutoSession = AutoSession): Option[EnumEntity] =
    withSQL {
      select.from(EnumTable as e)
    }.map(EnumTable(e.resultName)).single.apply()
