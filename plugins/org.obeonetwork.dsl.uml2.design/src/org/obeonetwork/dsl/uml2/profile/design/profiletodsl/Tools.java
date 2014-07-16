package org.obeonetwork.dsl.uml2.profile.design.profiletodsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.obeonetwork.dsl.uml2.design.services.LogServices;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */

public class Tools {

	public Tools() {
	}

	/* **************************************************************************************
	 * ************************** Tools *****************************************************
	 * **************************************************************************************
	 */
	public static EClass getElementByName(String eClassName, EList<EClassifier> eClassifierList) {
		for (EClassifier eClassifier : eClassifierList) {
			if (eClassifier.getName() != null && eClassifier.getName().equals(eClassName)
					&& eClassifier instanceof EClass) {
				return (EClass)eClassifier;
			}
		}
		return null;
	}

	/**
	 * Copy all elements of a given list into a new list.
	 * 
	 * @param initList
	 *            the given list
	 * @return the new list container of all copied elements
	 */
	public static EList<EObject> getCopy(EList<?> initList) {
		EList<EObject> copyOfList = new BasicEList<EObject>();
		for (Object eObject : initList) {
			copyOfList.add(EcoreUtil.copy((EObject)eObject));
		}
		return copyOfList;
	}

	/**
	 * Save a model to file.
	 * 
	 * @param package_
	 */
	public static void save(EPackage package_) {
		Resource resource = null;
		if (package_.eResource() != null) {
			resource = package_.eResource();
			try {
				resource.save(null);
			} catch (final IOException ioe) {
				new LogServices().error("save(" + package_.getClass() + ") not handled", ioe);
			}
		}
	}

	public static Collection<EClassifier> getAllEClassifiers(EPackage epackage) {
		Collection<EClassifier> allEClassifiers = new ArrayList<EClassifier>();
		allEClassifiers.addAll(epackage.getEClassifiers());
		for (EPackage subpackage : epackage.getESubpackages()) {
			allEClassifiers.addAll(getAllEClassifiers(subpackage));
		}
		return allEClassifiers;
	}

	/**
	 * Find the root of a model from a given element.
	 * 
	 * @param eObject
	 *            the given element.
	 * @return the root if found else element
	 */
	public static EPackage getRoot(EObject eObject) {
		if (eObject != null && eObject.eContainer() != null) {
			return getRoot(eObject.eContainer());
		} else if (eObject != null) {
			return (EPackage)eObject;
		}
		return null;
	}

	public static void cleanModel(EList<EObject> eObjectToDelete) {
		for (EObject eObject : eObjectToDelete) {
			EcoreUtil.delete(eObject);
		}
	}

	public static int toInt(Object obj) {
		if (obj instanceof String && !obj.equals("")) {
			return Integer.parseInt((String)obj);
		} else if (obj instanceof Integer) {
			return ((Integer)obj).intValue();
		} else {
			return 0;
		}
	}
}
