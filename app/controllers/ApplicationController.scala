package controllers

import com.mongodb.client.result.UpdateResult
import repositories.DataRepository
import models.{APIError, DataModel}
import org.mongodb.scala.result
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request, Result}
import play.core.j.JavaAction
import services.{ApplicationService, Book, RepositoryService}
import views.html.helper.CSRF

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       val service: ApplicationService,
                                       val repoService: RepositoryService
                                     ) (implicit val ec: ExecutionContext)extends BaseController with play.api.i18n.I18nSupport{

  def index(): Action[AnyContent] = Action.async { implicit request =>
    repoService.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repoService.create(dataModel).map{
          case Right(createdDataModel) => Created(Json.toJson(createdDataModel))
          case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    repoService.read(id).map {
      case Right(Some(item: DataModel)) => Ok(Json.toJson(item))
      case Right(None) => NotFound(Json.toJson(s"No items found with id:$id"))
      case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
    }
  }

  def readByName(name: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    repoService.readByName(name).map {
      case Right(Some(item: DataModel)) => Ok(Json.toJson(item))
      case Right(None) => NotFound(Json.toJson(s"No items found with name:$name"))
      case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
    }
  }
  def updateField(id:String, field:String, value:String): Action[JsValue] = Action.async(parse.json){implicit request =>
    val cleanField:String = field.strip().toLowerCase
    val cleanValue:String = value.strip().toLowerCase
    repoService.updateField(id, cleanField, cleanValue).map {
      case Right(result: UpdateResult) =>
        if( result.wasAcknowledged()) Accepted
        else NotFound(Json.obj("message" -> s"No item found with id: $id"))
      case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
        }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json){implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        // further validation of fields
      repoService.update(id, dataModel).map {
        case Right(result: UpdateResult) =>
          if( result.wasAcknowledged()) Accepted(Json.toJson(dataModel))
          else NotFound(Json.obj("message" -> s"No item found with id: $id"))

        case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
      }
      case JsError(_) => Future(BadRequest)
      }
    }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    repoService.delete(id).map {
      case Right(result: result.DeleteResult) =>
        if (result.getDeletedCount > 0) Accepted
        else NotFound(Json.obj("message" -> s"No item found with id: $id"))

      case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).value.map {
      case Right(book) => Ok(Json.toJson(book))
      case Left(error: APIError.BadAPIResponse) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
      case Left(_) => InternalServerError(Json.obj("message" -> "An unexpected error occurred"))
    }
  }

  def example(): Action[AnyContent] = Action.async {implicit request =>
    Future.successful(Ok(views.html.example()))
  }

  def addBook(): Action[AnyContent] = Action.async {implicit request =>
   Future.successful(Ok(views.html.addBook(DataModel.dataModelForm)))
  }

  def addBookForm(): Action[AnyContent] = ???

  def accessToken(implicit request: Request[_]) = {
    CSRF.getToken
  }

  def displayBook(isbn: String): Action[AnyContent] = Action.async { implicit request =>
    // get book from google by isbn
    service.getGoogleBook(search = isbn, term = "isbn").value.flatMap {
      case Left(error) => Future.successful(Status(error.httpResponseStatus)(Json.toJson(error.reason)))
      case Right(book) =>
        // if got book then store in mongo
        val dataObj = new DataModel(
          _id = isbn,
          title = book.title,
          subtitle = book.subtitle,
          pageCount = book.pageCount
        )
        repoService.create(dataObj).flatMap {
          case Left(error) => Future.successful(Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage)))
          case Right(storedBookData: DataModel) =>
            // if stored then print the book
            repoService.read(isbn).flatMap {
              case Left(error) => Future(Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage)))
              case Right(Some(data:DataModel)) => Future.successful(Ok(views.html.book(book)))
              case Right(None) => Future.successful(NotFound(Json.toJson(s"No items found with isbn: $isbn")))
            }

        }
    }

  }

}
