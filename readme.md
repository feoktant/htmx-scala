# Htmx with Scala, Cask and ScalaTags

The repo for work with book ["Hypermedia Systems"](https://hypermedia.systems/hypermedia-systems).
Book uses Python + Flask + Jinja2, and as it is mentioned:

> it shouldn’t be hard to map them into your preferred language and frameworks.

This is my first attempt to use [Cask](https://com-lihaoyi.github.io/cask) & 
[ScalaTags](https://com-lihaoyi.github.io/scalatags), so it could be fun to get to the last page.
Previously I had experience with Python/Django. I'll try to stick to original 
[contact-app](https://github.com/bigskysoftware/contact-app) code, even if it 
will not be canonical FP. Also, commits in repo will appear as code growth.

Main purpose - try new tech and refresh knowledge about modern Web.

## A Web 1.0 Application

As the first step, lets take the minimal dependencies, and built Web 1.0 app
without JS. It will make our hands dirty with it, and give taste of templates
without template engine.

Not sure if [one-file-one-template](https://com-lihaoyi.github.io/scalatags/#Old-schoolTemplates)
is so bad. I liked a lot Django's template engine, and missed it when worked with
JSP or Twirl.

[`missing.css`](https://missing.style) was updated to v1.1.1 as the latest release.
It lost tag `all-caps`.