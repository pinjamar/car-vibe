package controllers

import dao.AutoAdvertDataSource
import json._
import models.CreateUpdateAutoAdvert
import models.errors._
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject

class CarAdvertRestController @Inject()(
                                         val controllerComponents: ControllerComponents,
                                         autoRepo: AutoAdvertDataSource,
) extends BaseController with CirceExtensions {

  def getAllAdverts(sortBy: Option[String]): Action[AnyContent] = Action {
    val result = autoRepo.all(sortBy = convertApiFieldToDbField(sortBy))
    if (result.isEmpty)
      NoContent
    else
      Ok(asJson(result))
  }

  def getAdvertById(itemId: Long): Action[AnyContent] = Action {
    autoRepo.byId(itemId) match {
      case Some(car) => Ok(asJson(car))
      case None => NotFound
    }
  }

  def editAdvertById(itemId: Long): Action[CreateUpdateAutoAdvert] =  Action(circe.json[CreateUpdateAutoAdvert]) { implicit request =>
    val carAdvert =  autoRepo.byId(itemId)
      if (carAdvert.isEmpty)
        NoContent
      else
    autoRepo.editAdvert(request.body, itemId) match {
      case Left(error) => UnprocessableEntity(asJson(error))
      case Right(advert) => Ok(asJson(advert))
    }
  }

  def addNewAdvert(): Action[CreateUpdateAutoAdvert] = Action(circe.json[CreateUpdateAutoAdvert]) { implicit request =>
    autoRepo.newAdvert(request.body) match {
      case Left(error) => UnprocessableEntity(asJson(error))
      case Right(value) => Created(asJson(value))
    }
  }


  def deleteAdvert(itemId: Long): Action[AnyContent] = Action {
    if (autoRepo.delete(itemId)) {
      NoContent
    } else {
      NotFound
    }
  }

  private def convertApiFieldToDbField(field: Option[String]): Option[String] = {
    field.map {
      case "id" => "id"
      case "title" => "title"
      case "fuelType" => "fuel_type"
      case "price" => "price"
      case "mileage" => "mileage"
      case "isNew" => "is_new"
      case "firstRegistration" => "first_registration"
      case _  => null
    }
  }


}
