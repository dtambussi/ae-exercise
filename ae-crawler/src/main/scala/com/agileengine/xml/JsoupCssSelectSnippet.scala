package com.agileengine.xml

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._

object JsoupCssSelectSnippet extends App with LazyLogging {

  val CHARSET_NAME = "utf8"

  // Jsoup requires an absolute file path to resolve possible relative paths in HTML,
  // so providing InputStream through classpath resources is not a case
  val resourcePath = "./samples/startbootstrap-freelancer-gh-pages-cut.html"

  val cssQuery = "div[id=\"success\"] button[class*=\"btn-primary\"]"

  findElementsByQuery(new File(resourcePath), cssQuery).map { buttons =>
    buttons.iterator().toList.map { button =>
      button.attributes().toList.map(attr => s"${attr.getKey}=${attr.getValue}").mkString(", ")
    }
  } match {
    case Success(attrsList) => attrsList.foreach { attrs => logger.info("Target element attrs: [{}]", attrs) }
    case Failure(ex) => logger.error("Error occurred.", ex)
  }

  def findElementsByQuery(htmlFile: File, cssQuery: String): Try[Elements] = Try {
    Jsoup.parse(htmlFile, CHARSET_NAME, htmlFile.getAbsolutePath)
  }.map(_.select(cssQuery))

}
