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

package bitlap.scalikejdbc.binders.model

import bitlap.scalikejdbc.PostgresSQLSyntaxSupport
import bitlap.scalikejdbc.binders.model.User.*
import bitlap.scalikejdbc.binders.*
import scalikejdbc.*

final case class User(
  id: String,
  varcharArray: List[String],
  decimalArray: List[BigDecimal],
  intArray: List[Int],
  longArray: List[Long],
  parentId: String = ""
)

object User extends SQLSyntaxSupport[User], ArrayBinders, PostgresSQLSyntaxSupport:

  override def schemaName: Option[String] = Some("testdb")
  override val tableName                  = "t_user"

  val userColumn = User.column

  val user = User.syntax("u")

  def insertUser(
    user: User
  ): SQLUpdate =
    withSQL {
      insert
        .into(User)
        .namedValues(
          userColumn.id           -> user.id,
          userColumn.varcharArray -> user.varcharArray,
          userColumn.decimalArray -> user.decimalArray,
          userColumn.intArray     -> user.intArray,
          userColumn.longArray    -> user.longArray
        )
    }.update

  def insertUserByNameValues(
    nameValues: List[(SQLSyntax, ParameterBinder)]
  ): SQLUpdate =
    withSQL {
      insert
        .into(User)
        .namedValues(
          nameValues: _*
        )
    }.update
