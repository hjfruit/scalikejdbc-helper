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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.scalikejdbcSQLInterpolationImplicitDef

import java.sql.{ Connection, DriverManager }

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
class BinderSpec extends AnyFlatSpec with Matchers {

  lazy val conn = DriverManager.getConnection(
    "jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=RUNSCRIPT\nFROM 'classpath:test.sql'"
  )

  lazy val stmt = conn.createStatement()

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
}
