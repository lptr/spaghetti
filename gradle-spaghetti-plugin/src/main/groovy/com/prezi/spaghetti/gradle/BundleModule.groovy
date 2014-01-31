package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends AbstractBundleTask {

	public final File jsModuleFile

	File sourceMap

	BundleModule()
	{
		this.inputFile = new File(project.buildDir, "module.js")
		this.jsModuleFile = new File(project.buildDir, "spaghetti/module.js")
		this.outputFile = new File(project.buildDir, "spaghetti/module.zip")
	}

	@TaskAction
	bundle() {
		def config = readConfig(definition)
		def module = config.localModules.first()
		def processedJavaScript = createGenerator(config).processModuleJavaScript(module, inputFile.text)
		def wrappedJavaScript = Wrapper.wrap(config, Wrapping.module, processedJavaScript)

        // is a sourcemap present?
        def sourceMapText = sourceMap ? sourceMap.text : null;

		jsModuleFile.parentFile.mkdirs()
		jsModuleFile.delete()
		jsModuleFile << wrappedJavaScript

		def bundle = new ModuleBundle(module.name, definition.text, String.valueOf(project.version), sourceBaseUrl, wrappedJavaScript, sourceMapText)
		bundle.save(outputFile)
	}

	void sourceMap(Object sourceMap)
	{
		this.sourceMap = project.file(sourceMap)
	}

	@InputFile
	@Optional
	File getSourceMap()
	{
		if (!sourceMap) {
			def defSourceMap = new File(inputFile.toString() + ".map")
			if (defSourceMap.canRead()) {
				sourceMap = defSourceMap
				return sourceMap
			}
			return null
		}

		return sourceMap
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
