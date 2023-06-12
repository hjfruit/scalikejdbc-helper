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

import org.scalatest.*
import scalikejdbc.*

import java.sql.Statement
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres

/** @author
 *    梦境迷离
 *  @version 1.0,2023/6/12
 */
trait BaseSpec extends BeforeAndAfterAll { this: Suite =>

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = true,
    singleLineMode = true,
    printUnprocessedStackTrace = false,
    stackTraceDepth = 3,
    logLevel = "info",
    warningEnabled = true,
    warningThresholdMillis = 3000L,
    warningLogLevel = "info"
  )

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

}
