package services

import play.api.libs.json.{Json, OFormat}

case class Book(
               _id:String,
               name:String,
               description:String,
               pageCount:Int
               )
object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}