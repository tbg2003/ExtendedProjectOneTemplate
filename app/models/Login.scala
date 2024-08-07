package models

import play.api.data.Form
import play.api.data.Forms.{email, mapping, nonEmptyText, number, optional, text}
import play.api.data.validation.Constraints
import play.api.libs.json.{Json, OFormat}

case class Login(
                userName:String,
                password:String
                )
object Login{
  implicit val formats: OFormat[Login] = Json.format[Login]

  // Form mapping to handle optional fields
  val dataModelForm: Form[Login] = Form {
    mapping(
      "userName" -> email,
      "password" -> nonEmptyText,
    )(Login.apply)(Login.unapply)
  }
}