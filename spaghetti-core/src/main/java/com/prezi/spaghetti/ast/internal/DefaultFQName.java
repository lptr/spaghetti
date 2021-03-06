package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class DefaultFQName implements FQName {
	private final String namespace;
	private final String localName;
	private final String fullyQualifiedName;

	private DefaultFQName(String namespace, String localName) {
		this(makeFullyQualifiedName(namespace, localName), namespace, localName);
	}

	private static String makeFullyQualifiedName(final String namespace, final String localName) {
		if (!Strings.isNullOrEmpty(namespace)) {
			return namespace + "." + localName;
		} else {
			return localName;
		}
	}

	private DefaultFQName(String fullyQualifiedName, String namespace, String localName) {
		this.fullyQualifiedName = fullyQualifiedName;
		this.localName = localName;
		this.namespace = !Strings.isNullOrEmpty(namespace) ? namespace : null;
	}

	public static FQName fromString(String fqName) {
		if (fqName == null || fqName.isEmpty()) {
			throw new IllegalArgumentException("Qualified name cannot be empty");
		}

		String _name;
		String _namespace;
		int lastDot = fqName.lastIndexOf(".");
		if (lastDot == -1) {
			_namespace = null;
			_name = fqName;
		} else {
			_namespace = fqName.substring(0, lastDot);
			_name = fqName.substring(lastDot + 1);
		}

		return new DefaultFQName(fqName, _namespace, _name);
	}

	public static FQName fromString(String namespace, String name) {
		return new DefaultFQName(namespace, name);
	}

	public static FQName fromContext(ModuleParser.QualifiedNameContext context) {
		StringBuilder namespace = new StringBuilder();
		String localName = null;
		for (TerminalNode it : context.Name()) {
			if (!Strings.isNullOrEmpty(localName)) {
				if (namespace.length() > 0) {
					namespace.append(".");
				}

				namespace.append(localName);
			}

			localName = it.getText();
		}
		return fromString(namespace.toString(), localName);
	}

	public static FQName qualifyLocalName(String namespace, FQName name) {
		if (name.hasNamespace()) {
			return name;
		} else {
			return fromString(namespace, name.getLocalName());
		}
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	@Override
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@Override
	public FQName qualifyLocalName(FQName name) {
		if (name.hasNamespace()) {
			return name;
		} else {
			return fromString(namespace, name.getLocalName());
		}
	}

	@Override
	public List<String> getParts() {
		List<String> result = !Strings.isNullOrEmpty(namespace) ? Lists.newArrayList(namespace.split("\\.")) : Lists.<String> newArrayList();
		result.add(localName);
		return result;
	}

	@Override
	public boolean hasNamespace() {
		return namespace != null;
	}

	@Override
	public String toString() {
		return fullyQualifiedName;
	}

	@Override
	public int compareTo(FQName o) {
		return fullyQualifiedName.compareTo(o.getFullyQualifiedName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FQName fqName = (FQName) o;

		return fullyQualifiedName.equals(fqName.getFullyQualifiedName());
	}

	@Override
	public int hashCode() {
		return fullyQualifiedName.hashCode();
	}
}
