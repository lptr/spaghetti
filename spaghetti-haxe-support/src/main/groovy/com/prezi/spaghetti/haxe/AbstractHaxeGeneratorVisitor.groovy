package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractHaxeGeneratorVisitor extends AbstractModuleVisitor<String> {
	private static final def PRIMITIVE_TYPES = [
			bool: "Bool",
			int: "Int",
			float: "Float",
			String: "String",
			any: "Dynamic"
	]

	protected AbstractHaxeGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitCallbackTypeChain(@NotNull @NotNull ModuleParser.CallbackTypeChainContext ctx)
	{
		return ctx.returnType().collect { it.accept(this) }.join("->")
	}

	@Override
	String visitValueType(@NotNull @NotNull ModuleParser.ValueTypeContext ctx)
	{
		String type = ctx.getChild(0).accept(this)
		ctx.ArrayQualifier().each { type = "Array<${type}>" }
		return type
	}

	@Override
	String visitModuleType(@NotNull @NotNull ModuleParser.ModuleTypeContext ctx)
	{
		def localTypeName = FQName.fromContext(ctx.name)
		def fqTypeName = module.resolveName(localTypeName)
		def haxeType = fqTypeName.fullyQualifiedName
		return haxeType
	}

	@Override
	String visitVoidType(@NotNull @NotNull ModuleParser.VoidTypeContext ctx)
	{
		return "Void"
	}

	@Override
	String visitPrimitiveType(@NotNull @NotNull ModuleParser.PrimitiveTypeContext ctx)
	{
		return PRIMITIVE_TYPES.get(ctx.text)
	}

	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		return aggregate + nextResult
	}

	@Override
	protected String defaultResult() {
		return ""
	}
}
