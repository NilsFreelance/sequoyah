package org.eclipse.tml.framework.device.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tml.framework.device.factory.DeviceRegistry;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.model.IDeviceRegistry;
import org.eclipse.tml.framework.device.ui.view.provider.DeviceContentProvider;
import org.eclipse.tml.framework.device.ui.view.provider.DeviceLabelProvider;
import org.eclipse.ui.part.ViewPart;


/**
 * Insert the type's description here.
 * @see ViewPart
 */
public class DeviceView extends ViewPart {
	protected TreeViewer treeViewer;
	protected Text text;
	protected DeviceLabelProvider labelProvider;
	
	protected Action onlyBoardGamesAction, atLeatThreeItems;
	protected Action booksBoxesGamesAction, noArticleAction;
	protected Action addBookAction, removeAction;
	protected ViewerFilter onlyBoardGamesFilter, atLeastThreeFilter;
	protected ViewerSorter booksBoxesGamesSorter, noArticleSorter;
	
	protected IDeviceRegistry root;
	
	/**
	 * The constructor.
	 */
	public DeviceView() {
	}

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		/* Create a grid layout object so the text and treeviewer
		 * are layed out the way I want. */
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		
		/* Create a "label" to display information in. I'm
		 * using a text field instead of a lable so you can
		 * copy-paste out of it. */
		text = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(layoutData);
		
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new DeviceContentProvider());
		labelProvider = new DeviceLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
		
		// layout the tree viewer below the text field
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		
		// Create menu, toolbars, filters, sorters.
		//createFiltersAndSorters();
		//createActions();
		//createMenus();
		//createToolbar();
		//hookListeners();
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
	}
	
	protected void createFiltersAndSorters() {
		//atLeastThreeFilter = new ThreeItemFilter();
		//onlyBoardGamesFilter = new BoardgameFilter();
		//booksBoxesGamesSorter = new BookBoxBoardSorter();
		//noArticleSorter = new NoArticleSorter();
	}

	protected void hookListeners() {
//		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				// if the selection is empty clear the label
//				if(event.getSelection().isEmpty()) {
//					text.setText("");
//					return;
//				}
//				if(event.getSelection() instanceof IStructuredSelection) {
//					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
//					StringBuffer toShow = new StringBuffer();
//					for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
//						Object domain = (Model) iterator.next();
//						String value = labelProvider.getText(domain);
//						toShow.append(value);
//						toShow.append(", ");
//					}
//					// remove the trailing comma space pair
//					if(toShow.length() > 0) {
//						toShow.setLength(toShow.length() - 2);
//					}
//					text.setText(toShow.toString());
//				}
//			}
//		});
	}
	
	protected void createActions() {
//		onlyBoardGamesAction = new Action("Only Board Games") {
//			public void run() {
//				updateFilter(onlyBoardGamesAction);
//			}
//		};
//		onlyBoardGamesAction.setChecked(false);
//		
//		atLeatThreeItems = new Action("Boxes With At Least Three Items") {
//			public void run() {
//				updateFilter(atLeatThreeItems);
//			}
//		};
//		atLeatThreeItems.setChecked(false);
//		
//		booksBoxesGamesAction = new Action("Books, Boxes, Games") {
//			public void run() {
//				updateSorter(booksBoxesGamesAction);
//			}
//		};
//		booksBoxesGamesAction.setChecked(false);
//		
//		noArticleAction = new Action("Ignoring Articles") {
//			public void run() {
//				updateSorter(noArticleAction);
//			}
//		};
//		noArticleAction.setChecked(false);
//		
//		addBookAction = new Action("Add Book") {
//			public void run() {
//				addNewBook();
//			}			
//		};
//		addBookAction.setToolTipText("Add a New Book");
//		addBookAction.setImageDescriptor(DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_NEW_BOOK));
//
//		removeAction = new Action("Delete") {
//			public void run() {
//				removeSelected();
//			}			
//		};
//		removeAction.setToolTipText("Delete");
//		removeAction.setImageDescriptor(DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_REMOVE));		
	}
	
	/** Add a new book to the selected moving box.
	 * If a moving box is not selected, use the selected
	 * obect's moving box. 
	 * 
	 * If nothing is selected add to the root. */
	protected void addNewBook() {
//		MovingBox receivingBox;
//		if (treeViewer.getSelection().isEmpty()) {
//			receivingBox = root;
//		} else {
//			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
//			Model selectedDomainObject = (Model) selection.getFirstElement();
//			if (!(selectedDomainObject instanceof MovingBox)) {
//				receivingBox = selectedDomainObject.getParent();
//			} else {
//				receivingBox = (MovingBox) selectedDomainObject;
//			}
//		}
//		receivingBox.add(Book.newBook());
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing. */
	protected void removeSelected() {
//		if (treeViewer.getSelection().isEmpty()) {
//			return;
//		}
//		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
//		/* Tell the tree to not redraw until we finish
//		 * removing all the selected children. */
//		treeViewer.getTree().setRedraw(false);
//		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
//			Model model = (Model) iterator.next();
//			MovingBox parent = model.getParent();
//			parent.remove(model);
//		}
//		treeViewer.getTree().setRedraw(true);
	}
	
	protected void createMenus() {
		IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
	}


	protected void fillMenu(IMenuManager rootMenuManager) {
		IMenuManager filterSubmenu = new MenuManager("Filters");
		rootMenuManager.add(filterSubmenu);
		filterSubmenu.add(onlyBoardGamesAction);
		filterSubmenu.add(atLeatThreeItems);
		
		IMenuManager sortSubmenu = new MenuManager("Sort By");
		rootMenuManager.add(sortSubmenu);
		sortSubmenu.add(booksBoxesGamesAction);
		sortSubmenu.add(noArticleAction);
	}
	
	
	
	protected void updateSorter(Action action) {
//		if(action == booksBoxesGamesAction) {
//			noArticleAction.setChecked(!booksBoxesGamesAction.isChecked());
//			if(action.isChecked()) {
//				treeViewer.setSorter(booksBoxesGamesSorter);
//			} else {
//				treeViewer.setSorter(null);
//			}
//		} else if(action == noArticleAction) {
//			booksBoxesGamesAction.setChecked(!noArticleAction.isChecked());
//			if(action.isChecked()) {
//				treeViewer.setSorter(noArticleSorter);
//			} else {
//				treeViewer.setSorter(null);
//			}
//		}
			
	}
	
	/* Multiple filters can be enabled at a time. */
	protected void updateFilter(Action action) {
//		if(action == atLeatThreeItems) {
//			if(action.isChecked()) {
//				treeViewer.addFilter(atLeastThreeFilter);
//			} else {
//				treeViewer.removeFilter(atLeastThreeFilter);
//			}
//		} else if(action == onlyBoardGamesAction) {
//			if(action.isChecked()) {
//				treeViewer.addFilter(onlyBoardGamesFilter);
//			} else {
//				treeViewer.removeFilter(onlyBoardGamesFilter);
//			}
//		}
	}
	
	protected void createToolbar() {
//		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
//		toolbarManager.add(addBookAction);
//		toolbarManager.add(removeAction);
	}
	
	
	public IDeviceRegistry getInitalInput() {
		DeviceManager.getInstance().loadDevices();
		return DeviceRegistry.getInstance();
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {}

}
