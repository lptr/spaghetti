package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode

class HaxeConstGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	@Override
	String visitConstNode(ConstNode node) {
		def constants = visitChildren(node)
"""@:final class ${node.name} {
${constants}
}
"""
	}

	@Override
	String visitConstEntryNode(ConstEntryNode node) {
		String type = PRIMITIVE_TYPES.get(node.type.type)
		String value = HaxeUtils.toPrimitiveString(node.value)
		return "\tpublic static inline var ${node.name}:${type} = ${value};\n"
	}
}
