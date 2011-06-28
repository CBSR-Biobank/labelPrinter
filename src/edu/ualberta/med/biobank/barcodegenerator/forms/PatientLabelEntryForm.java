package edu.ualberta.med.biobank.barcodegenerator.forms;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.barcodegenerator.BarcodeGenPlugin;
import edu.ualberta.med.biobank.barcodegenerator.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.barcodegenerator.preferences.PreferenceInitializer;
import edu.ualberta.med.biobank.barcodegenerator.progress.PrintOperation;
import edu.ualberta.med.biobank.barcodegenerator.progress.SaveOperation;
import edu.ualberta.med.biobank.barcodegenerator.template.Template;
import edu.ualberta.med.biobank.barcodegenerator.template.TemplateStore;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.CBSRData;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions.CBSRGuiVerificationException;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.gui.common.forms.Actions;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryForm;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankServerException;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * 
 * View for entering patient information, selecting a logo and picking a
 * template file. The user prints and saves the barcode label prints from this
 * interface.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class PatientLabelEntryForm extends BgcEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.forms.SpecimanLabelEntryForm";

    private Button logoButton = null;

    private Button label1Checkbox = null;
    private Button label2Checkbox = null;
    private Button label3Checkbox = null;

    private Button value1Checkbox = null;
    private Button value2Checkbox = null;
    private Button value3Checkbox = null;

    private Button printBarcode1Checkbox = null;
    private Button printBarcode2Checkbox = null;
    private Button printBarcode3Checkbox = null;

    private Button specimenTypeCheckbox = null;
    private Button savePdfButton = null;

    private BgcBaseText projectTitleText = null;
    private BgcBaseText logoText = null;

    private BgcBaseText label1Text = null;
    private BgcBaseText label2Text = null;
    private BgcBaseText label3Text = null;

    private BgcBaseText value1Text = null;
    private BgcBaseText value2Text = null;
    private BgcBaseText value3Text = null;

    private BgcBaseText patientIDText = null;
    private BgcBaseText specimenTypeText = null;

    private Combo templateCombo = null;
    private Combo printerCombo = null;

    private Label intendedPrinter = null;

    private Shell shell;

    private IPreferenceStore perferenceStore;

    private Template loadedTemplate;
    private TemplateStore templateStore;

    private boolean loggedIn = false;

    @Override
    protected void init() throws Exception {
        setPartName("Patient Labels");
    }

    @Override
    protected void performDoubleClick(DoubleClickEvent event) {
        // do nothing for now
    }

    @Override
    protected Image getFormImage() {
        return null;
    }

    @Override
    public void setFocus() {
        // do nothing for now
    }

    @Override
    public void reset() {
        clearFields();
    }

    private void clearFieldsConfirm() {
        if (BgcPlugin
            .openConfirm("Reset Form Information",
                "Do you want to clear any information that you have entered into this form?")) {
            clearFields();
        }
    }

    private void clearFields() {
        patientIDText.setText("");
        value1Text.setText("");
        value2Text.setText("");
        value3Text.setText("");
    }

    @Override
    public boolean print() {
        PrintOperation printOperation = null;
        BarcodeViewGuiData guiData = null;
        try {
            guiData = new BarcodeViewGuiData();

            List<String> patientIDs = SessionManager.getAppService()
                .executeGetSourceSpecimenUniqueInventoryIds(32);

            // print operation
            printOperation = new PrintOperation(guiData, patientIDs);

            try {
                new ProgressMonitorDialog(shell)
                    .run(true, true, printOperation);
            } catch (InvocationTargetException e1) {
                printOperation.saveFailed();
                printOperation.setError("Error", "InvocationTargetException: "
                    + e1.getCause().getMessage());
            }

            if (printOperation.isSuccessful()) {
                updateSavePreferences();
                clearFieldsConfirm();
                return true;
            }

            if (printOperation.errorExists()) {
                BgcPlugin.openAsyncError(printOperation.getError()[0],
                    printOperation.getError()[1]);
                return false;
            }

        } catch (BiobankServerException e) {
            BgcPlugin.openAsyncError("Specimen ID Error", e.getMessage());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Server Error", e.getMessage());
        } catch (CBSRGuiVerificationException e1) {
            BgcPlugin.openAsyncError("Gui Validation", e1.getMessage());
        } catch (InterruptedException e2) {
            // do nothing
        }
        return false;
    }

    @Override
    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        formActions.addResetAction(Actions.GUI_COMMON_RESET);
        formActions.addPrintAction();
        form.updateToolBar();
    }

    @Override
    protected void createFormContent() throws Exception {
        super.createFormContent();
        form.setText("Patient Labels");
        form.setMessage("Print source specimen labels for a patient",
            IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

        loadPreferenceStore();

        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        brandingGroup();
        patientInfoGroup();
        specimenTextGroup();
        actionButtonGroup();

        sessionSourceProvider
            .addSourceProviderListener(new ISourceProviderListener() {
                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    if (sourceValue != null) {
                        loggedIn = sourceValue
                            .equals(BgcSessionState.LOGGED_IN);
                        updateForm();
                    }
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    @SuppressWarnings("rawtypes") Map sourceValuesByName) {
                    // do nothing for now
                }
            });

        templateStore = null;
        updateForm();

    }

    private void updateForm() {
        try {
            if (loggedIn) {
                if (templateStore == null) {
                    templateStore = new TemplateStore();
                }
                setEnable(true);

                // remove and reload template combo
                templateCombo.removeAll();
                for (String templateName : templateStore.getTemplateNames()) {
                    templateCombo.add(templateName);
                }

                if (templateCombo.getItemCount() > 0)
                    templateCombo.select(0);

                for (int i = 0; i < templateCombo.getItemCount(); i++) {
                    if (templateCombo.getItem(i).equals(
                        perferenceStore
                            .getString(PreferenceConstants.TEMPLATE_NAME))) {
                        templateCombo.select(i);
                        break;
                    }
                }
                templateCombo.redraw();

                loadSelectedTemplate();

            } else {
                setEnable(false);
                templateCombo.removeAll();
                templateCombo.redraw();

            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Database Error",
                "Error while updating form", e);
        }
    }

    private void setEnable(boolean enable) {
        projectTitleText.setEnabled(enable);
        logoText.setEnabled(enable);
        logoButton.setEnabled(enable);
        label1Text.setEnabled(enable);
        value1Checkbox.setEnabled(enable);
        label1Checkbox.setEnabled(enable);
        printBarcode1Checkbox.setEnabled(enable);
        value1Text.setEnabled(enable);
        patientIDText.setEnabled(enable);
        label2Checkbox.setEnabled(enable);
        label2Text.setEnabled(enable);
        value2Checkbox.setEnabled(enable);
        value2Text.setEnabled(enable);
        printBarcode2Checkbox.setEnabled(enable);
        label3Checkbox.setEnabled(enable);
        label3Text.setEnabled(enable);
        value3Checkbox.setEnabled(enable);
        value3Text.setEnabled(enable);
        printBarcode3Checkbox.setEnabled(enable);
        specimenTypeCheckbox.setEnabled(enable);
        specimenTypeText.setEnabled(enable);
        templateCombo.setEnabled(enable);
        printerCombo.setEnabled(enable);
        savePdfButton.setEnabled(enable);
    }

    private void loadPreferenceStore() {
        perferenceStore = null;

        if (BarcodeGenPlugin.getDefault() != null)
            perferenceStore = BarcodeGenPlugin.getDefault()
                .getPreferenceStore();

        if (perferenceStore == null) {
            System.err.println("WARNING: preference store was NULL!");
            perferenceStore = new PreferenceStore("barcodegen.properties");
            PreferenceInitializer.setDefaults(perferenceStore);
        }
    }

    private void createComposite3(Composite group3) {
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.verticalAlignment = GridData.CENTER;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = false;
        Composite composite3 = toolkit.createComposite(group3, SWT.NONE);
        composite3.setLayout(gridLayout);
        composite3.setLayoutData(gridData);
        new Label(composite3, SWT.NONE).setText("Project Title:");

        projectTitleText = new BgcBaseText(composite3, SWT.BORDER);
        projectTitleText.setLayoutData(gridData);
        projectTitleText.setTextLimit(12);
        projectTitleText.setText(perferenceStore
            .getString(PreferenceConstants.PROJECT_TITLE));

        new Label(composite3, SWT.NONE);
        new Label(composite3, SWT.NONE).setText("Logo:");
        logoText = new BgcBaseText(composite3, SWT.BORDER);
        logoText.setEditable(false);
        logoText.setLayoutData(gridData1);
        logoText.setText(perferenceStore
            .getString(PreferenceConstants.LOGO_FILE_LOCATION));
        logoButton = new Button(composite3, SWT.NONE);
        logoButton.setText("Browse...");
        logoButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {

                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Select Logo");
                String[] filterExt = { "*.png" };
                fd.setFilterExtensions(filterExt);
                String selected = fd.open();
                if (selected != null) {
                    logoText.setText(selected);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        GridData gridData21 = new GridData();
        gridData21.grabExcessHorizontalSpace = true;
        gridData21.verticalAlignment = GridData.CENTER;
        gridData21.horizontalAlignment = GridData.FILL;

        new Label(composite3, SWT.NONE).setText("Template:");
        templateCombo = new Combo(composite3, SWT.DROP_DOWN | SWT.BORDER);
        templateCombo.setLayoutData(gridData21);
        templateCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                loadSelectedTemplate();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        new Label(composite3, SWT.NONE);
        new Label(composite3, SWT.NONE).setText("Intended Printer:");

        intendedPrinter = new Label(composite3, SWT.NONE);
        intendedPrinter.setForeground(new Color(shell.getDisplay(), 255, 0, 0));
        intendedPrinter.setText("default");

        new Label(composite3, SWT.NONE);
        new Label(composite3, SWT.NONE).setText("Printer:");

        printerCombo = new Combo(composite3, SWT.DROP_DOWN | SWT.BORDER);
        printerCombo.setLayoutData(gridData21);

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null,
            null);

        for (PrintService ps : services) {
            printerCombo.add(ps.getName());
        }
        if (printerCombo.getItemCount() > 0)
            printerCombo.select(0);

        for (int i = 0; i < printerCombo.getItemCount(); i++) {
            if (printerCombo.getItem(i).equals(
                perferenceStore.getString(PreferenceConstants.PRINTER_NAME))) {
                printerCombo.select(i);
                break;
            }
        }
        loadSelectedTemplate();

    }

    /**
     * Loads the Template from templateCombo.
     * 
     * A new template is loaded for each selection change in templateCombo.
     */
    private void loadSelectedTemplate() {
        if (templateCombo.getSelectionIndex() >= 0) {
            try {

                String comboSelectedTemplate = templateCombo
                    .getItem(templateCombo.getSelectionIndex());

                // already loaded
                if ((loadedTemplate == null)
                    || !loadedTemplate.getName().equals(comboSelectedTemplate)) {
                    loadedTemplate = templateStore
                        .getTemplate(comboSelectedTemplate);
                }

            } catch (Exception ee) {
                BgcPlugin.openAsyncError("Verification Issue",
                    "Could not load template: " + ee.getMessage());
            }

            if (loadedTemplate != null)
                intendedPrinter.setText(loadedTemplate.getPrinterName());
        }

    }

    private void patientInfoGroup() {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalAlignment = GridData.FILL;

        Composite group1 = createSectionWithClient("Patient Information", page);
        group1.setLayoutData(gridData);
        group1.setLayout(new GridLayout());
        createComposite6(group1);
        createComposite5(group1);
    }

    private void createComposite5(Composite group1) {
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 5;
        gridLayout2.makeColumnsEqualWidth = false;
        Composite composite5 = toolkit.createComposite(group1, SWT.NONE);
        composite5.setLayout(gridLayout2);
        composite5.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        new Label(composite5, SWT.NONE).setText("Enable:");
        new Label(composite5, SWT.NONE)
            .setText("Label (Patient Name/PHN/etc):");
        new Label(composite5, SWT.NONE).setText("Enable:");
        new Label(composite5, SWT.NONE).setText("Value (eg BOB MARLEY):");
        new Label(composite5, SWT.NONE).setText("Print Barcode:");

        label1Checkbox = new Button(composite5, SWT.CHECK);
        label1Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.LABEL_CHECKBOX_1));
        label1Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                label1Text.setEnabled(label1Checkbox.getSelection());

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        label1Text = new BgcBaseText(composite5, SWT.BORDER);
        label1Text.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        label1Text.setTextLimit(12);
        label1Text.setText(perferenceStore
            .getString(PreferenceConstants.LABEL_TEXT_1));

        value1Checkbox = new Button(composite5, SWT.CHECK);
        value1Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.VALUE_CHECKBOX_1));
        value1Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (value1Checkbox.getSelection()) {
                    value1Text.setEnabled(true);
                    printBarcode1Checkbox.setEnabled(true);
                } else {
                    value1Text.setText("");
                    value1Text.setEnabled(false);
                    printBarcode1Checkbox.setSelection(false);
                    printBarcode1Checkbox.setEnabled(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        value1Text = new BgcBaseText(composite5, SWT.BORDER);
        value1Text.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        value1Text.setTextLimit(24);

        printBarcode1Checkbox = new Button(composite5, SWT.CHECK);
        printBarcode1Checkbox.setLayoutData(new GridData(GridData.BEGINNING,
            GridData.CENTER, false, false));
        printBarcode1Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.BARCODE_CHECKBOX_1));

        label2Checkbox = new Button(composite5, SWT.CHECK);
        label2Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.LABEL_CHECKBOX_2));
        label2Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                label2Text.setEnabled(label2Checkbox.getSelection());

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        label2Text = new BgcBaseText(composite5, SWT.BORDER);
        label2Text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
            false, false));
        label2Text.setTextLimit(12);
        label2Text.setText(perferenceStore
            .getString(PreferenceConstants.LABEL_TEXT_2));

        value2Checkbox = new Button(composite5, SWT.CHECK);
        value2Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.VALUE_CHECKBOX_2));
        value2Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (value2Checkbox.getSelection()) {
                    value2Text.setEnabled(true);
                    printBarcode2Checkbox.setEnabled(true);
                } else {
                    value2Text.setText("");
                    value2Text.setEnabled(false);
                    printBarcode2Checkbox.setSelection(false);
                    printBarcode2Checkbox.setEnabled(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        value2Text = new BgcBaseText(composite5, SWT.BORDER);
        value2Text.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        value2Text.setTextLimit(24);
        printBarcode2Checkbox = new Button(composite5, SWT.CHECK);
        printBarcode2Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.BARCODE_CHECKBOX_2));

        label3Checkbox = new Button(composite5, SWT.CHECK);
        label3Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.LABEL_CHECKBOX_3));
        label3Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                label3Text.setEnabled(label3Checkbox.getSelection());

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        label3Text = new BgcBaseText(composite5, SWT.BORDER);
        label3Text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
            false, false));
        label3Text.setTextLimit(12);
        label3Text.setText(perferenceStore
            .getString(PreferenceConstants.LABEL_TEXT_3));
        value3Checkbox = new Button(composite5, SWT.CHECK);
        value3Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.VALUE_CHECKBOX_3));
        value3Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (value3Checkbox.getSelection()) {
                    value3Text.setEnabled(true);
                    printBarcode3Checkbox.setEnabled(true);
                } else {
                    value3Text.setText("");
                    value3Text.setEnabled(false);
                    printBarcode3Checkbox.setSelection(false);
                    printBarcode3Checkbox.setEnabled(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        value3Text = new BgcBaseText(composite5, SWT.BORDER);
        value3Text.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        value3Text.setTextLimit(24);
        printBarcode3Checkbox = new Button(composite5, SWT.CHECK);
        printBarcode3Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.BARCODE_CHECKBOX_3));
    }

    private void createComposite6(Composite group1) {
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
        Composite composite6 = toolkit.createComposite(group1, SWT.NONE);
        composite6.setLayout(gridLayout3);
        new Label(composite6, SWT.NONE).setText("Patient ID:");
        patientIDText = new BgcBaseText(composite6, SWT.BORDER);
        patientIDText.setLayoutData(gridData4);
        patientIDText.setTextLimit(12);
        patientIDText.addListener(SWT.Verify, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (!e.text.matches("[{a-zA-Z0-9}]*")) {
                    e.doit = false;
                    return;
                }
            }
        });
    }

    private void specimenTextGroup() {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.FILL;

        GridData gridData2 = new GridData();
        gridData2.widthHint = 150;

        GridLayout gridLayout5 = new GridLayout();
        gridLayout5.numColumns = 4;

        Composite group2 = createSectionWithClient("Additonal Configuration",
            page);
        group2.setLayout(gridLayout5);
        group2.setLayoutData(gridData);

        specimenTypeCheckbox = new Button(group2, SWT.CHECK | SWT.LEFT);
        specimenTypeCheckbox.setText("Enable");
        specimenTypeCheckbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.SPECIMEN_TYPE_CHECKBOX));
        specimenTypeCheckbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                specimenTypeText.setEnabled(specimenTypeCheckbox.getSelection());

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        new Label(group2, SWT.NONE).setText("Specimen Type (on labels):");

        specimenTypeText = new BgcBaseText(group2, SWT.BORDER | SWT.V_SCROLL
            | SWT.SINGLE);
        specimenTypeText.setText(perferenceStore
            .getString(PreferenceConstants.SPECIMEN_TYPE_TEXT));
        specimenTypeText.setTextLimit(25);
        specimenTypeText.setLayoutData(gridData2);
        new Label(group2, SWT.LEFT | SWT.HORIZONTAL).setText("");
        new Label(group2, SWT.NONE);

    }

    private void brandingGroup() {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalAlignment = GridData.FILL;

        GridLayout gridLayout3 = new GridLayout();
        gridLayout3.verticalSpacing = 1;
        gridLayout3.numColumns = 1;
        gridLayout3.makeColumnsEqualWidth = true;

        Composite group3 = createSectionWithClient("Branding", page);
        group3.setLayoutData(gridData);
        group3.setLayout(gridLayout3);

        createComposite3(group3);
    }

    private void actionButtonGroup() {
        GridLayout gridLayout5 = new GridLayout();
        gridLayout5.numColumns = 6;
        gridLayout5.makeColumnsEqualWidth = true;

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalAlignment = GridData.FILL;

        GridData gridData7 = new GridData();
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.horizontalAlignment = GridData.FILL;

        Composite group4 = createSectionWithClient("Actions", page);

        savePdfButton = new Button(group4, SWT.NONE);
        savePdfButton.setText("Export to PDF");
        savePdfButton.addSelectionListener(savePdfListener);
        savePdfButton.setLayoutData(gridData7);

        new Label(group4, SWT.NONE);
        new Label(group4, SWT.NONE);
        new Label(group4, SWT.NONE);
        new Label(group4, SWT.NONE);
        new Label(group4, SWT.NONE);

        group4.setLayout(gridLayout5);
        group4.setLayoutData(gridData);

    }

    /**
     * Should be called after a successful print or save.
     */
    private void updateSavePreferences() {

        perferenceStore.setValue(PreferenceConstants.LOGO_FILE_LOCATION,
            logoText.getText());
        perferenceStore.setValue(PreferenceConstants.PROJECT_TITLE,
            projectTitleText.getText());

        if (templateCombo.getSelectionIndex() >= 0)
            perferenceStore.setValue(PreferenceConstants.TEMPLATE_NAME,
                templateCombo.getItem(templateCombo.getSelectionIndex()));

        if (printerCombo.getSelectionIndex() >= 0)
            perferenceStore.setValue(PreferenceConstants.PRINTER_NAME,
                printerCombo.getItem(printerCombo.getSelectionIndex()));

        perferenceStore.setValue(PreferenceConstants.LABEL_CHECKBOX_1,
            label1Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.LABEL_CHECKBOX_2,
            label2Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.LABEL_CHECKBOX_3,
            label3Checkbox.getSelection());

        perferenceStore.setValue(PreferenceConstants.LABEL_TEXT_1,
            label1Text.getText());
        perferenceStore.setValue(PreferenceConstants.LABEL_TEXT_2,
            label2Text.getText());
        perferenceStore.setValue(PreferenceConstants.LABEL_TEXT_3,
            label3Text.getText());

        perferenceStore.setValue(PreferenceConstants.VALUE_CHECKBOX_1,
            value1Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.VALUE_CHECKBOX_2,
            value2Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.VALUE_CHECKBOX_3,
            value3Checkbox.getSelection());

        perferenceStore.setValue(PreferenceConstants.BARCODE_CHECKBOX_1,
            printBarcode1Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.BARCODE_CHECKBOX_2,
            printBarcode2Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.BARCODE_CHECKBOX_3,
            printBarcode3Checkbox.getSelection());

        perferenceStore.setValue(PreferenceConstants.SPECIMEN_TYPE_CHECKBOX,
            specimenTypeCheckbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.SPECIMEN_TYPE_TEXT,
            specimenTypeText.getText());

    }

    public class BarcodeViewGuiData extends CBSRData {

        public BarcodeViewGuiData() throws CBSRGuiVerificationException {

            projectTileStr = projectTitleText.getText();

            if ((projectTileStr == null) || (projectTileStr.length() == 0)) {
                throw new CBSRGuiVerificationException("Incorrect Title",
                    "A valid title is required.");
            }

            ByteArrayInputStream bis = null;
            try {
                BufferedImage logoImage;

                logoImage = ImageIO.read(new File(logoText.getText()));
                ByteArrayOutputStream binaryOutputStream = new ByteArrayOutputStream();
                if (logoImage != null) {
                    ImageIO.write(logoImage, "PNG", binaryOutputStream);
                    bis = new ByteArrayInputStream(
                        binaryOutputStream.toByteArray());
                } else {
                    bis = null;
                }

            } catch (IOException e) {
                bis = null;
            }
            logoStream = bis;

            fontName = perferenceStore
                .getDefaultString(PreferenceConstants.TEXT_FONT_NAME);

            if (fontName == null)
                fontName = "";

            patientIdStr = patientIDText.getText();
            if ((patientIdStr == null) || (patientIdStr.length() == 0)) {
                throw new CBSRGuiVerificationException("Incorrect PatientID",
                    "A valid patient Id is required.");

            }
            // ------------ patient info start-----------------
            label1Str = null;
            if (label1Checkbox.getSelection()) {
                label1Str = label1Text.getText();
            }
            value1Str = null;
            barcode1Print = false;
            if (value1Checkbox.getSelection()) {
                value1Str = value1Text.getText();
                barcode1Print = printBarcode1Checkbox.getSelection();
            }

            label2Str = null;
            if (label2Checkbox.getSelection()) {
                label2Str = label2Text.getText();
            }
            value2Str = null;
            barcode2Print = false;
            if (value2Checkbox.getSelection()) {
                value2Str = value2Text.getText();
                barcode2Print = printBarcode2Checkbox.getSelection();
            }

            label3Str = null;
            if (label3Checkbox.getSelection()) {
                label3Str = label3Text.getText();
            }
            value3Str = null;
            barcode3Print = false;
            if (value3Checkbox.getSelection()) {
                value3Str = value3Text.getText();
                barcode3Print = printBarcode3Checkbox.getSelection();
            }
            // ------------ patient info end-----------------

            // only need if we are printing.
            if (printerCombo.getSelectionIndex() >= 0)
                printerNameStr = printerCombo.getItem(printerCombo
                    .getSelectionIndex());

            else
                printerNameStr = null;

            specimenTypeStr = null;
            if (specimenTypeCheckbox.getSelection()) {
                specimenTypeStr = specimenTypeText.getText();
            }

            template = loadedTemplate;

            if (template == null) {
                throw new CBSRGuiVerificationException("Verification Issue",
                    "Could not load template.. Selected template is null.");
            }

            if (!(template).jasperTemplateExists()) {
                throw new CBSRGuiVerificationException("Verification Issue",
                    "Template is lacking a jasper file.");
            }
        }
    };

    private SelectionListener savePdfListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            BarcodeViewGuiData guiData = null;

            try {
                guiData = new BarcodeViewGuiData();

                // save dialog for pdf file.
                FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
                fileDialog.setFilterPath(perferenceStore
                    .getString(PreferenceConstants.PDF_DIRECTORY_PATH));
                fileDialog.setOverwrite(true);
                fileDialog.setFileName("default.pdf");
                String pdfFilePath = fileDialog.open();

                if (pdfFilePath == null)
                    return;

                List<String> patientIDs = SessionManager.getAppService()
                    .executeGetSourceSpecimenUniqueInventoryIds(32);

                SaveOperation saveOperation = new SaveOperation(guiData,
                    patientIDs, pdfFilePath);

                try {
                    new ProgressMonitorDialog(shell).run(true, true,
                        saveOperation);

                } catch (InvocationTargetException e1) {
                    saveOperation.saveFailed();
                    saveOperation.setError("Error",
                        "InvocationTargetException: "
                            + e1.getCause().getMessage());

                } catch (InterruptedException e2) {
                    BgcPlugin.openAsyncError("Save error", e2);
                }

                if (saveOperation.isSuccessful()) {
                    String parentDir = new File(pdfFilePath).getParentFile()
                        .getPath();
                    if (parentDir != null)
                        perferenceStore.setValue(
                            PreferenceConstants.PDF_DIRECTORY_PATH, parentDir);

                    updateSavePreferences();
                    clearFieldsConfirm();
                    return;
                }

                if (saveOperation.errorExists()) {
                    BgcPlugin.openAsyncError(saveOperation.getError()[0],
                        saveOperation.getError()[1]);
                }

            } catch (CBSRGuiVerificationException e1) {
                BgcPlugin.openAsyncError("Gui Validation", e1.getMessage());
                return;
            } catch (BiobankServerException e2) {
                BgcPlugin.openAsyncError("Specimen ID Error", e2.getMessage());
            } catch (ApplicationException e3) {
                BgcPlugin.openAsyncError("Server Error", e3.getMessage());
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

}
