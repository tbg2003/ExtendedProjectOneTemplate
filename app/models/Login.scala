package models

import play.api.data.Form
import play.api.data.Forms.{email, mapping, nonEmptyText}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.{Json, OFormat}

case class Login(
                userName:String,
                password:String
                )
object Login{
  implicit val formats: OFormat[Login] = Json.format[Login]

  val passwordMinLength: Constraint[String] = Constraint("constraints.password.minLength")({
    plainText =>
      if (plainText.length >= 8) {
        Valid
      } else {
        Invalid(ValidationError("must be at least 8 characters long"))
      }
  })

  val passwordHasUpperCase: Constraint[String] = Constraint("constraints.password.upperCase")({
    plainText =>
      if (plainText.exists(_.isUpper)) {
        Valid
      } else {
        Invalid(ValidationError("must contain at least one uppercase letter"))
      }
  })

  val passwordHasLowerCase: Constraint[String] = Constraint("constraints.password.lowerCase")({
    plainText =>
      if (plainText.exists(_.isLower)) {
        Valid
      } else {
        Invalid(ValidationError("must contain at least one lowercase letter"))
      }
  })

  val passwordHasDigit: Constraint[String] = Constraint("constraints.password.digit")({
    plainText =>
      if (plainText.exists(_.isDigit)) {
        Valid
      } else {
        Invalid(ValidationError("must contain at least one digit"))
      }
  })

  val passwordHasSpecialChar: Constraint[String] = Constraint("constraints.password.specialChar")({
    plainText =>
      val specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~"
      if (plainText.exists(ch => specialChars.contains(ch))) {
        Valid
      } else {
        Invalid(ValidationError("must contain at least one special character"))
      }
  })

  val passwordConstraints: Constraint[String] = Constraint("constraints.password")({
    plainText =>
      val constraints = Seq(
        passwordMinLength,
        passwordHasUpperCase,
        passwordHasLowerCase,
        passwordHasDigit,
        passwordHasSpecialChar
      )

      val errors = constraints.flatMap(constraint => constraint(plainText) match {
        case Invalid(errors) => errors
        case _ => Nil
      })
      if (errors.isEmpty) Valid
      else Invalid(errors)
  })
  val loginForm: Form[Login] = Form {
    mapping(
      "userName" -> email,
      "password" -> nonEmptyText.verifying(passwordConstraints)
    )(Login.apply)(Login.unapply)
  }
}