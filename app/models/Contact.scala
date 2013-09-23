package models

import play.api.Play.current
import play.api.Play
import play.api.db._

import anorm._
import anorm.SqlParser._

/**
 * Contact Value Object
 *
 * @param name
 * @param email
 */
case class Contact(name:String, email:String)

/**
 * Defines the contract from the ContactService
 */
trait ContactService {
  def getList(sortCol:Int, asc:Boolean, pageSize:Int, page:Int): (Int, List[Contact])
}

/**
 * Abstract parent implementation of ContactService that implements getList and provides sorting.
 */
abstract class AbstractContactServiceImpl extends ContactService {

  def getList(sortCol:Int, asc:Boolean, pageSize:Int, page:Int): (Int, List[Contact]) = {

    val sortedContactList = getSortedList(sortCol, asc)
    val indexOfLast = sortedContactList.size - 1
    val firstIndex = page * pageSize
    val lastIndex = Math.min(firstIndex + pageSize - 1, indexOfLast)

    val resultList = (firstIndex to lastIndex).map(i => sortedContactList(i)).toList
    (sortedContactList.size, resultList)
  }

  private def getSortedList(sortCol:Int, asc:Boolean): List[Contact] = {
    val contactList = getListFromSource

    // Col 0 is the "Row no", which has sort disabled.
    if (asc) {
      sortCol match {
        case 0 => contactList.sortBy(contact => contact.name)
        case 1 => contactList.sortBy(contact => contact.email)
      }
    } else {
      sortCol match {
        case 0 => contactList.sortWith(_.name > _.name)
        case 1 => contactList.sortWith(_.email > _.email)

      }
    }
  }

  def getListFromSource(): List[Contact]

}

/**
 * Implementation of the ContactService that uses contacts from text file
 */
class FileContactServiceImpl extends AbstractContactServiceImpl {

  def getListFromSource(): List[Contact] = {
    // Read file conf/accessLog.txt
    val inputStream = Play.classloader.getResourceAsStream("contacts.txt")
    val lines = io.Source.fromInputStream(inputStream).getLines
    lines.map(convert(_)).toList

  }

  private def convert(line: String): Contact = {
    val tokens = line.split(',')
    val name = tokens(0)
    val email = tokens(1)
    Contact(name, email)
  }

}

/**
 * Implementation of the ContactService that uses In-Memory Database table
 */
class DatabaseContactServiceImpl extends AbstractContactServiceImpl {

  def getListFromSource(): List[Contact] = {
    List(Contact.findAll(): _*)
  }

}

/**
 * Database CRUD for Anorm
 */
object Contact {

  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("name") ~
    get[String]("email") map {
      case id~name~email => Contact(name, email)
    }
  }

  def findAll(): Seq[Contact] = {
    DB.withConnection { implicit connection =>
      SQL("select * from contact").as(Contact.simple *)
    }
  }

  def create(bar: Contact): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into contact(name, email) values ({name, email})").on(
        'name -> bar.name,
        'email -> bar.email
      ).executeUpdate()
    }
  }

}