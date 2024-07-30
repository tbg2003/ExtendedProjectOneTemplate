package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def index(): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    Future.successful(Ok)
  }

  def create(): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    Future.successful(Ok)
  }

  def read(id: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    Future.successful(Ok)
  }

  def update(id: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    Future.successful(Ok)
  }

  def delete(id: String): Action[AnyContent] = Action.async{implicit request: Request[AnyContent] =>
    Future.successful(Ok)
  }

}
