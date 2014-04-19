package com.prezi.spaghetti.gradle

import groovy.io.FileType
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.BinaryContainer
import org.gradle.language.base.ProjectSourceSet
import org.gradle.language.base.plugins.LanguageBasePlugin
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	private static final logger = LoggerFactory.getLogger(SpaghettiPlugin)

	private final Instantiator instantiator
	private final FileResolver fileResolver

	@Inject
	SpaghettiPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}

	@Override
	void apply(Project project) {
		project.plugins.apply(LanguageBasePlugin)
		project.plugins.apply(SpaghettiBasePlugin)

		Platform.createPlatformsTask(project)

		def binaryContainer = project.getExtensions().getByType(BinaryContainer)
		def projectSourceSet = project.getExtensions().getByType(ProjectSourceSet)
		def extension = project.extensions.getByType(SpaghettiExtension)

		// Add source sets
		def functionalSourceSet = projectSourceSet.maybeCreate("main")

		def spaghettiSourceSet = instantiator.newInstance(DefaultSpaghettiSourceSet, "spaghetti", functionalSourceSet, fileResolver)
		spaghettiSourceSet.source.srcDir("src/main/spaghetti")
		functionalSourceSet.add(spaghettiSourceSet)

		def spaghettiResourceSet = instantiator.newInstance(DefaultSpaghettiResourceSet, "spaghetti-resources", functionalSourceSet, fileResolver)
		spaghettiResourceSet.source.srcDir("src/main/spaghetti-resources")
		functionalSourceSet.add(spaghettiResourceSet)

		project.tasks.withType(AbstractSpaghettiTask).all { AbstractSpaghettiTask task ->
			task.conventionMapping.bundles = { extension.configuration }
		}
		project.tasks.withType(AbstractPlatformAwareSpaghettiTask).all { AbstractPlatformAwareSpaghettiTask task ->
			task.conventionMapping.platform = { extension.platform }
		}
		project.tasks.withType(AbstractDefinitionAwareSpaghettiTask).all { AbstractDefinitionAwareSpaghettiTask task ->
			task.conventionMapping.definitions = { findDefinitions(project) }
		}
		project.tasks.withType(AbstractBundleModuleTask).all { AbstractBundleModuleTask task ->
			task.conventionMapping.sourceBaseUrl = { extension.sourceBaseUrl }
			task.conventionMapping.resourceDirs = { spaghettiResourceSet.source.srcDirs }
		}

		// Automatically generate module headers
		def generateTask = project.task("generateHeaders", type: GenerateHeaders) {
			description = "Generates Spaghetti headers."
		} as GenerateHeaders
		logger.debug("Created ${generateTask}")

		// Create source set
		def spaghettiGeneratedSourceSet = functionalSourceSet.findByName("spaghetti-generated")
		if (!spaghettiGeneratedSourceSet) {
			spaghettiGeneratedSourceSet = instantiator.newInstance(DefaultSpaghettiGeneratedSourceSet, "spaghetti-generated", functionalSourceSet, fileResolver)
			functionalSourceSet.add(spaghettiGeneratedSourceSet)
			logger.debug("Added ${spaghettiGeneratedSourceSet}")
		}
		spaghettiGeneratedSourceSet.source.srcDir({ generateTask.getOutputDirectory() })
		spaghettiGeneratedSourceSet.builtBy(generateTask)

		binaryContainer.withType(SpaghettiCompatibleJavaScriptBinary).all(new Action<SpaghettiCompatibleJavaScriptBinary>() {
			@Override
			void execute(SpaghettiCompatibleJavaScriptBinary binary) {
				logger.debug("Creating bundle and obfuscation for ${binary}")

				// Automatically create bundle module task and artifact
				BundleModule bundleTask = createBundleTask(project, binary)
				def moduleBundleArtifact = new ModuleBundleArtifact(bundleTask)
				project.artifacts.add(extension.configuration.name, moduleBundleArtifact)
				logger.debug("Added bundle task ${bundleTask} with artifact ${moduleBundleArtifact}")

				// TODO Probably this should be enabled via command line
				// Automatically obfuscate bundle
				ObfuscateBundle obfuscateTask = createObfuscateTask(project, binary)
				def obfuscatedBundleArtifact = new ModuleBundleArtifact(obfuscateTask)
				project.artifacts.add(extension.obfuscatedConfiguration.name, obfuscatedBundleArtifact)
				logger.debug("Added obfuscate task ${obfuscateTask} with artifact ${obfuscatedBundleArtifact}")
			}
		})
	}

	private static BundleModule createBundleTask(Project project, SpaghettiCompatibleJavaScriptBinary binary) {
		def bundleTaskName = "bundle" + binary.name.capitalize()
		def bundleTask = project.task(bundleTaskName, type: BundleModule) {
			description = "Bundles ${binary} module."
		} as BundleModule
		bundleTask.conventionMapping.inputFile = { binary.getJavaScriptFile() }
		bundleTask.conventionMapping.sourceMap = { binary.getSourceMapFile() }
		bundleTask.dependsOn binary
		return bundleTask
	}

	private static ObfuscateBundle createObfuscateTask(Project project, SpaghettiCompatibleJavaScriptBinary binary) {
		def obfuscateTaskName = "obfuscate" + binary.name.capitalize()
		def obfuscateTask = project.task(obfuscateTaskName, type: ObfuscateBundle) {
			description = "Obfuscates ${binary} module."
		} as ObfuscateBundle
		obfuscateTask.conventionMapping.inputFile = { binary.getJavaScriptFile() }
		obfuscateTask.conventionMapping.sourceMap = { binary.getSourceMapFile() }
		return obfuscateTask
	}

	// TODO Make this into a lazy FileCollection
	static FileCollection findDefinitions(Project project) {
		Set<SpaghettiSourceSet> sources = project.extensions.getByType(ProjectSourceSet).getByName("main").withType(SpaghettiSourceSet)
		Set<File> sourceDirs = sources*.source*.srcDirs.flatten()
		return project.files(sourceDirs.collectMany { File dir ->
			def definitions = []
			if (dir.directory) {
				dir.eachFileMatch(FileType.FILES, ~/^.+\.module$/, { definitions << it })
			}
			return definitions
		})
	}
}
