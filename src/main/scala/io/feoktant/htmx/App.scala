package io.feoktant.htmx

import cask.model.Response
import scalatags.Text
import scalatags.Text._

object App extends cask.MainRoutes {

  @cask.staticResources("/static")
  def staticResourceRoutes() = "static"

  @cask.get("/")
  def index() =
    cask.Redirect("/contacts")

  @cask.get("/contacts")
  def contacts(q: Option[String] = None) =
    val contactSet = q match { // 1
      case Some(search) => Contact.search(search) // 2
      case _ => Contact.all() // 3
    }
    Templates.index(contactSet) // 4

  @cask.get("/contacts/new")
  def contactsNewGet(): Text.all.doctype =
    Templates.`new`()

  @cask.postForm("/contacts/new")
  def contactsNew(
      email: String,
      first_name: Option[String],
      last_name: Option[String],
      phone: Option[String],
  ): Response[Text.all.doctype] =
    val contact = Contact(first_name, last_name, phone, email)
    Contact.save(contact) match
      case Right(value) =>
        cask.Response(Text.all.doctype("html")(StringFrag("")), 301, Seq("Location" -> "/contacts"), Nil)
      case Left(errors) =>
        cask.Response(Templates.`new`(Some(contact), errors))

  Contact.loadDb()
  initialize()
}
