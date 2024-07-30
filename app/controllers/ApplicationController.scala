package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{
  def index(): Action[AnyContent] = TODO

  def create(): Action[AnyContent] = TODO


  def read(id: String): Action[AnyContent] = TODO


  def update(id: String): Action[AnyContent] = TODO


  def delete(id: String): Action[AnyContent] = TODO
}
