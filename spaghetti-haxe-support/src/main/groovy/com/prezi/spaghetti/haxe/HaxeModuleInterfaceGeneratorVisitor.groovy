package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleInterfaceGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	HaxeModuleInterfaceGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module)
	{
		super(config, module)
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull SpaghettiModuleParser.ModuleDefinitionContext ctx)
	{
		return addDocumentationIfNecessary(ctx.documentation) +
		"""interface ${module.name.localName} {
${super.visitModuleDefinition(ctx)}
}"""
	}

	// Do not generate code for types
	@Override
	String visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		// Suppress processing types
		return ""
	}
}
