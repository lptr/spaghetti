package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.TypeNode;

public class ModuleTypeResolver implements TypeResolver {
	private final TypeResolver parent;
	private final ModuleNode module;

	public ModuleTypeResolver(TypeResolver parent, ModuleNode module) {
		this.parent = parent;
		this.module = module;
	}

	@Override
	public TypeNode resolveType(TypeResolutionContext context) {
		FQName name = context.getName();
		QualifiedTypeNode type = module.getTypes().get(name);
		if (type == null) {
			type = ((QualifiedTypeNode) (parent.resolveType(context)));
		}

		return type;
	}
}
