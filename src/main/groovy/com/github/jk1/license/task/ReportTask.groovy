package com.github.jk1.license.task

import com.github.jk1.license.LicenseReportPlugin.LicenseReportExtension
import com.github.jk1.license.ProjectData
import com.github.jk1.license.reader.ProjectReader
import com.github.jk1.license.reporting.LicenseReportContainer
import com.github.jk1.license.reporting.LicenseReportContainerImpl
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.internal.ConventionTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.reporting.Reporting
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class ReportTask extends ConventionTask implements Reporting<LicenseReportContainer> {
  private Logger LOGGER = Logging.getLogger(ReportTask.class)
  private Set<Project> projects
  private final LicenseReportContainer reports
  
  ReportTask() {
    reports = new LicenseReportContainerImpl(this)
    reports.getHtml().setEnabled(true)
    getOutputs().upToDateWhen(new Spec<Task>() {
      boolean isSatisfiedBy(Task element) {
        return false
      }
    })
  }
  
  @TaskAction
  void generateReport() {
    LOGGER.info("Processing dependencies for project ${getProject().name}")
    LicenseReportExtension config = getProject().licenseReport
    new File(config.outputDir).mkdirs()
    ProjectData data = new ProjectReader().read(getProjects(), config)
    LOGGER.info("Building report for project ${getProject().name}")
    config.renderer.render(data)
    LOGGER.info("Dependency license report for project ${getProject().name} created in ${config.outputDir}")
  }
  
  /**
   * Returns the set of projects to generate a report for. By default, the report is generated for the task's
   * containing project.
   *
   * @return The set of files.
   */
  @Internal
  public Set<Project> getProjects() {
    return projects
  }
  
  /**
   * Specifies the set of projects to generate this report for.
   *
   * @param projects The set of projects. Must not be null.
   */
  public void setProjects(Set<Project> projects) {
    this.projects = projects
  }
  
  @Override
  LicenseReportContainer getReports() {
    return reports
  }
  
  @Override
  LicenseReportContainer reports(@DelegatesTo(type = "T", strategy = 1) Closure closure) {
    return reports(new ClosureBackedAction<LicenseReportContainer>(closure))
  }
  
  @Override
  LicenseReportContainer reports(Action<? super LicenseReportContainer> action) {
    action.execute(reports)
    return reports
  }
}
