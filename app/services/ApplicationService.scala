package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel}

import javax.inject.Inject
import scala.concurrent.impl.Promise
import scala.concurrent.{ExecutionContext, Future}

class ApplicationService @Inject()(connector: LibraryConnector) {


  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, Book] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search:$term")
    val result = connector.get[GoogleBooksResponse](url)

    result.map { googleBooksResponse =>
      googleBooksResponse.items.headOption match {
        case Some(googleBook) =>
          Book(
            isbn = findIsbn(googleBook.volumeInfo.industryIdentifiers),
            title = googleBook.volumeInfo.title,
            subtitle = googleBook.volumeInfo.subtitle.getOrElse(""),
            pageCount = googleBook.volumeInfo.pageCount.getOrElse(0)
          )
      }
    }
  }

  def findIsbn(identifiers: Seq[IndustryIdentifier]): String = {
    identifiers.find(_.`type` == "ISBN_13").map(_.identifier)
      .orElse(identifiers.find(_.`type` == "ISBN_10").map(_.identifier))
      .getOrElse(identifiers.headOption.map(_.identifier).getOrElse(""))
  }

  def getClosestMatches(search: String, term: String, maxResults:Int)(implicit ec: ExecutionContext): EitherT[Future, APIError, Seq[Book]]  = {
    val formattedTerm = term.split("\\s+").map(_.trim).mkString("+")
    val orderBy = "orderBy=relevance"
    val max = s"maxResults=$maxResults"
    val query = s"$search:$formattedTerm"
    val url = s"https://www.googleapis.com/books/v1/volumes?q=$query&$maxResults&$orderBy"
    val result = connector.get[GoogleBooksResponse](url)

    result.map { googleBooksResponse =>
      googleBooksResponse.items.map { googleBook =>
        Book(
          isbn = googleBook.volumeInfo.industryIdentifiers.find(_.`type` == "ISBN_13").map(_.identifier).getOrElse(""),
          title = googleBook.volumeInfo.title,
          subtitle = googleBook.volumeInfo.subtitle.getOrElse(""),
          pageCount = googleBook.volumeInfo.pageCount.getOrElse(0)
        )
      }
    }
  }
}
