package com.prezi.spaghetti.definition;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.prezi.spaghetti.bundle.ModuleBundle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Source with location for module definitions.
 */
public final class ModuleDefinitionSource {
	private final String location;
	private final String contents;

	private ModuleDefinitionSource(String location, String contents) {
		this.location = location;
		this.contents = contents;
	}

	/**
	 * Create a source from a bundle.
	 *
	 * @param bundle the bundle containing the definition.
	 */
	public static ModuleDefinitionSource fromBundle(ModuleBundle bundle) throws IOException {
		return new ModuleDefinitionSource("module: " + bundle.getName(), bundle.getDefinition());
	}

	/**
	 * Create a source from a file.
	 *
	 * @param file the file containing the definition.
	 */
	public static ModuleDefinitionSource fromFile(File file) throws IOException {
		return new ModuleDefinitionSource(file.getPath(), Files.asCharSource(file, Charsets.UTF_8).read());
	}

	/**
	 * Create a source from an URL resource.
	 *
	 * @param url the URL pointing to the definition resource.
	 */
	public static ModuleDefinitionSource fromUrl(URL url) throws IOException {
		return new ModuleDefinitionSource(url.toString(), Resources.asCharSource(url, Charsets.UTF_8).read());
	}

	/**
	 * Create a source from a string.
	 *
	 * @param location   the location to display for this source.
	 * @param definition the definition for this source.
	 */
	public static ModuleDefinitionSource fromString(String location, String definition) {
		return new ModuleDefinitionSource(location, definition);
	}

	/**
	 * Returns the location of this definition.
	 */
	public final String getLocation() {
		return location;
	}

	/**
	 * Returns the contents of this definition.
	 */
	public final String getContents() {
		return contents;
	}

	@Override
	@SuppressWarnings("RedundantIfStatement")
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModuleDefinitionSource that = (ModuleDefinitionSource) o;

		if (contents != null ? !contents.equals(that.contents) : that.contents != null) return false;
		if (location != null ? !location.equals(that.location) : that.location != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = location != null ? location.hashCode() : 0;
		result = 31 * result + (contents != null ? contents.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return location;
	}
}
