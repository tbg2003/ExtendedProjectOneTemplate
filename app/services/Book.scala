package services

import play.api.libs.json.{Json, OFormat}

case class Book(
               title:String,
               subtitle:String,
               pageCount:Int
               )
object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}