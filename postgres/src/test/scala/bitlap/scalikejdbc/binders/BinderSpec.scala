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

import bitlap.scalikejdbc.PostgresSQLSyntaxSupport
import bitlap.scalikejdbc.binders.User.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.*

import java.sql.{ Connection, DriverManager }
import javax.sql.DataSource
import scala.collection.immutable.List

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
class BinderSpec extends AnyFlatSpec with Matchers with PostgresSQLSyntaxSupport with ArrayBinders:

  ConnectionPool.add("default", "jdbc:h2:mem:testdb", "", "")
  val conn = DriverManager.getConnection(
    "jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=RUNSCRIPT\nFROM 'classpath:test.sql'"
  )
  val stmt = conn.createStatement()
  val users = List(
    User(id = "3", varcharArray = List("444", "444"), decimalArray = Nil, longArray = Nil, intArray = List(1))
  )

  val twoUsers = List(
    User(id = "6", varcharArray = List("444", "444"), decimalArray = Nil, longArray = Nil, intArray = List(1)),
    User(id = "7", varcharArray = List("444", "444"), decimalArray = Nil, longArray = Nil, intArray = List(1))
  )

  implicit def arrayStringMapping: Array[Any] => List[String] = a =>
    a.map(ae =>
      ae match
        case s: String => s
        case _         => ae.toString
    ).toList

  "DeriveTypeBinder String List" should "ok" in {
    val res = stmt.executeQuery("select * from `testdb`.t_user")
    res.next()
    val typeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)

    val strLists = typeBinder(res, 2)
    strLists shouldEqual List("a", "b")
  }

  "DeriveTypeBinder BigDecimal List" should "ok" in {
    val res = stmt.executeQuery("select decimal_array from `testdb`.t_user")
    res.next()
    val typeBinder = DeriveTypeBinder.array[BigDecimal, List](_.toList.map(s => BigDecimal(s.toString)), Nil)

    val decimalLists = typeBinder(res, 1)
    decimalLists shouldEqual List(0.1, 0.2)
  }

  "DeriveTypeBinder insert BigDecimal List" should "ok" in {
    DB.localTx { implicit session =>
      given Connection = session.connection
      User.insertUser(users.head).apply()
    }
    val res = stmt.executeQuery("select int_array,long_array,varchar_array from `testdb`.t_user where id = '3'")
    res.next()
    val stringTypeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)
    val intTypeBinder    = DeriveTypeBinder.array[Int, List](_.toList.map(_.toString.toInt), Nil)
    val longTypeBinder   = DeriveTypeBinder.array[Long, List](_.toList.map(_.toString.toLong), Nil)

    val intList = intTypeBinder(res, 1)
    intList shouldEqual List(1)

    val stringList = stringTypeBinder(res, 3)
    stringList shouldEqual List("444", "444")
  }

  "batchInsert method" should "ok" in {
    DB.localTx { implicit session =>
      given Connection = session.connection
      val usersNameValues = users.map(u =>
        List(
          User.userColumn.id           -> "4",
          User.userColumn.decimalArray -> u.decimalArray,
          User.userColumn.varcharArray -> u.varcharArray
        )
      )
      val sql =
        batchInsertNameValues(
          User,
          usersNameValues*
        )
      sql.statement shouldEqual "INSERT INTO testdb.t_user (id,decimal_array,varchar_array) VALUES(?, ?, ?)"
      sql.parameters.size shouldEqual 1
      sql.parameters.head.size shouldEqual 3
      sql.apply()
    }
    val res = stmt.executeQuery("select varchar_array from testdb.t_user where id = '4'")
    res.next()
    val stringTypeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)
    val stringList       = stringTypeBinder(res, 1)
    stringList shouldEqual List("444", "444")
  }

  "multipleValuesPlus method" should "ok" in {
    DB.localTx { implicit session =>
      given Connection = session.connection
      val usersNameValues = users.map(u =>
        List(
          User.userColumn.id           -> "5",
          User.userColumn.varcharArray -> u.varcharArray,
          User.userColumn.decimalArray -> u.decimalArray
        )
      )

      val sql: InsertSQLBuilder =
        insert
          .into(User)
          .columns(
            User.userColumn.id,
            User.userColumn.varcharArray,
            User.userColumn.decimalArray
          )
          .multipleValuesPlus(usersNameValues*)

      sql.toSQL.statement shouldEqual "insert into testdb.t_user (id, varchar_array, decimal_array) values (?, ?, ?)"
      sql.toSQL.parameters.size shouldEqual 3
      withSQL(sql).update.apply()
    }
    val res = stmt.executeQuery("select varchar_array from testdb.t_user where id = '5'")
    res.next()
    val stringTypeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)
    val stringList       = stringTypeBinder(res, 1)
    stringList shouldEqual List("444", "444")
  }

//  "multipleValuesPlus and on conflict method" should "ok" in {
//    DB.localTx { implicit session =>
//      given Connection = session.connection
//
//      val usersNameValues = twoUsers.map(u =>
//        List(
//          User.userColumn.id           -> u.id,
//          User.userColumn.varcharArray -> u.varcharArray,
//          User.userColumn.decimalArray -> u.decimalArray
//        )
//      )
//
//      val sql: InsertSQLBuilder =
//        insert
//          .into(User)
//          .columns(
//            User.userColumn.id,
//            User.userColumn.varcharArray,
//            User.userColumn.decimalArray
//          )
//          .multipleValuesPlus(usersNameValues*)
//      withSQL(sql).update.apply()
//
//      val usersNameValuesConflict = twoUsers.map(u =>
//        List(
//          User.userColumn.id -> u.id,
//          User.userColumn.varcharArray -> List("conflictListValues1", "conflictListValues2"),
//          User.userColumn.decimalArray -> u.decimalArray
//        )
//      )
//
//      val sqlConflict: InsertSQLBuilder =
//        insert
//          .into(User)
//          .columns(
//            User.userColumn.id,
//            User.userColumn.varcharArray,
//            User.userColumn.decimalArray
//          )
//          .multipleValuesPlus(usersNameValuesConflict *)
//          .onConflictUpdate(User.userColumn.id) {
//            User.userColumn.varcharArray
//          }
//      withSQL(sqlConflict).update.apply()
//    }
//    val res = stmt.executeQuery("select varchar_array from testdb.t_user where id = '6'")
//    res.next()
//    val stringTypeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)
//    val stringList       = stringTypeBinder(res, 1)
//    stringList shouldEqual List("conflictListValues1", "conflictListValues2")
//  }
