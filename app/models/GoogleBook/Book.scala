package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class Book(
                 isbn: String,
                 title: String,
                 subtitle: String,
                 pageCount: Int
               )

object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}


