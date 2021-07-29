package controllers

import dao.AutoAdvertRepository
import json._
import models.NewAutoAdvert
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject

class CarAdvertRestController @Inject()(
val controllerComponents: ControllerComponents,
ftRepo: AutoAdvertRepository,
) extends BaseController with CirceExtensions {

  def getAllAdverts: Action[AnyContent] = Action {
    val result = ftRepo.all
    if (result.isEmpty)
      NoContent
    else
      Ok(asJson(result))
  }

  def addNewAdvert(): Action[AnyContent] = Action {
    val result = ftRepo.all
    if (result.isEmpty)
      NoContent
    else
      Ok(asJson(result))
  }

  def deleteAdvert(itemId: Long): Action[AnyContent] = Action {
    if (ftRepo.delete(itemId)) {
      NoContent
    } else {
      NotFound
    }
  }

}
