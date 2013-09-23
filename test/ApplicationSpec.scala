package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.libs.json.{JsObject, Json}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "return HTTP 200 for a valid request to the show() route" in {
      running(FakeApplication()) {
        val response = route(FakeRequest(GET, "/")).get
        status(response) must equalTo(OK)
        contentType(response) must beSome.which(_ == "text/html")
        contentAsString(response) must contain ("Contacts in Play")
      }
    }
    
    "return JSON for a valid request to the load() route" in {
      running(FakeApplication()) {

        val pageSize = "10"
        val page = "1"
        val order = "asc"
        val sortCol = "1"
        val sEcho = "1"

        val request = FakeRequest(GET, "/load").
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
        (jsonResponse\"iTotalRecords").toString() must equalTo("11")
        (jsonResponse\"iTotalDisplayRecords").toString() must equalTo("11")
      }
    }
  }
}