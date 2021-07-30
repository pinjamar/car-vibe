package controllers

import dao.FuelTypesRepository
import json._
import play.api.mvc._

import javax.inject._

@Singleton
class FuelTypeController @Inject()(
  val controllerComponents: ControllerComponents,
  val fuelTypesRepository: FuelTypesRepository
) extends BaseController with CirceExtensions {

  def all: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val allFuels = fuelTypesRepository.all
    if (allFuels.isEmpty) {
      NotFound
    } else {
      Ok(asJson(allFuels))
    }
  }
}

