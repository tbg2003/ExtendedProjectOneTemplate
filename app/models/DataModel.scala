package models
import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String,
                     id: String,
                     title: String,
                     subtitle: String,
                     pageCount: Int)

object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]
}