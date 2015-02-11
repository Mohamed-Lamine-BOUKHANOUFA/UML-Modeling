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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.sirius.diagram.description.ContainerMapping;
import org.eclipse.sirius.diagram.description.DescriptionFactory;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.Layer;
import org.eclipse.sirius.diagram.description.NodeMapping;
import org.eclipse.sirius.diagram.description.tool.ContainerCreationDescription;
import org.eclipse.sirius.diagram.description.tool.ToolFactory;
import org.eclipse.sirius.diagram.description.tool.ToolSection;
import org.eclipse.sirius.ui.tools.api.project.ViewpointSpecificationProject;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.Group;
import org.eclipse.sirius.viewpoint.description.JavaExtension;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.description.UserColorsPalette;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.sirius.viewpoint.description.tool.ChangeContext;
import org.eclipse.sirius.viewpoint.description.tool.CreateInstance;
import org.eclipse.sirius.viewpoint.description.tool.SetValue;
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

	private String UML_DESIGNER_CLASS_DIAGRAM_CLASS_LAYER = "Class";

	private String UML_DESIGNER_CLASS_DIAGRAM = "Class Diagram";

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
	Group createVsmInNewProject(final String vsmPluginName, final String vsmModelName)
			throws CoreException {
		IProject oldVsmProject = getProject(vsmPluginName);
		if (oldVsmProject != null) {
			oldVsmProject.close(null);
			oldVsmProject.delete(true, null);
		}

		vsmProject = ViewpointSpecificationProject.createNewViewpointSpecificationProject(
				vsmPluginName, vsmModelName + "." + ViewpointSpecificationProject.VIEWPOINT_MODEL_EXTENSION);

		IFile descFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(vsmProject.getFullPath().append("description/" + vsmModelName));

		URI uri = URI.createPlatformResourceURI(descFile.getFullPath().toString() + "."
				+ ViewpointSpecificationProject.VIEWPOINT_MODEL_EXTENSION, true);

		EObject eObject = GenericUMLProfileTools.load(uri, DescriptionPackage.eINSTANCE
				.getEClassifier(ViewpointSpecificationProject.INITIAL_OBJECT_NAME));
		
		if (eObject instanceof Group){
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
	 * @param ProfileEcoreModel
	 */
	public void populateVsm(EPackage ProfileEcoreModel) {
		Layer defaultLayer = initVSM(vsmGroup);
		if (vsmGroup != null) {
			
			//load the UML Designer Odesign and the some diagrams
			Group umlDesignerGroup = loadUMLOdesign();
			Viewpoint umlDesignerGroupVPDesign = getViewpoint(umlDesignerGroup, UML_DESIGNER_VIEWPOINT_DESIGN);
			DiagramDescription umlDesignerClassDiagram = getDiagram(umlDesignerGroupVPDesign,
					UML_DESIGNER_CLASS_DIAGRAM);
			Layer umlDesignerClassLayer = getLayer(umlDesignerClassDiagram,
					UML_DESIGNER_CLASS_DIAGRAM_CLASS_LAYER);
			ContainerMapping umlDesignerPackageMapping = getPackageMapping(umlDesignerClassLayer,
					"CD_Package");

			// Copy the user colors palette from the Odesign of ULM Designer to the Odesign of the generated
			// DSL.
			vsmGroup.getUserColorsPalettes().addAll(
					(Collection<? extends UserColorsPalette>)Tools.getCopy(umlDesignerGroup
							.getUserColorsPalettes()));
			
			// Add the java extension from UML Designer Odesign.
			copyJavaExtensions(umlDesignerGroupVPDesign, vsmViewpoint);


			DslEAnnotation dslEAnnotation = new DslEAnnotation(ProfileEcoreModel);
			String mappingType;
			boolean eCLassAndAbstract = false;
			for (TreeIterator<EObject> iterator = ProfileEcoreModel.eAllContents(); iterator.hasNext();) {
				EObject eObject = iterator.next();

				// Used to avoid the creation of mapping for the abstracts classes.
				eCLassAndAbstract = false;
				if (eObject instanceof EClass && ((EClass)eObject).isAbstract()) {
					eCLassAndAbstract = true;
				}

				if (!eCLassAndAbstract && eObject instanceof ENamedElement
						&& dslEAnnotation.isSelectedInVsmMapping(eObject)) {

					mappingType = dslEAnnotation.getVSMMappingType(eObject);
					if (mappingType.equals(DslEAnnotation.CONTAINER)) {
						ContainerMapping containerMapping = MappingTools.createContainer(defaultLayer,
								eObject,
								umlDesignerPackageMapping, true);
					}

					if (mappingType.equals(DslEAnnotation.CONTAINMENT_EDGE) && eObject instanceof EReference) {
						EList<ContainerMapping> createdMapping = MappingTools.createContainedContainer(
								defaultLayer,
								(EReference)eObject, umlDesignerPackageMapping);
					}

					if (mappingType.equals(DslEAnnotation.NODE)) {
						// Node creation
						NodeMapping nodeMapping = DescriptionFactory.eINSTANCE.createNodeMapping();
						nodeMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
						defaultLayer.getNodeMappings().add(nodeMapping);

					}
					// if (mappingType.equals(DslEAnnotation.BORDERED_NODE)) {
					// NodeMapping nodeMapping = DescriptionFactory.eINSTANCE.createNodeMapping();
					// // nodeMapping.set
					// nodeMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
					// defaultLayer.getNodeMappings().add(nodeMapping);
					// }
					if (mappingType.equals(DslEAnnotation.ELEMENT_BASED_EDGE)) {
						EdgeMapping edgeMapping = DescriptionFactory.eINSTANCE.createEdgeMapping();
						edgeMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
						defaultLayer.getEdgeMappings().add(edgeMapping);
					}
					// if (mappingType.equals(DslEAnnotation.RELATION_BASED_EDGE)) {
					// EdgeMapping edgeMapping = DescriptionFactory.eINSTANCE.createEdgeMapping();
					// edgeMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
					// defaultLayer.getEdgeMappings().add(edgeMapping);
					// }
				}
			}

			createCreationToolsForContainers(defaultLayer);

		}
		GenericUMLProfileTools.save(vsmGroup);
	}

	/**
	 * Create the creation tool for all direct and indirect {@link ContainerMapping}s of a given {@link Layer}
	 * .
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 */
	public void createCreationToolsForContainers(Layer layer) {
		// Create the tools section
		ToolSection toolSection = (ToolSection)Tools.contains(DEFAULT_TOOLS_SECTION,
				(EList<ENamedElement>)(EList<?>)layer.getToolSections());
		if (toolSection == null) {
			toolSection = ToolFactory.eINSTANCE.createToolSection();
			toolSection.setName(DEFAULT_TOOLS_SECTION);
			layer.getToolSections().add(toolSection);
		}

		for (ContainerMapping containerMapping : MappingTools.getAllContainerMappings(layer)) {

			// Container creation tool
			ContainerCreationDescription containerCreationDescription = ToolFactory.eINSTANCE
					.createContainerCreationDescription();
			toolSection.getOwnedTools().add(containerCreationDescription);
			containerCreationDescription.setName(containerMapping.getName() + "Creation");
			containerCreationDescription.getContainerMappings().add(containerMapping);

			// Change context operation to the container
			ChangeContext changeContext = org.eclipse.sirius.viewpoint.description.tool.ToolFactory.eINSTANCE
					.createChangeContext();
			changeContext.setBrowseExpression("var:container");
			containerCreationDescription.getInitialOperation().setFirstModelOperations(changeContext);

			// create instance
			CreateInstance createInstance = org.eclipse.sirius.viewpoint.description.tool.ToolFactory.eINSTANCE
					.createCreateInstance();
			createInstance.setReferenceName(containerMapping.getSemanticCandidatesExpression().substring(8));
			createInstance.setTypeName(containerMapping.getDomainClass());
			changeContext.getSubModelOperations().add(createInstance);

			// set value for the name
			SetValue setValue = org.eclipse.sirius.viewpoint.description.tool.ToolFactory.eINSTANCE
					.createSetValue();
			setValue.setFeatureName(FEATURE_NAME);
			setValue.setValueExpression(containerMapping.getDomainClass());
			createInstance.getSubModelOperations().add(setValue);
		}
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
				+ "/description/uml2."
				+ ViewpointSpecificationProject.VIEWPOINT_MODEL_EXTENSION);

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
	 * Find a {@link DiagramDescription} among those of a given {@link Viewpoint} using a given name.
	 * 
	 * @param viewpoint
	 *            the {@link Viewpoint}
	 * @param diagramName
	 *            the given name
	 * @return the {@link DiagramDescription} if found, otherwise <code>null<code>.
	 */
	public DiagramDescription getDiagram(Viewpoint viewpoint, String diagramName) {
		for (RepresentationDescription diagramDescription : viewpoint.getOwnedRepresentations()) {
			if (diagramDescription instanceof DiagramDescription
					&& diagramDescription.getName().equals(diagramName)) {
				return (DiagramDescription)diagramDescription;
			}
		}
		return null;
	}

	/**
	 * Find a {@link DiagramDescription} among those of a given {@link Viewpoint} using a given name.
	 * 
	 * @param diagramDescription
	 *            the {@link Viewpoint}
	 * @param layerName
	 *            the given name
	 * @return the {@link DiagramDescription} if found, otherwise <code>null<code>.
	 */
	public Layer getLayer(DiagramDescription diagramDescription, String layerName) {
		for (Layer layer : diagramDescription.getAllLayers()) {
			if (layer.getName().equals(layerName)) {
				return layer;
			}
		}
		return null;
	}

	/**
	 * Find a {@link ContainerMapping} among those of a given {@link Layer} using a given name.
	 * 
	 * @param layer
	 *            the {@link Layer}
	 * @param containerMappingName
	 *            the given name
	 * @return the {@link ContainerMapping} if found, otherwise <code>null<code>.
	 */
	public ContainerMapping getPackageMapping(Layer layer, String containerMappingName) {
		for (ContainerMapping containerMapping : layer.getContainerMappings()) {
			if (containerMapping.getName().equals(containerMappingName)) {
				return containerMapping;
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
