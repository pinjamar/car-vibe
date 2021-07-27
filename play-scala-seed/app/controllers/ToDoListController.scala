
package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.collection._

import play.api.libs.json._

import models._





@Singleton
class TodoListController @Inject()(val controllerComponents: ControllerComponents)
extends BaseController {

 

    private val todoList = new mutable.ListBuffer[TodoListItem]()
  todoList += TodoListItem(1, "test", true)
  todoList += TodoListItem(2, "some other value", false)

  implicit val todoListJson = Json.format[TodoListItem]

  implicit val newTodoListJson = Json.format[NewTodoListItem]

 

  def getAll(): Action[AnyContent] = Action {
 if (todoList.isEmpty) {
    NoContent
  } else {
    Ok(Json.toJson(todoList))
  }
}

def getById(itemId: Long) = Action {
  val foundItem = todoList.find(_.id == itemId)
  foundItem match {
    case Some(item) => Ok(Json.toJson(item))
    case None => NotFound
  }
}

def addNewItem() = Action { implicit request => 
  val content = request.body 
  val jsonObject = content.asJson 
  val todoListItem: Option[NewTodoListItem] = 
    jsonObject.flatMap( 
      Json.fromJson[NewTodoListItem](_).asOpt 
    )

     todoListItem match {
    case Some(newItem) =>
      val nextId = todoList.map(_.id).max + 1
      val toBeAdded = TodoListItem(nextId, newItem.description, false)
      todoList += toBeAdded
      Created(Json.toJson(toBeAdded))
    case None =>
      BadRequest
  }
}
}