package models

import play.api.data.Form
import play.api.data.Forms.{email, mapping, nonEmptyText}
import play.api.libs.json.{Json, OFormat}

case class Login(
                userName:String,
                password:String
                )
object Login{
  implicit val formats: OFormat[Login] = Json.format[Login]

  val loginForm: Form[Login] = Form {
    mapping(
      "userName" -> email,
      "password" -> nonEmptyText,
    )(Login.apply)(Login.unapply)
  }
}