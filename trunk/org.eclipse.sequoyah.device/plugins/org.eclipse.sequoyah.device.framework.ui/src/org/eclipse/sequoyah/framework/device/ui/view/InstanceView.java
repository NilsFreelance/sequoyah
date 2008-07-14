/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Ot�vio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * Daniel Barboza Franco (Motorola) - Bug [239970] - Invisible Services
 ********************************************************************************/
package org.eclipse.tml.framework.device.ui.view;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.factory.IInstanceListeners;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceRegistry;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.ServiceHandlerAction;
import org.eclipse.tml.framework.device.ui.view.provider.InstanceContentProvider;
import org.eclipse.tml.framework.device.ui.view.provider.InstanceLabelProvider;
import org.eclipse.tml.framework.device.ui.view.sorter.InstanceSorter;
import org.eclipse.tml.framework.device.ui.view.sorter.StatusSorter;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ISharedImages;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Insert the type's description here.
 * @see ViewPart
 */
public class InstanceView extends ViewPart implements IInstanceListeners, IPartListener2 {
	
	private static final String MENU_DELETE = "Delete";
	private static final String MENU_PROPERTIES = "Properties";
	private static final String MENU_SORT_BY = "Sort by";
	private static final String ACTION_DEVICES = "Devices";
	private static final String ACTION_STATUS = "Status";
	private static final String PROPERTY_EDITOR_ID = "org.eclipse.tml.framework.device.ui.editors.InstancePropertyEditorDefault";
	
	protected TreeViewer treeViewer;
	protected Text text;
	protected InstanceLabelProvider labelProvider;
	protected boolean enablePropertiesMenu = false;	
	protected Action instanceSorterAction,statusSorterAction;
	protected ViewerSorter instanceSorter,statusSorter;
	protected IInstanceRegistry root;

	/**
	 * Constructor - Insert the type's description here
	 */
	public InstanceView() {
		InstanceRegistry.getInstance().addListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partClosed(IWorkbenchPartReference ref) {
		// TODO How do we know if the closed view is this one ?
		if (ref.getPart(false) == this) {
			InstanceRegistry.getInstance().removeListener(InstanceView.this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partActivated(IWorkbenchPartReference partRef) { }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partBroughtToTop(IWorkbenchPartReference partRef) {	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partDeactivated(IWorkbenchPartReference partRef) { }
		
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partHidden(IWorkbenchPartReference partRef) { }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partInputChanged(IWorkbenchPartReference partRef) {	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partOpened(IWorkbenchPartReference partRef) { }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partVisible(IWorkbenchPartReference partRef) { }

	/*
	 * Menu handler
	 */
	private class MenuDeleteListener implements Listener {
		public void handleEvent(Event event) {
			removeSelected();
		}
	}
	
	/**
	 * Menu handler
	 */
	private class MenuPropertiesListener implements Listener {
		public void handleEvent(Event event) {
		
			IAdaptable adaptable = InstanceManager.getInstance().getCurrentInstance();
			
			Shell shell = new Shell();
			PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(
					shell,
					adaptable,
					InstanceView.PROPERTY_EDITOR_ID,
					new String[] {},
					null);
			dialog.open();
		}
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
		
		// TODO - Provider without Viewer
		treeViewer.setContentProvider(new InstanceContentProvider());
		labelProvider = new InstanceLabelProvider();
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
		createFiltersAndSorters();
		createActions();
		createMenus();
		hookListeners();
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
	}
	
	protected void createFiltersAndSorters() {
		instanceSorter = new InstanceSorter();
		statusSorter = new StatusSorter();
	}

	protected void hookListeners() {
	    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				enablePropertiesMenu = false;

				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) {
					text.setText("");
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					
					StringBuffer toShow = new StringBuffer();
					for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
						Object domain = iterator.next();
						if (domain instanceof IInstance) {							
							String value = labelProvider.getText(domain);
							toShow.append(value);
							toShow.append(", ");
							InstanceManager.getInstance().setInstance((IInstance)domain);
							enablePropertiesMenu = true;
						}
					}
					// remove the trailing comma space pair
					if(toShow.length() > 0) {
						toShow.setLength(toShow.length() - 2);
					}
					text.setText(InstanceManager.getInstance().getCurrentInstance().getName());
				}
			}
		});
	}
	
	protected void createActions() {
		instanceSorterAction = new Action(InstanceView.ACTION_DEVICES) {
			public void run() {
				updateSorter(instanceSorterAction);
			}
		};
		instanceSorterAction.setChecked(false);
		
		statusSorterAction = new Action(InstanceView.ACTION_STATUS) {
			public void run() {
				updateSorter(statusSorterAction);
			}
		};
		statusSorterAction.setChecked(false);
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing.
	 */
	protected void removeSelected() {
		if (treeViewer.getSelection().isEmpty()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		InstanceRegistry instanceRegistry = InstanceRegistry.getInstance();
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			IInstance instance = (IInstance) iterator.next();
			InstanceRegistry.getInstance().removeInstance(instance);
		}
		instanceRegistry.setDirty(true);
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
		IMenuManager sortSubmenu = new MenuManager(InstanceView.MENU_SORT_BY);
		rootMenuManager.add(sortSubmenu);
		sortSubmenu.add(instanceSorterAction);
		sortSubmenu.add(statusSorterAction);
		
		final Menu menu = new Menu(treeViewer.getTree()); 
		treeViewer.getTree().setMenu(menu); 
		menu.addMenuListener(new MenuAdapter() { 
			public void menuShown(MenuEvent e) { 
		        // Get rid of existing menu items 
		        MenuItem[] items = menu.getItems(); 
		        for (int i = 0; i < items.length; i++) { 
		           ((MenuItem) items[i]).dispose(); 
		        }
		        if (enablePropertiesMenu == true) {
		        	fillMenuContext(menu,InstanceManager.getInstance().getCurrentInstance());	           
		        }
			}
		}); 
	}

	protected void fillMenuContext(Menu menu, IInstance instance) {
        IDevice device = DeviceManager.getInstance().getDevice(instance);
		MenuItem newItem = null;
		
		newItem = new MenuItem(menu, SWT.PUSH);
	    newItem.setText(InstanceView.MENU_DELETE);
        newItem.addListener(SWT.Selection, new MenuDeleteListener());
        newItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
        
		newItem = new MenuItem(menu, SWT.SEPARATOR);
        
		newItem = new MenuItem(menu, SWT.PUSH);
	    newItem.setText(InstanceView.MENU_PROPERTIES);
        newItem.addListener(SWT.Selection, new MenuPropertiesListener());
        
		newItem = new MenuItem(menu, SWT.SEPARATOR);
				
		// TODO: verify this code
		for (IService service:device.getServices()){
			if (service.isVisible()) {
				newItem = new MenuItem(menu, SWT.PUSH);		
				newItem.setImage(service.getImage().createImage());
				newItem.setEnabled((service.getStatusTransitions(instance.getStatus())!=null));
				newItem.setText(service.getName());
				newItem.addListener(SWT.Selection,  new ServiceHandlerAction(instance,service.getHandler()));
			}
		}
	}
	
	protected void updateSorter(Action action) {
		if(action == instanceSorterAction) {			
			if(action.isChecked()) {
				treeViewer.setSorter(instanceSorter);
				statusSorterAction.setChecked(!instanceSorterAction.isChecked());
			} else {
				treeViewer.setSorter(null);
			}
		} else if(action == statusSorterAction) {					
			if(action.isChecked()) {
				instanceSorterAction.setChecked(!statusSorterAction.isChecked());
				treeViewer.setSorter(statusSorter);
			} else {
				treeViewer.setSorter(null);
			}
		}
			
	}

	public IInstanceRegistry getInitalInput() {
		InstanceManager.getInstance();
		return InstanceRegistry.getInstance();
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {}

	public void dirtyChanged() {
		treeViewer.setInput(getInitalInput());
		treeViewer.refresh();
		treeViewer.expandAll();
	}
}
