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
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.diagram.BundledImageShape;
import org.eclipse.sirius.diagram.LabelPosition;
import org.eclipse.sirius.diagram.ResizeKind;
import org.eclipse.sirius.diagram.description.AbstractNodeMapping;
import org.eclipse.sirius.diagram.description.ContainerMapping;
import org.eclipse.sirius.diagram.description.DescriptionFactory;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.diagram.description.EdgeMapping;
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
	public MappingTools(EPackage profileEcoreModel, Group vsmGroup_p, Group umlDesignerGroup_p) {
		vsmGroup = vsmGroup_p;
		umlDesignerGroup = umlDesignerGroup_p;

		umlDesignerGroupVPDesign = getViewpoint(umlDesignerGroup, UML_DESIGNER_VIEWPOINT_DESIGN);
		DiagramDescription umlDesignerClassDiagram = getDiagram(umlDesignerGroupVPDesign,
				UML_DESIGNER_CLASS_DIAGRAM);
		Layer umlDesignerClassLayer = getLayer(umlDesignerClassDiagram,
				UML_DESIGNER_CLASS_DIAGRAM_CLASS_LAYER);
		defaultContainerMappingForStyle = getPackageMapping(umlDesignerClassLayer,
				"CD_Package");
		defaultNodeMappingForStyle = getNodeMapping(umlDesignerClassLayer, "CD_Class");


		dslEAnnotation = new DslEAnnotation(profileEcoreModel);

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
	public AbstractNodeMapping createMappings(Layer layer, EObject eObject, boolean setContainer) {

		AbstractNodeMapping createdMapping = null;

		String mappingType = dslEAnnotation.getVSMMappingType(eObject);
		if (mappingType.equals(DslEAnnotation.CONTAINER)) {
			// ***********************************************
			// To find the containerMapping to reuse its style.
			ContainerMapping containerMappingForStyle = getUml2ContainerMappingEquivalence(eObject);
			// ***********************************************
			ContainerMapping containerMapping = MappingTools.createContainer(layer, eObject,
					containerMappingForStyle.getStyle(), setContainer);
			createdMapping = containerMapping;
		}

		if (mappingType.equals(DslEAnnotation.CONTAINMENT_EDGE) && eObject instanceof EReference) {
			EList<AbstractNodeMapping> containedContainerMappings = createContainedContainer(layer,
					(EReference)eObject);
			createdMapping = containedContainerMappings.get(0);

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
				createdMapping = nodeMapping;

			} else if (originalStyle.equals(DslEAnnotation.CONTAINER)) {

				// ***********************************************
				// To find the containerMapping to reuse its style.
				ContainerMapping containerMappingForStyle = getUml2ContainerMappingEquivalence(eObject);
				// ***********************************************
				NodeMapping nodeMapping = MappingTools.createNode(layer, eObject,
						MappingTools.getNodeStyle(containerMappingForStyle.getStyle(), vsmGroup),
						setContainer);
				createdMapping = nodeMapping;
			}


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
			layer.getEdgeMappings().add(edgeMapping);
		}
		// if (mappingType.equals(DslEAnnotation.RELATION_BASED_EDGE)) {
		// EdgeMapping edgeMapping = DescriptionFactory.eINSTANCE.createEdgeMapping();
		// edgeMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
		// defaultLayer.getEdgeMappings().add(edgeMapping);
		// }
		return createdMapping;
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
	public EList<AbstractNodeMapping> createContainedContainer(Layer layer, EReference eReference) {
		// The created mappings for this EReference.
		EList<AbstractNodeMapping> createdMappings = new BasicEList<AbstractNodeMapping>();
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
				AbstractNodeMapping createdMapping = createMappings(layer, eClass, false);
				createdMapping.setName("Sub" + createdMapping.getName());
				createdMapping.setSemanticCandidatesExpression("feature:" + eReference.getName());
				createdMappings.add(createdMapping);

				// code for the recursive mapping
				// verify if the element is self referenced by this reference, if yes:
				// reference all created Mappings in this new Mapping
				if (eClass.equals(referenceContainer)
						|| eClass.getEAllSuperTypes().contains(referenceContainer)) {
					recursiveMappings.add(createdMapping);
				}

				// add the new created mapping to possible container Mapping
				EList<EClass> allSubTypes = Tools.getEAllSubTypes(referenceContainer);
				allSubTypes.add(referenceContainer);
				EList<ContainerMapping> possibleContainerMappings = getContainerMappings(layer, allSubTypes);
				for (ContainerMapping possibleContainer : possibleContainerMappings) {
					if (createdMapping.eContainer() == null) {
						if (createdMapping instanceof ContainerMapping) {
							possibleContainer.getSubContainerMappings().add((ContainerMapping)createdMapping);
						} else if (createdMapping instanceof NodeMapping) {
							possibleContainer.getSubNodeMappings().add((NodeMapping)createdMapping);
						}
					} else {
						if (createdMapping instanceof ContainerMapping) {
							possibleContainer.getReusedContainerMappings().add(
									(ContainerMapping)createdMapping);
						} else if (createdMapping instanceof NodeMapping) {
							possibleContainer.getReusedNodeMappings().add((NodeMapping)createdMapping);
						}
					}
				}
			}
		}

		// code for the recursive mapping
		for (AbstractNodeMapping recursiveMapping : recursiveMappings) {
			if (recursiveMapping instanceof ContainerMapping) {
				for (AbstractNodeMapping abstractNodeMapping : createdMappings) {
					if (abstractNodeMapping instanceof ContainerMapping) {
						((ContainerMapping)recursiveMapping).getReusedContainerMappings().add(
								(ContainerMapping)abstractNodeMapping);
					} else if (abstractNodeMapping instanceof NodeMapping) {

						((ContainerMapping)recursiveMapping).getReusedNodeMappings().add(
								(NodeMapping)abstractNodeMapping);
					}
				}
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
	public static NodeMapping createNode(Layer layer, EObject eObject,
 NodeStyleDescription nodeStyleToUse,
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
			ColorDescription colorDescription = ((FlatContainerStyleDescription)newNodeMapping
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
						if (containerMapping.getDomainClass()
.equals("uml." + nodeMappingDomainClassName)) {
							return containerMapping;
						}
					}
				}
			}
		}
		return null;
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
			for (ContainerMapping containerMapping : layer.getContainerMappings()) {

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
	 * Create the creation tool for all direct and indirect {@link NodeMapping}s of a given {@link Layer} .
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 */
	public void createCreationToolsForNodes(Layer layer) {
		// Create the tools section
		ToolSection toolSection = createSectionTool(DEFAULT_TOOLS_SECTION, layer);

		for (NodeMapping nodeMapping : MappingTools.getAllNodeMappings(layer)) {

			// Container creation tool
			NodeCreationDescription nodeCreationDescription = ToolFactory.eINSTANCE
					.createNodeCreationDescription();
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
