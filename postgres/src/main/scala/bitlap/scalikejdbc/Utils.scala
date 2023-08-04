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

import scala.quoted.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/8
 */
object Utils:

  def lowerUnderscore(camelCaseStr: String): String = {
    if (camelCaseStr == null) return null
    val charArray = camelCaseStr.toCharArray
    val buffer    = new StringBuffer
    var i         = 0
    val l         = charArray.length
    while (i < l) {
      if (charArray(i) >= 65 && charArray(i) <= 90) buffer.append("_").append((charArray(i).toInt + 32).toChar)
      else buffer.append(charArray(i))
      i += 1
    }
    buffer.toString
  }

  inline def showCode_[A](inline a: A): String = ${ showCode[A]('{ a }) }

  def showCode[A: Type](a: Expr[A])(using quotes: Quotes): Expr[String] =
    import quotes.reflect.*
    Expr(Printer.TreeShortCode.show(a.asTerm))
