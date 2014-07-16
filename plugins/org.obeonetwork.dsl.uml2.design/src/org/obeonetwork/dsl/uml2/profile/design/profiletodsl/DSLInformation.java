/*******************************************************************************
 * Copyright (c) 2009, 2011 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.obeonetwork.dsl.uml2.profile.design.profiletodsl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.dsl.uml2.design.services.LogServices;
import org.obeonetwork.dsl.uml2.profile.design.exportprofile.ExportProfileService;
import org.obeonetwork.dsl.uml2.profile.design.exportprofile.UmlToEcore;
import org.obeonetwork.dsl.uml2.profile.design.services.GenericUMLProfileTools;
import org.obeonetwork.dsl.uml2.profile.design.services.UMLProfileServices;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class DSLInformation {
	Resource profileEcoreResource;

	/**
	 * Constructor.
	 */
	public DSLInformation() {
	}

	/* *************************************************************
	 * *************** EMF Project creation ************************
	 * *************************************************************
	 */

	/**
	 * Create a new EMF project.
	 */
	Resource createEcoreModel(IWizard wizard, final String dslPluginName, final Profile rootProfile,
			final String dslName) {
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		try {
			wizard.getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("Ecore plug-in creation ...", 100);
					monitor.subTask("Create the model plgu-in");
					final IProject profilePlugin = ExportProfileService.createPluginProject(dslPluginName,
							shell);
					monitor.worked(20);

					// the model folder.
					monitor.subTask("Create the Ecore model");
					final IFolder modelFolder = profilePlugin.getFolder("model");
					try {
						modelFolder.create(false, true, null);
					} catch (CoreException e) {
						new LogServices().error("exportProfile(" + rootProfile.getClass() + ") not handled",
								e);
					}

					// make a copy of the profile into the new plug-in used for
					// the creation of dsl ecore
					final IFile profileCopyIFile = modelFolder.getFile(dslName + "."
							+ UMLResource.FILE_EXTENSION);

					final IFile rootProfileIFile = GenericUMLProfileTools.resourceToIFile(rootProfile
							.eResource());
					try {
						rootProfileIFile.copy(profileCopyIFile.getFullPath(), true, new NullProgressMonitor());
					} catch (final CoreException e) {
						new LogServices().error("exportProfile(" + rootProfile.getClass() + ") not handled",
								e);
					}

					final Resource profileCopyResource = new ResourceSetImpl().createResource(URI
							.createURI(profileCopyIFile.getFullPath().toString()));

					final Profile profileCopy = (Profile)GenericUMLProfileTools.load(profileCopyResource
							.getURI());
					UMLProfileServices.undefineProfile(profileCopy);
					GenericUMLProfileTools.save(profileCopy);

					// create the ecore file
					final UmlToEcore umlToEcore = new UmlToEcore();
					profileEcoreResource = umlToEcore.umlToEcore(profileCopy);
					monitor.worked(20);
					monitor.subTask("Init next Page");
					monitor.worked(20);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return profileEcoreResource;
	}
}
