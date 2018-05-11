package com.github.jk1.license.reader

import com.github.jk1.license.ConfigurationData
import com.github.jk1.license.LicenseReportPlugin
import com.github.jk1.license.task.ReportTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class ConfigurationReader {

    private Logger LOGGER = Logging.getLogger(ReportTask.class)
    private ModuleReader moduleReader = new ModuleReader()
    private LicenseReportPlugin.LicenseReportExtension config

    ConfigurationData read(LicenseReportPlugin.LicenseReportExtension config, Configuration configuration) {
        this.config = config
        LOGGER.info("Processing configuration [$configuration], configuration will be resolved")
        configuration.resolvedConfiguration // force configuration resolution
        ConfigurationData data = new ConfigurationData()
        data.name = configuration.name
        Set<ResolvedDependency> dependencies = new TreeSet<ResolvedDependency>(new ResolvedDependencyComparator())
        for (ResolvedDependency dependency : configuration.resolvedConfiguration.getFirstLevelModuleDependencies()) {
            collectDependencies(dependencies, dependency)
        }
        LOGGER.info("Processing dependencies for configuration [$configuration]: " + dependencies.join(','))
        for (ResolvedDependency dependency : dependencies) {
            LOGGER.debug("Processing dependency: $dependency")
            data.dependencies.add(moduleReader.read(project, dependency))
        }
        return data
    }

    private Set<ResolvedDependency> collectDependencies(Set<ResolvedDependency> accumulator, ResolvedDependency root){
        // avoiding dependency cycles
        if (!accumulator.contains(root)) {
            LOGGER.debug("Collecting dependency ${root.name}")
            if (!config.isExcluded(root)) {
                LOGGER.debug("Not collecting dependency ${root.name} due to explicit exclude configured")
                accumulator.add(root)
            }
            root.children.each {collectDependencies(accumulator, it)}
        }
        accumulator
    }

    private static class ResolvedDependencyComparator implements Comparator<ResolvedDependency>{
        @Override
        int compare(ResolvedDependency first, ResolvedDependency second) {
            int result = first.moduleGroup.compareTo(second.moduleGroup)
            result == 0 ? first.moduleName.compareTo(second.moduleName) : result
        }
    }
}
