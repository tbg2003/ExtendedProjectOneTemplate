package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class GoogleBooksResponse(
                                kind: String,
                                totalItems: Int,
                                items: Seq[GoogleBook]
                              )
object GoogleBooksResponse {
  implicit val format: OFormat[GoogleBooksResponse] = Json.format[GoogleBooksResponse]
}