package com.github.jk1.license

import com.github.jk1.license.importer.DependencyDataImporter
import com.github.jk1.license.render.ReportRenderer
import com.github.jk1.license.render.SimpleHtmlReportRenderer
import com.github.jk1.license.task.ReportTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.ProjectReportsPluginConvention

import java.util.concurrent.Callable

class LicenseReportPlugin implements Plugin<Project> {
    
    public static final String HTML_LICENSE_REPORT = "htmlLicenseReport"

    @Override
    void apply(Project project) {
        final ProjectReportsPluginConvention convention = new ProjectReportsPluginConvention(project)
        LicenseReportExtension ext = new LicenseReportExtension(project)
        project.extensions.add('licenseReport', ext)
        project.task(['type': ReportTask.class], "generateLicenseReport")
    
        ReportTask htmlLicenseReport = project.getTasks().create(HTML_LICENSE_REPORT, ReportTask.class)
        htmlLicenseReport.setDescription("Generates an HTML report about your library dependencies licenses.")
        
        new DslObject(htmlLicenseReport.getReports().getHtml()).getConventionMapping().map("destination", new Callable<Object>() {
            Object call() throws Exception {
                return new File(convention.getProjectReportDir(), "dependencies")
            }
        })
        
        htmlLicenseReport.conventionMapping("projects", new Callable<Object>() {
            Object call() throws Exception {
                return convention.getProjects()
            }
        })
    }

    static class LicenseReportExtension {

        String outputDir
        ReportRenderer renderer
        DependencyDataImporter[] importers
        String[] configurations
        String[] excludeGroups
        String[] excludes

        LicenseReportExtension(Project project) {
            outputDir = "${project.buildDir}/reports/dependency-license"
            renderer = new SimpleHtmlReportRenderer()
            configurations = ['runtime']
            excludeGroups = [project.group]
            excludes = []
            importers = new DependencyDataImporter[0]
        }

        boolean isExcluded(ResolvedDependency module) {
            return excludeGroups.contains(module.moduleGroup) ||
                    excludes.contains("$module.moduleGroup:$module.moduleName")
        }
    }
}
