package models

import play.api.Play.current
import play.api.Play

case class Contact(name:String, title:String, email:String)

object ContactService {

  /**
   *  sortCol - index of the column to be sorted.
   *  asc - true if sort is ascending.
   *  pageSize : No of records per page
   *  page : (O based). The page to be returned.
   *
   *  Return (total no of records, records to be displayed)
   */
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
        case 1 => contactList.sortBy(contact => contact.title)
        case 2 => contactList.sortBy(contact => contact.email)
      }
    } else {
      sortCol match {
        case 0 => contactList.sortWith(_.name > _.name)
        case 1 => contactList.sortWith(_.title > _.title)
        case 2 => contactList.sortWith(_.email > _.email)

      }

    }
  }

  /**
   * Read the csv file conf/accessLog.txt and produce a list of Access objects.
   */
  def getListFromSource(): List[Contact] = {
    // Read file conf/accessLog.txt
    val inputStream = Play.classloader.getResourceAsStream("contacts.txt")
    val lines = io.Source.fromInputStream(inputStream).getLines
    lines.map(convert(_)).toList

  }

  private def convert(line: String): Contact = {
    val tokens = line.split(',')
    val name = tokens(0)
    val title = tokens(1)
    val email = tokens(2)
    Contact(name, title, email)
  }

}