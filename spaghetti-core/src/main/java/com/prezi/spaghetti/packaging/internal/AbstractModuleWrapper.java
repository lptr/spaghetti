package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.prezi.spaghetti.internal.Version;
import com.prezi.spaghetti.packaging.ModuleWrapper;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.DEPENDENCIES;
import static com.prezi.spaghetti.generator.ReservedWords.GET_MODULE_NAME;
import static com.prezi.spaghetti.generator.ReservedWords.GET_MODULE_VERSION;
import static com.prezi.spaghetti.generator.ReservedWords.GET_RESOURCE_URL;
import static com.prezi.spaghetti.generator.ReservedWords.GET_SPAGHETTI_VERSION;
import static com.prezi.spaghetti.packaging.internal.CommentUtils.appendAfterInitialComment;

public abstract class AbstractModuleWrapper implements ModuleWrapper {
	protected void wrapModuleObject(StringBuilder builder, ModuleWrapperParameters params, CharSequence baseUrlDeclaration, CharSequence externalDependenciesDeclaration, Map<String, String> dependencies) throws IOException {
		Iterable<String> dependencyLines = Iterables.transform(dependencies.entrySet(), new Function<Map.Entry<String, String>, String>() {
			@Override
			public String apply(Map.Entry<String, String> entry) {
				return String.format("\"%s\":%s", entry.getKey(), entry.getValue());
			}
		});
		String moduleName = params.bundle.getName();

		builder.append("var module=(function(dependencies){");
			builder.append("return function(init){");
				builder.append("return init.call({},(function(){");
					builder.append(baseUrlDeclaration);
					builder.append("return{");
						builder.append(GET_SPAGHETTI_VERSION).append(":function(){");
							builder.append("return \"").append(Version.SPAGHETTI_VERSION).append("\";");
						builder.append("},");
						builder.append(GET_MODULE_NAME).append(":function(){");
							builder.append("return \"").append(moduleName).append("\";");
						builder.append("},");
						builder.append(GET_MODULE_VERSION).append(":function(){");
							builder.append("return \"").append(params.bundle.getVersion()).append("\";");
						builder.append("},");
						builder.append(GET_RESOURCE_URL).append(":function(resource){");
							builder.append("if(resource.substr(0,1)!=\"/\"){");
								builder.append("resource=\"/\"+resource;");
							builder.append("}");
							builder.append("return baseUrl+resource;");
						builder.append("},");
						builder.append("\"").append(DEPENDENCIES).append("\":{");
							builder.append(Joiner.on(',').join(dependencyLines));
						builder.append("}");
					builder.append("};");
				builder.append("})());");
			builder.append("};");
		builder.append("})(arguments);");
		builder.append(externalDependenciesDeclaration);
		appendAfterInitialComment(builder, "return{\"module\":(function(){return ", params.bundle.getJavaScript());
		builder.append("\n})(),");
		builder.append("\"version\":\"").append(params.bundle.getVersion()).append("\",");
		builder.append("\"spaghettiVersion\":\"").append(Version.SPAGHETTI_VERSION).append("\"");
		builder.append("};");
	}

	public String makeApplication(Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute, Map<String, String> externals) {
		StringBuilder result = new StringBuilder();
		makeConfig(result, dependencyTree, externals);
		if (!Strings.isNullOrEmpty(mainModule)) {
			makeMainModuleSetup(result, mainModule, execute);
		}
		return result.toString();
	}

	// Make the configuration for the application
	protected abstract StringBuilder makeConfig(StringBuilder result, Map<String, Set<String>> dependencyTree, Map<String, String> externals);

	// Make the setup instructions for the main module
	protected abstract StringBuilder makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute);
}
