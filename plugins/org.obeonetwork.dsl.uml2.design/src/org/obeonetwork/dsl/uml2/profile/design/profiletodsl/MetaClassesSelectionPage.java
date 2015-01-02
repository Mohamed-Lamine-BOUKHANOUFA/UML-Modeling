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
import org.eclipse.swt.layout.RowLayout;
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

	protected EList<EClassifier> importedMetaClassesInTheProfile = new BasicEList<EClassifier>();

	protected EList<EClassifier> candidateMetaClassesForTheDSL = new BasicEList<EClassifier>();

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

		Composite expandCollapseContainer = new Composite(container, SWT.NULL);
		expandCollapseContainer.setLayout(new RowLayout());

		Button checkAll = new Button(expandCollapseContainer, SWT.NONE);
		checkAll.setText(" v ");
		checkAll.setToolTipText("Check All");
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAllChecked(containerCheckedTreeViewer, true);
			}
		});

		Button unCheckAll = new Button(expandCollapseContainer, SWT.NONE);
		unCheckAll.setText("  ");
		unCheckAll.setToolTipText("Uncheck All");
		unCheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAllChecked(containerCheckedTreeViewer, false);
			}
		});

		Button collapseAll = new Button(expandCollapseContainer, SWT.NONE);
		collapseAll.setText(" - ");
		collapseAll.setToolTipText("Collapse All");
		collapseAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				containerCheckedTreeViewer.collapseAll();
			}
		});

		Button expandAll = new Button(expandCollapseContainer, SWT.NONE);
		expandAll.setText("+");
		expandAll.setToolTipText("Expand All");
		expandAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				containerCheckedTreeViewer.expandAll();
			}
		});

		Button recommendedElements = new Button(expandCollapseContainer, SWT.NONE);
		recommendedElements.setText("&Recommended Elements");
		recommendedElements.setToolTipText("Check Recommended Elements");
		recommendedElements.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkRecommendedElements(containerCheckedTreeViewer);
			}
		});

		Button createEMFProject = new Button(container, SWT.NONE);
		createEMFProject.setText("&Apply selection");
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

	/**
	 * Check the elements that not reference an UML element.
	 * @param containerCheckedTreeViewer_p
	 */
	protected void checkRecommendedElements(ContainerCheckedTreeViewer containerCheckedTreeViewer_p) {
		if (containerCheckedTreeViewer_p.getInput() instanceof List) {
			List<?> treeInput = (List<?>)containerCheckedTreeViewer_p.getInput();
			for (Object object : treeInput) {
				if (object instanceof EClass) {
					EClass eClass = (EClass)object;
					if (eClass.getEStructuralFeatures().isEmpty()) {
						containerCheckedTreeViewer_p.setChecked(object, true);

					} else {
						for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures()) {
							if (eStructuralFeature.getEType().eResource().getURI().toString()
									.endsWith("uml.ecore")) {
								containerCheckedTreeViewer_p.setChecked(eStructuralFeature, false);
							} else {
								containerCheckedTreeViewer_p.setChecked(eStructuralFeature, true);
							}

						}
					}
				}
			}
		}
	}

	/**
	 * initialize the input of the meta classes selection page.
	 */
	public void initInput() {

		ecoreModel = GenericUMLProfileTools.load(profileEcoreResource.getURI(),
				EcorePackage.Literals.EPACKAGE);
		MetaClassesSelection metaClassesSelection = new MetaClassesSelection(ecoreModel);
		metaClassesSelection.createCandidateMetaClassesAndReferences();
		importedMetaClassesInTheProfile = metaClassesSelection.getImportedMetaClassesInTheProfile();
		candidateMetaClassesForTheDSL = metaClassesSelection.getCandidateMetaClassesForTheDSL();
		if (containerCheckedTreeViewer.getInput() == null) {
			containerCheckedTreeViewer.setInput(candidateMetaClassesForTheDSL);
			containerCheckedTreeViewer.setSubtreeChecked(candidateMetaClassesForTheDSL, true);
		}


		comboBoxCellEditor.setInput(Tools.getAllEClassifiers(ecoreModel));
		containerCheckedTreeViewer.expandAll();
		containerCheckedTreeViewer.collapseAll();
	}

	/**
	 * This class adds border to the text box and sets it height to 10 pixels more than the font height.
	 *
	 * @author Mohamed-Lamine BOUKHANOUFA <a
	 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> * *
	 */
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

	/**
	 * Cell Editing support for the Meta Classes Selection page.
	 *
	 * @author Mohamed-Lamine BOUKHANOUFA <a
	 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> * *
	 */
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

	/**
	 * The label provider for viewers that have column support such as TreeViewer and TableViewerell.
	 *
	 * @author Mohamed-Lamine BOUKHANOUFA <a
	 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> * *
	 */
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

	/**
	 * The label provider for viewers that have column support such as TreeViewer and TableViewerell.
	 *
	 * @author Mohamed-Lamine BOUKHANOUFA <a
	 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> * *
	 */
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

	/**
	 * Cell Editing support for the Meta Classes Selection page.
	 *
	 * @author Mohamed-Lamine BOUKHANOUFA <a
	 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> * *
	 */
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

	/**
	 * Cell Editing support for the Meta Classes Selection page.
	 *
	 * @author Mohamed-Lamine BOUKHANOUFA <a
	 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> * *
	 */
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

	/**
	 * Content providers for tree-structure-oriented viewers.
	 *
	 * @author Mohamed-Lamine BOUKHANOUFA <a
	 *         href="mailto:mohamed-lamine.boukhanoufa@obeo.fr">mohamed-lamine.boukhanoufa@obeo.fr</a> * *
	 */
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

	/**
	 * Apply the user meta classes selection to the DSL meta model.
	 */
	public void applySelection() {

		for (EClassifier candidateMetaClasseForTheDSL : candidateMetaClassesForTheDSL) {
			if (containerCheckedTreeViewer.getChecked(candidateMetaClasseForTheDSL)) {
				if (!ecoreModel.getEClassifiers().contains(candidateMetaClasseForTheDSL)) {
					ecoreModel.getEClassifiers().add(candidateMetaClasseForTheDSL);
				}
				for (Iterator<EStructuralFeature> iterator = ((EClass)candidateMetaClasseForTheDSL)
						.getEAllStructuralFeatures().iterator(); iterator.hasNext();) {
					EObject eObject = iterator.next();
					if (!containerCheckedTreeViewer.getChecked(eObject)
							&& !(eObject instanceof EReference && ((EReference)eObject).getName().equals(
									"eSuperTypes"))) {
						eObjectToDelete.add(eObject);
					}
				}
			} else {
				eObjectToDelete.add(candidateMetaClasseForTheDSL);

			}
		}
		Tools.cleanModel(eObjectToDelete);
		Tools.save(ecoreModel);

	}

	/**
	 * Set the profile ecore resource.
	 * 
	 * @param profileEcoreResource
	 */
	public void setProfileEcoreResource(Resource profileEcoreResource) {
		this.profileEcoreResource = profileEcoreResource;
	}
    
	/**
	 * Sets to the given value the checked state for all elements in this viewer.
	 *
	 * @param containerCheckedTreeViewer_p
	 *            the {@link ContainerCheckedTreeViewer}
	 * @param state
	 *            <code>true</code> if the element should be checked, and <code>false</code> if it should be
	 *            unchecked
	 */

	public void setAllChecked(ContainerCheckedTreeViewer containerCheckedTreeViewer_p, boolean state) {
		if (containerCheckedTreeViewer_p.getInput() instanceof List) {
			List<?> treeInput = (List<?>)containerCheckedTreeViewer_p.getInput();
			for (Object object : treeInput) {
				containerCheckedTreeViewer_p.setChecked(object, state);
			}
		}

	}
}
