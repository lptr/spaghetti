package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleConfigurationParser
import com.prezi.spaghetti.ModuleDefinitionSource
import org.gradle.api.tasks.Input

/**
 * Created by lptr on 19/04/14.
 */
class AbstractPlatformAwareSpaghettiTask extends AbstractSpaghettiTask {
	@Input
	String platform

	protected Generator createGenerator(ModuleConfiguration config) {
		return Platform.createGeneratorForPlatform(getPlatform(), config)
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
		def dependentDefinitions = ModuleDefinitionLookup.getAllDefinitionSources(getBundles())
		def config = ModuleConfigurationParser.parse(
				dependentDefinitions,
				localDefinitions,
				Platform.getExterns(getPlatform())
		)
		logger.info("Loaded configuration: ${config}")
		return config
	}
}
