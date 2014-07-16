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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.uml2.uml.Profile;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class ProfileToDSLWizard extends Wizard {

	protected DSLInformationPage pageOne;

	protected MetaClassesSelectionPage pageTwo;

	protected Profile rootProfile;

	protected Resource profileEcoreResource;

	/**
	 * @param profileEcoreResource
	 *            the profileEcoreResource to set
	 */
	public void setProfileEcoreResource(Resource profileEcoreResource) {
		this.profileEcoreResource = profileEcoreResource;
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
		pageTwo = new MetaClassesSelectionPage("MetaClasses selection");

		addPage(pageOne);
		addPage(pageTwo);

	}

	protected void initInputPageTwo() {
		pageTwo.setProfileEcoreResource(profileEcoreResource);
		pageTwo.initInput();
	}

}
