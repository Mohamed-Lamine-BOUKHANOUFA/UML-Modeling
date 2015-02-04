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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.uml2.uml.Profile;
import org.obeonetwork.dsl.uml2.profile.design.services.GenericUMLProfileTools;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class ProfileToDSLWizard extends Wizard {

	protected MMInformationPage pageOne;

	protected MetaClassesSelectionPage pageTwo;

	protected MappingsSelectionPage pageThree;

	protected VSMInformationPage pageFour;

	protected Profile rootProfile;

	protected Resource profileEcoreResource;

	protected EPackage profileEcoreModel;

	protected IProject mmPlugin;

		/**
	 * @return the profileEcoreModel
	 */
	public EPackage getProfileEcoreModel() {
		return profileEcoreModel;
	}

	/**
	 * @param profileEcoreModel_p
	 *            the profileEcoreModel to set
	 */
	public void setProfileEcoreModel(EPackage profileEcoreModel_p) {
		this.profileEcoreModel = profileEcoreModel_p;
	}

	/**
	 * @param profileEcoreResource_p
	 *            the profileEcoreResource to set
	 */
	public void setProfileEMFInformation(Resource profileEcoreResource_p, IProject mmPlugin_p) {
		profileEcoreResource = profileEcoreResource_p;
		mmPlugin = mmPlugin_p;
		EObject eObject = GenericUMLProfileTools.load(profileEcoreResource_p.getURI(),
				EcorePackage.Literals.EPACKAGE);
		if (eObject instanceof EPackage) {
			profileEcoreModel = (EPackage)eObject;
		} else {
			profileEcoreModel = null;
		}
	}

	/**
	 * @return the profileEcoreResource
	 */
	public Resource getProfileEcoreResource() {
		return profileEcoreResource;
	}

	/**
	 * Constructor.
	 */
	public ProfileToDSLWizard(Profile rootProfilePara) {
		setNeedsProgressMonitor(true);
		this.rootProfile = rootProfilePara;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		pageFour.createVsmInNewProject();
		return true;
	}

	@Override
	public void addPages() {
		pageOne = new MMInformationPage("Information about the DSL", rootProfile);
		pageTwo = new MetaClassesSelectionPage("MetaClasses selection", "Meta-classes selection");
		pageThree = new MappingsSelectionPage("Mapping selection", "Mapping selection");
		pageFour = new VSMInformationPage("Information about the VSM", "Information about the VSM");

		addPage(pageOne);
		addPage(pageTwo);
		addPage(pageThree);
		addPage(pageFour);


	}

	protected void initInputPageTwo() {
		initNextPages();
		// pageTwo.setProfileEcoreResource(profileEcoreModel);
		// pageTwo.initInput();
	}

	protected void initInputPageThree() {
		pageThree.setProfileEcoreResource(profileEcoreModel);
		pageThree.initInput();
	}

	protected void initNextPages() {
		pageTwo.setProfileEcoreResource(profileEcoreModel);
		pageTwo.initInput();
		pageThree.setProfileEcoreResource(profileEcoreModel);
		pageThree.initInput();
		pageFour.setProfileEcoreResource(profileEcoreModel);
		pageFour.initInput();
	}

	protected void refreshPages() {
		// pageTwo.refresh();
		pageThree.refresh();

	}

	public void closeAll() {
		try {
			mmPlugin.close(null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cleanMmPlugin() {
		MMInformation.cleanMMPlugin(mmPlugin, profileEcoreModel);
	}

	public void closeMMPlugin() {
		try {
			mmPlugin.close(null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openMMPlugin() {
		try {
			mmPlugin.open(null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
