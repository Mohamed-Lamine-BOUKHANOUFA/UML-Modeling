/*******************************************************************************
 * Copyright (c) 2015 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.obeonetwork.dsl.uml2.profile.design.profiletodsl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a>
 */
public class ManifestTools {

	private File manifestFile;

	/**
	 * Constructor.
	 *
	 */
	public ManifestTools(File manifestFile_p) {
		manifestFile = manifestFile_p;
	}

	/**
	 * Add a given bundle to the require bundle of a manifest file.
	 * 
	 * @param bundle
	 *            the bundle name
	 * @return the new manifest file content
	 * @throws IOException
	 */
	public String addRequireBundle(String bundle) throws IOException {
		return addRequireBundle(bundle, null);
	}

	/**
	 * Add a given bundle to the require bundle of a manifest file.
	 * 
	 * @param bundle
	 *            the bundle name
	 * @param version
	 *            the bundle version, can be <code>null</code>
	 * @return the new manifest file content
	 * @throws IOException
	 */
	public String addRequireBundle(String bundle, Version version) throws IOException {
		String originalContent = Files.toString(manifestFile, Charsets.UTF_8);
		String updatedFileContent = addRequireBundle(bundle, version, originalContent);
		if (!originalContent.equals(updatedFileContent)) {
			Files.write(updatedFileContent, manifestFile, Charsets.UTF_8);
		return updatedFileContent;
		}
		return originalContent;
	}

	/**
	 * Add a given bundle to the require bundle of a given content of a manifest file.
	 * 
	 * @param bundle
	 *            the bundle name
	 * @param version
	 *            the bundle version, can be <code>null</code>
	 * @param originalContent
	 *            the given content of a manifest file
	 * @return a new content.
	 */
	public String addRequireBundle(String bundle, Version version, String originalContent) {
		List<String> updatedContent = Lists.newArrayList();
		boolean isRequireBundle = false;
		for (String part : Splitter.on(": ").split(originalContent)) {
			if (isRequireBundle) {

				List<String> requireBundleList = Lists.newArrayList();

				List<String> lines = Lists.newArrayList(Splitter.on("\n").split(part));
				String startOfNextDirective = lines.get(lines.size() - 1);
				lines.remove(lines.size() - 1);
				for (String line : lines) {
					if (line.endsWith(",")) {
						requireBundleList.add(line + "\n");
					} else {
						requireBundleList.add(line + ",\n");
					}
				}
				if (version != null) {
					updatedContent.add(Joiner.on("").join(requireBundleList) + " " + bundle + ";"
							+ Constants.BUNDLE_VERSION_ATTRIBUTE + "=\"" + version + "\"\n"
							+ startOfNextDirective);
				} else {
					updatedContent.add(Joiner.on("").join(requireBundleList) + " " + bundle + "\n"
							+ startOfNextDirective);
				}
			} else {
				updatedContent.add(part);
			}
			isRequireBundle = part.endsWith(Constants.REQUIRE_BUNDLE);
		}
		String updatedFileContent = Joiner.on(": ").join(updatedContent);
		return updatedFileContent;
	}
}
