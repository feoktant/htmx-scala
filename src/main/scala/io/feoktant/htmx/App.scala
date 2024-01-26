package io.feoktant.htmx

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

  Contact.loadDb()
  initialize()
}
