package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.InputFiles
/**
 * Created by lptr on 12/11/13.
 */
abstract class AbstractGenerateTask extends AbstractSpaghettiTask {

	@OutputDirectory
	File outputDirectory

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}
}
