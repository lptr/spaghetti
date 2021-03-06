package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Map;

public class TypeParameterResolver {
	public static TypeReferenceInternal resolveTypeParameters(TypeReference node, Map<TypeParameterNode, TypeReference> bindings) {
		if (node instanceof VoidTypeReferenceInternal) {
			return (VoidTypeReferenceInternal) node;
		} else if (node instanceof PrimitiveTypeReferenceInternal) {
			return (PrimitiveTypeReferenceInternal) node;
		} else if (node instanceof EnumReferenceInternal) {
			return (EnumReferenceInternal) node;
		} else if (node instanceof FunctionTypeInternal) {
			FunctionTypeInternal functionType = (FunctionTypeInternal) node;
			DefaultFunctionType result = new DefaultFunctionType(functionType.getLocation(), functionType.getArrayDimensions());
			for (TypeReferenceInternal elem : functionType.getElementsInternal()) {
				result.getElementsInternal().add(resolveTypeParameters(elem, bindings));
			}
			return result;
		} else if (node instanceof ParametrizedTypeNodeReferenceInternal) {
			ParametrizedTypeNodeReferenceInternal<?> parametrizedRef = (ParametrizedTypeNodeReferenceInternal<?>) node;
			if (parametrizedRef.getArguments().isEmpty()) {
				return parametrizedRef;
			}
			ParametrizedTypeNodeReferenceInternal<?> result;
			if (node instanceof StructReferenceInternal) {
				StructReferenceInternal structRef = (StructReferenceInternal) node;
				result = new DefaultStructReference(structRef.getLocation(), structRef.getType(), structRef.getArrayDimensions());
			} else if(node instanceof InterfaceReferenceInternal) {
				InterfaceReferenceInternal ifaceRef = (InterfaceReferenceInternal) node;
				result = new DefaultInterfaceReference(ifaceRef.getLocation(), ifaceRef.getType(), ifaceRef.getArrayDimensions());
			} else if (node instanceof ExternInterfaceReferenceInternal) {
				ExternInterfaceReferenceInternal externRef = (ExternInterfaceReferenceInternal) node;
				result = new DefaultExternInterfaceReference(externRef.getLocation(), externRef.getType(), externRef.getArrayDimensions());
			} else {
				throw new AssertionError("Unknown parametrized type: " + node.getClass());
			}
			for (TypeReferenceInternal argument : parametrizedRef.getArgumentsInternal()) {
				result.getArgumentsInternal().add(resolveTypeParameters(argument, bindings));
			}
			return result;
		} else if (node instanceof TypeParameterReferenceInternal) {
			TypeParameterReferenceInternal paramRef = (TypeParameterReferenceInternal) node;
			if (!bindings.containsKey(paramRef.getType())) {
				return paramRef;
			} else {
				TypeReferenceInternal resolvedReference = resolveTypeParameters(bindings.get(paramRef.getType()), bindings);
				if (paramRef.getArrayDimensions() == 0) {
					return resolvedReference;
				} else {
					return resolvedReference.withAdditionalArrayDimensions(paramRef.getArrayDimensions());
				}
			}
		} else {
			throw new AssertionError("Unknown type: " + node.getClass());
		}
	}
}
