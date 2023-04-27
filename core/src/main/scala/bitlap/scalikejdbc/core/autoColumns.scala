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

package bitlap.scalikejdbc.core

import bitlap.scalikejdbc.core.internal.EntityUtil
import bitlap.scalikejdbc.core.internal.EntityUtil.ConstructorParam
import scalikejdbc.*

import scala.language.`3.0`
import scala.quoted.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/4/13
 */
object autoColumns {

  def applyImpl[E: Type](table: Expr[SQLSyntaxSupport[E]], excludes: Expr[Seq[String]])(using
    quotes: Quotes
  ): Expr[List[SQLSyntax]] = {
    import quotes.reflect.*
    Expr.ofList(
      EntityUtil.constructorParams(excludes).collect {
        case c: ConstructorParam[quotes.type] if !c.excluded =>
          val name = c.name
          '{
            $table.column.selectDynamic(${ Expr(name) })
          }
      }
    )
  }

  inline def apply[E](
    table: SQLSyntaxSupport[E],
    inline excludes: String*
  ): List[SQLSyntax] =
    ${ applyImpl('table, 'excludes) }
}
