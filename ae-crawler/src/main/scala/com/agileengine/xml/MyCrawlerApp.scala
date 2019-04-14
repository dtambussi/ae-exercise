package com.agileengine.xml

import java.io.File

import org.jsoup.nodes.{Attribute, Element}

import scala.collection.JavaConversions._
import scala.util.Try

object MyCrawlerApp {

  val DefaultTargetElementId = "make-everything-ok-button"
  val SingleOcurrenceHtmlElements = Set("html", "body")

  val ZeroWeight = 0

  type HtmlElement = Element
  type SimilarityAnalyzer = HtmlElement => SimilarityAnalysisResult

  case class SimilarityAnalysisResult(element: HtmlElement, similarityWeight: Double, description: String) {
    override def toString = s"SimilarityAnalysisResult(similarity:$similarityWeight, description:$description)"
  }

  trait CrawlingResult

  case class EmptyCrawlingResult(targetElementId: String) extends  CrawlingResult {
    override def toString = s"Unable to find results for target element id: $targetElementId"
  }

  case class SuccessfulCrawlingResult (targetElementId: String, elementPath: String, totalWeight: Double, elementContents: String, details: List[SimilarityAnalysisResult]) extends CrawlingResult {
    override def toString: String =
      s"""
         |target element: $targetElementId
         |element path: $elementPath
         |total weight (score): $totalWeight
         |element contents: $elementContents
         |detail: $details
       """.stripMargin
  }

  /**
    *
    * Main crawling logic
    *
    **/

  def crawl(originalFilePath: String, sampleFilePath: String, targetElementId: String): CrawlingResult = {
    val originalHtmlFile = new File(originalFilePath)
    val sampleHtmlFile = new File(sampleFilePath)

    val analysisResults =
      findTargetElementInOriginalPage(originalHtmlFile, targetElementId).toOption
        .map(targetElement => crawlSamplePage(sampleHtmlFile, sameTagTypeQueryScriptFor(targetElement), similarityAnalyzersFor(targetElement)))
        .getOrElse(List.empty)
        .filter(_.similarityWeight > ZeroWeight)
        .groupBy(_.element)

    // for each candidate element we have many analyses, given that we provided many similarity analyzers we must the sum
    val analysisSummary = analysisResults.map { case (elem, analyses) => (elem, analyses.map(x => x.similarityWeight).sum) }

    if (analysisSummary.nonEmpty) {
      // obtain best (element, similarityWeight) entry by higher similarityWeight, we could also add a config minimum here
      val bestResultEntry = analysisSummary.entrySet().iterator().reduce((e1, e2) =>  if (e1.getValue >= e2.getValue) e1 else e2)
      val bestElement = bestResultEntry.getKey
      val similarityWeight = bestResultEntry.getValue
      val evaluationDetails = analysisResults.getOrElse(bestElement, List.empty)

      SuccessfulCrawlingResult(targetElementId, elementPathFor(bestElement), similarityWeight, bestElement.html(), evaluationDetails)

    } else {
      EmptyCrawlingResult(targetElementId)
    }
  }

  private def findTargetElementInOriginalPage(originalPageHtmlFile: File, targetElementId: String) : Try[HtmlElement] =
    JsoupFindByIdSnippet.findElementById(originalPageHtmlFile, targetElementId)

  private def sameTagTypeQueryScriptFor(targetElement: HtmlElement) = s"${targetElement.tag().getName}"

  private def findMatchingElementsByCssQuery(htmlFile: File, query: String): Try[List[HtmlElement]] = {
    JsoupCssSelectSnippet
      .findElementsByQuery(htmlFile, query)
      .map(_.iterator().toList)
  }

  private def crawlSamplePage(samplePageHtmlFile: File, targetTagTypeQuery: String, similarityEvaluators: List[SimilarityAnalyzer]): List[SimilarityAnalysisResult] = {
    // markup elements matched by tag query
    val evaluationElements = findMatchingElementsByCssQuery(samplePageHtmlFile, targetTagTypeQuery).getOrElse(List.empty)
    for {
      element <- evaluationElements
      evaluator <- similarityEvaluators
    } yield evaluator(element)
  }

  /**
    *
    * Simmilarity analyzers, including weight configuration in case of matching
    *
    **/

  private def similarityAnalyzersFor(targetElement: HtmlElement) : List[SimilarityAnalyzer] = {
    val attributes = targetElement.attributes().asList().toList
    val titleAnalyzer = attrValue(attributes, "title").map(title => attrContainsWordAnalyzer("title", title, 5)).getOrElse(emptyAnalizer)
    List(
      titleAnalyzer,
      tagTextContainsWordAnalyzer(targetElement.text(), 10),
      attrContainsWordAnalyzer("href", "ok", 1),
      attrContainsWordAnalyzer("class", targetElement.className(), 3),
      attrContainsWordAnalyzer("class", "success", 1)
    )
  }

  private def attrContainsWordAnalyzer(attrName: String, word: String, weight: Double): SimilarityAnalyzer = {
    def analyze(element: HtmlElement): SimilarityAnalysisResult = {
      val similarityWeight = if (attrValue(element.attributes().asList().toList, attrName).exists(_.contains(word))) weight else ZeroWeight
      SimilarityAnalysisResult(element, similarityWeight, s"attrContainsWordAnalyzer('$attrName','$word')")
    }
    analyze
  }

  private def tagTextContainsWordAnalyzer(word: String, weight: Double): SimilarityAnalyzer = {
    def analyze(element: HtmlElement): SimilarityAnalysisResult = {
      val similarityWeight = if (element.text().contains(word)) weight else ZeroWeight
      SimilarityAnalysisResult(element, similarityWeight, s"tagValueContainsWordAnalyzer('$word')")
    }
    analyze
  }

  private def emptyAnalizer: SimilarityAnalyzer = (element: HtmlElement) => SimilarityAnalysisResult(element, ZeroWeight, "")

  private def elementPathFor(element: HtmlElement): String =
    element.parents().toList.reverse.map(elem => elem.tagName + siblingIndexToString(elem)) :+ (element.tagName() + siblingIndexToString(element)) mkString " > "

  private def siblingIndexToString(element: HtmlElement): String = if (!SingleOcurrenceHtmlElements.contains(element.tagName())) s"[${element.elementSiblingIndex}]" else ""

  private def attrValue(attributes: List[Attribute], attrName: String): Option[String] = attributes.find(_.getKey == attrName).map(_.getValue)

  def main(args: Array[String]): Unit = {
    if (args.length < 2) { println("Expecting args: originalFilePath sampleFilePath, optional third arg is targetElementId") }
    val originalFileName = args(0)
    val sampleFileName = args(1)
    val targetElementId = if (args.length > 2) args(2) else DefaultTargetElementId
    val result = crawl(originalFileName, sampleFileName, targetElementId)
    println(result)
  }
}
