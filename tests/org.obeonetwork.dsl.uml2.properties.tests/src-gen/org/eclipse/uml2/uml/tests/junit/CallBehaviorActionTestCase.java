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
import java.util.List;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.eef.runtime.tests.SWTBotEEFTestCase;
import org.eclipse.emf.eef.runtime.tests.exceptions.InputModelInvalidException;
import org.eclipse.emf.eef.runtime.tests.utils.EEFTestsModelsUtils;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.uml2.uml.CallBehaviorAction;
import org.eclipse.uml2.uml.UMLPackage;
import org.obeonetwork.dsl.uml2.properties.uml.parts.UmlViewsRepository;
import org.obeonetwork.dsl.uml2.properties.uml.providers.UmlMessages;
/**
 * TestCase for CallBehaviorAction
 * @author <a href="mailto:stephane.bouchet@obeo.fr">Stephane Bouchet</a>
 */
public class CallBehaviorActionTestCase extends SWTBotEEFTestCase {

	/**
	 * The EClass of the type to edit
	 */
	private EClass callBehaviorActionMetaClass = UMLPackage.eINSTANCE.getCallBehaviorAction();

	/**
	 * The type to edit
	 */
	private EObject callBehaviorAction;

	/**
	 * The enum value for the enum class visibility
	 */
	private Object enumValueForVisibility;
	/**
	 * The reference value for the reference class inStructuredNode
	 */
	private Object referenceValueForInStructuredNode;

	/**
	 * The reference value for the reference class behavior
	 */
	private Object referenceValueForBehavior;

	/**
	 * The reference value for the reference class onPort
	 */
	private Object referenceValueForOnPort;

	/**
	 * The reference value for the reference class incoming
	 */
	private Object referenceValueForIncoming;

	/**
	 * The reference value for the reference class inInterruptibleRegion
	 */
	private Object referenceValueForInInterruptibleRegion;

	/**
	 * The reference value for the reference class outgoing
	 */
	private Object referenceValueForOutgoing;

	/**
	 * The reference value for the reference class inPartition
	 */
	private Object referenceValueForInPartition;

	/**
	 * The reference value for the reference class activity
	 */
	private Object referenceValueForActivity;

	/**
	 * The reference value for the reference class redefinedNode
	 */
	private Object referenceValueForRedefinedNode;

	/**
	 * The reference value for the reference class clientDependency
	 */
	private Object referenceValueForClientDependency;
	/**
	 * The EClass of the reference to edit
	 */
	private EClass interruptibleActivityRegionMetaClass = UMLPackage.eINSTANCE.getInterruptibleActivityRegion();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass structuredActivityNodeMetaClass = UMLPackage.eINSTANCE.getStructuredActivityNode();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass portMetaClass = UMLPackage.eINSTANCE.getPort();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass activityMetaClass = UMLPackage.eINSTANCE.getActivity();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass behaviorMetaClass = UMLPackage.eINSTANCE.getBehavior();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass activityNodeMetaClass = UMLPackage.eINSTANCE.getActivityNode();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass activityPartitionMetaClass = UMLPackage.eINSTANCE.getActivityPartition();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass activityEdgeMetaClass = UMLPackage.eINSTANCE.getActivityEdge();

	/**
	 * The EClass of the reference to edit
	 */
	private EClass dependencyMetaClass = UMLPackage.eINSTANCE.getDependency();
	/**
	 * The eObjects list contained in widgets
	 */
	private List allInstancesOf;
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
	protected void initializeExpectedModelForCallBehaviorActionName() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
				cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getNamedElement_Name(), UPDATED_VALUE));
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
	public void testEditCallBehaviorActionName() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionName();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the name feature of the CallBehaviorAction element 
				bot.editTextFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.name, UPDATED_VALUE);

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
	protected void initializeExpectedModelForCallBehaviorActionVisibility() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
				cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getNamedElement_Visibility(), UPDATED_VALUE));
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
	public void testEditCallBehaviorActionVisibility() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		enumValueForVisibility = bot.changeEnumLiteralValue(UMLPackage.eINSTANCE.getVisibilityKind(), ((CallBehaviorAction)callBehaviorAction).getVisibility().getLiteral());
		// Create the expected model
		initializeExpectedModelForCallBehaviorActionVisibility();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the visibility feature of the CallBehaviorAction element 
				bot.editTextFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.visibility, UPDATED_VALUE);

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
	protected void initializeExpectedModelForCallBehaviorActionClientDependency() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, dependencyMetaClass);
		referenceValueForClientDependency = bot.changeReferenceValue(allInstancesOf, ((CallBehaviorAction)callBehaviorAction).getClientDependency());
		cc.append(AddCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getNamedElement_ClientDependency(), referenceValueForClientDependency));
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
	public void testEditCallBehaviorActionClientDependency() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionClientDependency();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the clientDependency feature of the CallBehaviorAction element 
		bot.editAdvancedReferencesTableFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.clientDependency, referenceValueForClientDependency);

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
	protected void initializeRemoveExpectedModelForCallBehaviorActionClientDependency() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		List<EObject> allReferencedInstances = (List<EObject>)callBehaviorAction.eGet(UMLPackage.eINSTANCE.getNamedElement_ClientDependency());
		if (allReferencedInstances.size() > 0) {
			cc.append(RemoveCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getNamedElement_ClientDependency(), allReferencedInstances.get(0)));
		}
		else {
			throw new InputModelInvalidException();
		}
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
	public void testRemoveCallBehaviorActionClientDependency() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeRemoveExpectedModelForCallBehaviorActionClientDependency();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the clientDependency feature of the CallBehaviorAction element 
		bot.removeAdvancedReferencesTableFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.clientDependency, UmlMessages.PropertiesEditionPart_RemoveListViewerLabel);

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
	protected void initializeExpectedModelForCallBehaviorActionIsLeaf() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
				cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getRedefinableElement_IsLeaf(), UPDATED_VALUE));
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
	public void testEditCallBehaviorActionIsLeaf() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionIsLeaf();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the isLeaf feature of the CallBehaviorAction element 
				bot.editTextFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.isLeaf, UPDATED_VALUE);

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
	protected void initializeExpectedModelForCallBehaviorActionInStructuredNode() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, structuredActivityNodeMetaClass);
		referenceValueForInStructuredNode = bot.changeReferenceValue(allInstancesOf, ((CallBehaviorAction)callBehaviorAction).getInStructuredNode());
		cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_InStructuredNode(), referenceValueForInStructuredNode));
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
	public void testEditCallBehaviorActionInStructuredNode() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionInStructuredNode();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the inStructuredNode feature of the CallBehaviorAction element 
		bot.editEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.inStructuredNode, allInstancesOf.indexOf(referenceValueForInStructuredNode)+1);

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
	protected void initializeRemoveExpectedModelForCallBehaviorActionInStructuredNode() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, structuredActivityNodeMetaClass);
		cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_InStructuredNode(), null));
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
	public void testRemoveCallBehaviorActionInStructuredNode() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeRemoveExpectedModelForCallBehaviorActionInStructuredNode();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the inStructuredNode feature of the CallBehaviorAction element
		bot.removeEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.inStructuredNode);
		

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
	protected void initializeExpectedModelForCallBehaviorActionActivity() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, activityMetaClass);
		referenceValueForActivity = bot.changeReferenceValue(allInstancesOf, ((CallBehaviorAction)callBehaviorAction).getActivity());
		cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_Activity(), referenceValueForActivity));
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
	public void testEditCallBehaviorActionActivity() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionActivity();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the activity feature of the CallBehaviorAction element 
		bot.editEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.activity, allInstancesOf.indexOf(referenceValueForActivity)+1);

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
	protected void initializeRemoveExpectedModelForCallBehaviorActionActivity() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, activityMetaClass);
		cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_Activity(), null));
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
	public void testRemoveCallBehaviorActionActivity() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeRemoveExpectedModelForCallBehaviorActionActivity();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the activity feature of the CallBehaviorAction element
		bot.removeEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.activity);
		

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
	protected void initializeExpectedModelForCallBehaviorActionInPartition() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, activityPartitionMetaClass);
		referenceValueForInPartition = bot.changeReferenceValue(allInstancesOf, ((CallBehaviorAction)callBehaviorAction).getInPartition());
		cc.append(AddCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_InPartition(), referenceValueForInPartition));
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
	public void testEditCallBehaviorActionInPartition() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionInPartition();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the inPartition feature of the CallBehaviorAction element 
		bot.editAdvancedReferencesTableFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.inPartition, referenceValueForInPartition);

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
	protected void initializeRemoveExpectedModelForCallBehaviorActionInPartition() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		List<EObject> allReferencedInstances = (List<EObject>)callBehaviorAction.eGet(UMLPackage.eINSTANCE.getActivityNode_InPartition());
		if (allReferencedInstances.size() > 0) {
			cc.append(RemoveCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_InPartition(), allReferencedInstances.get(0)));
		}
		else {
			throw new InputModelInvalidException();
		}
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
	public void testRemoveCallBehaviorActionInPartition() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeRemoveExpectedModelForCallBehaviorActionInPartition();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the inPartition feature of the CallBehaviorAction element 
		bot.removeAdvancedReferencesTableFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.inPartition, UmlMessages.PropertiesEditionPart_RemoveListViewerLabel);

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
	protected void initializeExpectedModelForCallBehaviorActionInInterruptibleRegion() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, interruptibleActivityRegionMetaClass);
		referenceValueForInInterruptibleRegion = bot.changeReferenceValue(allInstancesOf, ((CallBehaviorAction)callBehaviorAction).getInInterruptibleRegion());
		cc.append(AddCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_InInterruptibleRegion(), referenceValueForInInterruptibleRegion));
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
	public void testEditCallBehaviorActionInInterruptibleRegion() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionInInterruptibleRegion();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the inInterruptibleRegion feature of the CallBehaviorAction element 
		bot.editAdvancedReferencesTableFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.inInterruptibleRegion, referenceValueForInInterruptibleRegion);

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
	protected void initializeRemoveExpectedModelForCallBehaviorActionInInterruptibleRegion() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		List<EObject> allReferencedInstances = (List<EObject>)callBehaviorAction.eGet(UMLPackage.eINSTANCE.getActivityNode_InInterruptibleRegion());
		if (allReferencedInstances.size() > 0) {
			cc.append(RemoveCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getActivityNode_InInterruptibleRegion(), allReferencedInstances.get(0)));
		}
		else {
			throw new InputModelInvalidException();
		}
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
	public void testRemoveCallBehaviorActionInInterruptibleRegion() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeRemoveExpectedModelForCallBehaviorActionInInterruptibleRegion();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the inInterruptibleRegion feature of the CallBehaviorAction element 
		bot.removeAdvancedReferencesTableFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.inInterruptibleRegion, UmlMessages.PropertiesEditionPart_RemoveListViewerLabel);

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
	protected void initializeExpectedModelForCallBehaviorActionOnPort() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, portMetaClass);
		referenceValueForOnPort = bot.changeReferenceValue(allInstancesOf, ((CallBehaviorAction)callBehaviorAction).getOnPort());
		cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getInvocationAction_OnPort(), referenceValueForOnPort));
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
	public void testEditCallBehaviorActionOnPort() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionOnPort();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the onPort feature of the CallBehaviorAction element 
		bot.editEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.onPort, allInstancesOf.indexOf(referenceValueForOnPort)+1);

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
	protected void initializeRemoveExpectedModelForCallBehaviorActionOnPort() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
		allInstancesOf = EEFTestsModelsUtils.getAllInstancesOf(expectedModel, portMetaClass);
		cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getInvocationAction_OnPort(), null));
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
	public void testRemoveCallBehaviorActionOnPort() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeRemoveExpectedModelForCallBehaviorActionOnPort();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the onPort feature of the CallBehaviorAction element
		bot.removeEObjectFlatComboViewerFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.onPort);
		

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
	protected void initializeExpectedModelForCallBehaviorActionIsSynchronous() throws InputModelInvalidException, IOException {
		// Create the expected model content by applying the attempted command on a copy of the input model content
		createExpectedModel();
		EObject callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(expectedModel, callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());
		CompoundCommand cc = new CompoundCommand();
				cc.append(SetCommand.create(editingDomain, callBehaviorAction, UMLPackage.eINSTANCE.getCallAction_IsSynchronous(), UPDATED_VALUE));
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
	public void testEditCallBehaviorActionIsSynchronous() throws Exception {

		// Import the input model
		initializeInputModel();

		callBehaviorAction = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (callBehaviorAction == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		// Create the expected model
		initializeExpectedModelForCallBehaviorActionIsSynchronous();

		// Open the input model with the treeview editor
		SWTBotEditor modelEditor = bot.openActiveModel();

		// Open the EEF wizard (by double click) to edit the CallBehaviorAction element
		EObject firstInstanceOf = EEFTestsModelsUtils.getFirstInstanceOf(bot.getActiveResource(), callBehaviorActionMetaClass);
		if (firstInstanceOf == null)
			throw new InputModelInvalidException(callBehaviorActionMetaClass.getName());

		SWTBotShell wizardShell = bot.prepareBatchEditing(modelEditor, callBehaviorActionMetaClass, firstInstanceOf, null);

		// Change value of the isSynchronous feature of the CallBehaviorAction element 
				bot.editTextFeature(wizardShell, UmlViewsRepository.CallBehaviorAction.Properties.isSynchronous, UPDATED_VALUE);

		// Save the modification
		bot.finalizeEdition(modelEditor);

		// Compare real model with expected model
		assertExpectedModelReached(expectedModel);

		// Delete the input model
		deleteModels();

	}






















}
