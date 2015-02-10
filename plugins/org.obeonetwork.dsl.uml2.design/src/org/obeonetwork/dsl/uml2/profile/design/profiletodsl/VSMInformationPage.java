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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sirius.viewpoint.description.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class VSMInformationPage extends WizardPage {

	ProfileToDSLWizard profileToDSLWizard;

	private EPackage profileEcoreModel;

	/**
	 * Dialog title: Profile Export.
	 */
	private String obeoNetworkPluginName = new String("org.obeonetwork");

	private String separator = new String("/");

	private String defaultVSMName = new String("dslFromUML");

	private Text vsmNameField;

	private Text vsmPluginNameField;

	private boolean vsmNameFieldState;

	private boolean vsmPluginNameFieldState;

	private String vsmName;

	private String vsmPluginName;

	private Group vsmGroup;


	/**
	 * Constructor.
	 *
	 * @param pageName
	 */
	public VSMInformationPage(String pageName, String title) {
		super(pageName);
		setTitle(title);
		vsmNameFieldState = true;
		vsmPluginNameFieldState = true; // default value
	}

	/**
	 * Constructor.
	 *
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public VSMInformationPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(Composite parent) {
		profileToDSLWizard = (ProfileToDSLWizard)this.getWizard();

		Composite contentArea = new Composite(parent, SWT.NULL);

		GridData dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		contentArea.setLayout(gridLayout);
		
		 // Messages for guidance.
		 Label label1 = new Label(contentArea, SWT.NONE);
		label1.setText("Enter the information required to generate the VSM plug-ins.");
		 Label label2 = new Label(contentArea, SWT.NONE);
		 label2.setText("All text zones must be specified.");
		 Label line = new Label(contentArea, SWT.SEPARATOR | SWT.HORIZONTAL);
		 line.setLayoutData(dataFillHorizontal);

		// VSM information.
		Label label3 = new Label(contentArea, SWT.NONE);
		label3.setText("VSM name :");

		dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);
		vsmNameField = new Text(contentArea, SWT.SINGLE | SWT.BORDER);
		vsmNameField.setLayoutData(dataFillHorizontal);
		vsmNameField.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (e.getSource() instanceof Text) {
					vsmName = ((Text)e.getSource()).getText();
				}
				vsmNameFieldState = !isEmpty(e);
				applyButtonFilter();
			}
		});

		final Label profilePluginNameLabel = new Label(contentArea, SWT.RIGHT);
		profilePluginNameLabel.setText("VSM plug-in name: ");

		vsmPluginNameField = new Text(contentArea, SWT.SINGLE | SWT.BORDER);
		vsmPluginNameField.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (e.getSource() instanceof Text) {
					vsmPluginName = ((Text)e.getSource()).getText();
				}
				vsmPluginNameFieldState = !isEmpty(e);
				applyButtonFilter();
			}
		});
		dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);
		vsmPluginNameField.setLayoutData(dataFillHorizontal);

		// Ecore plug-in creation message and button.
		Label line2 = new Label(contentArea, SWT.SEPARATOR | SWT.HORIZONTAL);
		line2.setLayoutData(dataFillHorizontal);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		applyButtonFilter();

		setControl(contentArea);
	}


	/**
	 * Test if the text source of an event is empty.
	 * 
	 * @param e
	 *            the event.
	 * @return true if length of the text = 0 else false
	 */
	private static boolean isEmpty(final ModifyEvent e) {
		final Object src = e.getSource();
		if (src instanceof Text) {
			final Text txt = (Text)src;
			return txt.getText().length() == 0;
		}
		return true;
	}

	/**
	 * Apply button filter.
	 */
	private void applyButtonFilter() {
		setPageComplete(vsmNameFieldState && vsmPluginNameFieldState);
	}

	/**
	 * Set the profile ecore resource.
	 * 
	 * @param profileEcoreModel_p
	 */
	public void setProfileEcoreResource(EPackage profileEcoreModel_p) {
		profileEcoreModel = profileEcoreModel_p;
	}

	/**
	 * initialize the input of the meta classes selection page.
	 */
	public void initInput() {
		initParameters();
		vsmNameField.setText(vsmName);
		vsmPluginNameField.setText(vsmPluginName);
	}

	/* *************************************************************
	 * *************** VSM Project creation ************************
	 * *************************************************************
	 */

	/**
	 * Create a new EMF project.
	 */
	void createVsmInNewProject() {
		profileToDSLWizard.cleanMmPlugin();
		profileToDSLWizard.closeMMPlugin();
		VSMInformation vsmInformation = new VSMInformation();
		try {
			vsmGroup = vsmInformation
					.createVsmInNewProject(vsmPluginName, profileEcoreModel.getName());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		vsmInformation.populateVsm(profileToDSLWizard.getProfileEcoreModel());
		profileToDSLWizard.openMMPlugin();
	}

	/**
	 * Initiate the needed parameters for the ecore creation from profile.
	 *
	 * @param the
	 *            profile
	 */
	protected void initParameters() {

		if (profileEcoreModel.getName() != null && profileEcoreModel.getName().length() != 0)
			vsmName = profileEcoreModel.getName().toLowerCase();
		else
			vsmName = defaultVSMName;

		if (profileEcoreModel.getNsURI() != null && profileEcoreModel.getNsURI().length() != 0) {
			vsmPluginName = profileEcoreModel.getNsURI().replace("http://", "").replace(separator, ".")
					+ "design";
		} else {
			vsmPluginName = obeoNetworkPluginName + "." + vsmName + "." + "plugin";
		}

	}
}
