package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class GoogleBook(
                       id: String,
                       volumeInfo: VolumeInfo
                     )
object GoogleBook {
  implicit val format: OFormat[GoogleBook] = Json.format[GoogleBook]
}