package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApplicationService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, Book] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search+$term")
    val result = connector.get[GoogleBooksResponse](url)

    result.map { googleBooksResponse =>
      googleBooksResponse.items.headOption match {
        case Some(googleBook) =>
          Book(
            isbn = googleBook.volumeInfo.industryIdentifiers.find(_.`type` == "ISBN_13").map(_.identifier).getOrElse(""),
            title = googleBook.volumeInfo.title,
            subtitle = googleBook.volumeInfo.subtitle.getOrElse(""),
            pageCount = googleBook.volumeInfo.pageCount.getOrElse(0)
          )
        case None => throw new RuntimeException("No book found with the provided ISBN")
      }
    }
  }
}
