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

	protected EList<EClassifier> importedMetaClasses = new BasicEList<EClassifier>();

	protected EList<EClassifier> candidateImportedMetaClassesToKeep = new BasicEList<EClassifier>();

	protected EList<EObject> eObjectToDelete = new BasicEList<EObject>();

	/* *************************************************************
	 * **************** Setters and Getters ************************
	 * *************************************************************
	 */

	/**
	 * @return the importedMetaClasses
	 */
	public EList<EClassifier> getImportedMetaClasses() {
		return importedMetaClasses;
	}

	/**
	 * @return the candidateImportedMetaClassesToKeep
	 */
	public EList<EClassifier> getCandidateImportedMetaClassesToKeep() {
		return candidateImportedMetaClassesToKeep;
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

	public void identifyCandidateMetaClasses() {

		findCandidateMetaClassesToKeep(ecoreModel);
		Tools.cleanModel(eObjectToDelete);
		Tools.save(ecoreModel);
	}

	/**
	 * Remove the base attributes from the classes of a given ecore model.
	 * 
	 * @param ecoreModel
	 *            the ecore model.
	 */
	public void findCandidateMetaClassesToKeep(EPackage ecoreModel) {
		for (EObject iterable_EObject : ecoreModel.eContents()) {
			handleEObject(iterable_EObject);
		}
		findCandidateEReferenceOfImportedClasses(ecoreModel);
	}

	/**
	 * Remove the base attributes from the classes of a given ecore object.
	 * 
	 * @param eObject
	 */
	public void handleEObject(EObject eObject) {
		for (EObject iterable_EObject : eObject.eContents()) {
			if (iterable_EObject instanceof EReference && eObject instanceof EClass) {
				if (isBaseReference((EReference)iterable_EObject)) {
					handleBaseReference((EReference)iterable_EObject, (EClass)eObject);

				}
			} else {
				handleEObject(iterable_EObject);
			}
		}
	}

	/**
	 * Test of the reference is a base reference.
	 * 
	 * @param eReference
	 *            the reference to test.
	 * @return true if is a reference base, else false
	 */
	public boolean isBaseReference(EReference eReference) {

		if (eReference.getName().startsWith("base_") && eReference.getEType() instanceof EClass) {
			return true;
		}
		return false;
	}

	/**
	 * Handle a reference of a class by the creation of a new abstract class using the name of the type of the
	 * reference and create a generalization link from the class (owner of the reference) to the new created
	 * abstract class. Then, remove the reference.
	 * 
	 * @param eReference
	 *            the reference
	 * @param eClass
	 *            the class owner of the reference
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
			importedMetaClasses.add(eReference.getEType());
			candidateImportedMetaClassesToKeep.add(importedMetaClassCopy);

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

	public void findCandidateEReferenceOfImportedClasses(EPackage ecoreModel) {
		EList<EReference> candidateEReference = new BasicEList<EReference>();
		for (EClassifier eClassifier : importedMetaClasses) {
			if (eClassifier instanceof EClass) {
				EClass eClassCondidate = Tools.getElementByName(eClassifier.getName(),
						candidateImportedMetaClassesToKeep);

				EClass eClass = ((EClass)eClassifier);
				candidateEReference = new BasicEList<EReference>();
				candidateEReference = getCandidateEReference(eClass, ecoreModel);
				eClassCondidate.getEStructuralFeatures().addAll(candidateEReference);
			}
		}
	}

	public EList<EReference> getCandidateEReference(EClass eClass, EPackage ecoreModel) {

		EList<EReference> porposedEReferences = new BasicEList<EReference>();
		EList<EClassifier> eClassifierTypes = new BasicEList<EClassifier>();
		boolean superEReferenceTypeIsToImport = false;
		for (EReference eReference : eClass.getEAllReferences()) {
			EReference eReferenceToAdd = EcoreUtil.copy(eReference);
			eClassifierTypes = new BasicEList<EClassifier>();
			superEReferenceTypeIsToImport = false;
			EClass refType = eReference.getEReferenceType();

			for (EClassifier eClassifierType : importedMetaClasses) {

				if (eClassifierType.equals(refType)
						|| (eClassifierType instanceof EClass && ((EClass)eClassifierType)
								.getEAllSuperTypes().contains(refType))) {
					if (!superEReferenceTypeIsToImport) {
						porposedEReferences.add(eReferenceToAdd);
					}
					eClassifierTypes.add(Tools.getElementByName(eClassifierType.getName(),
							candidateImportedMetaClassesToKeep));
					superEReferenceTypeIsToImport = true;
				}
			}
			// Create a new "Meta" abstract class for super class of two or more base classes, if this super
			// class is a type of a reference of one base class.
			if (superEReferenceTypeIsToImport && eClassifierTypes.size() > 1) {
				String xBaseESuperClassName = refType.getName();
				EClass xBaseESupperClass = Tools.getElementByName("Meta" + xBaseESuperClassName,
						candidateImportedMetaClassesToKeep);
				if (xBaseESupperClass == null) {
					xBaseESupperClass = EcoreFactory.eINSTANCE.createEClass();
					xBaseESupperClass.setName("Meta" + xBaseESuperClassName);
					xBaseESupperClass.setAbstract(true);
					xBaseESupperClass.getEStructuralFeatures().addAll(
							(Collection<? extends EStructuralFeature>)Tools.getCopy(refType
									.getEAllAttributes()));
					candidateImportedMetaClassesToKeep.add(xBaseESupperClass);
					ecoreModel.getEClassifiers().add(xBaseESupperClass);

				}
				eReferenceToAdd.setEType(xBaseESupperClass);
				for (EClassifier eClassifier : eClassifierTypes) {
					((EClass)eClassifier).getESuperTypes().add(xBaseESupperClass);
				}

			} else if (superEReferenceTypeIsToImport) {
				eReferenceToAdd.setEType(eClassifierTypes.get(0));
			}

		}
		return porposedEReferences;
	}

	void prepareCandidateBaseClasses() {

	}
}
