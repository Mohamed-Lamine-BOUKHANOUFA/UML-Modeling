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

	protected DSLInformationPage pageOne;

	protected MetaClassesSelectionPage pageTwo;

	protected MappingsSelectionPage pageThree;

	protected Profile rootProfile;

	protected Resource profileEcoreResource;

	protected EPackage profileEcoreModel;


		/**
	 * @return the profileEcoreModel
	 */
	public EPackage getProfileEcoreModel() {
		return profileEcoreModel;
	}

	/**
	 * @param profileEcoreModel
	 *            the profileEcoreModel to set
	 */
	public void setProfileEcoreModel(EPackage profileEcoreModel) {
		this.profileEcoreModel = profileEcoreModel;
	}

	/**
	 * @param profileEcoreResource_p
	 *            the profileEcoreResource to set
	 */
	public void setProfileEcoreResource(Resource profileEcoreResource_p) {
		profileEcoreResource = profileEcoreResource_p;
		profileEcoreModel = GenericUMLProfileTools.load(profileEcoreResource_p.getURI(),
				EcorePackage.Literals.EPACKAGE);
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
		return false;
	}

	@Override
	public void addPages() {
		pageOne = new DSLInformationPage("Information about the DSL", rootProfile);
		pageTwo = new MetaClassesSelectionPage("MetaClasses selection", "Meta-classes selection");
		pageThree = new MappingsSelectionPage("Mapping selection", "Mapping selection");

		addPage(pageOne);
		addPage(pageTwo);
		addPage(pageThree);


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

	}

	protected void refreshPages() {
		// pageTwo.refresh();
		pageThree.refresh();

	}
}
