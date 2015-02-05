package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.NodeSets;
import com.prezi.spaghetti.ast.PropertyNode;
import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.StructReference;

import java.util.Set;

public class DefaultStructNode extends AbstractParametrizedTypeNode implements StructNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final Set<StructReference> superStructs = Sets.newLinkedHashSet();
	private final NamedNodeSet<PropertyNode> properties = NodeSets.newNamedNodeSet("property");
	private final NamedNodeSet<MethodNode> methods = NodeSets.newNamedNodeSet("method");

	public DefaultStructNode(Location location, FQName qualifiedName) {
		super(location, qualifiedName);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), superStructs, properties, methods);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitStructNode(this);
	}

	@Override
	public NamedNodeSet<AnnotationNode> getAnnotations() {
		return annotations;
	}

	@Override
	public DocumentationNode getDocumentation() {
		return documentation;
	}

	@Override
	public void setDocumentation(DocumentationNode documentation) {
		this.documentation = documentation;
	}

	@Override
	public Set<StructReference> getSuperStructs() {
		return superStructs;
	}

	@Override
	public NamedNodeSet<PropertyNode> getProperties() {
		return properties;
	}

	@Override
	public NamedNodeSet<MethodNode> getMethods() {
		return methods;
	}
}
