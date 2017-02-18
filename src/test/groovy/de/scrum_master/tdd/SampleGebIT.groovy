package de.scrum_master.tdd

import geb.driver.CachingDriverFactory
import geb.spock.GebReportingSpec
import org.openqa.selenium.Keys
import org.spockframework.runtime.model.FeatureInfo
import spock.lang.Shared

/**
 * See http://stackoverflow.com/questions/42069291
 */
class SampleGebIT extends GebReportingSpec {

  @Override
  void report(String label = "") {
    // GebReportingSpec tries to write a report (screenshot) at the end of each feature
    // method. But because we use 'CachingDriverFactory.clearCacheAndQuitDriver()',
    // there is no valid driver instance anymore from which to get a screenshot. Geb is
    // unprepared for this kind of error, so we handle it gracefully so as to keep the
    // test from failing just because the last screenshot cannot be taken anymore.
    try {
      super.report(label)
    }
    catch (Exception e) {
      System.err.println("Cannot create screenshot: ${e.message}")
    }
  }

  // We cannot use 'specificationContext' directly from 'setupSpec()' because of this
  // compilation error: "Only @Shared and static fields may be accessed from here"
  // Okay then, so use we a @Shared field as a workaround. ;-)
  @Shared
  def currentSpec = specificationContext.currentSpec

  def setupSpec() {
    // Make sure that feature methods are run in declaration order. Normally we could
    // use @Stepwise for this, but because @Stepwise implies staying in the same
    // browser session, it would not work in connection with
    // 'CachingDriverFactory.clearCacheAndQuitDriver()'. This is the workaround for it.
    for (FeatureInfo feature : currentSpec.features)
      feature.executionOrder = feature.declarationOrder
  }

  def "Search web site Scrum-Master.de"() {
    setup:
    def deactivateAutoComplete =
      "document.getElementById('mod_search_searchword')" +
      ".setAttribute('autocomplete', 'off')"
    def regexNumberOfMatches = /Insgesamt wurden ([0-9]+) Ergebnisse gefunden/

    when:
    go "https://scrum-master.de"
    report "welcome page"

    then:
    $("h2").text().startsWith("Herzlich Willkommen bei Scrum-Master.de")

    when:
    js.exec(deactivateAutoComplete)
    $("form").searchword = "Product Owner" + Keys.ENTER

    then:
    waitFor { $("form#searchForm") }

    when:
    report "search results"
    def searchResultSummary = $("form#searchForm").$("table.searchintro").text()
    def numberOfMatches = (searchResultSummary =~ regexNumberOfMatches)[0][1] as int

    then:
    numberOfMatches > 0

    cleanup:
    println "Closing browser and WebDriver"
    CachingDriverFactory.clearCacheAndQuitDriver()
  }

  def "Visit Scrum-Master.de download page"() {
    when:
    go "https://scrum-master.de/Downloads"
    report "download page"

    then:
    $("h2").text().startsWith("Scrum on a Page")
  }
}
