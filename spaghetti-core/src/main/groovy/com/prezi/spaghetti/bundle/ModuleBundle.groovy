package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.structure.StructuredWriter

interface ModuleBundle extends Comparable<ModuleBundle> {
	static final def DEFINITION_PATH = "module.def"
	static final def SOURCE_MAP_PATH = "module.map"
	static final def JAVASCRIPT_PATH = "module.js"
	static final def MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"
	static final def RESOURCES_PREFIX = "resources/"

	String getName()
	String getVersion()
	String getSourceBaseUrl()
	Set<String> getDependentModules()
	Set<String> getResourcePaths()

	String getDefinition()
	String getJavaScript()
	String getSourceMap()

	void extract(StructuredWriter.StructuredAppender output, EnumSet<ModuleBundleElement> elements)
}
