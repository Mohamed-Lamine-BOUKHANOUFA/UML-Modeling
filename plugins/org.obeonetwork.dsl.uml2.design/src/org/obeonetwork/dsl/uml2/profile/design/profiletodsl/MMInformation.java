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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
public class MMInformation {
	protected Resource profileEcoreResource;

	protected IProject mmPlugin;

	/**
	 * @return the EMF project
	 */
	public IProject getMMPlugin() {
		return mmPlugin;
	}

	/**
	 * Constructor.
	 */
	public MMInformation() {
	}

	/* *************************************************************
	 * *************** EMF Project creation ************************
	 * *************************************************************
	 */

	/**
	 * Create a new EMF project.
	 */
	Resource createMMInNewProject(IWizard wizard, final String mmPluginName, final Profile rootProfile,
			final String mmName, final String mmUri) {
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		try {
			wizard.getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("Ecore plug-in creation ...", 100);
					monitor.subTask("Create the model plug-in");
					mmPlugin = ExportProfileService.createPluginProject(mmPluginName,
							shell);
					monitor.worked(20);

					// the model folder.
					monitor.subTask("Create the Ecore model");
					final IFolder modelFolder = mmPlugin.getFolder("model");
					try {
						modelFolder.create(false, true, null);
					} catch (CoreException e) {
						new LogServices().error("exportProfile(" + rootProfile.getClass() + ") not handled",
								e);
					}

					// make a copy of the profile into the new plug-in used for
					// the creation of mm ecore
					final IFile profileCopyIFile = modelFolder.getFile(mmName + "."
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
					profileCopy.setURI(mmUri);
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

	/**
	 * Delete the non selected {@link EObject} in the meta-model of given EMF {@link IProject} using the {@link DslEAnnotation} annotation. 
	 * @param mmPlugin the {@link IProject}
	 * @param profileEcoreModel the meta-model
	 */
	public static void cleanMMPlugin(IProject mmPlugin, EPackage profileEcoreModel) {
		List<EObject> eObjectsToRemove = new ArrayList<EObject>();
		eObjectsToRemove.clear();

		DslEAnnotation dslEAnnotation = new DslEAnnotation(profileEcoreModel);

		for (TreeIterator<EObject> iterator = profileEcoreModel.eAllContents(); iterator.hasNext();) {
			EObject eObject = iterator.next();
			if (eObject instanceof EClassifier || eObject instanceof EStructuralFeature) {
			if (!dslEAnnotation.isSelectedInDslFactory(eObject)) {
				eObjectsToRemove.add(eObject);
				}
			}
		}
		for (EObject eObject : eObjectsToRemove) {
			EcoreUtil.delete(eObject, true);
		}
		GenericUMLProfileTools.save(profileEcoreModel);
		// cleanMMPlugin(mmPlugin);

	}
	public static void cleanMMPlugin(IProject mmPlugin_p) {

		IFile umlEcoreFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(mmPlugin_p.getFullPath().append("model/" + "uml.ecore"));
		IFile typesEcoreFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(mmPlugin_p.getFullPath().append("model/" + "types.ecore"));
		IFile ecoreEcoreFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(mmPlugin_p.getFullPath().append("model/" + "ecore.ecore"));
		if (umlEcoreFile != null && typesEcoreFile != null&&ecoreEcoreFile != null) {
		try {
			ResourcesPlugin.getWorkspace().delete(new IResource[] {ecoreEcoreFile}, true, null);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}}
	}
}
