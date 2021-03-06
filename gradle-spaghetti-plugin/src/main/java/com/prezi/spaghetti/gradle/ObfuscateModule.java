package com.prezi.spaghetti.gradle;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor;
import com.prezi.spaghetti.generator.internal.Generators;
import com.prezi.spaghetti.gradle.internal.AbstractBundleModuleTask;
import com.prezi.spaghetti.obfuscation.ModuleObfuscator;
import com.prezi.spaghetti.obfuscation.ObfuscationParameters;
import com.prezi.spaghetti.obfuscation.ObfuscationResult;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;

public class ObfuscateModule extends AbstractBundleModuleTask {
	private final Set<String> additionalSymbols = Sets.newLinkedHashSet();
	private final Set<Object> closureExterns = Sets.newLinkedHashSet();
	private String compilationLevel = "advanced";
	private File workDir;
	private String nodeSourceMapRoot;

	public ObfuscateModule() {
		this.getConventionMapping().map("workDir", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/obfuscation/work");
			}

		});
		this.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/obfuscation/bundle");
			}

		});
	}

	@Override
	protected ModuleBundle createBundle(ModuleConfiguration config, ModuleNode module, String javaScript, String sourceMap, File resourceDir) throws IOException {
		JavaScriptBundleProcessor processor = Generators.getService(JavaScriptBundleProcessor.class, getLanguage());
		ModuleObfuscator obfuscator = new ModuleObfuscator(processor.getProtectedSymbols());
		ObfuscationResult result = obfuscator.obfuscateModule(new ObfuscationParameters(
				config,
				module,
				javaScript,
				sourceMap,
				null,
				getNodeSourceMapRoot(),
				getClosureExterns(),
				getAdditionalSymbols(),
				getWorkDir(),
				getCompilationLevel()
		));
		return super.createBundle(config, module, result.javaScript, result.sourceMap, resourceDir);
	}

	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(Object workDir) {
		this.workDir = getProject().file(workDir);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void workDir(String workDir) {
		setWorkDir(workDir);
	}

	@Input
	public String getCompilationLevel() { return compilationLevel; }

	public void setCompilationLevel(String compilationLevel) { this.compilationLevel = compilationLevel; }

	@Input
	public Set<String> getAdditionalSymbols() {
		return additionalSymbols;
	}

	@SuppressWarnings("UnusedDeclaration")
	public Boolean additionalSymbols(String... symbols) {
		return additionalSymbols.addAll(Arrays.asList((String[]) symbols));
	}

	@SuppressWarnings("UnusedDeclaration")
	public void closureExterns(Object... externs) {
		closureExterns.addAll(Arrays.asList(externs));
	}

	@SuppressWarnings("UnusedDeclaration")
	public void closureExtern(Object... externs) {
		closureExterns(externs);
	}

	@InputFiles
	public Set<File> getClosureExterns() {
		return getProject().files(this.closureExterns).getFiles();
	}

	@Input
	@Optional
	public String getNodeSourceMapRoot() {
		return nodeSourceMapRoot;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void nodeSourceMapRoot(String sourceMapRoot) {
		this.nodeSourceMapRoot = sourceMapRoot;
	}
}
