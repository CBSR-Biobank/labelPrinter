package edu.ualberta.med.biobank.barcodegenerator.views;

import java.io.File;

import javax.imageio.ImageIO;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewer;
import javax.swing.JTable;

public class BarcodeView extends ViewPart {

	public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.views.BarcodeView2"; // TODO Needs to be whatever is mentioned in plugin.xml
	private Composite top = null;
	private Composite composite = null;
	private Composite composite1 = null;
	private Composite composite2 = null;
	private Composite composite3 = null;
	private Composite composite4 = null;
	private Label label = null;
	private Text text = null;
	private Label label1 = null;
	private Text text1 = null;
	private Button button = null;
	private Group group = null;
	private Canvas canvas = null;
	private Group group1 = null;
	private Composite composite5 = null;
	private Label label2 = null;
	private Label label3 = null;
	private Label label4 = null;
	private Label label5 = null;
	private Label label6 = null;
	private Text text2 = null;
	private Button checkBox = null;
	private Button checkBox1 = null;
	private Button checkBox2 = null;
	private Text text3 = null;
	private Composite composite6 = null;
	private Label label7 = null;
	private Text text4 = null;
	private Button checkBox3 = null;
	private Text text5 = null;
	private Button checkBox4 = null;
	private Text text6 = null;
	private Button checkBox5 = null;
	private Button checkBox6 = null;
	private Text text7 = null;
	private Button checkBox7 = null;
	private Text text8 = null;
	private Button checkBox8 = null;
	private Group group2 = null;
	private Composite composite7 = null;
	private Label label8 = null;
	private Button checkBox9 = null;
	private Text text9 = null;
	private Label label9 = null;
	private Text text10 = null;
	private Button button2 = null;
	private Composite composite8 = null;
	private Group group3 = null;
	private Group group4 = null;
	private Button button1 = null;
	private Button button3 = null;
	private Composite composite9 = null;
	private Table table = null;
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout.fill = true;
		top = new Composite(parent, SWT.NONE);
		top.setBackground(new Color(Display.getCurrent(), 237, 236, 235));
		createGroup3();
		createComposite1();
		top.setLayout(rowLayout);
		createComposite2();
		createComposite8();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		composite = new Composite(group3, SWT.NONE);
		composite.setBackground(new Color(Display.getCurrent(), 237, 56, 235));
		composite.setLayout(new FillLayout());
		composite.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
		createComposite3();
		createComposite4();
	}

	/**
	 * This method initializes composite1	
	 *
	 */
	private void createComposite1() {
		composite1 = new Composite(top, SWT.NONE);
		composite1.setLayout(new FillLayout());
		createGroup1();
	}

	/**
	 * This method initializes composite2	
	 *
	 */
	private void createComposite2() {
		composite2 = new Composite(top, SWT.NONE);
		createGroup2();
		composite2.setLayout(new FillLayout());
	}

	/**
	 * This method initializes composite3	
	 *
	 */
	private void createComposite3() {
		GridData gridData21 = new GridData();
		gridData21.grabExcessHorizontalSpace = true;
		gridData21.verticalAlignment = GridData.CENTER;
		gridData21.horizontalAlignment = GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.makeColumnsEqualWidth = false;
		composite3 = new Composite(composite, SWT.NONE);
		composite3.setLayout(gridLayout);
		label = new Label(composite3, SWT.NONE);
		label.setText("Project Title:");
		text = new Text(composite3, SWT.BORDER);
		text.setLayoutData(gridData);
		label1 = new Label(composite3, SWT.NONE);
		label1.setText("Logo:");
		text1 = new Text(composite3, SWT.BORDER);
		text1.setEditable(false);
		text1.setLayoutData(gridData1);
		button = new Button(composite3, SWT.NONE);
		button.setText("Browse...");
		Label filler6 = new Label(composite3, SWT.NONE);
		Label filler7 = new Label(composite3, SWT.NONE);
		label9 = new Label(composite3, SWT.NONE);
		label9.setText("Template:");
		text10 = new Text(composite3, SWT.BORDER);
		text10.setEditable(false);
		text10.setLayoutData(gridData21);
		button2 = new Button(composite3, SWT.NONE);
		button2.setText("Browse...");
	}

	/**
	 * This method initializes composite4	
	 *
	 */
	private void createComposite4() {
		GridLayout gridLayout1 = new GridLayout();
		composite4 = new Composite(composite, SWT.NONE);
		composite4.setLayout(gridLayout1);
		createGroup();
	}

	/**
	 * This method initializes group	
	 *
	 */
	private void createGroup() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		group = new Group(composite4, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText("Logo");
		createCanvas();
		group.setLayoutData(gridData2);
	}

	/**
	 * This method initializes canvas	
	 *
	 */
	private void createCanvas() {
		GridData gridData3 = new GridData();
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.verticalAlignment = GridData.FILL;
		gridData3.grabExcessVerticalSpace = true;
		canvas = new Canvas(group, SWT.NONE);
		canvas.setBackground(new Color(Display.getCurrent(), 66, 122, 235));
		canvas.setLayoutData(gridData3);
	}

	/**
	 * This method initializes group1	
	 *
	 */
	private void createGroup1() {
		group1 = new Group(composite1, SWT.NONE);
		group1.setText("Patient Info Fields");
		group1.setLayout(new GridLayout());
		createComposite6();
		createComposite5();
	}

	/**
	 * This method initializes composite5	
	 *
	 */
	private void createComposite5() {
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.BEGINNING;
		gridData11.verticalAlignment = GridData.CENTER;
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.FILL;
		gridData10.verticalAlignment = GridData.CENTER;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.FILL;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.verticalAlignment = GridData.CENTER;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.grabExcessHorizontalSpace = true;
		gridData7.verticalAlignment = GridData.FILL;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessVerticalSpace = false;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.verticalAlignment = GridData.FILL;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 5;
		gridLayout2.makeColumnsEqualWidth = false;
		composite5 = new Composite(group1, SWT.NONE);
		composite5.setLayout(gridLayout2);
		label2 = new Label(composite5, SWT.NONE);
		label2.setText("Enable:");
		label3 = new Label(composite5, SWT.NONE);
		label3.setText("Label (Patient Name/PHN/etc):");
		label4 = new Label(composite5, SWT.NONE);
		label4.setText("Enable:");
		label5 = new Label(composite5, SWT.NONE);
		label5.setText("Value (eg BOB MARLEY):");
		label6 = new Label(composite5, SWT.NONE);
		label6.setText("Print Barcode:");
		checkBox1 = new Button(composite5, SWT.CHECK);
		text2 = new Text(composite5, SWT.BORDER);
		text2.setLayoutData(gridData6);
		checkBox = new Button(composite5, SWT.CHECK);
		text3 = new Text(composite5, SWT.BORDER);
		text3.setLayoutData(gridData7);
		checkBox2 = new Button(composite5, SWT.CHECK);
		checkBox2.setLayoutData(gridData11);
		checkBox3 = new Button(composite5, SWT.CHECK);
		text5 = new Text(composite5, SWT.BORDER);
		text5.setLayoutData(gridData8);
		checkBox4 = new Button(composite5, SWT.CHECK);
		text6 = new Text(composite5, SWT.BORDER);
		text6.setLayoutData(gridData5);
		checkBox5 = new Button(composite5, SWT.CHECK);
		checkBox6 = new Button(composite5, SWT.CHECK);
		text7 = new Text(composite5, SWT.BORDER);
		text7.setLayoutData(gridData10);
		checkBox7 = new Button(composite5, SWT.CHECK);
		text8 = new Text(composite5, SWT.BORDER);
		text8.setLayoutData(gridData9);
		checkBox8 = new Button(composite5, SWT.CHECK);
	}

	/**
	 * This method initializes composite6	
	 *
	 */
	private void createComposite6() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.horizontalSpan = 4;
		gridData4.horizontalIndent = 9;
		gridData4.widthHint = 150;
		gridData4.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.verticalSpacing = 2;
		gridLayout3.numColumns = 5;
		composite6 = new Composite(group1, SWT.NONE);
		composite6.setLayout(gridLayout3);
		label7 = new Label(composite6, SWT.NONE);
		label7.setText("Patient ID:");
		text4 = new Text(composite6, SWT.BORDER);
		text4.setLayoutData(gridData4);
	}

	/**
	 * This method initializes group2	
	 *
	 */
	private void createGroup2() {
		RowLayout rowLayout2 = new RowLayout();
		rowLayout2.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout2.fill = true;
		group2 = new Group(composite2, SWT.NONE);
		createComposite7();
		createComposite9();
		group2.setLayout(rowLayout2);
		group2.setText("Additonal Configuration");
		
		 
		
	}

	/**
	 * This method initializes composite7	
	 *
	 */
	private void createComposite7() {
		GridData gridData13 = new GridData();
		gridData13.widthHint = 150;
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 5;
		composite7 = new Composite(group2, SWT.NONE);
		composite7.setLayout(gridLayout4);
		checkBox9 = new Button(composite7, SWT.CHECK);
		label8 = new Label(composite7, SWT.NONE);
		label8.setText("Sample Type (on labels):");
		Label filler61 = new Label(composite7, SWT.NONE);
		text9 = new Text(composite7, SWT.BORDER);
		text9.setLayoutData(gridData13);
	}

	/**
	 * This method initializes composite8	
	 *
	 */
	private void createComposite8() {
		composite8 = new Composite(top, SWT.NONE);
		composite8.setLayout(new FillLayout());
		createGroup4();
	}

	/**
	 * This method initializes group3	
	 *
	 */
	private void createGroup3() {
		group3 = new Group(top, SWT.NONE);
		group3.setLayout(new FillLayout());
		group3.setText("Branding");
		createComposite();
	}

	/**
	 * This method initializes group4	
	 *
	 */
	private void createGroup4() {
		GridLayout gridLayout5 = new GridLayout();
		gridLayout5.numColumns = 3;
		group4 = new Group(composite8, SWT.NONE);
		group4.setText("Actions");
		group4.setLayout(gridLayout5);
		button1 = new Button(group4, SWT.NONE);
		button1.setText("Exit Label Maker");
		button3 = new Button(group4, SWT.NONE);
		button3.setText("Print Label Sheet");
	}

	/**
	 * This method initializes composite9	
	 *
	 */
	private void createComposite9() {
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = GridData.FILL;
		gridData12.grabExcessHorizontalSpace = true;
		gridData12.grabExcessVerticalSpace = true;
		gridData12.widthHint = -1;
		gridData12.heightHint = 300;
		gridData12.verticalAlignment = GridData.FILL;
		composite9 = new Composite(group2, SWT.NONE);
		composite9.setLayout(new GridLayout());
		table = new Table(composite9, SWT.NONE);
		table.setHeaderVisible(true);
		table.setLayoutData(gridData12);
		table.setLinesVisible(true);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10,460,531"

