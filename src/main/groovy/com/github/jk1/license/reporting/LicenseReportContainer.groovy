package com.github.jk1.license.reporting

import org.gradle.api.reporting.Report
import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.Internal

public interface LicenseReportContainer extends ReportContainer<Report> {
  @Internal
  SingleFileReport getHtml()
}