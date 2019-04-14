package com.agileengine.xml

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._

object JsoupFindByIdSnippet extends App with LazyLogging {

  private val CHARSET_NAME = "utf8"

  // Jsoup requires an absolute file path to resolve possible relative paths in HTML,
  // so providing InputStream through classpath resources is not a case
  val resourcePath = "./samples/startbootstrap-freelancer-gh-pages-cut.html"
  val targetElementId = "sendMessageButton"

  findElementById(new File(resourcePath), targetElementId).map { button =>
    button.attributes().asList().map(attr => s"${attr.getKey}=${attr.getValue}").mkString(", ")
  } match {
    case Success(attrs) => logger.info("Target element attrs: [{}]", attrs)
    case Failure(ex) => logger.error("Error occurred.", ex)
  }

  def findElementById(htmlFile: File, targetElementId: String): Try[Element] = Try {
    Jsoup.parse(htmlFile, CHARSET_NAME, htmlFile.getAbsolutePath)
  }.map(_.getElementById(targetElementId))

}
