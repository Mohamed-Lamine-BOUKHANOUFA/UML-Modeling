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

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * This class provides tools for the annotation of the DSL ecore.
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class DslEAnnotation {
	
	protected EPackage ecoreModel;
	
	final public static String MAPPING = "_Mapping";

	final public static String RELATION_BASED_EDGE = "RelationBasedEdge";

	final public static String ELEMENT_BASED_EDGE = "ElementBasedEdge";

	final public static String NODE = "Node";

	final public static String BORDERED_NODE = "BorderedNode";

	final public static String CONTAINER = "Container";

	final protected List<String> MAPPINGS = Arrays.asList(CONTAINER, NODE, BORDERED_NODE, ELEMENT_BASED_EDGE,
			RELATION_BASED_EDGE);

	final protected String ANNOTATION_UML_KEY_DOCUMENTATION = "documentation";

	final protected String ANNOTATION_DSL_SOURCE_DOCUMENTATION = "Documentation";

	final protected String ANNOTATION_DSL_SOURCE_DOCUMENTATION_KEY_DESCRIPTION = "Description";

	final protected String ANNOTATION_DSL_SOURCE_UML2MAPPING = "UML2Mapping";

	final protected String ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_UMLSYSML_EQUIVALENCES = "UML/SysML semantic equivalences";

	final protected String ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_ORIGINALDESCRIPTION = "Original Description";

	final protected String ANNOTATION_DSL_SOURCE_DSL_FACTORY = "DSLFactory";

	final protected String ANNOTATION_DSL_SOURCE_DSL_FACTOR_KEY_SELECTED = "Selected";

	final protected String ANNOTATION_DSL_SOURCE_VSM_MAPPING = "VSMMapping";

	final protected String ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_TYPE = "VSM Mapping Type";

	final protected String ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_NAME = "VSM Mapping Name";

	final protected String ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_STYLE = "VSM Mapping Style";

	final protected String ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_SELECTED = "Selected";

	final protected String ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_CONTAINER_OF_PORT = "Container of Port";

	final protected String ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_STYLE = "VSM Mapping Style";

	final protected String ANNOTATION_UML_SOURCE_GENMODEL = "http://www.eclipse.org/emf/2002/GenModel";

	/**
	 * @return the mAPPINGS
	 */
	public List<String> getMAPPINGS() {
		return MAPPINGS;
	}

	/**
	 * Constructor.
	 *
	 */
	public DslEAnnotation(EPackage ecoreModel_p) {
		ecoreModel = ecoreModel_p;
	}
	
	/**
	 * Add the DSL annotation to given {@link EStructuralFeature}s.
	 * 
	 * @param eStructuralFeatures
	 *            the {@link EStructuralFeature}s
	 */
	public void addAnnotationToEStructuralFeatures(EList<EStructuralFeature> eStructuralFeatures) {
		for (EStructuralFeature eStructuralFeature : eStructuralFeatures) {

			// DSL factory annotation
			EAnnotation eAnnotationDslFactory = eStructuralFeature.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_DSL_FACTORY);
			if (eAnnotationDslFactory == null) {
				eAnnotationDslFactory = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotationDslFactory.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_DSL_FACTORY);
				eStructuralFeature.getEAnnotations().add(eAnnotationDslFactory);
				eAnnotationDslFactory.getDetails()
.put(ANNOTATION_DSL_SOURCE_DSL_FACTOR_KEY_SELECTED, "true");
			}

			// VSM Mapping annotation
			if (eStructuralFeature instanceof EReference) {
				String vsmMapping = RELATION_BASED_EDGE;

			EAnnotation eAnnotationVSMMapping = eStructuralFeature.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			if (eAnnotationVSMMapping == null) {
				eAnnotationVSMMapping = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotationVSMMapping.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_VSM_MAPPING);
				eStructuralFeature.getEAnnotations().add(eAnnotationVSMMapping);
					eAnnotationVSMMapping.getDetails().put(
							ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_TYPE,
						vsmMapping);

					eAnnotationVSMMapping.getDetails().put(
							ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_NAME,
							eStructuralFeature.getName() + MAPPING);

					eAnnotationVSMMapping.getDetails().put(
							ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_STYLE, "Style");
					eAnnotationVSMMapping.getDetails().put(
							ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_SELECTED, "false");
				}
			}
		}
	}

	/**
	 * Add the DSL annotation to a given {@link EClass} inspiring from a given UML {@link EClass}.
	 * 
	 * @param eClass
	 *            the given {@link EClass}
	 * @param eClassUml
	 *            the given UML {@link EClass}
	 */
	public void addAnnotation(EClass eClass, EClass eClassUml) {

		// Documentation annotation
		EAnnotation eAnnotationDocumentation = eClass.getEAnnotation(ecoreModel.getNsURI()
				+ ANNOTATION_DSL_SOURCE_DOCUMENTATION);
		if (eAnnotationDocumentation == null) {
			eAnnotationDocumentation = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotationDocumentation.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_DOCUMENTATION);
			eClass.getEAnnotations().add(eAnnotationDocumentation);
			eAnnotationDocumentation.getDetails().put(ANNOTATION_DSL_SOURCE_DOCUMENTATION_KEY_DESCRIPTION,
					"todo");
		}

		// UML2Mapping annotation
		EAnnotation eAnnotationMapping = eClass.getEAnnotation(ecoreModel.getNsURI()
				+ ANNOTATION_DSL_SOURCE_UML2MAPPING);
		if (eAnnotationMapping == null) {
			eAnnotationMapping = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotationMapping.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_UML2MAPPING);
			eClass.getEAnnotations().add(eAnnotationMapping);
		}
		String uml2MappingEquivalences = eAnnotationMapping.getDetails().get(
				ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_UMLSYSML_EQUIVALENCES);
		if (uml2MappingEquivalences != null) {
			eAnnotationMapping.getDetails().put(ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_UMLSYSML_EQUIVALENCES,
					uml2MappingEquivalences + ", " + eClassUml.getName());
		} else {
			eAnnotationMapping.getDetails().put(ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_UMLSYSML_EQUIVALENCES,
					eClassUml.getName());
		}

		EAnnotation eClassUmlGenModelAnnotation = eClassUml.getEAnnotation(ANNOTATION_UML_SOURCE_GENMODEL);

		if (eClassUmlGenModelAnnotation != null) {
			String eClassUmlGenModelDocumentationKey = eClassUmlGenModelAnnotation.getDetails().get(
					ANNOTATION_UML_KEY_DOCUMENTATION);
			if (eClassUmlGenModelDocumentationKey != null) {
				String umlMappingOriginalDescription = eAnnotationMapping.getDetails().get(
						ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_ORIGINALDESCRIPTION);
				if (umlMappingOriginalDescription == null) {
					eAnnotationMapping.getDetails().put(
							ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_ORIGINALDESCRIPTION,
							eClassUmlGenModelDocumentationKey);
				} else {
					eAnnotationMapping.getDetails().put(
							ANNOTATION_DSL_SOURCE_UML2MAPPING_KEY_ORIGINALDESCRIPTION,
							umlMappingOriginalDescription + "\n" + eClassUmlGenModelDocumentationKey);
				}
			}
		}

		// DSL factory annotation

		EAnnotation eAnnotationDslFactory = eClass.getEAnnotation(ecoreModel.getNsURI()
				+ ANNOTATION_DSL_SOURCE_DSL_FACTORY);
		if (eAnnotationDslFactory == null) {
			eAnnotationDslFactory = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotationDslFactory.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_DSL_FACTORY);
			eClass.getEAnnotations().add(eAnnotationDslFactory);
			eAnnotationDslFactory.getDetails().put(ANNOTATION_DSL_SOURCE_DSL_FACTOR_KEY_SELECTED, "true");
		}
		
		// VSM Mapping annotation

		String vsmMapping = identifyTheVSMMapping(eClassUml);

		EAnnotation eAnnotationVSMMapping = eClass.getEAnnotation(ecoreModel.getNsURI()
				+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
		if (eAnnotationVSMMapping == null) {
			eAnnotationVSMMapping = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotationVSMMapping.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			eClass.getEAnnotations().add(eAnnotationVSMMapping);
			eAnnotationVSMMapping.getDetails().put(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_TYPE,
					vsmMapping);

			eAnnotationVSMMapping.getDetails().put(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_NAME,
					eClass.getName() + MAPPING);

			eAnnotationVSMMapping.getDetails().put(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_STYLE,
					"Style");

			eAnnotationVSMMapping.getDetails().put(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_SELECTED,
					"false");

			if (Tools.contains(UMLPackage.Literals.ENCAPSULATED_CLASSIFIER.getName(),
					(EList<ENamedElement>)(EList<?>)eClassUml.getEAllSuperTypes()) != null) {
				eAnnotationVSMMapping.getDetails().put(
						ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_CONTAINER_OF_PORT, "true");
			}
		}

	}

	/**
	 * Identify the potential Sirius mapping for a given {@link EClass} using the UML graphical
	 * representation.
	 * 
	 * @param eClass
	 *            the given {@link EClass}
	 * @return the mapping.
	 */
	public String identifyTheVSMMapping(EClass eClass) {
		String defaultMapping = CONTAINER;
		EList<EClass> superTypes = new BasicEList<EClass>();
		superTypes.add(eClass);
		superTypes.addAll(eClass.getEAllSuperTypes());
		for (EClass eSuperClass : superTypes) {

			if (eSuperClass.getName().equals(UMLPackage.Literals.TEMPLATE_PARAMETER.getName())
					|| eSuperClass.getName().equals(UMLPackage.Literals.PORT.getName())) {
				return BORDERED_NODE;
			}
			if (eSuperClass.getName().equals(UMLPackage.Literals.STRUCTURAL_FEATURE.getName())) {
				return NODE;
			}

			if (eSuperClass.getName().equals(UMLPackage.Literals.RELATIONSHIP.getName())) {
				return ELEMENT_BASED_EDGE;
			}
			if (eSuperClass.getName().equals(UMLPackage.Literals.PACKAGE.getName())
					|| eSuperClass.getName().equals(UMLPackage.Literals.STRUCTURED_CLASSIFIER.getName())) {
				return CONTAINER;
			}
		}
		return defaultMapping;
	}


	/**
	 * @param eObject
	 */
	public void verifyEAnnotation(EObject eObject) {
		if (eObject instanceof EClass && !((EClass)eObject).getESuperTypes().isEmpty()) {
			addAnnotation((EClass)eObject, ((EClass)eObject).getESuperTypes().get(0));
		}

	}

	/**
	 * Set the key value of the annotation DSL Factory of a given {@link Object} with a given value.
	 * 
	 * @param object
	 *            the {@link Object}
	 * @param value
	 *            the value.
	 */
	public void setDslFactoryeAnnotation(Object object, String value) {

		// DSL factory annotation

		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;

			EAnnotation eAnnotationDslFactory = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_DSL_FACTORY);
			if (eAnnotationDslFactory == null) {
				eAnnotationDslFactory = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotationDslFactory.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_DSL_FACTORY);
				eModelElement.getEAnnotations().add(eAnnotationDslFactory);
			}
			eAnnotationDslFactory.getDetails().put(ANNOTATION_DSL_SOURCE_DSL_FACTOR_KEY_SELECTED, value);
		}
	}

	/**
	 * Set to <code>true</code> the annotation DSL Factory of the given {@link Object}s.
	 * 
	 * @param objects
	 *            the {@link Object}s
	 */
	public void setDslFactoryeAnnotationToTrue(Object[] objects) {

		// DSL factory annotation

		for (Object object : objects) {
			setDslFactoryeAnnotation(object, "true");
		}

	}

	/**
	 * Set to <code>false</code> the annotation DSL Factory of the given {@link Object}s.
	 * 
	 * @param objects
	 *            the {@link Object}s
	 */
	public void setDslFactoryeAnnotationToFalse(Object objects) {

		// DSL factory annotation

		if (objects instanceof List) {

			for (Object object : (List<?>)objects) {
				setDslFactoryeAnnotation(object, "false");
			}
		}

	}

	/**
	 * Verify if the key 'Mapping Selected' of the {@link EAnnotation} 'VSM Mapping' is <code>true</code> or
	 * not for a given {@link Object}.
	 * 
	 * @param object
	 *            the {@link Object}.
	 * @return <code>true</code> if the key 'Selected' is <code>true<code>, otherwise <code>false</code>
	 */
	public boolean isSelectedInVsmMapping(Object object) {

		// DSL factory annotation
		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;
			EAnnotation eAnnotationDslFactory = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			if (eAnnotationDslFactory != null
					&& eAnnotationDslFactory.getDetails().get(
							ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_SELECTED) != null) {
				if (eAnnotationDslFactory.getDetails()
						.get(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_SELECTED).equalsIgnoreCase("true")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Verify if the key 'Selected' of the {@link EAnnotation} 'DSL Factory' is <code>false</code> or not for
	 * a given {@link Object}.
	 * 
	 * @param object
	 *            the {@link Object}.
	 * @return <code>false</code> if the key 'Selected' is <code>false<code>, otherwise <code>true</code>
	 */
	public boolean isSelectedInDslFactory(Object object) {

		// DSL factory annotation
		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;
			EAnnotation eAnnotationDslFactory = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_DSL_FACTORY);
			if (eAnnotationDslFactory != null
					&& eAnnotationDslFactory.getDetails().get(ANNOTATION_DSL_SOURCE_DSL_FACTOR_KEY_SELECTED) != null) {
				if (eAnnotationDslFactory.getDetails().get(ANNOTATION_DSL_SOURCE_DSL_FACTOR_KEY_SELECTED)
						.equalsIgnoreCase("false")) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Get the mapping type of the {@link Object}.
	 * 
	 * @param object
	 *            the {@link Object}
	 * @return the mapping type
	 */
	public String getVSMMappingType(Object object) {
		String vsmMapping = new String();

		// VSM Mapping annotation

		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;
			EAnnotation eAnnotationVSMMapping = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			if (eAnnotationVSMMapping != null
					&& eAnnotationVSMMapping.getDetails().get(
							ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_TYPE) != null) {
				return eAnnotationVSMMapping.getDetails().get(
						ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_TYPE);
			}
		}
		return vsmMapping;
	}

	/**
	 * Set the mapping type of the {@link Object}.
	 * 
	 * @param object
	 *            the {@link Object}
	 * @param value
	 *            the value
	 */
	public void setVSMMappingType(Object object, String value) {

		// VSM Mapping annotation
		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;
			EAnnotation eAnnotationVSMMapping = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			if (eAnnotationVSMMapping == null) {
				eAnnotationVSMMapping = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotationVSMMapping.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_VSM_MAPPING);
				eModelElement.getEAnnotations().add(eAnnotationVSMMapping);
			}
			eAnnotationVSMMapping.getDetails().put(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_TYPE, value);
		}
	}

	/**
	 * Get the mapping name of the {@link Object}.
	 * 
	 * @param object
	 *            the {@link Object}
	 * @return the mapping name
	 */
	public String getVSMMappingName(Object object) {
		String vsmMapping = new String();

		// VSM Mapping annotation

		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;
			EAnnotation eAnnotationVSMMapping = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			if (eAnnotationVSMMapping != null
					&& eAnnotationVSMMapping.getDetails().get(
							ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_NAME) != null) {
				return eAnnotationVSMMapping.getDetails().get(
						ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_NAME);
			}
		}
		return vsmMapping;
	}

	/**
	 * Set the mapping name of the {@link Object}.
	 * 
	 * @param object
	 *            the {@link Object}
	 * @param value
	 *            the name
	 */
	public void setVSMMappingName(Object object, String value) {

		// VSM Mapping annotation

		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;
			EAnnotation eAnnotationVSMMapping = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			if (eAnnotationVSMMapping == null) {
				eAnnotationVSMMapping = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotationVSMMapping.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_VSM_MAPPING);
				eModelElement.getEAnnotations().add(eAnnotationVSMMapping);
			}
			eAnnotationVSMMapping.getDetails().put(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_NAME, value);
		}
	}

	/**
	 * Set the key value of the annotation VSM_MAPPING_KEY_MAPPING_SELECTED of a given {@link Object} with a
	 * given value.
	 * 
	 * @param object
	 *            the {@link Object}
	 * @param value
	 *            the value.
	 */
	public void setVSMMappingSelection(Object object, String value) {

		// DSL factory annotation

		if (object instanceof EModelElement) {
			EModelElement eModelElement = (EModelElement)object;

			EAnnotation eAnnotationDslFactory = eModelElement.getEAnnotation(ecoreModel.getNsURI()
					+ ANNOTATION_DSL_SOURCE_VSM_MAPPING);
			if (eAnnotationDslFactory == null) {
				eAnnotationDslFactory = EcoreFactory.eINSTANCE.createEAnnotation();
				eAnnotationDslFactory.setSource(ecoreModel.getNsURI() + ANNOTATION_DSL_SOURCE_VSM_MAPPING);
				eModelElement.getEAnnotations().add(eAnnotationDslFactory);
			}
			eAnnotationDslFactory.getDetails().put(ANNOTATION_DSL_SOURCE_VSM_MAPPING_KEY_MAPPING_SELECTED,
					value);
		}
	}

	/**
	 * Set to <code>true</code> the annotation VSM_MAPPING_KEY_MAPPING_SELECTED of the given {@link Object}s.
	 * 
	 * @param objects
	 *            the {@link Object}s
	 */
	public void setVSMMappingSelectionToTrue(Object[] objects) {

		// DSL factory annotation

		for (Object object : objects) {
			setVSMMappingSelection(object, "true");
		}

	}

	/**
	 * Set to <code>false</code> the annotation VSM_MAPPING_KEY_MAPPING_SELECTED of the given {@link Object}s.
	 * 
	 * @param objects
	 *            the {@link Object}s
	 */
	public void setVSMMappingSelectionToFalse(Object objects) {

		// DSL factory annotation

		if (objects instanceof List) {

			for (Object object : (List<?>)objects) {
				setVSMMappingSelection(object, "false");
			}
		}

	}

}
