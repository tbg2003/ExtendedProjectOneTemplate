package controllers

import com.mongodb.client.result.{DeleteResult, UpdateResult}
import repositories.DataRepository
import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request, WrappedRequest}


import services.ApplicationService
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
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    dataRepository.read(id).map {
      case Some(item: DataModel) => Ok(Json.toJson(item))
      case None => NotFound(Json.toJson(s"No items found with id:$id"))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json){implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        // further validation of fields
      dataRepository.update(id, dataModel).map {
        case result: UpdateResult if result.wasAcknowledged() => Accepted {
          Json.toJson(dataModel)
        }
        case result: UpdateResult if !result.wasAcknowledged() => NotFound
      }
      case JsError(_) => Future(BadRequest)
      }
    }

  def delete(id: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    dataRepository.delete(id) map{
      case result: DeleteResult if result.wasAcknowledged() => Accepted
      case result: DeleteResult if !result.wasAcknowledged() => NotFound
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).map {
      ???
    }
  }

}
