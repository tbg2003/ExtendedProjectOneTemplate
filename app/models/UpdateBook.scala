package models

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.data.validation.Constraints.nonEmpty
import play.api.libs.json.{Json, OFormat}


case class UpdateBook(
                       title: Option[String],
                       subtitle: Option[String],
                       pageCount: Option[Int]
                     )

object UpdateBook {
  implicit val formats: OFormat[UpdateBook] = Json.format[UpdateBook]

  // Form mapping to handle optional fields
  val dataModelForm: Form[UpdateBook] = Form {
    mapping(
      "title" -> optional(text),
      "subtitle" -> optional(text),
      "pageCount" -> optional(number.verifying(Constraints.min(0)))
    )(UpdateBook.apply)(UpdateBook.unapply)
  }
}