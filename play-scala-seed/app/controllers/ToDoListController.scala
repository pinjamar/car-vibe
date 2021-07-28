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


  private var todoList = new mutable.ListBuffer[TodoListItem]()
  todoList += TodoListItem(1, "test", true)
  todoList += TodoListItem(2, "some other value", false)

  implicit val todoListJson = Json.format[TodoListItem]

  implicit val newTodoListJson = Json.format[NewTodoListItem]
  implicit val editTodoListJson = Json.format[EditTodoListItem]


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

  def deleteAllDone(): Action[AnyContent] = Action {
    val deletableItems = todoList.filter({ item => item.isItDone })
    if (deletableItems.isEmpty) {
      NotFound
    } else {
      todoList = todoList.dropWhile(_.isItDone)
      Ok
    }
  }

  def delete(itemId: Long): Action[AnyContent] = Action {

    val foundItem = todoList.find(_.id == itemId)
    if (foundItem.isEmpty) {
      NotFound
    }
    else {
      todoList = todoList.dropWhile(_.id == itemId)
      Ok
    }

  }

  def updateItem(itemId: Long): Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson
    val editListItem: Option[EditTodoListItem] =
      jsonObject.flatMap(
        Json.fromJson[EditTodoListItem](_).asOpt
      )

    val updateableItem = todoList.find(_.id == itemId)

    editListItem match {
      case Some(editItem) =>
        if (updateableItem.isEmpty) {
          NotFound
        } else {
          todoList = todoList.dropWhile(_.id == itemId)
          val newItem = updateableItem.get.copy(
            description = editItem.description,
            isItDone = editItem.isItDone
          )
          todoList.addOne(newItem)
          Ok(Json.toJson(newItem))
        }
      case None =>
        BadRequest


    }
  }

}