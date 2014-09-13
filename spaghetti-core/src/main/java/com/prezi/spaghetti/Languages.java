package com.prezi.spaghetti;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.prezi.spaghetti.config.ModuleConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class Languages {
	private static final Logger logger = LoggerFactory.getLogger(Languages.class);
	private static final Map<String, GeneratorFactory> generatorFactories = initGeneratorFactories();

	private static Map<String, GeneratorFactory> initGeneratorFactories() {
		LinkedHashMap<String, GeneratorFactory> genFactories = Maps.newLinkedHashMap();
		for (GeneratorFactory factory : ServiceLoader.load(GeneratorFactory.class)) {
			genFactories.put(factory.getLanguage(), factory);
		}

		logger.info("Loaded generators for " + String.valueOf(genFactories.keySet()));

		return genFactories;
	}

	public static Set<GeneratorFactory> getGeneratorFactories() {
		return ImmutableSet.copyOf(generatorFactories.values());
	}

	public static Generator createGeneratorForLanguage(String language, ModuleConfiguration config) {
		GeneratorFactory generatorFactory = getGeneratorFactory(language);
		return generatorFactory.createGenerator(config);
	}

	public static Set<String> getProtectedSymbols(String language) {
		GeneratorFactory generatorFactory = getGeneratorFactory(language);
		return generatorFactory.getProtectedSymbols();
	}

	private static GeneratorFactory getGeneratorFactory(final String language) {
		GeneratorFactory generatorFactory = generatorFactories.get(language);
		if (generatorFactory == null) {
			throw new IllegalArgumentException("No generator found for language \"" + language + "\". Supported languages are: " + StringUtils.join(generatorFactories.keySet(), ", "));
		}

		return generatorFactory;
	}
}