package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.config.ModuleConfiguration
import com.prezi.spaghetti.config.ModuleConfigurationParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.InputFiles

class AbstractSpaghettiTask extends ConventionTask {

	@InputFiles
	Configuration dependentModules
	void dependentModules(Configuration dependentModules) {
		this.dependentModules = dependentModules
	}

	ConfigurableFileCollection additionalDirectDependentModulesInternal = project.files()
	void additionalDirectDependentModules(Object... additionalDirectDependentModules) {
		this.getAdditionalDirectDependentModulesInternal().from(*additionalDirectDependentModules)
	}
	void additionalDirectDependentModule(Object... additionalDirectDependentModules) {
		this.additionalDirectDependentModules(additionalDirectDependentModules)
	}

	@InputFiles
	ConfigurableFileCollection getAdditionalDirectDependentModules() {
		return project.files(this.getAdditionalDirectDependentModulesInternal())
	}

	ConfigurableFileCollection additionalTransitiveDependentModulesInternal = project.files()
	void additionalTransitiveDependentModules(Object... additionalTransitiveDependentModules) {
		this.getAdditionalTransitiveDependentModulesInternal().from(*additionalTransitiveDependentModules)
	}
	void additionalTransitiveDependentModule(Object... additionalTransitiveDependentModules) {
		this.additionalTransitiveDependentModules(additionalTransitiveDependentModules)
	}

	@InputFiles
	ConfigurableFileCollection getAdditionalTransitiveDependentModules() {
		return project.files(this.getAdditionalTransitiveDependentModulesInternal())
	}

	protected SpaghettiPlugin getPlugin()
	{
		return project.getPlugins().getPlugin(SpaghettiPlugin)
	}

	protected ModuleBundleLookupResult lookupBundles() {
		def directFilesFromConfiguration = getDependentModules().resolvedConfiguration.firstLevelModuleDependencies*.moduleArtifacts*.file.flatten()
		def bundles = ModuleBundleLookup.lookup(
				directFilesFromConfiguration + getAdditionalDirectDependentModules().files,
				getDependentModules().files + getAdditionalTransitiveDependentModules().files)
		return bundles
	}

	ModuleConfiguration readConfig(Iterable<File> files) {
		readConfigInternal(files.collect() { file ->
			new ModuleDefinitionSource(file.toString(), file.text)
		})
	}

	ModuleConfiguration readConfig() {
		readConfigInternal([])
	}

	private ModuleConfiguration readConfigInternal(Collection<ModuleDefinitionSource> localDefinitions) {
		def bundles = lookupBundles()
		def directSources = makeModuleSources(bundles.directBundles)
		def transitiveSources = makeModuleSources(bundles.transitiveBundles)
		def config = ModuleConfigurationParser.parse(
				localDefinitions,
				directSources,
				transitiveSources)
		logger.info("Loaded configuration: ${config}")
		return config
	}

	private static List<ModuleDefinitionSource> makeModuleSources(Set<ModuleBundle> bundles) {
		return bundles.collect { ModuleBundle module ->
			return new ModuleDefinitionSource("module: " + module.name, module.definition)
		}
	}
}
