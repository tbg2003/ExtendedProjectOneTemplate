package models
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.data.validation.Constraints.nonEmpty
import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String,
                     title: String,
                     subtitle: String,
                     pageCount: Int)

object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

  val dataModelForm: Form[DataModel] = Form {
    mapping(
      "_id" -> text.verifying(nonEmpty),
      "title" -> text.verifying(nonEmpty),
      "subtitle" -> text,
      "pageCount" -> number.verifying(Constraints.min(0)),
    )(DataModel.apply)(DataModel.unapply)
  }
}


