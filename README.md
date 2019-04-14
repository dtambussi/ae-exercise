**Usage Guide**

**cd {local path to project}/ae-exercise/out/artifacts/ae_crawler_jar**

java -jar ae-crawler.jar originalFilePath sampleFilePath

_Test Execution Results_

Position yourself inside the artifact containing folder, for example:

[mac:...ut/artifacts/ae_crawler_jar]$ pwd                                                                         (master✱)
**/Users/mac/ae-exercise/ae-crawler/out/artifacts/ae_crawler_jar**

* Using Test File 1

`java -jar ae-crawler.jar /Users/mac/Desktop/startbootstrap/sample-0-origin.html /Users/mac/Desktop/startbootstrap/sample-1-evil-gemini.html`  
  
target element: make-everything-ok-button  
element path: html > body > div[0] > div[1] > div[2] > div[0] > div[0] > div[1] > a[1]  
total weight (score): 20.0  
detail: List(SimilarityAnalysisResult(similarity:5.0, description:attrContainsWordAnalyzer('title','Make-Button')), SimilarityAnalysisResult(similarity:10.0, description:tagValueContainsWordAnalyzer('Make everything OK')), SimilarityAnalysisResult(similarity:1.0, description:attrContainsWordAnalyzer('href','ok')), SimilarityAnalysisResult(similarity:3.0, description:attrContainsWordAnalyzer('class','btn btn-success')), SimilarityAnalysisResult(similarity:1.0, description:attrContainsWordAnalyzer('class','success')))  

* Using Test File 2

`java -jar ae-crawler.jar /Users/mac/Desktop/startbootstrap/sample-0-origin.html /Users/mac/Desktop/startbootstrap/sample-2-container-and-clone.html`

target element: make-everything-ok-button  
element path: html > body > div[0] > div[1] > div[2] > div[0] > div[0] > div[1] > div[0] > a[0]  
total weight (score): 16.0  
detail: List(SimilarityAnalysisResult(similarity:5.0, description:attrContainsWordAnalyzer('title','Make-Button')), SimilarityAnalysisResult(similarity:10.0, description:tagValueContainsWordAnalyzer('Make everything OK')), SimilarityAnalysisResult(similarity:1.0, description:attrContainsWordAnalyzer('href','ok')))  

* Using Test File 3

`java -jar ae-crawler.jar /Users/mac/Desktop/startbootstrap/sample-0-origin.html /Users/mac/Desktop/startbootstrap/sample-3-the-escape.html`

target element: make-everything-ok-button  
element path: html > body > div[0] > div[1] > div[2] > div[0] > div[0] > div[2] > a[0]  
total weight (score): 5.0  
detail: List(SimilarityAnalysisResult(similarity:1.0, description:attrContainsWordAnalyzer('href','ok')), SimilarityAnalysisResult(similarity:3.0, description:attrContainsWordAnalyzer('class','btn btn-success')), SimilarityAnalysisResult(similarity:1.0, description:attrContainsWordAnalyzer('class','success')))  

* Using Test File 4

`java -jar ae-crawler.jar /Users/mac/Desktop/startbootstrap/sample-0-origin.html /Users/mac/Desktop/startbootstrap/sample-4-the-mash.html`

target element: make-everything-ok-button  
element path: html > body > div[0] > div[1] > div[2] > div[0] > div[0] > div[2] > a[0]  
total weight (score): 10.0  
detail: List(SimilarityAnalysisResult(similarity:5.0, description:attrContainsWordAnalyzer('title','Make-Button')), SimilarityAnalysisResult(similarity:1.0, description:attrContainsWordAnalyzer('href','ok')), SimilarityAnalysisResult(similarity:3.0, description:attrContainsWordAnalyzer('class','btn btn-success')), SimilarityAnalysisResult(similarity:1.0, description:attrContainsWordAnalyzer('class','success')))  

* Notes

Due to technical issues with my home setup, plus time constraints, I ended up generating jar file with IntelliJ. It is
larger than it should be.






