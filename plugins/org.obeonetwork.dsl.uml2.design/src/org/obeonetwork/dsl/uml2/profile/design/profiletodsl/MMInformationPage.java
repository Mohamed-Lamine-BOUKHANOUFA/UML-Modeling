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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.editor.UMLEditorPlugin;
import org.eclipse.uml2.uml.util.UMLUtil;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class MMInformationPage extends WizardPage {

	private ProfileToDSLWizard profileToDSLWizard;

	private IProject mmPlugin;

	/**
	 * Dialog title: Profile Export.
	 */
	private String obeoNetworkPluginName = new String("org.obeonetwork");

	private String obeoNetworkURI = new String("http://www.obeonetwork.org/");

	private String separator = new String("/");

	private String defaultMmName = new String("dslFromUML");

	private Text mmNameField;

	private Text mmURIField;

	private Text mmPluginNameField;

	private boolean mmURIFieldState;

	private boolean mmPluginNameFieldState;

	private boolean ecoreGenerated;

	private String mmName;

	private String mmURI;

	private String mmPluginName;

	private Button createEMFProject;

	private Label ecoreCreationMessage;

	private Profile rootProfile;

	private Map<String, String> options = new HashMap<String, String>();

	private Map<String, String> choiceLabels = new HashMap<String, String>();

	private String discardChoiceLabel = UMLEditorPlugin.INSTANCE.getString("_UI_Discard_label");//$NON-NLS-1$

	private String ignoreChoiceLabel = UMLEditorPlugin.INSTANCE.getString("_UI_Ignore_label");//$NON-NLS-1$

	private String processChoiceLabel = UMLEditorPlugin.INSTANCE.getString("_UI_Process_label");//$NON-NLS-1$

	private String reportChoiceLabel = UMLEditorPlugin.INSTANCE.getString("_UI_Report_label");//$NON-NLS-1$

	private Resource profileEcoreResource;

	private EList<EClassifier> candidateImportedMetaClassesToKeep = new BasicEList<EClassifier>();

	/**
	 * Constructor.
	 *
	 * @param pageName
	 */
	public MMInformationPage(String pageName, Profile rootProfilePara) {
		super(pageName);
		rootProfile = rootProfilePara;
		initParameters(rootProfile);
		mmURIFieldState = true; // default value
		mmPluginNameFieldState = true; // default value
		ecoreGenerated = false; // default value
		setTitle("Information about the Meta-model");
	}

	/**
	 * Constructor.
	 *
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public MMInformationPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(Composite parent) {
		profileToDSLWizard = (ProfileToDSLWizard)this.getWizard();
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);

		// Tab about standard information
		TabItem infromationTab = new TabItem(tabFolder, SWT.NONE);
		infromationTab.setText("Information");
		infromationTab.setControl(createInformationTabArea(tabFolder));

		// Tab about advanced information from the conversion from uml to ecore.
		TabItem advancedInfromationTab = new TabItem(tabFolder, SWT.NONE);
		advancedInfromationTab.setText("Advanced information");
		advancedInfromationTab.setControl(createAdvancedInformationTabArea(tabFolder));

		setControl(tabFolder);
	}

	/* *************************************************************
	 * ******************* Standard area ***************************
	 * *************************************************************
	 */
	/**
	 * Create the standard tab content.
	 * 
	 * @param tab
	 *            the tab
	 * @return the control
	 */
	public Control createInformationTabArea(Composite tab) {
		Composite standardContentArea = new Composite(tab, SWT.NULL);

		GridData dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		standardContentArea.setLayout(gridLayout);

		// Messages for guidance.
		Label label1 = new Label(standardContentArea, SWT.NONE);
		label1.setText("Enter the information required to generate the Meta-model plug-ins.");
		Label label2 = new Label(standardContentArea, SWT.NONE);
		label2.setText("All text zones must be specified.");
		Label line = new Label(standardContentArea, SWT.SEPARATOR | SWT.HORIZONTAL);
		line.setLayoutData(dataFillHorizontal);

		// MM information.
		Label label3 = new Label(standardContentArea, SWT.NONE);
		label3.setText("Meta-Model (profile) name, editable in the profile (model/diagrams):");

		dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);
		mmNameField = new Text(standardContentArea, SWT.SINGLE | SWT.BORDER);
		mmNameField.setLayoutData(dataFillHorizontal);
		mmNameField.setText(mmName);
		mmNameField.setEnabled(false);

		final Label mmURILabel = new Label(standardContentArea, SWT.SEARCH);
		mmURILabel.setText("Root profile URI: ");

		mmURIField = new Text(standardContentArea, SWT.SINGLE | SWT.BORDER);
		mmURIField.setLayoutData(dataFillHorizontal);
		mmURIField.setText(mmURI);
		mmURIField.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (e.getSource() instanceof Text) {
					mmURI = ((Text)e.getSource()).getText();
				}
				mmURIFieldState = !isEmpty(e);
				ecoreGenerated = false;
				applyButtonFilter();
			}
		});

		final Label profilePluginNameLabel = new Label(standardContentArea, SWT.RIGHT);
		profilePluginNameLabel.setText("Profile plug-in name: ");

		mmPluginNameField = new Text(standardContentArea, SWT.SINGLE | SWT.BORDER);
		mmPluginNameField.setText(mmPluginName);
		mmPluginNameField.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (e.getSource() instanceof Text) {
					mmPluginName = ((Text)e.getSource()).getText();
				}
				mmPluginNameFieldState = !isEmpty(e);
				ecoreGenerated = false;
				applyButtonFilter();
			}
		});
		dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);
		mmPluginNameField.setLayoutData(dataFillHorizontal);

		// Ecore plug-in creation message and button.
		Label line2 = new Label(standardContentArea, SWT.SEPARATOR | SWT.HORIZONTAL);
		line2.setLayoutData(dataFillHorizontal);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		createEMFProject = new Button(standardContentArea, SWT.NONE);
		createEMFProject.setText("&Create the Ecore plug-in");
		createEMFProject.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		createEMFProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				candidateImportedMetaClassesToKeep = new BasicEList<EClassifier>();
				createMMInNewProject();
				profileToDSLWizard.setProfileEMFInformation(profileEcoreResource, mmPlugin);
				profileToDSLWizard.initInputPageTwo();
				// MMInformation.cleanMMPlugin(mmPlugin);
				ecoreGenerated = true;
				applyButtonFilter();
			}
		});

		ecoreCreationMessage = new Label(standardContentArea, SWT.NONE);
		ecoreCreationMessage.setText("Cannot continue without the Ecore plug-in creation.");
		ecoreCreationMessage.setForeground(new Color(standardContentArea.getDisplay(), 255, 0, 0));

		applyButtonFilter();
		return standardContentArea;
	}

	/* *************************************************************
	 * ******************* Advanced area ***************************
	 * *************************************************************
	 */

	/**
	 * Create the advanced tab content.
	 * 
	 * @param tab
	 *            the tab
	 * @return the control
	 */
	public Control createAdvancedInformationTabArea(Composite tab) {
		final ScrolledComposite sc = new ScrolledComposite(tab, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite advancedContentArea = new Composite(sc, SWT.NULL);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		advancedContentArea.setLayout(gridLayout);

		// Messages for guidance.
		Label label1 = new Label(advancedContentArea, SWT.NONE);
		label1.setText("Please choose options.");
		Label line = new Label(advancedContentArea, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData dataFillHorizontal = new GridData(GridData.FILL_HORIZONTAL);
		line.setLayoutData(dataFillHorizontal);

		choiceLabels.put(discardChoiceLabel, UMLUtil.OPTION__DISCARD);
		choiceLabels.put(ignoreChoiceLabel, UMLUtil.OPTION__IGNORE);
		choiceLabels.put(processChoiceLabel, UMLUtil.OPTION__PROCESS);
		choiceLabels.put(reportChoiceLabel, UMLUtil.OPTION__REPORT);

		createOptionAreas(advancedContentArea);

		sc.setContent(advancedContentArea);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(sc.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return sc;
	}

	/**
	 * Create the all options for the advanced area. The default parameters are used for the initialization of
	 * the combo box.
	 * 
	 * @param parent
	 */
	protected void createOptionAreas(Composite parent) {
		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_EcoreTaggedValues_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__ECORE_TAGGED_VALUES, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_DerivedFeatures_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__DERIVED_FEATURES, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent,
				UMLEditorPlugin.INSTANCE.getString("_UI_DuplicateFeatureInheritance_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__DUPLICATE_FEATURE_INHERITANCE, new String[] {
						ignoreChoiceLabel, reportChoiceLabel, discardChoiceLabel, processChoiceLabel},
				processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_DuplicateFeatures_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__DUPLICATE_FEATURES, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, discardChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(
				parent,
				UMLEditorPlugin.INSTANCE.getString("_UI_DuplicateOperationInheritance_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__DUPLICATE_OPERATION_INHERITANCE, new String[] {
						ignoreChoiceLabel, reportChoiceLabel, discardChoiceLabel, processChoiceLabel},
				processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_DuplicateOperations_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__DUPLICATE_OPERATIONS, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, discardChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_RedefiningOperations_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__REDEFINING_OPERATIONS, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_RedefiningProperties_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__REDEFINING_PROPERTIES, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_SubsettingProperties_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__SUBSETTING_PROPERTIES, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_UnionProperties_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__UNION_PROPERTIES, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, reportChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_SuperClassOrder_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__SUPER_CLASS_ORDER, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_AnnotationDetails_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__ANNOTATION_DETAILS, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_InvariantConstraints_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__INVARIANT_CONSTRAINTS, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_ValidationDelegates_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__VALIDATION_DELEGATES, new String[] {ignoreChoiceLabel,
						processChoiceLabel}, ignoreChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_NonAPIInvariants_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__NON_API_INVARIANTS, new String[] {ignoreChoiceLabel,
						processChoiceLabel}, ignoreChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_OperationBodies_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__OPERATION_BODIES, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_InvocationDelegates_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__INVOCATION_DELEGATES, new String[] {ignoreChoiceLabel,
						processChoiceLabel}, ignoreChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_PropertyDefaultExpressions_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__PROPERTY_DEFAULT_EXPRESSIONS, new String[] {
						ignoreChoiceLabel, reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_Comments_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__COMMENTS, new String[] {ignoreChoiceLabel,
						reportChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_ForeignDefinitions_label"), //$NON-NLS-1$
				UMLUtil.Profile2EPackageConverter.OPTION__FOREIGN_DEFINITIONS, new String[] {
						ignoreChoiceLabel, processChoiceLabel}, processChoiceLabel);

		createOptionArea(parent,
				UMLEditorPlugin.INSTANCE.getString("_UI_UntypedProperties_label"), //$NON-NLS-1$
				UMLUtil.Profile2EPackageConverter.OPTION__UNTYPED_PROPERTIES, new String[] {
						ignoreChoiceLabel, reportChoiceLabel, discardChoiceLabel, processChoiceLabel},
				reportChoiceLabel);

		createOptionArea(parent, UMLEditorPlugin.INSTANCE.getString("_UI_OppositeRoleNames_label"), //$NON-NLS-1$
				UMLUtil.UML2EcoreConverter.OPTION__OPPOSITE_ROLE_NAMES, new String[] {ignoreChoiceLabel,
						processChoiceLabel}, ignoreChoiceLabel);
	}

	/**
	 * Create the option for the advanced area. Copied from
	 * {@link org.eclipse.uml2.uml.editor.dialogs.OptionsDialog.createOptionArea} see
	 * {@link org.eclipse.uml2.uml.editor.dialogs.OptionsDialog.createOptionArea}
	 */
	protected void createOptionArea(final Composite parent, String uiLabel, final String option,
			String[] choices, String initialChoice) {

		Label label = new Label(parent, SWT.LEFT);
		{
			label.setText(uiLabel);

			GridData data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			label.setLayoutData(data);
		}

		final CCombo combo = new CCombo(parent, SWT.BORDER | SWT.READ_ONLY);
		{
			GridData data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			combo.setLayoutData(data);

			combo.setItems(choices);

			combo.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent me) {
					options.put(option, choiceLabels.get(combo.getText()));
					ecoreGenerated = false;
					applyButtonFilter();
				}
			});
			combo.setText(initialChoice);

		}
	}

	/**
	 * Test if the text source of an event is empty.
	 * 
	 * @param e
	 *            the event.
	 * @return true if length of the text = 0 else false
	 */
	private boolean isEmpty(final ModifyEvent e) {
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
		setPageComplete(mmURIFieldState && mmPluginNameFieldState && ecoreGenerated);
		createEMFProject.setEnabled(mmURIFieldState && mmPluginNameFieldState);
		ecoreCreationMessage.setVisible(!ecoreGenerated);
	}

	/* *************************************************************
	 * *************** EMF Project creation ************************
	 * *************************************************************
	 */

	/**
	 * Create a new EMF project.
	 */
	void createMMInNewProject() {
		MMInformation mmInformation = new MMInformation();
		profileEcoreResource = mmInformation.createMMInNewProject(this.getWizard(), mmPluginName,
				rootProfile,
				mmName, mmURI);
		mmPlugin = mmInformation.getMMPlugin();
	}

	/**
	 * Initiate the needed parameters for the ecore creation from profile.
	 * 
	 * @param the
	 *            profile
	 */
	protected void initParameters(final Profile profile) {

		if (profile.getName() != null && profile.getName().length() != 0)
			mmName = profile.getName().toLowerCase();
		else
			mmName = defaultMmName;

		if (profile.getURI() != null && profile.getURI().length() != 0) {
			mmURI = profile.getURI() + separator;
			mmPluginName = mmURI.replace("http://", "").replace(separator, ".") + "plugin";
		} else {
			mmURI = obeoNetworkURI + mmName + separator;
			mmPluginName = obeoNetworkPluginName + "." + mmName + "." + "plugin";
		}

	}

	/* *************************************************************
	 * **************** Setters and Getters ************************
	 * *************************************************************
	 */

	/**
	 * @return the profileEcoreResource
	 */
	public Resource getProfileEcoreResource() {
		return profileEcoreResource;
	}

}
