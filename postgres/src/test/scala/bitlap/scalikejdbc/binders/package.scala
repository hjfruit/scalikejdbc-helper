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

package bitlap.scalikejdbc.binders

import java.util.ArrayList as JArrayList
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.control.Breaks.*
import java.io.*
import scalikejdbc.*

/** @author
 *    梦境迷离
 *  @version 1.0,2023/4/11
 */
def parseInitFile(initFile: String): List[String] =
  val file                      = new File(initFile)
  var br: BufferedReader        = null
  var initSqlList: List[String] = Nil
  try
    val input = new FileInputStream(file)
    br = new BufferedReader(new InputStreamReader(input, "UTF-8"))
    var line: String = null
    val sb           = new mutable.StringBuilder("")
    while {
      line = br.readLine
      line != null
    } do
      line = line.trim
      if line.nonEmpty then
        if line.startsWith("#") || line.startsWith("--") then {
          // todo: continue is not supported
        } else {
          line = line.concat(" ")
          sb.append(line)
        }
    initSqlList = getInitSql(sb.toString)
  catch
    case e: IOException =>
      throw new IOException(e)
  finally if br != null then br.close()
  initSqlList

def getInitSql(sbLine: String): List[String] =
  val sqlArray    = sbLine.toCharArray
  val initSqlList = new JArrayList[String]
  var index       = 0
  var beginIndex  = 0
  while index < sqlArray.length do
    if sqlArray(index) == ';' then
      val sql = sbLine.substring(beginIndex, index).trim
      initSqlList.add(sql)
      beginIndex = index + 1

    index += 1
  initSqlList.asScala.toList
