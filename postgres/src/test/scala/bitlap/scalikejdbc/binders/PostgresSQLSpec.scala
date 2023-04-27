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
import bitlap.scalikejdbc.internal.DeriveTypeBinder
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.*

import java.sql.{ Connection, DriverManager, Statement }
import javax.sql.DataSource
import scala.collection.immutable.List

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
class PostgresSQLSpec
    extends AnyFlatSpec
    with Matchers
    with PostgresSQLSyntaxSupport
    with ArrayBinders
    with BeforeAndAfterAll:

  final def jdbcUriTemplate: String = "jdbc:postgresql://localhost:%s/postgres"

  var embeddedPostgres: EmbeddedPostgres = _
  var stmt: Statement                    = _

  override protected def beforeAll(): Unit = {
    embeddedPostgres = EmbeddedPostgres
      .builder()
      .start()

    ConnectionPool.singleton(jdbcUriTemplate.format(embeddedPostgres.getPort), "postgres", "postgres")
    stmt = embeddedPostgres.getPostgresDatabase.getConnection.createStatement()

    val sqls = parseInitFile(getClass.getClassLoader.getResource("test.sql").getFile)
    sqls.foreach(sql => stmt.execute(sql))
  }

  override protected def afterAll(): Unit =
    if (embeddedPostgres != null)
      embeddedPostgres.close()

    if (stmt != null) stmt.close()

  val users3_4 = List(
    User(id = "3", varcharArray = List("333", "333"), decimalArray = Nil, longArray = Nil, intArray = List(1)),
    User(id = "4", varcharArray = List("444", "444"), decimalArray = Nil, longArray = Nil, intArray = List(1))
  )

  val users6_7 = List(
    User(id = "6", varcharArray = List("666", "666"), decimalArray = Nil, longArray = Nil, intArray = List(1)),
    User(id = "7", varcharArray = List("777", "777"), decimalArray = Nil, longArray = Nil, intArray = List(1))
  )

  "DeriveTypeBinder String List" should "ok" in {
    val res = stmt.executeQuery("select * from testdb.t_user")
    res.next()
    val typeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)

    val strLists = typeBinder(res, 2)
    strLists shouldEqual List("a", "b")
  }

  "DeriveTypeBinder BigDecimal List" should "ok" in {
    val res = stmt.executeQuery("select decimal_array from testdb.t_user")
    res.next()
    val typeBinder = DeriveTypeBinder.array[BigDecimal, List](_.toList.map(s => BigDecimal(s.toString)), Nil)

    val decimalLists = typeBinder(res, 1)
    decimalLists shouldEqual List(0.1, 0.2)
  }

  "DeriveTypeBinder insert BigDecimal List" should "ok" in {
    DB.localTx { implicit session =>
      User.insertUser(users3_4.head).apply()
    }
    val res = stmt.executeQuery("select int_array,long_array,varchar_array from testdb.t_user where id = '3'")
    res.next()
    val stringTypeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)
    val intTypeBinder    = DeriveTypeBinder.array[Int, List](_.toList.map(_.toString.toInt), Nil)
    val intList          = intTypeBinder(res, 1)
    intList shouldEqual List(1)

    val stringList = stringTypeBinder(res, 3)
    stringList shouldEqual List("333", "333")
  }

  "multipleValuesPlus method" should "ok" in {
    DB.localTx { implicit session =>
      val usersNameValues = users3_4.map(u =>
        List(
          User.userColumn.id           -> (u.id.toInt + 10).toString,
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

      sql.toSQL.statement shouldEqual "insert into testdb.t_user (id, varchar_array, decimal_array) values (?, ?, ?), (?, ?, ?)"
      sql.toSQL.parameters.size shouldEqual 6
      withSQL(sql).update.apply()
    }
    val stringList = getFirstArrayColumnAsList("select varchar_array from testdb.t_user where id = '13'")
    stringList shouldEqual List("333", "333")
  }

  "multipleValuesPlus and on conflict method" should "ok" in {
    DB.localTx { implicit session =>
      val usersNameValues = users6_7.map(u =>
        List(
          User.userColumn.id           -> u.id,
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

      sql.toSQL.statement shouldEqual "insert into testdb.t_user (id, varchar_array, decimal_array) values (?, ?, ?), (?, ?, ?)"
      withSQL(sql).update.apply()

      val usersNameValuesConflict = users6_7.map(u =>
        List(
          User.userColumn.id           -> u.id,
          User.userColumn.varcharArray -> List("conflictListValues1", "conflictListValues2"),
          User.userColumn.decimalArray -> u.decimalArray
        )
      )

      val sqlConflict: InsertSQLBuilder =
        insert
          .into(User)
          .columns(
            User.userColumn.id,
            User.userColumn.varcharArray,
            User.userColumn.decimalArray
          )
          .multipleValuesPlus(usersNameValuesConflict*)
          .onConflictUpdate(User.userColumn.id) {
            User.userColumn.varcharArray
          }
      sqlConflict.toSQL.statement shouldEqual "insert into testdb.t_user (id, varchar_array, decimal_array) values (?, ?, ?), (?, ?, ?) ON CONFLICT (id) DO UPDATE SET varchar_array = EXCLUDED.varchar_array"

      withSQL(sqlConflict).update.apply()
    }

    val stringList = getFirstArrayColumnAsList("select varchar_array from testdb.t_user where id = '6'")
    stringList shouldEqual List("conflictListValues1", "conflictListValues2")

  }

  "with recursive method" should "ok" in {
    given SQLSyntaxSupport[User] = User
    val withRecursiveSql = sqls.withRecursive[User](
      List(User.userColumn.id, User.userColumn.parentId),
      sqls"${User.userColumn.id} = 3".and(sqls"${User.userColumn.parentId} = 0")
    )(_.column.id, _.column.parentId, _.column.id)

    println(withRecursiveSql.value)

    withRecursiveSql.value shouldEqual
      """WITH RECURSIVE cte_tb AS 
        |        (
        |          SELECT id, parent_id FROM testdb.t_user outer  where id = 3 and (parent_id = 0)
        |          UNION (SELECT inner.id, inner.parent_id FROM cte_tb INNER JOIN testdb.t_user inner ON cte_tb.id = inner.parent_id) 
        |        ) SELECT id FROM cte_tb""".stripMargin
  }

  private def getFirstArrayColumnAsList(sql: String): List[String] = {
    val res = stmt.executeQuery(sql)
    res.next()
    val stringTypeBinder = DeriveTypeBinder.array[String, List](_.toList.map(_.toString), Nil)
    val stringList       = stringTypeBinder(res, 1)
    stringList
  }
