package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.ModuleVisitor

class DefaultDocumentationNode extends AbstractNode implements DocumentationNode {
	final List<String> documentation

	DefaultDocumentationNode(List<String> documentation) {
		this.documentation = documentation
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitDocumentationNode(this)
	}

	@Override
	String toString() {
		return "<Documentation>"
	}
}