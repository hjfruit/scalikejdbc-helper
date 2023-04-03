package bitlap.scalikejdbc

import scalikejdbc.*

trait PostgresSQLSyntaxSupport:

  extension (self: InsertSQLBuilder)
    def onConflictUpdate(constraint: String)(columnsAndValues: (SQLSyntax, Any)*): InsertSQLBuilder = {
      val cvs = columnsAndValues map { case (c, v) =>
        sqls"$c = $v"
      }
      self.append(
        sqls"ON CONFLICT ON CONSTRAINT ${SQLSyntax.createUnsafely(constraint)} DO UPDATE SET ${sqls.csv(cvs: _*)}"
      )
    }

    def onConflictDoNothing(): InsertSQLBuilder = self.append(sqls"ON CONFLICT DO NOTHING")

  extension (self: sqls.type) def values(column: SQLSyntax): SQLSyntax = sqls"values($column)"

end PostgresSQLSyntaxSupport
