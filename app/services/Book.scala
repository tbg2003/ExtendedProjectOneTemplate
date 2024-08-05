package services

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, OFormat, Reads, __}

/** --- JSON RESPONSE FORMAT ---
 *
 * kind
 * totalItems
 * items : [id,
 *          volumeInfo:[title,
 *                      subtitle,
 *                      industryIdentifiers:[ type,
 *                                            identifier],
 *                      pageCount
 *         ]...
 * */
case class GoogleBooksResponse(
                                kind: String,
                                totalItems: Int,
                                items: Seq[GoogleBook]
                              )
object GoogleBooksResponse {
  implicit val format: OFormat[GoogleBooksResponse] = Json.format[GoogleBooksResponse]
}
case class GoogleBook(
                       id: String,
                       volumeInfo: VolumeInfo
                     )
object GoogleBook {
  implicit val format: OFormat[GoogleBook] = Json.format[GoogleBook]
}

case class VolumeInfo(
                       title: String,
                       subtitle: Option[String],
                       pageCount: Option[Int],
                       industryIdentifiers: Seq[IndustryIdentifier]
                     )
object VolumeInfo {
  implicit val format: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}


case class IndustryIdentifier(`type`: String, identifier: String)

object IndustryIdentifier {
  implicit val format: OFormat[IndustryIdentifier] = Json.format[IndustryIdentifier]
}


case class Book(
                 isbn: String,
                 title: String,
                 subtitle: String,
                 pageCount: Int
               )

object Book {
  implicit val reads: Reads[Book] = (
    (__ \ "volumeInfo" \ "industryIdentifiers").read[Seq[IndustryIdentifier]].map { identifiers =>
      identifiers.find(_.`type` == "ISBN_13").map(_.identifier)
        .orElse(identifiers.find(_.`type` == "ISBN_10").map(_.identifier))
        .getOrElse(identifiers.headOption.map(_.identifier).getOrElse(""))
    } and
      (__ \ "volumeInfo" \ "title").read[String] and
      (__ \ "volumeInfo" \ "subtitle").read[String].orElse(Reads.pure("")) and
      (__ \ "volumeInfo" \ "pageCount").read[Int].orElse(Reads.pure(0))
    )(Book.apply _)

  implicit val format: OFormat[Book] = Json.format[Book]
}


