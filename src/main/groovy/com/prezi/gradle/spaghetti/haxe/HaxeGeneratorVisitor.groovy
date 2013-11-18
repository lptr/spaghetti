package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.FQName
import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.Token
import prezi.spaghetti.SpaghettiModuleBaseVisitor

/**
 * Created by lptr on 16/11/13.
 */
abstract class HaxeGeneratorVisitor<T> extends SpaghettiModuleBaseVisitor<Object> {
	static def HAXE_TYPE_NAME_CONVERSION = [
			(ModuleConfiguration.TYPE_VOID): "Void",
			(ModuleConfiguration.TYPE_BOOL): "Bool",
			(ModuleConfiguration.TYPE_INT): "Int",
			(ModuleConfiguration.TYPE_FLOAT): "Float",
			(ModuleConfiguration.TYPE_STRING): "String"
	]

	protected final ModuleConfiguration config
	protected final ModuleDefinition module
	protected final File outputDirectory

	protected File currentFile

	protected HaxeGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		this.config = config
		this.module = module
		this.outputDirectory = outputDirectory
	}

	protected File createHaxeSourceFile(String name)
	{
		return createHaxeSourceFile(name, module.name, outputDirectory)
	}

	public static File createHaxeSourceFile(FQName fqName, File outputDirectory) {
		return createHaxeSourceFile(fqName.localName, fqName, outputDirectory)
	}

	public static File createHaxeSourceFile(String name, FQName baseName, File outputDirectory) {
		def packageDir = baseName.createNamespacePath(outputDirectory)
		packageDir.mkdirs()
		def file = new File(packageDir, name + ".hx")
		file.delete()
		file << "/*\n"
		file << " * Generated by Spaghetti.\n"
		file << " */\n"
		if (baseName.hasNamespace())
		{
			file << "package ${baseName.namespace};\n"
			file << "\n"
		}
		return file
	}

	protected String haxeTypeName(String typeName)
	{
		def fqName = config.resolveTypeName(typeName, module.name)
		return HAXE_TYPE_NAME_CONVERSION.get(fqName) ?: fqName.fullyQualifiedName
	}

	protected void addDocumentationIfNecessary(Token doc)
	{
		def documentation = doc?.text
		if (documentation != null)
		{
			currentFile << documentation
		}
	}
}