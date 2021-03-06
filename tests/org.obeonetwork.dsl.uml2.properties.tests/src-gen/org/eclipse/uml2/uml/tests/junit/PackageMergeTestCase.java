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
package org.eclipse.uml2.uml.tests.junit;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase;
import org.eclipse.emf.eef.runtime.tests.exceptions.InputModelInvalidException;
import org.eclipse.emf.eef.runtime.tests.utils.EEFTestsModelsUtils;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.uml2.uml.PackageMerge;
import org.eclipse.uml2.uml.UMLPackage;
import org.obeonetwork.dsl.uml2.properties.uml.parts.UmlViewsRepository;
/**
 * TestCase for PackageMerge
 * @author <a href="mailto:stephane.bouchet@obeo.fr">Stephane Bouchet</a>
 */
public class PackageMergeTestCase extends SWTBotEEFTestCase {

	/**
	 * The EClass of the type to edit
	 */
	private EClass packageMergeMetaClass = UMLPackage.eINSTANCE.getPackageMerge();

	/**
	 * The type to edit
	 */
	private EObject packageMerge;

	/**
	 * The reference value for the reference class receivingPackage
	 */
	private Object referenceValueForReceivingPackage;

	/**
	 * The reference value for the reference class mergedPackage
	 */
	private Object referenceValueForMergedPackage;
	/**
	 * The EClass of the reference to edit
	 */
	private EClass packageMetaClass = UMLPackage.eINSTANCE.getPackage();
	/**
	 * Updated value of the feature
	 */
	private static final String UPDATED_VALUE = "value2";

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase#getExpectedModelName()
	 */
	protected String getExpectedModelName() {
		return "expected.uML";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase#getInputModelFolder()
	 */
	protected String getInputModelFolder() {
		return "input";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase#getInputModelName()
	 */
	protected String getInputModelName() {
		return "input.uML";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase#getTestsProjectName()
	 */
	protected String getTestsProjectName() {
		return "org.obeonetwork.dsl.uml2.properties.tests";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase#getExpectedModelFolder()
	 */
	protected String getExpectedModelFolder() {
		// The project that contains models for tests
		return "expected";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase#getImportModelsFolder()
	 */
	protected String getImportModelsFolder() {
		return  "models";
	}
	/**
	 * Create the expected model from the input model
	 * @throws InputModelInvalidException error during expected model initialization
	 * @throws IOException error during expected model serialization
	 */
	protected void initializeExpectedModelForPackageMergeMergedPackage() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject packageMerge = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, packageMergeMetaClass);
		if (packageMerge == null)
			throw new InputModelInvalidException(packageMergeMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, packageMetaClass);
		referenceValueForMergedPackage = bot.changeReferenceValue(allInstancesOf, ((PackageMerge)packageMerge).getMergedPackage());
		cc.append(SetCommand.create(editingDomain, packageMerge, UMLPackage.eINSTANCE.getPackageMerge_MergedPackage(), referenceValueForMergedPackage));
		editingDomain.getCommandStack().execute(cc);
		expectedModel.save(Collections.EMPTY_MAP);
	}
	/**
	 * Test the editor properties :
	 * - init the input model
	 * - calculate the expected model
	 * - initialize the model editor
	 * - change the properties in the editor properties
	 * - compare the expected and the real model : if they are equals the test pass
	 * - delete the models
	 */
	public void testEditPackageMergeMergedPackage() throws Exception {

		// Import the input model
		initializeInputModel();

		packageMerge = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), packageMergeMetaClass);
		if (packageMerge == null)
			throw new InputModelInvalidException(packageMergeMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForPackageMergeMergedPackage();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the PackageMerge element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), packageMergeMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(packageMergeMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, packageMergeMetaClass, firstInstanceOf, null);

		// Change value of the mergedPackage feature of the PackageMerge element 
		bot.editEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.PackageMerge.Properties.mergedPackage, allInstancesOf.indexOf(referenceValueForMergedPackage));

		// Save the modification
		bot.finalizeEdition(modelEditor);

		// Compare real model with expected model
		assertExpectedModelReached(expectedModel);

		// Delete the input model
		deleteModels();

	}
	/**
	 * Create the expected model from the input model
	 * @throws InputModelInvalidException error during expected model initialization
	 * @throws IOException error during expected model serialization
	 */
	protected void initializeExpectedModelForPackageMergeReceivingPackage() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject packageMerge = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, packageMergeMetaClass);
		if (packageMerge == null)
			throw new InputModelInvalidException(packageMergeMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, packageMetaClass);
		referenceValueForReceivingPackage = bot.changeReferenceValue(allInstancesOf, ((PackageMerge)packageMerge).getReceivingPackage());
		cc.append(SetCommand.create(editingDomain, packageMerge, UMLPackage.eINSTANCE.getPackageMerge_ReceivingPackage(), referenceValueForReceivingPackage));
		editingDomain.getCommandStack().execute(cc);
		expectedModel.save(Collections.EMPTY_MAP);
	}
	/**
	 * Test the editor properties :
	 * - init the input model
	 * - calculate the expected model
	 * - initialize the model editor
	 * - change the properties in the editor properties
	 * - compare the expected and the real model : if they are equals the test pass
	 * - delete the models
	 */
	public void testEditPackageMergeReceivingPackage() throws Exception {

		// Import the input model
		initializeInputModel();

		packageMerge = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), packageMergeMetaClass);
		if (packageMerge == null)
			throw new InputModelInvalidException(packageMergeMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForPackageMergeReceivingPackage();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the PackageMerge element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), packageMergeMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(packageMergeMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, packageMergeMetaClass, firstInstanceOf, null);

		// Change value of the receivingPackage feature of the PackageMerge element 
		bot.editEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.PackageMerge.Properties.receivingPackage, allInstancesOf.indexOf(referenceValueForReceivingPackage));

		// Save the modification
		bot.finalizeEdition(modelEditor);

		// Compare real model with expected model
		assertExpectedModelReached(expectedModel);

		// Delete the input model
		deleteModels();

	}






}
