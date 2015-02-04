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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Profile;
import org.obeonetwork.dsl.uml2.profile.design.exportprofile.ExportProfileService;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class ProfileToDSLService {

	public void profileToDSL(Profile rootProfile) {
		final Shell activeShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

		boolean isProfile = ExportProfileService.isProfileRoot(rootProfile);
		if (isProfile && ExportProfileService.validateUmlElementWithProgress(rootProfile)) {

			ProfileToDSLWizard p2dWizard = new ProfileToDSLWizard(rootProfile);
			p2dWizard.setWindowTitle("UML Profile to DSL");
			final WizardDialog wd = new WizardDialog(activeShell, p2dWizard);
			wd.setTitle("UML Profile to DSL exportation");
			wd.setMinimumPageSize(300, 350);
			wd.setPageSize(300, 350);
			wd.create();
			wd.open();

		} else {
			if (!isProfile) {
				MessageDialog
						.openError(activeShell, "Exportation error",
								"The root element of this model is not a profile. Due to the error, the exportation will be stopped.");
			} else
				MessageDialog.openError(activeShell, "Exportation error",
						"Due to the error, the exportation will be stopped.");
		}

	}

}
