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

package bitlap.scalikejdbc

import bitlap.scalikejdbc.binders.*
import scalikejdbc.*

import scala.collection.immutable.Nil

trait PostgresSQLSyntaxSupport:

  extension (self: InsertSQLBuilder)

    /** Insert and `ON CONFLICT ... DO UPDATE SET ...`.
     *
     *  It also supports Array values by [[bitlap.scalikejdbc.binders.ArrayBinders]], and supports Json by
     *  [[bitlap.scalikejdbc.binders.JsonBinders]].
     */
    def onConflictUpdate(constraintColumns: SQLSyntax*)(columnsAndValues: SQLSyntax*): InsertSQLBuilder =
      val cvs = columnsAndValues map { c =>
        sqls"$c = EXCLUDED.$c"
      }
      self.append(
        sqls"ON CONFLICT (${sqls.csv(constraintColumns: _*)}) DO UPDATE SET ${sqls.csv(cvs: _*)}"
      )

    /** Insert and `ON CONFLICT DO NOTHING`
     */
    def onConflictDoNothing(): InsertSQLBuilder = self.append(sqls"ON CONFLICT DO NOTHING")

    /** Insert multiple values with name.
     *
     *  It also supports Array values by [[bitlap.scalikejdbc.binders.ArrayBinders]], and supports Json by
     *  [[bitlap.scalikejdbc.binders.JsonBinders]].
     */
    def multipleValuesPlus(
      multipleValues: List[(SQLSyntax, ParameterBinder)]*
    ): InsertSQLBuilder = {
      val vs = batchValues(multipleValues*)
      self.copy(sql = sqls"${self.sql} values ${sqls.csv(vs: _*)}")
    }

  end extension

  def batchValues(multipleValues: List[(SQLSyntax, ParameterBinder)]*): Seq[SQLSyntax] = multipleValues match {
    case Nil => Seq(sqls"()")
    case ss  => ss.map(s => sqls"(${sqls.csv(s.map(v => sqls"${v._2}"): _*)})")
  }

  extension (self: sqls.type)
    /** WITH RECURSIVE
     */
    def withRecursive[T: SQLSyntaxSupport](
      cols: List[SQLSyntax],
      outerWhereConditions: SQLSyntax
    )(
      onCteTable: SQLSyntaxSupport[T] => SQLSyntax,
      onInnerTable: SQLSyntaxSupport[T] => SQLSyntax,
      returnCols: (SQLSyntaxSupport[T] => SQLSyntax)*
    ): SQLSyntax =
      val tableSyntax  = summon[SQLSyntaxSupport[T]]
      val outerAliasAs = tableSyntax.as(tableSyntax.syntax("outer"))
      val innerAliasAs = tableSyntax.as(tableSyntax.syntax("inner"))
      val outerWhereConds =
        if (outerWhereConditions.isEmpty) sqls.empty else sqls.where + outerWhereConditions
      val aliasT2Cols = cols.map(c => sqls"inner.$c")
      val cteOn =
        sqls"cte_tb.${onCteTable(tableSyntax)}" // why cte_tb ? because they become placeholders after using interpolation.
      val innerOn     = sqls"inner.${onInnerTable(tableSyntax)}"
      val unionSelect = sqls"SELECT ${sqls.csv(aliasT2Cols*)} FROM cte_tb INNER JOIN $innerAliasAs ON $cteOn = $innerOn"
      val returnSelect = sqls"SELECT ${sqls.csv(returnCols.map(_.apply(tableSyntax)): _*)} FROM cte_tb"

      // scalafmt: { maxColumn = 400 }
      sqls"""WITH RECURSIVE cte_tb AS 
            |        (
            |          SELECT ${sqls.csv(cols: _*)} FROM $outerAliasAs $outerWhereConds
            |          UNION ($unionSelect) 
            |        ) $returnSelect""".stripMargin

end PostgresSQLSyntaxSupport
