package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.QualifiedNode;
import com.prezi.spaghetti.ast.QualifiedNodeSet;

import java.util.Set;

public class DefaultQualifiedNodeSet<T extends QualifiedNode> extends AbstractNodeSet<FQName, T> implements QualifiedNodeSet<T> {
	public DefaultQualifiedNodeSet(String type) {
		super(type);
	}

	public DefaultQualifiedNodeSet(String type, Set<T> elements) {
		super(type, elements);
	}

	@Override
	protected FQName key(T node) {
		return node.getQualifiedName();
	}

}