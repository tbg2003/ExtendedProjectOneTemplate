package controllers

import com.mongodb.client.result.{DeleteResult, UpdateResult}
import repositories.DataRepository
import models.{APIError, DataModel}
import org.mongodb.scala.result
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request, WrappedRequest}
import play.mvc.Results.status
import services.{ApplicationService, Book}

import javax.inject.{Inject, Singleton}
import scala.concurrent.impl.Promise
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success


@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       val dataRepository: DataRepository,
                                       val service: ApplicationService
                                     ) (implicit val ec: ExecutionContext)extends BaseController{

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map{
          case Right(createdDataModel) => Created(Json.toJson(createdDataModel))
          case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    dataRepository.read(id).map {
      case Right(Some(item: DataModel)) => Ok(Json.toJson(item))
      case Right(None) => NotFound(Json.toJson(s"No items found with id:$id"))
      case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json){implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        // further validation of fields
      dataRepository.update(id, dataModel).map {
        case Right(result: UpdateResult) =>
          if( result.wasAcknowledged()) Accepted(Json.toJson(dataModel))
          else NotFound(Json.obj("message" -> s"No item found with id: $id"))

        case Left(error) => Status(error.upstreamStatus)(Json.toJson(error.upstreamMessage))
      }
      case JsError(_) => Future(BadRequest)
      }
    }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.delete(id).map {
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
}
