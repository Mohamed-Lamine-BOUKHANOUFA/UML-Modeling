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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.obeonetwork.dsl.uml2.profile.design.services.GenericUMLProfileTools;

/**
 * @author Mohamed-Lamine BOUKHANOUFA <a
 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> *
 */
public class MetaClassesSelectionPage extends WizardPage {
	protected EPackage ecoreModel;

	protected ProfileToDSLWizard profileToDSLWizard;

	protected Resource profileEcoreResource;

	protected ContainerCheckedTreeViewer containerCheckedTreeViewer;

	private ComboBoxViewerCellEditor comboBoxCellEditor;

	protected EList<EClassifier> importedMetaClasses = new BasicEList<EClassifier>();

	protected EList<EClassifier> candidateImportedMetaClassesToKeep = new BasicEList<EClassifier>();

	protected EList<EObject> eObjectToDelete = new BasicEList<EObject>();

	protected Object[] checkedElement;

	/**
	 * Constructor.
	 *
	 * @param pageName
	 */
	public MetaClassesSelectionPage(String pageName) {
		super(pageName);
	}

	/**
	 * Constructor.
	 *
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public MetaClassesSelectionPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(Composite parent) {
		profileToDSLWizard = (ProfileToDSLWizard)this.getWizard();

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());

		containerCheckedTreeViewer = new ContainerCheckedTreeViewer(container, SWT.BORDER);

		containerCheckedTreeViewer.setContentProvider(new MyTreeContentProvider());
		containerCheckedTreeViewer.getTree().setVisible(true);

		TreeViewerColumn umlElmentColumn = new TreeViewerColumn(containerCheckedTreeViewer, SWT.LEFT);
		umlElmentColumn.getColumn().setText("UML Element");
		umlElmentColumn.setLabelProvider(new EcoreElementLabelProvider());
		umlElmentColumn.getColumn().setWidth(300);

		TreeViewerColumn nameColumn = new TreeViewerColumn(containerCheckedTreeViewer, SWT.LEFT);
		nameColumn.getColumn().setText("Name");
		nameColumn.setLabelProvider(new EcoreElementNameLabelProvider());
		nameColumn.getColumn().setWidth(200);

		TreeViewerColumn lowerBoundColumn = new TreeViewerColumn(containerCheckedTreeViewer, SWT.LEFT);
		lowerBoundColumn.getColumn().setText("Lower Bound");
		lowerBoundColumn.setLabelProvider(new EAttributeLowerBoundLabelProvider());
		lowerBoundColumn.getColumn().setWidth(90);

		TreeViewerColumn upperBoundColumn = new TreeViewerColumn(containerCheckedTreeViewer, SWT.LEFT);
		upperBoundColumn.getColumn().setText("Upper Bound");
		upperBoundColumn.setLabelProvider(new EAttributeUpperBoundLabelProvider());
		upperBoundColumn.getColumn().setWidth(90);

		TreeViewerColumn typeColumn = new TreeViewerColumn(containerCheckedTreeViewer, SWT.LEFT);
		typeColumn.getColumn().setText("Type");
		typeColumn.setLabelProvider(new TypeLabelProvider());
		typeColumn.getColumn().setWidth(100);

		containerCheckedTreeViewer.setUseHashlookup(true);
		containerCheckedTreeViewer.getTree().setHeaderVisible(true);
		containerCheckedTreeViewer.getTree().setLinesVisible(true);

		Button createEMFProject = new Button(container, SWT.NONE);
		createEMFProject.setText("Apply selection");
		createEMFProject.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		createEMFProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkedElement = containerCheckedTreeViewer.getCheckedElements();
				applySelection();
			}
		});

		/************************** Edit *****************************/
		// To trigger editing in Tree cells on double click, we need to create TreeViewerEditor.
		TreeViewerEditor.create(containerCheckedTreeViewer, new ColumnViewerEditorActivationStrategy(
				containerCheckedTreeViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}
		}, ColumnViewerEditor.DEFAULT);

		final TextCellEditor cellEditor = new MyTextCellEditor(containerCheckedTreeViewer.getTree());

		nameColumn.setEditingSupport(new NameEditingSupport(containerCheckedTreeViewer, cellEditor));
		upperBoundColumn.setEditingSupport(new BoundEditingSupport(containerCheckedTreeViewer, cellEditor,
				"upper"));

		lowerBoundColumn.setEditingSupport(new BoundEditingSupport(containerCheckedTreeViewer, cellEditor,
				"lower"));
		typeColumn.setEditingSupport(new ComboBoxEditingSupport(containerCheckedTreeViewer));


		/************************** Edit *****************************/

		containerCheckedTreeViewer.getTree()
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setControl(container);

	}

	public void initInput() {

		ecoreModel = GenericUMLProfileTools.load(profileEcoreResource.getURI(),
				EcorePackage.Literals.EPACKAGE);
		MetaClassesSelection metaClassesSelection = new MetaClassesSelection(ecoreModel);
		metaClassesSelection.identifyCandidateMetaClasses();
		importedMetaClasses = metaClassesSelection.getImportedMetaClasses();
		candidateImportedMetaClassesToKeep = metaClassesSelection.getCandidateImportedMetaClassesToKeep();
		if (containerCheckedTreeViewer.getInput() == null) {
			containerCheckedTreeViewer.setInput(candidateImportedMetaClassesToKeep);
		}

		comboBoxCellEditor.setInput(Tools.getAllEClassifiers(ecoreModel));

	}

	// The following class adds border to the text box and sets it height to 10 pixels more than the font
	// height.
	class MyTextCellEditor extends TextCellEditor {
		int minHeight = 0;

		public MyTextCellEditor(Tree tree) {
			super(tree, SWT.BORDER);
			Text txt = (Text)getControl();

			Font fnt = txt.getFont();
			FontData[] fontData = fnt.getFontData();
			if (fontData != null && fontData.length > 0)
				minHeight = fontData[0].getHeight() + 10;
		}

		public LayoutData getLayoutData() {
			LayoutData data = super.getLayoutData();
			if (minHeight > 0)
				data.minimumHeight = minHeight;
			return data;
		}
	}

	public final class ComboBoxEditingSupport extends EditingSupport {

		private ComboBoxEditingSupport(ColumnViewer viewer) {
			super(viewer);
			comboBoxCellEditor = new ComboBoxViewerCellEditor((Composite)getViewer().getControl(),
					SWT.READ_ONLY);
			comboBoxCellEditor.setLabelProvider(new EcoreElementNameLabelProvider());
			comboBoxCellEditor.setContentProvider(new ArrayContentProvider());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return comboBoxCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			if (element instanceof ETypedElement) {
				return true;
			} else
				return false;
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof ETypedElement) {
				return ((ETypedElement)element).getEType();
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (element instanceof ETypedElement && value instanceof EClassifier) {
				ETypedElement data = (ETypedElement)element;
				EClassifier newValue = (EClassifier)value;
				/* only set new value if it differs from old one */
				if (!data.getEType().equals(newValue)) {
					data.setEType(newValue);
					containerCheckedTreeViewer.update(element, null);

				}
			}
		}

	}

	public class EcoreElementLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof EClass) {
				return ((EClass)element).getName();
			} else if (element instanceof EAttribute) {
				return ((EAttribute)element).getName() + " [" + ((EAttribute)element).getLowerBound() + ","
						+ ((EAttribute)element).getUpperBound() + "] : "
						+ ((EAttribute)element).getEType().getName();
			} else if (element instanceof EReference) {
				return ((EReference)element).getName() + " : " + ((EReference)element).getEType().getName();
			}
			return null;
		}
	}

	public class EcoreElementNameLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof ENamedElement) {
				return ((ENamedElement)element).getName();
			}
			return null;
		}
	}

	public class EAttributeUpperBoundLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof EAttribute) {
				return Integer.toString(((EAttribute)element).getUpperBound());
			}
			return null;
		}
	}

	public class EAttributeLowerBoundLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof EAttribute) {
				return Integer.toString(((EAttribute)element).getLowerBound());
			}
			return null;
		}
	}

	public class TypeLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof ETypedElement) {
				return ((ETypedElement)element).getEType().getName();
			}
			return null;
		}
	}

	public class NameEditingSupport extends EditingSupport {
		ContainerCheckedTreeViewer containerCheckedTreeViewer;

		TextCellEditor cellEditor;

		public NameEditingSupport(ContainerCheckedTreeViewer treeViewer, TextCellEditor cE) {
			super(treeViewer);
			containerCheckedTreeViewer = treeViewer;
			cellEditor = cE;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (element instanceof ENamedElement) {
				((ENamedElement)element).setName(value.toString());
			}
			containerCheckedTreeViewer.update(element, null);
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof ENamedElement) {
				return ((ENamedElement)element).getName();
			}
			return element.toString();
		}

		@Override
		protected TextCellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}
	}

	public class BoundEditingSupport extends EditingSupport {
		ContainerCheckedTreeViewer containerCheckedTreeViewer;

		TextCellEditor cellEditor;

		String bound;

		public BoundEditingSupport(ContainerCheckedTreeViewer treeViewer, TextCellEditor cE, String boundPara) {
			super(treeViewer);
			containerCheckedTreeViewer = treeViewer;
			cellEditor = cE;
			bound = boundPara;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (element instanceof EAttribute) {
				if (bound.equals("upper")
						&& ((Tools.toInt(value) >= ((EAttribute)element).getLowerBound() && (Tools
								.toInt(value) >= 0)) || (Tools.toInt(value) == -1))) {
					((EAttribute)element).setUpperBound(Tools.toInt(value));
				} else if (bound.equals("lower")
						&& (Tools.toInt(value) >= 0 && ((Tools.toInt(value) <= ((EAttribute)element)
								.getUpperBound()) || ((EAttribute)element).getUpperBound() == -1))) {
					((EAttribute)element).setLowerBound(Tools.toInt(value));
				}
			}
			containerCheckedTreeViewer.update(element, null);
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof EAttribute) {
				if (bound.equals("upper")) {
					return Integer.toString(((EAttribute)element).getUpperBound());
				} else if (bound.equals("lower")) {
					return Integer.toString(((EAttribute)element).getLowerBound());
				}
			}
			return element.toString();
		}

		@Override
		protected TextCellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			if (element instanceof EAttribute) {
				return true;
			} else
				return false;
		}
	}

	public class MyTreeContentProvider implements ITreeContentProvider {

		private Object[] EMPTY_ARRAY = new Object[0];

		// Called just for the first-level objects.
		// Here we provide a list of objects
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List)
				return ((List<?>)inputElement).toArray();
			else
				return EMPTY_ARRAY;
		}

		// Queried to know if the current node has children
		public boolean hasChildren(Object element) {
			if (element instanceof EClass) {
				return true;
			}
			return false;
		}

		// Queried to load the children of a given node
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof EClass) {
				EClass eClass = (EClass)parentElement;
				List<EObject> attributesReferences = new ArrayList<EObject>();
				attributesReferences.addAll(eClass.getEStructuralFeatures());
				return attributesReferences.toArray();
			}
			return EMPTY_ARRAY;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object getParent(Object element) {
			return null;
		}

	}

	public void applySelection() {

		for (EClassifier eClassifier : candidateImportedMetaClassesToKeep) {
			if (containerCheckedTreeViewer.getChecked(eClassifier)) {
				for (Iterator<EStructuralFeature> iterator = ((EClass)eClassifier)
						.getEAllStructuralFeatures().iterator(); iterator.hasNext();) {
					EObject eObject = iterator.next();
					if (!containerCheckedTreeViewer.getChecked(eObject)
							&& !(eObject instanceof EReference && ((EReference)eObject).getName().equals(
									"eSuperTypes"))) {
						eObjectToDelete.add(eObject);
					}
				}
			} else {
				eObjectToDelete.add(eClassifier);

			}
		}
		Tools.cleanModel(eObjectToDelete);
		Tools.save(ecoreModel);

	}

	public void setProfileEcoreResource(Resource profileEcoreResource) {
		this.profileEcoreResource = profileEcoreResource;
	}
}
