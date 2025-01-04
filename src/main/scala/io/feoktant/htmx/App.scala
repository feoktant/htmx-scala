package io.feoktant.htmx

import cask.model.Response
import io.feoktant.htmx.Contact.NewContactId
import scalatags.Text
import scalatags.Text.all.*

object App extends cask.MainRoutes {

  @cask.staticResources("/static")
  def staticResourceRoutes() = "static"

  @cask.get("/")
  def index() =
    cask.Redirect("/contacts")

  @cask.get("/contacts")
  def contacts(q: Option[String] = None): doctype =
    val contactSet = q match { // 1
      case Some(search) => Contact.search(search) // 2
      case _ => Contact.all() // 3
    }
    Templates.index(contactSet) // 4

  @cask.get("/contacts/new")
  def contactsNewGet(): doctype =
    Templates.`new`()

  @cask.postForm("/contacts/new")
  def contactsNew(
      email: String,
      first_name: Option[String],
      last_name: Option[String],
      phone: Option[String],
  ): Response[doctype] =
    val contact = Contact(NewContactId, first_name, last_name, phone, email)
    Contact.save(contact) match
      case Right(value) =>
        cask.Redirect("/contacts").copy(data = doctype("html")(StringFrag("")))
      case Left(errors) =>
        cask.Response(Templates.`new`(Some(contact), errors))

  @cask.get("/contacts/:contactId")
  def contactsView(contactId: Int): Response[doctype] =
    Contact.find(contactId) match
      case Some(contact) =>
        cask.Response(Templates.show(contact))
      case None =>
        cask.Response(doctype("html")(StringFrag("")), 404)

  @cask.get("/contacts/:contactId/edit")
  def contactsEditGet(contactId: Int): Response[doctype] =
    Contact.find(contactId) match
      case Some(contact) =>
        cask.Response(Templates.edit(contact))
      case None =>
        cask.Response(doctype("html")(StringFrag("")), 404)

  @cask.postForm("/contacts/:contactId/edit")
  def contactsEditPost(
    contactId: Int,
    email: String,
    first_name: Option[String],
    last_name: Option[String],
    phone: Option[String],
  ): Response[doctype] =
    Contact.find(contactId)
      .map(_.copy(first = first_name, last = last_name, email = email, phone = phone)) match
      case None => cask.Response(doctype("html")(StringFrag("")), 404)
      case Some(contact) =>
        Contact.save(contact) match
          case Right(value) =>
            cask.Redirect(s"/contacts/$contactId").copy(data = doctype("html")(StringFrag("")))
          case Left(errors) =>
            cask.Response(Templates.edit(contact, errors))

  @cask.postForm("/contacts/:contactId/delete")
  def contactsDelete(contactId: Int): Response[doctype] =
    Contact.delete(contactId) match
      case None =>
        cask.Response(doctype("html")(StringFrag("")), 404)
      case Some(contact) =>
        cask.Redirect("/contacts").copy(doctype("html")(StringFrag("")))

  Contact.loadDb()
  initialize()
}
