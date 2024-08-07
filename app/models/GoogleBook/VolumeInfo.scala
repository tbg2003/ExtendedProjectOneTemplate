package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(
                       title: String,
                       subtitle: Option[String],
                       pageCount: Option[Int],
                       industryIdentifiers: Seq[IndustryIdentifier]
                     )
object VolumeInfo {
  implicit val format: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}