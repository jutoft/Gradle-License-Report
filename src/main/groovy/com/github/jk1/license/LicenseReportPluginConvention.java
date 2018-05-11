package com.github.jk1.license;

import java.io.File;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.util.WrapUtil;

public class LicenseReportPluginConvention
{
  private String projectReportDirName = "license";
  private final Project project;

  public LicenseReportPluginConvention(Project project) {
    this.project = project;
  }

  public String getProjectReportDirName() {
    return this.projectReportDirName;
  }

  public void setProjectReportDirName(String projectReportDirName) {
    this.projectReportDirName = projectReportDirName;
  }

  public File getProjectReportDir() {
    return this.project.getExtensions().getByType(ReportingExtension.class).file(this.projectReportDirName);
  }

  public Set<Project> getProjects() {
    return WrapUtil.toSet(new Project[]{this.project});
  }
}
