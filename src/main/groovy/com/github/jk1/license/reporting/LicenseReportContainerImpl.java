package com.github.jk1.license.reporting;

import org.gradle.api.Task;
import org.gradle.api.reporting.*;
import org.gradle.api.reporting.internal.*;

public class LicenseReportContainerImpl extends TaskReportContainer<Report> implements LicenseReportContainer
{
  public LicenseReportContainerImpl(Task task) {
    super(Report.class, task);
    this.add(TaskGeneratedSingleFileReport.class, new Object[]{"html", task});
  }
  
  public SingleFileReport getHtml() {
    return (SingleFileReport)this.getByName("html");
  }
}