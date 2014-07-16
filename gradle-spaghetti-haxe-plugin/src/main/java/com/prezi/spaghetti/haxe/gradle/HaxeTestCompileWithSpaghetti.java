package com.prezi.spaghetti.haxe.gradle;

import com.google.common.collect.ImmutableMap;
import com.prezi.haxe.gradle.HaxeCommandBuilder;
import com.prezi.haxe.gradle.HaxeTestCompile;
import com.prezi.spaghetti.ReservedWords;
import com.prezi.spaghetti.haxe.HaxeGenerator;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.gradle.api.DomainObjectSet;
import org.gradle.language.base.LanguageSourceSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HaxeTestCompileWithSpaghetti extends HaxeTestCompile {
	@Override
	protected HaxeCommandBuilder configureHaxeCommandBuilder(File output, DomainObjectSet<LanguageSourceSet> sources) {
		HaxeCommandBuilder builder = super.configureHaxeCommandBuilder(output, sources);

		try {
			SimpleTemplateEngine engine = new SimpleTemplateEngine();
			Template template;
			try {
				template = engine.createTemplate(HaxeTestCompileWithSpaghetti.class.getResource("/SpaghettiTest.hx"));
			} catch (ClassNotFoundException ex) {
				throw new AssertionError(ex);
			}

			FileWriter writer = new FileWriter(new File(getTestsDirectory(), "SpaghettiTest.hx"));
			try {
				template.make(ImmutableMap.builder()
						.put("config", ReservedWords.CONFIG)
						.put("haxeModule", HaxeGenerator.HAXE_MODULE_VAR)
						.put("module", ReservedWords.INSTANCE)
						.put("modules", ReservedWords.MODULES)
						.build()
				).writeTo(writer);
			} finally {
				writer.close();
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return builder;
	}
}