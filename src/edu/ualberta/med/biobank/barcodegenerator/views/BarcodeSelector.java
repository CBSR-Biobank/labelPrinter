package edu.ualberta.med.biobank.barcodegenerator.views;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;

public class BarcodeSelector extends ViewPart {

	public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.views.BarcodeSelector"; // TODO Needs to be whatever is mentioned in plugin.xml
	private Composite top = null;
	private Group group = null;
	private Composite composite = null;
	private Composite composite1 = null;
	private Label label = null;
	private Combo combo = null;
	private Table table = null;
	private TableViewer tableViewer = null;
	private Composite composite2 = null;
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
		createGroup();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes group	
	 *
	 */
	private void createGroup() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		group = new Group(top, SWT.NONE);
		group.setText("Configuration");
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout());
		createComposite();
		createComposite1();
		createComposite2();
	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite = new Composite(group, SWT.NONE);
		composite.setLayout(gridLayout);
		label = new Label(composite, SWT.NONE);
		label.setText("Template File:");
		createCombo();
	}

	/**
	 * This method initializes composite1	
	 *
	 */
	private void createComposite1() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.heightHint = 300;
		gridData2.widthHint = 300;
		gridData2.verticalAlignment = GridData.FILL;
		composite1 = new Composite(group, SWT.NONE);
		composite1.setLayout(new GridLayout());
		table = new Table(composite1, SWT.NONE);
		table.setHeaderVisible(true);
		table.setLayoutData(gridData2);
		table.setLinesVisible(true);
		tableViewer = new TableViewer(table);
	}

	/**
	 * This method initializes combo	
	 *
	 */
	private void createCombo() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		combo = new Combo(composite, SWT.V_SCROLL);
		combo.setLayoutData(gridData1);
	}

	/**
	 * This method initializes composite2	
	 *
	 */
	private void createComposite2() {
		composite2 = new Composite(group, SWT.NONE);
		composite2.setLayout(new GridLayout());
	}

}  //  @jve:decl-index=0:visual-constraint="10,10,358,332"
