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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.diagram.BundledImageShape;
import org.eclipse.sirius.diagram.LabelPosition;
import org.eclipse.sirius.diagram.ResizeKind;
import org.eclipse.sirius.diagram.description.AbstractNodeMapping;
import org.eclipse.sirius.diagram.description.ContainerMapping;
import org.eclipse.sirius.diagram.description.DescriptionFactory;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.description.Layer;
import org.eclipse.sirius.diagram.description.NodeMapping;
import org.eclipse.sirius.diagram.description.style.BundledImageDescription;
import org.eclipse.sirius.diagram.description.style.ContainerStyleDescription;
import org.eclipse.sirius.diagram.description.style.FlatContainerStyleDescription;
import org.eclipse.sirius.diagram.description.style.NodeStyleDescription;
import org.eclipse.sirius.diagram.description.style.StyleFactory;
import org.eclipse.sirius.diagram.description.tool.ContainerCreationDescription;
import org.eclipse.sirius.diagram.description.tool.NodeCreationDescription;
import org.eclipse.sirius.diagram.description.tool.ToolFactory;
import org.eclipse.sirius.diagram.description.tool.ToolSection;
import org.eclipse.sirius.viewpoint.description.ColorDescription;
import org.eclipse.sirius.viewpoint.description.ComputedColor;
import org.eclipse.sirius.viewpoint.description.Group;
import org.eclipse.sirius.viewpoint.description.IdentifiedElement;
import org.eclipse.sirius.viewpoint.description.InterpolatedColor;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.description.UserColor;
import org.eclipse.sirius.viewpoint.description.UserColorsPalette;
import org.eclipse.sirius.viewpoint.description.UserFixedColor;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.sirius.viewpoint.description.tool.ChangeContext;
import org.eclipse.sirius.viewpoint.description.tool.CreateInstance;
import org.eclipse.sirius.viewpoint.description.tool.SetValue;
import org.eclipse.sirius.viewpoint.description.tool.ToolEntry;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a>
 */
public class MappingTools {

	private String UML_DESIGNER_CLASS_DIAGRAM_CLASS_LAYER = "Class";

	private String UML_DESIGNER_CLASS_DIAGRAM = "Class Diagram";

	private String UML_DESIGNER_VIEWPOINT_DESIGN = "Design";

	private DslEAnnotation dslEAnnotation;

	private Group vsmGroup;

	private Group umlDesignerGroup;

	private Viewpoint umlDesignerGroupVPDesign;

	private ContainerMapping defaultContainerMappingForStyle;

	private NodeMapping defaultNodeMappingForStyle;

	private String DEFAULT_TOOLS_SECTION = "Tools";

	private String FEATURE_NAME = "name";

	private EPackage profileEcoreModel;

	/**
	 * Constructor. A set of tools for the Mappings management.
	 *
	 * @param profileEcoreModel
	 *            the {@link EPackage} of the model.
	 * @param vsmGroup_p
	 *            the {@link Group} in which the new mappings will be added
	 * @param umlDesignerGroup_p
	 *            the {@link Group} from which the mapping styles will be reused
	 */
	public MappingTools(EPackage profileEcoreModel_p, Group vsmGroup_p, Group umlDesignerGroup_p) {
		profileEcoreModel = profileEcoreModel_p;
		vsmGroup = vsmGroup_p;
		umlDesignerGroup = umlDesignerGroup_p;

		umlDesignerGroupVPDesign = getViewpoint(umlDesignerGroup, UML_DESIGNER_VIEWPOINT_DESIGN);
		DiagramDescription umlDesignerClassDiagram = getDiagram(umlDesignerGroupVPDesign,
				UML_DESIGNER_CLASS_DIAGRAM);
		Layer umlDesignerClassLayer = getLayer(umlDesignerClassDiagram,
				UML_DESIGNER_CLASS_DIAGRAM_CLASS_LAYER);
		defaultContainerMappingForStyle = getPackageMapping(umlDesignerClassLayer, "CD_Package");
		defaultNodeMappingForStyle = getNodeMapping(umlDesignerClassLayer, "CD_Class");

		dslEAnnotation = new DslEAnnotation(profileEcoreModel_p);

	}

	/**
	 * Create the possible {@link AbstractNodeMapping} for a given {@link EObject} in a given {@link Layer}.
	 * 
	 * @param layer
	 *            the a given {@link Layer}
	 * @param eObject
	 *            the given {@link EObject}
	 * @param setContainer
	 *            if <code>true</code> the container of the the created {@link AbstractNodeMapping} will be
	 *            set.
	 * @return the created {@link AbstractNodeMapping}
	 */
	public DiagramElementMapping createMappings(Layer layer, EObject eObject, boolean setContainer) {

		DiagramElementMapping createdDiagramElementMapping = null;

		String mappingType = dslEAnnotation.getVSMMappingType(eObject);
		if (mappingType.equals(DslEAnnotation.CONTAINER)) {
			// ***********************************************
			// To find the containerMapping to reuse its style.
			ContainerMapping containerMappingForStyle = getUml2ContainerMappingEquivalence(eObject);
			// ***********************************************
			ContainerMapping containerMapping = MappingTools.createContainer(layer, eObject,
					containerMappingForStyle.getStyle(), setContainer);
			createdDiagramElementMapping = containerMapping;
		}

		if (mappingType.equals(DslEAnnotation.CONTAINMENT_EDGE) && eObject instanceof EReference) {
			EList<DiagramElementMapping> containedContainerMappings = createContainedContainer(layer,
					(EReference)eObject, true);
			createdDiagramElementMapping = containedContainerMappings.get(0);

		}

		if (mappingType.equals(DslEAnnotation.NODE)) {
			String originalStyle = dslEAnnotation.identifyTheVSMMapping((EClass)eObject);
			if (mappingType.equals(originalStyle)) {
				// ***********************************************
				// To find the nodeMapping to reuse its style.
				NodeMapping nodeMappingForStyle = getUml2NodeMappingEquivalence(eObject);
				// ***********************************************
				NodeMapping nodeMapping = MappingTools.createNode(layer, eObject,
						nodeMappingForStyle.getStyle(), setContainer);
				createdDiagramElementMapping = nodeMapping;

			} else if (originalStyle.equals(DslEAnnotation.CONTAINER)) {

				// ***********************************************
				// To find the containerMapping to reuse its style.
				ContainerMapping containerMappingForStyle = getUml2ContainerMappingEquivalence(eObject);
				// ***********************************************
				NodeMapping nodeMapping = MappingTools.createNode(layer, eObject,
						MappingTools.getNodeStyle(containerMappingForStyle.getStyle(), vsmGroup),
						setContainer);
				createdDiagramElementMapping = nodeMapping;
			}

		} else

		if (mappingType.equals(DslEAnnotation.BORDERED_NODE)) {
			String originalStyle = dslEAnnotation.identifyTheVSMMapping((EClass)eObject);
			if (mappingType.equals(originalStyle)) {
				// ***********************************************
				// To find the nodeMapping to reuse its style.
				NodeMapping nodeMappingForStyle = getUml2BorderedNodeMappingEquivalence(eObject);
				// ***********************************************
				NodeMapping nodeMapping = createBorderedNode(layer, eObject, nodeMappingForStyle.getStyle(),
						setContainer);

			} else if (originalStyle.equals(DslEAnnotation.CONTAINER)) {
				// TODO to update

				// ***********************************************
				// To find the containerMapping to reuse its style.
				ContainerMapping containerMappingForStyle = getUml2ContainerMappingEquivalence(eObject);
				// ***********************************************
				NodeMapping nodeMapping = createBorderedNode(layer, eObject,
						MappingTools.getNodeStyle(containerMappingForStyle.getStyle(), vsmGroup),
						setContainer);
			}
		}
		return createdDiagramElementMapping;
	}

	/**
	 * Create an new {@link ContainerMapping} for a given {@link EObject} using a given
	 * {@link ContainerStyleDescription}. Add the new {@link ContainerMapping} to a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eObject
	 *            the given {@link EObject}
	 * @param containerStyleToUse
	 *            the given {@link ContainerStyleDescription}.
	 * @param setTheContainer
	 *            if <code>true</code> set the container of the new created {@link ContainerMapping}
	 * @return the new created {@link ContainerMapping}.
	 */
	public static ContainerMapping createContainer(Layer layer, EObject eObject,
			ContainerStyleDescription containerStyleToUse, boolean setTheContainer) {
		// Container creation
		ContainerMapping newContainerMapping = DescriptionFactory.eINSTANCE.createContainerMapping();
		newContainerMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
		newContainerMapping.setDomainClass(((ENamedElement)eObject).getName());
		if (setTheContainer) {
			layer.getContainerMappings().add(newContainerMapping);
		}
		// Copy the style from the node of UML Designer
		newContainerMapping.setStyle(EcoreUtil.copy(containerStyleToUse));
		// Use the color copied to the vsm Odesign
		if (newContainerMapping.getStyle() instanceof FlatContainerStyleDescription) {
			ColorDescription colorDescription = ((FlatContainerStyleDescription)newContainerMapping
					.getStyle()).getForegroundColor();
			String colorName = new String();
			if (colorDescription instanceof UserFixedColor) {
				colorName = ((UserFixedColor)colorDescription).getName();
			}
			if (colorDescription instanceof InterpolatedColor) {
				colorName = ((InterpolatedColor)colorDescription).getName();
			}
			if (colorDescription instanceof ComputedColor) {
				colorName = ((ComputedColor)colorDescription).getName();
			}
			UserColor localColor = getUserColor(
					((Group)EcoreUtil.getRootContainer(layer)).getUserColorsPalettes(), colorName);
			((FlatContainerStyleDescription)newContainerMapping.getStyle())
					.setForegroundColor((ColorDescription)localColor);
		}
		// Set the label of the mapping
		newContainerMapping.getStyle().setLabelExpression("feature:name");
		newContainerMapping.setSemanticCandidatesExpression("feature:" + MetaClassesSelection.OWNED_REFERENCE
				+ ((ENamedElement)eObject).getName());
		return newContainerMapping;
	}

	/**
	 * Create the possible {@link AbstractNodeMapping}s for the type of a given {@link EReference}. Add the
	 * new {@link AbstractNodeMapping}s to the {@link ContainerMapping} of the container of the
	 * {@link EReference} and to it's hierarchy.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eReference
	 *            the given {@link EReference}
	 * @return the new created {@link AbstractNodeMapping}s.
	 */
	public EList<DiagramElementMapping> createContainedContainer(Layer layer, EReference eReference,
			boolean setContainer) {
		// The created mappings for this EReference.
		EList<DiagramElementMapping> createdMappings = new BasicEList<DiagramElementMapping>();
		// The recursive mappings
		EList<AbstractNodeMapping> recursiveMappings = new BasicEList<AbstractNodeMapping>();
		EClass referenceContainer = (EClass)eReference.eContainer();
		EClass referenceType = eReference.getEReferenceType();
		EList<EClass> referenceTypes = new BasicEList<EClass>();

		if (!referenceType.isAbstract()) {
			referenceTypes.add(referenceType);
		} else {
			referenceTypes.addAll(Tools.getEAllSubTypes(referenceType));
		}

		for (EClass eClass : referenceTypes) {
			if (!eClass.isAbstract()) {
				// Create a new mapping.
				DiagramElementMapping createdDiagramElementMapping = createMappings(layer, eClass,
						setContainer);
				createdDiagramElementMapping.setName("Sub" + createdDiagramElementMapping.getName());
				createdDiagramElementMapping.setSemanticCandidatesExpression("feature:"
						+ eReference.getName());
				createdMappings.add(createdDiagramElementMapping);
			}
		}
		return createdMappings;
	}

	/**
	 * Create an new {@link NodeMapping} for a given {@link EObject} using a given
	 * {@link NodeStyleDescription}. Add the new {@link NodeMapping} to a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eObject
	 *            the given {@link EObject}
	 * @param nodeStyleToUse
	 *            the given {@link NodeStyleDescription}.
	 * @param setTheContainer
	 *            if <code>true</code> set the container of the new created {@link NodeMapping}.
	 * @return the new created {@link NodeMapping}.
	 */
	public static NodeMapping createNode(Layer layer, EObject eObject, NodeStyleDescription nodeStyleToUse,
			boolean setTheContainer) {
		// Node creation
		NodeMapping newNodeMapping = DescriptionFactory.eINSTANCE.createNodeMapping();

		newNodeMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
		newNodeMapping.setDomainClass(((ENamedElement)eObject).getName());
		if (setTheContainer) {
			layer.getNodeMappings().add(newNodeMapping);
		}
		// Copy the style from the node of UML Designer
		newNodeMapping.setStyle(EcoreUtil.copy(nodeStyleToUse));
		// Use the color copied to the vsm Odesign
		if (newNodeMapping.getStyle() instanceof FlatContainerStyleDescription) {
			ColorDescription colorDescription = ((FlatContainerStyleDescription)newNodeMapping.getStyle())
					.getForegroundColor();
			String colorName = new String();
			if (colorDescription instanceof UserFixedColor) {
				colorName = ((UserFixedColor)colorDescription).getName();
			}
			if (colorDescription instanceof InterpolatedColor) {
				colorName = ((InterpolatedColor)colorDescription).getName();
			}
			if (colorDescription instanceof ComputedColor) {
				colorName = ((ComputedColor)colorDescription).getName();
			}
			UserColor localColor = getUserColor(
					((Group)EcoreUtil.getRootContainer(layer)).getUserColorsPalettes(), colorName);
			((FlatContainerStyleDescription)newNodeMapping.getStyle())
					.setForegroundColor((ColorDescription)localColor);
		}
		// Set the label of the mapping
		newNodeMapping.getStyle().setLabelExpression("feature:name");
		newNodeMapping.setSemanticCandidatesExpression("feature:" + MetaClassesSelection.OWNED_REFERENCE
				+ ((ENamedElement)eObject).getName());
		return newNodeMapping;
	}

	/**
	 * Create an new Bordered{@link NodeMapping} for a given {@link EObject} using a given
	 * {@link NodeStyleDescription}. Add the new {@link NodeMapping} to a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eObject
	 *            the given {@link EObject}
	 * @param nodeStyleToUse
	 *            the given {@link NodeStyleDescription}.
	 * @param setTheContainer
	 *            if <code>true</code> set the container of the new created {@link NodeMapping}.
	 * @return the new created Bordered{@link NodeMapping}.
	 */
	public NodeMapping createBorderedNode(Layer layer, EObject eObject, NodeStyleDescription nodeStyleToUse,
			boolean setTheContainer) {
		NodeMapping newNodeMapping = DescriptionFactory.eINSTANCE.createNodeMapping();

		if (eObject != null && eObject instanceof EClass) {
			EClass eClass = (EClass)eObject;

			EList<EClass> eClassAndItsParents = new BasicEList<EClass>();
			eClassAndItsParents.add(eClass);
			eClassAndItsParents.addAll(eClass.getEAllSuperTypes());

			Map<EObject, Collection<Setting>> crossReferences = EcoreUtil.UsageCrossReferencer.findAll(
					eClassAndItsParents, profileEcoreModel);

			for (Entry<EObject, Collection<Setting>> entrySet : crossReferences.entrySet()) {
				entrySet.getKey();
				Collection<Setting> settings = entrySet.getValue();
				for (Setting setting : settings) {
					if (setting.getEObject() instanceof EReference) {
						EReference eReference = (EReference)setting.getEObject();
						newNodeMapping = isNodeMappingExist(((ENamedElement)eObject).getName(), "feature:"
								+ eReference.getName(), layer);
						if (newNodeMapping == null) {
							newNodeMapping = MappingTools.createNode(layer, eObject, nodeStyleToUse,
									setTheContainer);
							newNodeMapping.setSemanticCandidatesExpression("feature:" + eReference.getName());
							newNodeMapping.setName(((ENamedElement)eObject).getName() + "_"
									+ eReference.getName() + DslEAnnotation.MAPPING);
						}
					}
				}
			}
		}
		return newNodeMapping;
	}

	/**
	 * Verify if a {@link NodeMapping} with a given domain class name and a semantic candidate expression
	 * exists in a given {@link Layer}.
	 * 
	 * @param domainClass
	 *            the given domain class
	 * @param semanticCandidatesExpression
	 *            the given semantic candidate expression
	 * @param layer
	 *            the given {@link Layer}.
	 * @return the found {@link NodeMapping}, else null.
	 */
	public static NodeMapping isNodeMappingExist(String domainClass, String semanticCandidatesExpression,
			Layer layer) {

		EList<NodeMapping> allNodeMappings = getAllNodeMappings(layer);
		for (NodeMapping nodeMapping : allNodeMappings) {
			if (nodeMapping.getDomainClass().equals(domainClass)
					&& nodeMapping.getSemanticCandidatesExpression().equals(semanticCandidatesExpression)) {
				return nodeMapping;
			}
		}
		return null;
	}

	/**
	 * Verify if a {@link NodeCreationDescription} for a given {@link NodeMapping} is defined in a given
	 * {@link Layer}.
	 * 
	 * @param nodeMapping
	 *            the given {@link NodeMapping}
	 * @param layer
	 *            the given {@link Layer}.
	 * @return the found {@link NodeCreationDescription}, else <code>null</code>
	 */
	public NodeCreationDescription isNodeCreationToolExist(NodeMapping nodeMapping, Layer layer) {

		ToolSection toolSection = getToolSection(DEFAULT_TOOLS_SECTION, layer);
		for (ToolEntry toolEntry : toolSection.getOwnedTools()) {
			if (toolEntry instanceof NodeCreationDescription
					&& ((NodeCreationDescription)toolEntry).getNodeMappings().contains(nodeMapping)) {
				return ((NodeCreationDescription)toolEntry);
			}
		}
		return null;
	}

	/**
	 * Verify if a {@link ContainerCreationDescription} for a given {@link ContainerMapping} is defined in a
	 * given {@link Layer}.
	 * 
	 * @param containerMapping
	 *            the given {@link ContainerMapping}
	 * @param layer
	 *            the given {@link Layer}.
	 * @return the found {@link ContainerCreationDescription}, else <code>null</code>
	 */
	public ContainerCreationDescription isContainerCreationToolExist(ContainerMapping containerMapping,
			Layer layer) {

		ToolSection toolSection = getToolSection(DEFAULT_TOOLS_SECTION, layer);
		for (ToolEntry toolEntry : toolSection.getOwnedTools()) {
			if (toolEntry instanceof ContainerCreationDescription
					&& ((ContainerCreationDescription)toolEntry).getContainerMappings().contains(
							containerMapping)) {
				return ((ContainerCreationDescription)toolEntry);
			}
		}
		return null;
	}

	/**
	 * Find the Uml2 {@link ContainerMapping} equivalence of a given {@link EObject}.
	 * 
	 * @param eObject
	 *            the given {@link EObject}
	 * @return the found {@link ContainerMapping}
	 */
	public ContainerMapping getUml2ContainerMappingEquivalence(EObject eObject) {
		String uml2MappingEquivalence = dslEAnnotation.getUml2MappingEquivalence(eObject);

		ContainerMapping containerMappingForStyle = getContainerMapping(umlDesignerGroupVPDesign,
				uml2MappingEquivalence);
		if (containerMappingForStyle == null) {
			containerMappingForStyle = defaultContainerMappingForStyle;
		}
		return containerMappingForStyle;
	}

	/**
	 * Find a {@link ContainerMapping} in a given {@link Viewpoint} defined for a given Domain Class Name.
	 * 
	 * @param viewpoint
	 *            given {@link Viewpoint}
	 * @param containerMappingDomainClassName
	 *            the given Domain Class Name
	 * @return the found {@link ContainerMapping}
	 */
	public static ContainerMapping getContainerMapping(Viewpoint viewpoint,
			String containerMappingDomainClassName) {
		for (RepresentationDescription representationDescription : viewpoint.getOwnedRepresentations()) {
			if (representationDescription instanceof DiagramDescription) {
				DiagramDescription diagram = (DiagramDescription)representationDescription;
				for (Layer layer : diagram.getAllLayers()) {
					for (ContainerMapping containerMapping : MappingTools.getAllContainerMappings(layer)) {
						if (containerMapping.getDomainClass()
								.equals("uml." + containerMappingDomainClassName)) {
							return containerMapping;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Find the Uml2 {@link NodeMapping} equivalence of a given {@link EObject}.
	 * 
	 * @param eObject
	 *            the given {@link EObject}
	 * @return the found {@link NodeMapping}
	 */
	public NodeMapping getUml2NodeMappingEquivalence(EObject eObject) {

		String uml2MappingEquivalence = dslEAnnotation.getUml2MappingEquivalence(eObject);

		NodeMapping nodeMappingForStyle = getNodeMapping(umlDesignerGroupVPDesign, uml2MappingEquivalence);
		if (nodeMappingForStyle == null) {
			nodeMappingForStyle = defaultNodeMappingForStyle;
		}
		return nodeMappingForStyle;
	}

	/**
	 * Find the Uml2 {@link NodeMapping} equivalence of a given {@link EObject}.
	 * 
	 * @param eObject
	 *            the given {@link EObject}
	 * @return the found {@link NodeMapping}
	 */
	public NodeMapping getUml2BorderedNodeMappingEquivalence(EObject eObject) {

		String uml2MappingEquivalence = dslEAnnotation.getUml2MappingEquivalence(eObject);

		NodeMapping nodeMappingForStyle = getBorderedNodeMapping(umlDesignerGroupVPDesign,
				uml2MappingEquivalence);
		if (nodeMappingForStyle == null) {
			nodeMappingForStyle = defaultNodeMappingForStyle;
		}
		return nodeMappingForStyle;
	}

	/**
	 * Find a {@link NodeMapping} in a given {@link Viewpoint} defined for a given Domain Class Name.
	 * 
	 * @param viewpoint
	 *            given {@link Viewpoint}
	 * @param nodeMappingDomainClassName
	 *            the given Domain Class Name
	 * @return the found {@link NodeMapping}
	 */
	public static NodeMapping getNodeMapping(Viewpoint viewpoint, String nodeMappingDomainClassName) {
		for (RepresentationDescription representationDescription : viewpoint.getOwnedRepresentations()) {
			if (representationDescription instanceof DiagramDescription) {
				DiagramDescription diagram = (DiagramDescription)representationDescription;
				for (Layer layer : diagram.getAllLayers()) {
					for (NodeMapping containerMapping : MappingTools.getAllNodeMappings(layer)) {
						if (containerMapping.getDomainClass().equals("uml." + nodeMappingDomainClassName)) {
							return containerMapping;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Find a {@link NodeMapping} in a given {@link Viewpoint} defined for a given Domain Class Name.
	 * 
	 * @param viewpoint
	 *            given {@link Viewpoint}
	 * @param nodeMappingDomainClassName
	 *            the given Domain Class Name
	 * @return the found {@link NodeMapping}
	 */
	public static NodeMapping getBorderedNodeMapping(Viewpoint viewpoint, String nodeMappingDomainClassName) {
		for (RepresentationDescription representationDescription : viewpoint.getOwnedRepresentations()) {
			if (representationDescription instanceof DiagramDescription) {
				DiagramDescription diagram = (DiagramDescription)representationDescription;
				for (Layer layer : diagram.getAllLayers()) {
					for (NodeMapping containerMapping : MappingTools.getAllBorderedNodeMappings(layer)) {
						if (containerMapping.getDomainClass().equals("uml." + nodeMappingDomainClassName)) {
							return containerMapping;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Find the {@link AbstractNodeMapping}s defined for a given list of {@link EClass} in a given
	 * {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eClassList
	 *            the given list of {@link EClass}
	 * @return list of found {@link AbstractNodeMapping}s
	 */
	public static EList<AbstractNodeMapping> getAbstractNodeMappings(Layer layer, EList<EClass> eClassList) {
		EList<AbstractNodeMapping> containerMappings = new BasicEList<AbstractNodeMapping>();

		for (EClass eclass : eClassList) {
			for (AbstractNodeMapping containerMapping : getAllAbstractNodeMappings(layer)) {

				if (containerMapping.getDomainClass().equals(eclass.getName())) {
					containerMappings.add(containerMapping);
				}
			}
		}
		return containerMappings;
	}

	/**
	 * Find the {@link ContainerMapping}s defined for a given list of {@link EClass} in a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eClassList
	 *            the given list of {@link EClass}
	 * @return list of found {@link ContainerMapping}s
	 */
	public static EList<ContainerMapping> getContainerMappings(Layer layer, EList<EClass> eClassList) {
		EList<ContainerMapping> containerMappings = new BasicEList<ContainerMapping>();

		for (EClass eclass : eClassList) {
			for (ContainerMapping containerMapping : getAllContainerMappings(layer)) {

				if (containerMapping.getDomainClass().equals(eclass.getName())) {
					containerMappings.add(containerMapping);
				}
			}
		}
		return containerMappings;
	}

	/**
	 * Returns direct and indirect {@link ContainerMapping}s of a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @return an {@link EList} of {@link ContainerMapping}
	 */
	public static EList<ContainerMapping> getAllContainerMappings(Layer layer) {
		EList<ContainerMapping> ownedMappings = new BasicEList<ContainerMapping>();
		for (ContainerMapping containerMapping : layer.getContainerMappings()) {
			ownedMappings.add(containerMapping);
			if (!containerMapping.getAllContainerMappings().isEmpty()) {
				ownedMappings.addAll(getAllContainerMappings(containerMapping));
			}
		}
		return ownedMappings;
	}

	/**
	 * Returns direct and indirect {@link ContainerMapping}s of a given {@link ContainerMapping}.
	 * 
	 * @param container
	 *            the given {@link ContainerMapping}
	 * @return an {@link EList} of {@link ContainerMapping}
	 */
	public static EList<ContainerMapping> getAllContainerMappings(ContainerMapping container) {
		EList<ContainerMapping> ownedMappings = new BasicEList<ContainerMapping>();

		for (ContainerMapping containerMapping : container.getAllContainerMappings()) {
			// this list is used to avoid the referenced (imported) mappings and handle the contained
			// mappings.
			List<ContainerMapping> subContainersOfContainerMapping = new ArrayList<ContainerMapping>();
			subContainersOfContainerMapping.addAll(containerMapping.getAllContainerMappings());
			subContainersOfContainerMapping.removeAll(containerMapping.getReusedContainerMappings());
			if (!containerMapping.equals(container)) {
				ownedMappings.add(containerMapping);
				if (!subContainersOfContainerMapping.isEmpty()) {
					ownedMappings.addAll(getAllContainerMappings(containerMapping));
				}
			}
		}
		return ownedMappings;
	}

	/**
	 * Returns direct and indirect {@link AbstractNodeMapping}s of a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @return an {@link EList} of {@link AbstractNodeMapping}
	 */
	public static EList<AbstractNodeMapping> getAllAbstractNodeMappings(Layer layer) {
		EList<AbstractNodeMapping> ownedMappings = new BasicEList<AbstractNodeMapping>();
		for (NodeMapping nodeMapping : layer.getNodeMappings()) {
			ownedMappings.add(nodeMapping);
			ownedMappings.addAll(getAllBorderedNodeMappings(nodeMapping));
		}
		for (ContainerMapping containerMapping : getAllContainerMappings(layer)) {
			ownedMappings.add(containerMapping);
			for (NodeMapping nodeMapping : containerMapping.getAllNodeMappings()) {
				ownedMappings.add(nodeMapping);
				ownedMappings.addAll(getAllBorderedNodeMappings(nodeMapping));
			}
		}
		return ownedMappings;
	}

	/**
	 * Returns direct and indirect BorderedNodeMappings of a given {@link NodeMapping}.
	 * 
	 * @param nodeMapping
	 *            the given {@link NodeMapping}
	 * @return an {@link EList} of BorderedNodeMappings
	 */
	public static EList<NodeMapping> getAllBorderedNodeMappings(NodeMapping nodeMapping) {
		EList<NodeMapping> ownedMappings = new BasicEList<NodeMapping>();
		for (NodeMapping borderedNodeMapping : nodeMapping.getAllBorderedNodeMappings()) {
			ownedMappings.add(borderedNodeMapping);
			if (!borderedNodeMapping.getAllBorderedNodeMappings().isEmpty()) {
				ownedMappings.addAll(getAllBorderedNodeMappings(borderedNodeMapping));
			}
		}
		return ownedMappings;
	}

	/**
	 * Returns direct and indirect {@link NodeMapping}s of a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @return an {@link EList} of {@link NodeMapping}
	 */
	public static EList<NodeMapping> getAllNodeMappings(Layer layer) {
		EList<NodeMapping> ownedMappings = new BasicEList<NodeMapping>();
		ownedMappings.addAll(layer.getNodeMappings());
		for (ContainerMapping containerMapping : getAllContainerMappings(layer)) {
			ownedMappings.addAll(containerMapping.getAllNodeMappings());
			ownedMappings.addAll(containerMapping.getAllBorderedNodeMappings());
		}
		return ownedMappings;
	}

	/**
	 * Returns direct and indirect {@link NodeMapping}s of a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @return an {@link EList} of {@link NodeMapping}
	 */
	public static EList<NodeMapping> getAllBorderedNodeMappings(Layer layer) {
		// TODO update this method do not find all possible bordered node, those of the node.
		EList<NodeMapping> ownedMappings = new BasicEList<NodeMapping>();
		ownedMappings.addAll(layer.getNodeMappings());
		for (ContainerMapping containerMapping : getAllContainerMappings(layer)) {
			ownedMappings.addAll(containerMapping.getAllBorderedNodeMappings());
		}
		return ownedMappings;
	}

	/**
	 * Find a {@link UserColor} among those of a given {@link UserColorsPalette}s using a given name.
	 * 
	 * @param userColorsPalettes
	 *            the {@link UserColorsPalette}
	 * @param containerMappingName
	 *            the given name
	 * @return the {@link UserColor} if found, otherwise <code>null<code>.
	 */
	public static UserColor getUserColor(EList<UserColorsPalette> userColorsPalettes,
			String colorDescriptionName) {
		for (UserColorsPalette userColorsPalette : userColorsPalettes) {
			for (UserColor userColor : userColorsPalette.getEntries()) {
				if (userColor.getName().equals(colorDescriptionName)) {
					return userColor;
				}
			}
		}
		return null;
	}

	/**
	 * Create a {@link BundledImageDescription} using the common information from a given
	 * {@link ContainerStyleDescription} and the color palette of a given {@link Group}.
	 * 
	 * @param containerStyle
	 *            the given {@link ContainerStyleDescription}
	 * @param group
	 *            the given{@link Group}
	 * @return a new {@link BundledImageDescription}
	 */
	public static BundledImageDescription getNodeStyle(ContainerStyleDescription containerStyle, Group group) {
		BundledImageDescription bundledImageDescription = StyleFactory.eINSTANCE
				.createBundledImageDescription();
		if (containerStyle != null) {
			bundledImageDescription.setShape(BundledImageShape.SQUARE_LITERAL);
			// Label
			bundledImageDescription.setLabelSize(containerStyle.getLabelSize());
			bundledImageDescription.setLabelFormat(containerStyle.getLabelFormat());
			bundledImageDescription.setLabelExpression("feature:name");
			bundledImageDescription.setLabelPosition(LabelPosition.NODE_LITERAL);
			// Color
			if (containerStyle instanceof FlatContainerStyleDescription) {
				ColorDescription colorDescription = ((FlatContainerStyleDescription)containerStyle)
						.getForegroundColor();
				String colorName = new String();
				if (colorDescription instanceof UserFixedColor) {
					colorName = ((UserFixedColor)colorDescription).getName();
				}
				if (colorDescription instanceof InterpolatedColor) {
					colorName = ((InterpolatedColor)colorDescription).getName();
				}
				if (colorDescription instanceof ComputedColor) {
					colorName = ((ComputedColor)colorDescription).getName();
				}
				UserColor localColor = getUserColor(group.getUserColorsPalettes(), colorName);
				((FlatContainerStyleDescription)containerStyle)
						.setForegroundColor((ColorDescription)localColor);

				bundledImageDescription.setColor((ColorDescription)localColor);

				bundledImageDescription
						.setSizeComputationExpression(((FlatContainerStyleDescription)containerStyle)
								.getWidthComputationExpression());
			}
			bundledImageDescription.setLabelColor(containerStyle.getLabelColor());
			bundledImageDescription.setBorderColor(containerStyle.getBorderColor());
			// Size
			bundledImageDescription.setBorderSizeComputationExpression(containerStyle
					.getBorderSizeComputationExpression());
			// ReSize
			bundledImageDescription.setResizeKind(ResizeKind.NSEW_LITERAL);
		}

		return bundledImageDescription;
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
	 * Find a {@link NodeMapping} among those of a given {@link Layer} using a given name.
	 * 
	 * @param layer
	 *            the {@link Layer}
	 * @param containerMappingName
	 *            the given name
	 * @return the {@link NodeMapping} if found, otherwise <code>null<code>.
	 */
	public NodeMapping getNodeMapping(Layer layer, String containerMappingName) {
		for (NodeMapping nodeMapping : layer.getNodeMappings()) {
			if (nodeMapping.getName().equals(containerMappingName)) {
				return nodeMapping;
			}
		}
		return null;
	}

	// ***************************** Tools section **********************************
	/**
	 * Find a {@link ToolSection} in a given {@link Layer} by a given name. If not found, a new one is
	 * created.
	 * 
	 * @param sectionName
	 *            the given name
	 * @param layer
	 *            the given {@link Layer}
	 * @return the found or the created {@link ToolSection}
	 */
	public static ToolSection createSectionTool(String sectionName, Layer layer) {
		ToolSection toolSection = (ToolSection)Tools.containsIdentifiedElement(sectionName,
				(EList<IdentifiedElement>)(EList<?>)layer.getToolSections());
		if (toolSection == null) {
			toolSection = ToolFactory.eINSTANCE.createToolSection();
			toolSection.setName(sectionName);
			layer.getToolSections().add(toolSection);
		}
		return toolSection;
	}

	/**
	 * Find a {@link ToolSection} with a given name in a given {@link Layer}
	 * 
	 * @param sectionName
	 *            the given name
	 * @param layer
	 *            the given {@link Layer}
	 * @return the found {@link ToolSection}, else null
	 */
	public static ToolSection getToolSection(String sectionName, Layer layer) {
		for (ToolSection toolSection : layer.getToolSections()) {
			if (toolSection.getName().equals(sectionName)) {
				return toolSection;
			}
		}
		return null;
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
		ToolSection toolSection = MappingTools.createSectionTool(DEFAULT_TOOLS_SECTION, layer);

		for (ContainerMapping containerMapping : MappingTools.getAllContainerMappings(layer)) {
			ContainerCreationDescription containerCreationDescription = isContainerCreationToolExist(
					containerMapping, layer);
			if (containerCreationDescription == null) {

				// Container creation tool
				containerCreationDescription = ToolFactory.eINSTANCE.createContainerCreationDescription();
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
				createInstance.setReferenceName(containerMapping.getSemanticCandidatesExpression().substring(
						8));
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
	}

	/**
	 * Create the creation tool for all direct and indirect {@link NodeMapping}s of a given {@link Layer} .
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 */
	public void createCreationToolsForNodes(Layer layer) {
		// Create the tools section
		ToolSection toolSection = createSectionTool(DEFAULT_TOOLS_SECTION, layer);

		for (NodeMapping nodeMapping : MappingTools.getAllNodeMappings(layer)) {
			NodeCreationDescription nodeCreationDescription = isNodeCreationToolExist(nodeMapping, layer);
			if (nodeCreationDescription == null) {
				// Container creation tool
				nodeCreationDescription = ToolFactory.eINSTANCE.createNodeCreationDescription();
				toolSection.getOwnedTools().add(nodeCreationDescription);
				nodeCreationDescription.setName(nodeMapping.getName() + "Creation");
				nodeCreationDescription.getNodeMappings().add(nodeMapping);

				// Change context operation to the container
				ChangeContext changeContext = org.eclipse.sirius.viewpoint.description.tool.ToolFactory.eINSTANCE
						.createChangeContext();
				changeContext.setBrowseExpression("var:container");
				nodeCreationDescription.getInitialOperation().setFirstModelOperations(changeContext);

				// create instance
				CreateInstance createInstance = org.eclipse.sirius.viewpoint.description.tool.ToolFactory.eINSTANCE
						.createCreateInstance();
				createInstance.setReferenceName(nodeMapping.getSemanticCandidatesExpression().substring(8));
				createInstance.setTypeName(nodeMapping.getDomainClass());
				changeContext.getSubModelOperations().add(createInstance);

				// set value for the name
				SetValue setValue = org.eclipse.sirius.viewpoint.description.tool.ToolFactory.eINSTANCE
						.createSetValue();
				setValue.setFeatureName(FEATURE_NAME);
				setValue.setValueExpression(nodeMapping.getDomainClass());
				createInstance.getSubModelOperations().add(setValue);
			}
		}
	}

	/**
	 * Create all possible relationship between the {@link AbstractNodeMapping} of a given {@link layer}.
	 * 
	 * @param layer
	 *            the given {@link layer}
	 */
	public void handleMappingsRelations(Layer layer) {
		EList<AbstractNodeMapping> abstractNodeMappings = getAllAbstractNodeMappings(layer);
		for (DiagramElementMapping diagramElementMapping : abstractNodeMappings) {
			if (diagramElementMapping instanceof AbstractNodeMapping) {
				AbstractNodeMapping abstractNodeMapping = (AbstractNodeMapping)diagramElementMapping;
				ENamedElement eNamedElement = Tools.contains(abstractNodeMapping.getDomainClass(),
						profileEcoreModel.eAllContents());
				if (eNamedElement != null && eNamedElement instanceof EClass) {
					EClass eClass = (EClass)eNamedElement;
					String mappingType = dslEAnnotation.getVSMMappingType(eClass);

					EList<EClass> eClassAndItsParents = new BasicEList<EClass>();
					eClassAndItsParents.add(eClass);
					eClassAndItsParents.addAll(eClass.getEAllSuperTypes());

					Map<EObject, Collection<Setting>> result = EcoreUtil.UsageCrossReferencer.findAll(
							eClassAndItsParents, profileEcoreModel);

					for (Entry<EObject, Collection<Setting>> entrySet : result.entrySet()) {
						entrySet.getKey();
						Collection<Setting> settings = entrySet.getValue();
						for (Setting setting : settings) {
							if (setting.getEObject() instanceof EReference) {
								EReference eReference = (EReference)setting.getEObject();
								ENamedElement referencer = ((ENamedElement)eReference.eContainer());

								for (AbstractNodeMapping abstractNodeMappingContainer : abstractNodeMappings) {

									EClass domainClass = (EClass)Tools.contains(
											abstractNodeMappingContainer.getDomainClass(),
											profileEcoreModel.eAllContents());

									if (domainClass != null
											&& (domainClass.equals(referencer) || domainClass
													.getEAllSuperTypes().contains(referencer))) {

										if (abstractNodeMapping.getSemanticCandidatesExpression().equals(
												"feature:" + eReference.getName())
												&& !abstractNodeMapping.equals(abstractNodeMappingContainer)) {

											if (eReference.isContainment()) {

												// *******************Container Mapping
												if (abstractNodeMappingContainer instanceof ContainerMapping) {
													if (abstractNodeMapping instanceof ContainerMapping) {
														if (abstractNodeMapping.eContainer() instanceof AbstractNodeMapping) {
															((ContainerMapping)abstractNodeMappingContainer)
																	.getReusedContainerMappings()
																	.add((ContainerMapping)abstractNodeMapping);
														} else {
															((ContainerMapping)abstractNodeMappingContainer)
																	.getSubContainerMappings()
																	.add((ContainerMapping)abstractNodeMapping);
														}
													}
													// *******************Node Mapping
													if (abstractNodeMapping instanceof NodeMapping) {
														if (abstractNodeMapping.eContainer() instanceof AbstractNodeMapping) {
															((ContainerMapping)abstractNodeMappingContainer)
																	.getReusedNodeMappings().add(
																			(NodeMapping)abstractNodeMapping);
														} else {
															((ContainerMapping)abstractNodeMappingContainer)
																	.getSubNodeMappings().add(
																			(NodeMapping)abstractNodeMapping);
														}
													}
												}
												// *******************BORDERED_NODE Mapping
											} else if (mappingType.equals(DslEAnnotation.BORDERED_NODE)
													&& abstractNodeMapping instanceof NodeMapping) {
												if (abstractNodeMapping.eContainer() instanceof AbstractNodeMapping) {
													abstractNodeMappingContainer
															.getReusedBorderedNodeMappings().add(
																	(NodeMapping)abstractNodeMapping);
												} else {
													abstractNodeMappingContainer.getBorderedNodeMappings()
															.add((NodeMapping)abstractNodeMapping);
												}

											}
										}

									}

								}
							}
						}
					}
				}
			}
		}
	}
}
