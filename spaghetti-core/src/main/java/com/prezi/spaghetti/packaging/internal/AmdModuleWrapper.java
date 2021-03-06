package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class AmdModuleWrapper extends AbstractModuleWrapper implements StructuredModuleWrapper {

	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {
		Iterable<String> moduleNamesWithRequire = Iterables.concat(Arrays.asList("require"), Sets.newTreeSet(params.bundle.getDependentModules()));
		Map<String, String> modules = Maps.newLinkedHashMap();
		int requireDependencyIndex = params.bundle.getExternalDependencies().size();
		int index = requireDependencyIndex;
		for (String name : moduleNamesWithRequire) {
			modules.put(name, "dependencies[" + index + "]");
			index++;
		}

		String baseUrlDeclaration = "var moduleUrl=dependencies[" + requireDependencyIndex + "][\"toUrl\"](\"" + params.bundle.getName() + ".js\");"
			+ "var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf(\"/\"));";

		StringBuilder result = new StringBuilder();
		result.append("define([\"").append(Joiner.on("\",\"").join(Iterables.concat(params.bundle.getExternalDependencies().values(), moduleNamesWithRequire))).append("\"],function(){");
		StringBuilder externalDependenciesDeclaration = new StringBuilder();
		int externalDependencyIdx = 0;
		for (String externalDependency : params.bundle.getExternalDependencies().keySet()) {
			externalDependenciesDeclaration.append(String.format("var %s=arguments[%d];", externalDependency, externalDependencyIdx));
			externalDependencyIdx++;
		}
		wrapModuleObject(result, params, baseUrlDeclaration, externalDependenciesDeclaration, modules);
		result.append("});");
		return result.toString();
	}

	@Override
	protected StringBuilder makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
		result.append("require([\"").append(mainModule).append("\"],function(__mainModule){");
		if (execute) {
            result.append("__mainModule[\"").append(MODULE).append("\"][\"main\"]();");
        }
		result.append("});\n");
		return result;
	}

	@Override
	protected StringBuilder makeConfig(StringBuilder result, Map<String, Set<String>> dependencyTree, Map<String, String> externals) {
		Iterable<String> moduleDependencyPaths = makeModuleDependencies(Sets.newTreeSet(dependencyTree.keySet()), getModulesDirectory());
		Iterable<String> externalDependencyPaths = Collections2.transform(externals.entrySet(), new Function<Map.Entry<String, String>, String>() {
			@Override
			public String apply(Map.Entry<String, String> external) {
				return String.format("\"%s\":\"%s\"", external.getKey(), external.getValue());
			}
		});
		// Begin config
		result.append("require[\"config\"]({\"baseUrl\":\".\",\"paths\":{");
		// Append path definitions
		Joiner.on(',').appendTo(result, Iterables.concat(moduleDependencyPaths, externalDependencyPaths));
		// End config
		result.append("}});");
		return result;
	}

	private static Collection<String> makeModuleDependencies(Collection<String> moduleNames, final String modulesRoot) {
		final String normalizedModulesRoot = modulesRoot.endsWith("/") ? modulesRoot : modulesRoot + "/";
		return Collections2.transform(moduleNames, new Function<String, String>() {
			@Override
			public String apply(String moduleName) {
				return String.format("\"%s\":\"%s%s/%s\"", moduleName, normalizedModulesRoot, moduleName, moduleName);
			}
		});
	}
	@Override
	public String getModulesDirectory() {
		return "modules";
	}

}
