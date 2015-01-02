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

import java.util.Collection;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class MetaClassesSelection {

	protected EPackage ecoreModel;

	protected EList<EClassifier> importedMetaClassesInTheProfile = new BasicEList<EClassifier>();

	protected EList<EClassifier> candidateMetaClassesForTheDSL = new BasicEList<EClassifier>();

	protected EList<EObject> eObjectToDelete = new BasicEList<EObject>();

	/* *************************************************************
	 * **************** Setters and Getters ************************
	 * *************************************************************
	 */

	/**
	 * @return the imported MetaClasses In The Profile
	 */
	public EList<EClassifier> getImportedMetaClassesInTheProfile() {
		return importedMetaClassesInTheProfile;
	}

	/**
	 * @return the candidate MetaClasses For The DSL
	 */
	public EList<EClassifier> getCandidateMetaClassesForTheDSL() {
		return candidateMetaClassesForTheDSL;
	}

	/**
	 * Constructor.
	 */
	public MetaClassesSelection(EPackage ecoreModelPara) {
		ecoreModel = ecoreModelPara;
	}

	/********************************************************************************************/
	/*********************************** Core of the work ***************************************/
	/********************************************************************************************/

	/**
	 * Identify and create the candidate meta classes and references that can be selected by the user to added
	 * to the resultant meta model (Ecore)
	 */
	public void createCandidateMetaClassesAndReferences() {

		createCandidateMetaClassesAndReferences(ecoreModel);
		Tools.cleanModel(eObjectToDelete);
		Tools.save(ecoreModel);
	}

	/**
	 * Identify and create the candidate meta classes and references that can be selected by the user to added
	 * to the resultant meta model (Ecore)
	 * 
	 * @param ecoreModel
	 *            the {@link EPackage}.
	 */
	public void createCandidateMetaClassesAndReferences(EPackage ecoreModel) {
		for (EObject iterable_EObject : ecoreModel.eContents()) {
			// Handle Base references: replace the base references by generalizations to new created abstract
			// classes
			handleBaseReferencesOfEObject(iterable_EObject);
		}
		createCandidateEReferenceOfImportedClasses(ecoreModel);
	}

	/**
	 * Handle the base {@link EReference}s of a given {@link EObject}.
	 * 
	 * @param eObject
	 *            the {@link EObject}.
	 */
	public void handleBaseReferencesOfEObject(EObject eObject) {
		for (EObject iterable_EObject : eObject.eContents()) {
			if (iterable_EObject instanceof EReference && eObject instanceof EClass) {
				if (isBaseReference((EReference)iterable_EObject)) {
					handleBaseReference((EReference)iterable_EObject, (EClass)eObject);

				}
			} else {
				handleBaseReferencesOfEObject(iterable_EObject);
			}
		}
	}

	/**
	 * Verify if the {@link EReference} is a base reference.
	 * 
	 * @param eReference
	 *            the {@link EReference} to test.
	 * @return <code>true</code> if the {@link EReference} is a base reference, else <code>false</code>
	 */
	public boolean isBaseReference(EReference eReference) {

		if (eReference.getName().startsWith("base_") && eReference.getEType() instanceof EClass) {
			return true;
		}
		return false;
	}

	/**
	 * Handle a given base {@link EReference} of a given {@link EClass} by the creation of a new abstract
	 * {@link EClass} using the name of the type of the {@link EReference} and create a generalization link
	 * from the {@link EClass} (owner of the {@link EReference}) to the new created abstract {@link EClass}.
	 * Then, remove the {@link EReference}.
	 * 
	 * @param eReference
	 *            the {@link EReference}
	 * @param eClass
	 *            the {@link EClass} owner of the {@link EReference}
	 */
	public void handleBaseReference(EReference eReference, EClass eClass) {
		EClass importedMetaClassCopy;
		EClass eType = (EClass)eReference.getEType();
		String className = eType.getName();
		if (ecoreModel.getEClassifier(className) != null
				&& ecoreModel.getEClassifier(className) instanceof EClass) {
			importedMetaClassCopy = (EClass)ecoreModel.getEClassifier(className);

		} else {
			importedMetaClassCopy = EcoreFactory.eINSTANCE.createEClass();
			importedMetaClassCopy.setName(className);
			importedMetaClassCopy.setAbstract(true);
			for (EObject eObject : Tools.getCopy(eType.getEAllAttributes())) {
				if (!importedMetaClassCopy.getEStructuralFeatures().contains(eObject)) {
					importedMetaClassCopy.getEStructuralFeatures().add((EStructuralFeature)eObject);
				}
			}
			ecoreModel.getEClassifiers().add(importedMetaClassCopy);
			importedMetaClassesInTheProfile.add(eReference.getEType());
			candidateMetaClassesForTheDSL.add(importedMetaClassCopy);

		}
		eClass.getESuperTypes().add(importedMetaClassCopy);

		eObjectToDelete.add(eReference);

	}

	/**
	 * Find a class in an ecore model using a given name of the class.
	 * 
	 * @param className
	 *            the class name
	 * @param ecoreModel
	 *            the ecore model
	 * @return the class if found, else null
	 */
	public EClass getClass(String className, EPackage ecoreModel) {
		if (ecoreModel.getEClassifier(className) != null
				&& ecoreModel.getEClassifier(className) instanceof EClass) {
			return (EClass)ecoreModel.getEClassifier(className);
		} else {
			for (EPackage iterable_eclassifier : ecoreModel.getESubpackages()) {
				return getClass(className, iterable_eclassifier);
			}
		}
		return null;
	}

	/**
	 * Create the candidate {@link EReference}s and the potential related {@link EClass}s to the imported
	 * metaClass in the given {@link EPckage}.
	 * 
	 * @param ecoreModel
	 */
	public void createCandidateEReferenceOfImportedClasses(EPackage ecoreModel) {
		EList<EReference> candidateEReference = new BasicEList<EReference>();
		for (EClassifier importedMetaClasseInTheProfile : importedMetaClassesInTheProfile) {
			if (importedMetaClasseInTheProfile instanceof EClass) {
				EClass candidateMetaClasseForTheDSL = Tools.getElementByName(importedMetaClasseInTheProfile.getName(),
						candidateMetaClassesForTheDSL);

				candidateEReference = new BasicEList<EReference>();
				candidateEReference = getCandidateEReference(((EClass)importedMetaClasseInTheProfile),
						ecoreModel);
				candidateMetaClasseForTheDSL.getEStructuralFeatures().addAll(candidateEReference);
			}
		}
	}

	/**
	 * Identify and create the candidate {@link EReference}s and the potential related {@link EClass}s to a
	 * given {@link EClass}.
	 * 
	 * @param importedMetaClasseInTheProfile_p
	 *            the {@link EClass}
	 * @param ecoreModel
	 * @return the {@link EReference}s.
	 */
	public EList<EReference> getCandidateEReference(EClass importedMetaClasseInTheProfile_p, EPackage ecoreModel) {

		EList<EReference> candidateEReferences = new BasicEList<EReference>();
		EList<EClassifier> eClassifierTypes = new BasicEList<EClassifier>();
		boolean superEReferenceTypeIsToImport = false;

		for (EReference eReference : importedMetaClasseInTheProfile_p.getEAllReferences()) {

			EReference eReferenceToAdd = EcoreUtil.copy(eReference);
			eClassifierTypes = new BasicEList<EClassifier>();
			superEReferenceTypeIsToImport = false;
			EClass eReferenceType = eReference.getEReferenceType();

			for (EClassifier importedMetaClasseInTheProfile : importedMetaClassesInTheProfile) {

				if (importedMetaClasseInTheProfile.equals(eReferenceType)
						|| (importedMetaClasseInTheProfile instanceof EClass && ((EClass)importedMetaClasseInTheProfile)
								.getEAllSuperTypes().contains(eReferenceType))) {
					if (!superEReferenceTypeIsToImport) {
						candidateEReferences.add(eReferenceToAdd);
						superEReferenceTypeIsToImport = true;
					}
					eClassifierTypes.add(Tools.getElementByName(importedMetaClasseInTheProfile.getName(),
							candidateMetaClassesForTheDSL));
				}
			}
			// Create a new "Meta" abstract class for super class of two or more base classes, if this super
			// class is a type of a reference of one base class.
			if (superEReferenceTypeIsToImport && eClassifierTypes.size() > 1) {
				String xBaseESuperClassName = eReferenceType.getName();
				EClass xBaseESupperClass = Tools.getElementByName(xBaseESuperClassName,
						candidateMetaClassesForTheDSL);
				if (xBaseESupperClass == null) {
					xBaseESupperClass = Tools.getElementByName("Meta" + xBaseESuperClassName,
							candidateMetaClassesForTheDSL);
				}
				if (xBaseESupperClass == null) {
					xBaseESupperClass = EcoreFactory.eINSTANCE.createEClass();
					xBaseESupperClass.setName("Meta" + xBaseESuperClassName);
					xBaseESupperClass.setAbstract(true);
					xBaseESupperClass.getEStructuralFeatures().addAll(
							(Collection<? extends EStructuralFeature>)Tools.getCopy(eReferenceType
									.getEAllAttributes()));
					candidateMetaClassesForTheDSL.add(xBaseESupperClass);
					ecoreModel.getEClassifiers().add(xBaseESupperClass);

				}
				eReferenceToAdd.setEType(xBaseESupperClass);
				for (EClassifier eClassifier : eClassifierTypes) {
					if (!eClassifier.equals(xBaseESupperClass)) {
					((EClass)eClassifier).getESuperTypes().add(xBaseESupperClass);
					}
				}

			} else if (superEReferenceTypeIsToImport) {
				eReferenceToAdd.setEType(eClassifierTypes.get(0));
			}

		}
		return candidateEReferences;
	}

	void prepareCandidateBaseClasses() {

	}
}
