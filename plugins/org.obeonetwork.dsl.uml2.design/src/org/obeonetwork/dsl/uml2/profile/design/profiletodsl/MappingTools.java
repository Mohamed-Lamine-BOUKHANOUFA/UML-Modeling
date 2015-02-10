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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.diagram.description.ContainerMapping;
import org.eclipse.sirius.diagram.description.DescriptionFactory;
import org.eclipse.sirius.diagram.description.Layer;
import org.eclipse.sirius.diagram.description.style.FlatContainerStyleDescription;
import org.eclipse.sirius.viewpoint.description.ColorDescription;
import org.eclipse.sirius.viewpoint.description.ComputedColor;
import org.eclipse.sirius.viewpoint.description.Group;
import org.eclipse.sirius.viewpoint.description.InterpolatedColor;
import org.eclipse.sirius.viewpoint.description.UserColor;
import org.eclipse.sirius.viewpoint.description.UserColorsPalette;
import org.eclipse.sirius.viewpoint.description.UserFixedColor;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a>
 */
public class MappingTools {

	/**
	 * Constructor.
	 *
	 */
	public MappingTools() {
	}

	/**
	 * Create an new {@link ContainerMapping} for a given {@link EObject} using the style of a given
	 * {@link ContainerMapping}. Add the new {@link ContainerMapping} to a given {@link Layer}.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eObject
	 *            the given {@link EObject}
	 * @param umlDesignerPackageMapping
	 *            the given {@link ContainerMapping}.
	 * @param setTheContainer
	 *            if <code>true</code> set the container of the new created {@link ContainerMapping}
	 * @return the new created {@link ContainerMapping}.
	 */
	public static ContainerMapping createContainer(Layer layer, EObject eObject,
			ContainerMapping umlDesignerPackageMapping, boolean setTheContainer) {
		// Container creation
		ContainerMapping newContainerMapping = DescriptionFactory.eINSTANCE.createContainerMapping();
		newContainerMapping.setName(((ENamedElement)eObject).getName() + DslEAnnotation.MAPPING);
		newContainerMapping.setDomainClass(((ENamedElement)eObject).getName());
		if (setTheContainer) {
			layer.getContainerMappings().add(newContainerMapping);
		}
		// Copy the style from the package of UML Designer
		newContainerMapping.setStyle(EcoreUtil.copy(umlDesignerPackageMapping.getStyle()));
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
	 * Create an new {@link ContainerMapping} for the type of a given {@link EReference} using the style of a
	 * given {@link ContainerMapping}. Add the new {@link ContainerMapping} to the {@link ContainerMapping} of
	 * the container of the {@link EReference} and to it's hierarchy.
	 * 
	 * @param layer
	 *            the given {@link Layer}
	 * @param eReference
	 *            the given {@link EReference}
	 * @param umlDesignerPackageMapping
	 *            the given {@link ContainerMapping}.
	 * @param setTheContainer
	 *            if <code>true</code> set the container of the new created {@link ContainerMapping}
	 * @return the new created {@link ContainerMapping}.
	 */
	public static EList<ContainerMapping> createContainedContainer(Layer layer, EReference eReference,
			ContainerMapping umlDesignerPackageMapping) {
		EList<ContainerMapping> createdMapping = new BasicEList<ContainerMapping>();
		EClass referenceType = eReference.getEReferenceType();
		EClass referenceContainer = (EClass)eReference.eContainer();

		// Container creation
		ContainerMapping newContainerMapping = createContainer(layer, eReference, umlDesignerPackageMapping,
				false);
		newContainerMapping.setName("Sub" + (referenceType).getName() + DslEAnnotation.MAPPING);
		newContainerMapping.setDomainClass(referenceType.getName());
		createdMapping.add(newContainerMapping);
		// ****************************
		// ----------------------------- code for the recursive mapping
		// verify if the element is self referenced by this reference, if yes:
		// 1-Exclude this class from the Semantic candidate expression of the mapping
		// 2-create a new mapping for this element
		ContainerMapping newSubContainerMapping = null;
		if (referenceContainer.equals(referenceType)
				|| referenceContainer.getEAllSuperTypes().contains(referenceType)) {
			newContainerMapping.setSemanticCandidatesExpression("[self." + eReference.getName()
					+ "->select(not oclIsKindOf(" + referenceContainer.getName() + "))/]");

			// Container creation
			newSubContainerMapping = DescriptionFactory.eINSTANCE.createContainerMapping();
			newSubContainerMapping.setName("Sub" + (referenceContainer).getName() + DslEAnnotation.MAPPING);
			newSubContainerMapping.setDomainClass(referenceContainer.getName());
			// set the style
			newSubContainerMapping.setStyle(EcoreUtil.copy(newContainerMapping.getStyle()));
			// Set the Semantic candidate expression
			newSubContainerMapping.setSemanticCandidatesExpression("feature:" + eReference.getName());
			newSubContainerMapping.getReusedContainerMappings().add(newSubContainerMapping);
			newSubContainerMapping.getReusedContainerMappings().add(newContainerMapping);
			createdMapping.add(newSubContainerMapping);
		} else {
			newContainerMapping.getReusedContainerMappings().add(newContainerMapping);
		}
		// ---------------------------------------------------------------
		EList<EClass> allSubTypes = Tools.getEAllSubTypes(referenceContainer);
		allSubTypes.add(referenceContainer);
		EList<ContainerMapping> possibleContainerMappings = getMapping(layer, allSubTypes);
		for (ContainerMapping possibleContainer : possibleContainerMappings) {
			if (newContainerMapping.eContainer() == null) {
				possibleContainer.getSubContainerMappings().add(newContainerMapping);
				if (newSubContainerMapping != null) {
					possibleContainer.getSubContainerMappings().add(newSubContainerMapping);
				}

			} else {
				possibleContainer.getReusedContainerMappings().add(newContainerMapping);
				if (newSubContainerMapping != null) {
					possibleContainer.getReusedContainerMappings().add(newSubContainerMapping);
				}

			}
		}
		// ****************************
		return createdMapping;
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
	public static EList<ContainerMapping> getMapping(Layer layer, EList<EClass> eClassList) {
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
}
