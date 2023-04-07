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

package bitlap.scalikejdbc

import bitlap.scalikejdbc.binders.Utils
import scalikejdbc.*
import scalikejdbc.interpolation.SQLSyntax.join

import java.sql.Connection
import scala.Product2

trait PostgresSQLSyntaxSupport:

  extension (self: InsertSQLBuilder)
    def onConflictUpdate(constraintColumns: SQLSyntax*)(columnsAndValues: SQLSyntax*): InsertSQLBuilder =
      val cvs = columnsAndValues map { c =>
        sqls"$c = EXCLUDED.$c"
      }
      self.append(
        sqls"ON CONFLICT (${sqls.csv(constraintColumns: _*)}) DO UPDATE SET ${sqls.csv(cvs: _*)}"
      )

    def onConflictDoNothing(): InsertSQLBuilder = self.append(sqls"ON CONFLICT DO NOTHING")

    def multipleValuesPlus(
      multipleValues: collection.Seq[(SQLSyntax, ParameterBinder)]*
    ): InsertSQLBuilder = {
      val vs = multipleValues match {
        case Nil => Seq(sqls"()")
        case ss  => ss.map(s => sqls"(${sqls.csv(s.map(v => sqls"${v._2}").toList: _*)})")
      }
      self.copy(sql = sqls"${self.sql} values ${sqls.join(vs, sqls",", false)}")
    }

  end extension

  extension (self: sqls.type) def values(column: SQLSyntax): SQLSyntax = sqls"VALUES($column)"

  /** Batch insert with name values
   */
  def batchInsertNameValues(table: SQLSyntaxSupport[_], entities: List[(SQLSyntax, ParameterBinder)]*)(using
    Connection
  ): SQLBatch =
    assert(entities.nonEmpty)
    val sys         = sqls.join(entities.map(f => sqls.csv(f.map(f => sqls"$f"): _*)), sqls",", false)
    val nameColumns = entities.head.map(e => Utils.lowerUnderscore(e._1.value)).mkString(",")
    val s           = SQL(s"""INSERT INTO ${table.tableNameWithSchema} ($nameColumns) VALUES(${sys.value})""")
    println(s.statement)
    SQL(s"""INSERT INTO ${table.tableNameWithSchema} ($nameColumns) VALUES(${sys.value})""")
      .batch(entities.map(_.map(f => f._2))*)

end PostgresSQLSyntaxSupport
