package controllers

import play.api.mvc._
import models.ContactService
import play.api.libs.json._

object Application extends Controller {

  def show = Action(
    Ok(views.html.index("Contacts DataTable")))

  def load = Action(
    implicit request => {
      // Search is disabled.
      //val filter = request.getQueryString("sSearch")

      // Might able to use Mapping.
      val pageSize:Int = request.getQueryString("iDisplayLength").getOrElse("10").toInt
      val page = request.getQueryString("iDisplayStart").getOrElse("1").toInt  / pageSize
      val order = request.getQueryString("sSortDir_0").getOrElse("asc")
      val sortCol = request.getQueryString("iSortCol_0").getOrElse("1").toInt
      val sEcho = request.getQueryString("sEcho")

      val asc = (order == "asc")
      val (iTotalRecords, displayRecords) = ContactService.getList(sortCol, asc, pageSize, page)

      val contactDataList = displayRecords.zipWithIndex.map{ case (contact,index) =>
      {
        Json.arr(contact.name, contact.email)
      }}

      val result = Json.obj("sEcho" -> sEcho,
        "iTotalRecords" -> iTotalRecords,
        "iTotalDisplayRecords" -> iTotalRecords,
        "aaData" -> contactDataList)

      val json = Json.toJson(result)
      Ok(json)
    })
  
}