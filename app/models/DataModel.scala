package models
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String,
                     title: String,
                     subtitle: String,
                     pageCount: Int)

object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

  val dataModelForm: Form[DataModel] = Form {
    mapping(
      "_id" -> text,
      "title" -> text,
      "subtitle" -> text,
      "pageCount" -> number,
    )(DataModel.apply)(DataModel.unapply)
  }
}


