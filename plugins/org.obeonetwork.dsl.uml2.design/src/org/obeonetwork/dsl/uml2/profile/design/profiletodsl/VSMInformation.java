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

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sirius.diagram.description.DescriptionFactory;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.description.Layer;
import org.eclipse.sirius.ui.tools.api.project.ViewpointSpecificationProject;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.Group;
import org.eclipse.sirius.viewpoint.description.JavaExtension;
import org.eclipse.sirius.viewpoint.description.UserColorsPalette;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.obeonetwork.dsl.uml2.profile.design.services.GenericUMLProfileTools;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class VSMInformation {

	private IProject vsmProject;

	private Group vsmGroup;

	private Viewpoint vsmViewpoint;

	private DiagramDescription vsmFreeDiagram;

	private String FREE_DIAGRAM = "FreeDiagram";

	private String DEFAULT_LAYER = "DefaultLayer";

	private String DEFAULT_TOOLS_SECTION = "Tools";

	private String UML_DESIGNER_VIEWPOINT_DESIGN = "Design";

	private String FEATURE_NAME = "name";

	/**
	 * Constructor.
	 */
	public VSMInformation() {
	}

	/* *************************************************************
	 * *************** VSM Project creation ************************
	 * *************************************************************
	 */

	/**
	 * Create a new EMF project.
	 * 
	 * @throws CoreException
	 */
	Group createVsmInNewProject(final String vsmPluginName, final String vsmModelName) throws CoreException {
		IProject oldVsmProject = getProject(vsmPluginName);
		if (oldVsmProject != null) {
			oldVsmProject.close(null);
			oldVsmProject.delete(true, null);
		}

		vsmProject = ViewpointSpecificationProject.createNewViewpointSpecificationProject(vsmPluginName,
				vsmModelName + "." + ViewpointSpecificationProject.VIEWPOINT_MODEL_EXTENSION);

		IFile descFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(vsmProject.getFullPath().append("description/" + vsmModelName));

		URI uri = URI.createPlatformResourceURI(descFile.getFullPath().toString() + "."
				+ ViewpointSpecificationProject.VIEWPOINT_MODEL_EXTENSION, true);

		EObject eObject = GenericUMLProfileTools.load(uri, DescriptionPackage.eINSTANCE
				.getEClassifier(ViewpointSpecificationProject.INITIAL_OBJECT_NAME));

		if (eObject instanceof Group) {
			vsmGroup = ((Group)eObject);
		} else {
			vsmGroup = null;
		}

		return vsmGroup;
	}

	/**
	 * Create in the VSM a viewpoint, Diagram and default layer
	 */
	public Layer initVSM(Group vsmGroup_p) {

		// Viewpoint
		vsmViewpoint = org.eclipse.sirius.viewpoint.description.DescriptionFactory.eINSTANCE
				.createViewpoint();
		vsmViewpoint.setName(vsmGroup_p.getName());
		vsmViewpoint.setLabel(vsmGroup_p.getName());
		vsmGroup_p.getOwnedViewpoints().add(vsmViewpoint);
		// Diagram
		vsmFreeDiagram = DescriptionFactory.eINSTANCE.createDiagramDescription();
		vsmFreeDiagram.setName(FREE_DIAGRAM);
		vsmFreeDiagram.setLabel(FREE_DIAGRAM);
		vsmFreeDiagram.setInitialisation(true);
		vsmFreeDiagram.setDomainClass(MetaClassesSelection.ROOT_ELEMENT_NAME);
		vsmViewpoint.getOwnedRepresentations().add(vsmFreeDiagram);
		// Layer
		Layer defaultLayer = DescriptionFactory.eINSTANCE.createLayer();
		defaultLayer.setName(DEFAULT_LAYER);
		defaultLayer.setLabel(DEFAULT_LAYER);
		vsmFreeDiagram.setDefaultLayer(defaultLayer);

		return defaultLayer;
	}

	/**
	 * Populate the VSM using the {@link EClass} of a given {@link EPackage}.
	 * 
	 * @param profileEcoreModel
	 */
	public void populateVsm(EPackage profileEcoreModel) {
		Layer defaultLayer = initVSM(vsmGroup);
		if (vsmGroup != null) {

			// load the UML Designer Odesign and the some diagrams
			Group umlDesignerGroup = loadUMLOdesign();
			Viewpoint umlDesignerGroupVPDesign = getViewpoint(umlDesignerGroup, UML_DESIGNER_VIEWPOINT_DESIGN);
			vsmGroup.getUserColorsPalettes().addAll(
					(Collection<? extends UserColorsPalette>)Tools.getCopy(umlDesignerGroup
							.getUserColorsPalettes()));

			// Add the java extension from UML Designer Odesign.
			copyJavaExtensions(umlDesignerGroupVPDesign, vsmViewpoint);


			DslEAnnotation dslEAnnotation = new DslEAnnotation(profileEcoreModel);
			MappingTools mappingTools = new MappingTools(profileEcoreModel, vsmGroup, umlDesignerGroup);
			boolean eCLassAndAbstract = false;
			EList<DiagramElementMapping> diagramElementMappings = new BasicEList<DiagramElementMapping>();
			for (TreeIterator<EObject> iterator = profileEcoreModel.eAllContents(); iterator.hasNext();) {
				EObject eObject = iterator.next();

				// Used to avoid the creation of mapping for the abstracts classes.
				eCLassAndAbstract = false;
				if (eObject instanceof EClass && ((EClass)eObject).isAbstract()) {
					eCLassAndAbstract = true;
				}

				if (!eCLassAndAbstract && eObject instanceof ENamedElement
						&& dslEAnnotation.isSelectedInVsmMapping(eObject)) {
					diagramElementMappings.add(mappingTools.createMappings(defaultLayer, eObject, true));
				}
			}

			mappingTools.handleMappingsRelations(defaultLayer);

			mappingTools.createCreationToolsForContainers(defaultLayer);
			mappingTools.createCreationToolsForNodes(defaultLayer);


		}
		GenericUMLProfileTools.save(vsmGroup);
	}

	/**
	 * Find the project with a given name.
	 * 
	 * @param projectName
	 *            the project name
	 * @return the project or null
	 */
	public IProject getProject(String projectName) {
		int projectsSize = ResourcesPlugin.getWorkspace().getRoot().getProjects().length;
		for (int i = 0; i < projectsSize; i++) {
			if (ResourcesPlugin.getWorkspace().getRoot().getProjects()[i].getName().equals(projectName)) {
				return ResourcesPlugin.getWorkspace().getRoot().getProjects()[i];
			}
		}
		return null;
	}

	public Group loadUMLOdesign() {
		Group umlDesignerGroup;

		org.osgi.framework.Bundle bundleUMLDDesigner = Platform.getBundle("org.obeonetwork.dsl.uml2.design");

		URI uriOdesignFile = URI.createURI("platform:/plugin/" + bundleUMLDDesigner.getSymbolicName()
				+ "/description/uml2." + ViewpointSpecificationProject.VIEWPOINT_MODEL_EXTENSION);

		EObject eObject = GenericUMLProfileTools.load(uriOdesignFile, DescriptionPackage.eINSTANCE
				.getEClassifier(ViewpointSpecificationProject.INITIAL_OBJECT_NAME));

		if (eObject instanceof Group) {
			umlDesignerGroup = ((Group)eObject);
		} else {
			umlDesignerGroup = null;
		}

		// ************************
		IFile file = vsmProject.getFile("/META-INF/MANIFEST.MF");
		ManifestTools meManifestTools = new ManifestTools(file.getRawLocation().toFile());

		try {
			meManifestTools.addRequireBundle(bundleUMLDDesigner.getSymbolicName(),
					bundleUMLDDesigner.getVersion());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// *********************
		return umlDesignerGroup;
	}

	/**
	 * Find a {@link Viewpoint} among those of a given {@link Group} using a given name.
	 * 
	 * @param group
	 *            the {@link Group}
	 * @param vpName
	 *            the given name
	 * @return the {@link Viewpoint} if found, otherwise <code>null<code>.
	 */
	public Viewpoint getViewpoint(Group group, String vpName) {
		for (Viewpoint viewpoint : group.getOwnedViewpoints()) {
			if (viewpoint.getName().equals(vpName)) {
				return viewpoint;
			}
		}
		return null;

	}


	/**
	 * Copy all {@link JavaExtension} of a source {@link Viewpoint} to a target {@link Viewpoint}.
	 * 
	 * @param sourceViewpoint
	 *            the source {@link Viewpoint}
	 * @param targetViewpoint
	 *            the target {@link Viewpoint}
	 */
	public void copyJavaExtensions(Viewpoint sourceViewpoint, Viewpoint targetViewpoint) {
		targetViewpoint.getOwnedJavaExtensions().addAll(
				(Collection<? extends JavaExtension>)Tools.getCopy(sourceViewpoint.getOwnedJavaExtensions()));
	}
}
