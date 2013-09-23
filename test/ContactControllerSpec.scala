package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.libs.json.{JsObject, Json}
import controllers.ContactController
import models.FileContactServiceImpl


class ContactControllerSpec extends Specification {

  "ContactController" should {

    "return HTTP 200 for a valid request to the show() route" in {
      running(FakeApplication(withGlobal = Some(FakeContactGlobal))) {
        val response = route(FakeRequest(GET, "/")).get
        status(response) must equalTo(OK)
        contentType(response) must beSome.which(_ == "text/html")
        contentAsString(response) must contain ("Contacts in Play")
      }
    }
    
    "return JSON for a valid request to the load() route" in {
      running(FakeApplication(withGlobal = Some(FakeContactGlobal))) {
        val pageSize = "10"
        val page = "1"
        val order = "asc"
        val sortCol = "1"
        val sEcho = "1"

        val request = FakeRequest(GET, "/loadContacts").
                           withFormUrlEncodedBody(
                             "iDisplayLength" -> pageSize,
                             "iDisplayStart" -> page,
                             "sSortDir_0" -> order,
                             "iSortCol_0" -> sortCol,
                             "sEcho" -> sEcho
                           )
        val response = route(request).get

        status(response) must equalTo(OK)

        contentType(response) must beSome.which(_ == "application/json")

        val jsonResponse = Json.parse( contentAsString(response) ).asInstanceOf[JsObject]
        (jsonResponse\"iTotalRecords").toString() must equalTo("16")
        (jsonResponse\"iTotalDisplayRecords").toString() must equalTo("16")
      }
    }
  }
}

object FakeContactGlobal extends play.api.GlobalSettings {
    override def getControllerInstance[A](clazz: Class[A]) = {
      new ContactController(new FileContactServiceImpl).asInstanceOf[A]
    }
}