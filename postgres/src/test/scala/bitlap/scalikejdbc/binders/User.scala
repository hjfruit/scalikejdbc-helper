package bitlap.scalikejdbc.binders

import scalikejdbc.*

import java.time.Instant
import bitlap.scalikejdbc.binders.ArrayBinders
import java.sql.Connection

final case class User(
  id: String,
  varcharArray: List[String],
  decimalArray: List[BigDecimal],
  intArray: List[Int],
  longArray: List[Long]
)

object User extends SQLSyntaxSupport[User], ArrayBinders:

  implicit def arrayStringMapping: Array[Any] => List[String] = a =>
    a.map(ae =>
      ae match
        case s: String => s
        case _         => ae.toString
    ).toList

  override def schemaName: Option[String] = Some("testdb")
  override val tableName                  = "t_user"

  val userColumn = User.column

  val user = User.syntax("u")

  def insertUser(
    id: String,
    intArray: List[Int],
    longArray: List[Long],
    varcharArray: List[String],
    decimalArray: List[BigDecimal]
  )(using Connection): SQLUpdate =
    withSQL {
      insert
        .into(User)
        .namedValues(
          userColumn.id           -> id,
          userColumn.varcharArray -> varcharArray,
          userColumn.decimalArray -> decimalArray,
          userColumn.intArray     -> intArray,
          userColumn.longArray    -> longArray
        )
    }.update
