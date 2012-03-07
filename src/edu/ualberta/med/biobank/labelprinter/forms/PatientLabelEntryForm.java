package edu.ualberta.med.biobank.labelprinter.forms;

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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryForm;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.labelprinter.BarcodeGenPlugin;
import edu.ualberta.med.biobank.labelprinter.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.labelprinter.preferences.PreferenceInitializer;
import edu.ualberta.med.biobank.labelprinter.progress.PrintOperation;
import edu.ualberta.med.biobank.labelprinter.progress.SaveOperation;
import edu.ualberta.med.biobank.labelprinter.template.Template;
import edu.ualberta.med.biobank.labelprinter.template.TemplateStore;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.CBSRData;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.exceptions.CBSRGuiVerificationException;
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

    public static final String ID =
        "edu.ualberta.med.biobank.labelprinter.forms.SpecimanLabelEntryForm"; //$NON-NLS-1$

    public static final BgcLogger logger = BgcLogger
        .getLogger(PatientLabelEntryForm.class.getName());

    private BgcBaseText projectTitleText = null;
    private BgcBaseText logoText = null;
    private BgcBaseText printerText = null;
    private Button logoButton = null;

    private Combo templateCombo = null;
    private Combo printerCombo = null;

    private Button barcode2DTextCheckbox = null;
    private BgcBaseText patientNumText = null;
    private BgcBaseText labelCustomTextField = null;
    private BgcBaseText labelCustomTextValue = null;
    private Button labelCustomFieldTypeCheckbox = null;
    private Button labelCustomValueTypeCheckbox = null;

    private Button customField1Checkbox = null;
    private Button customField2Checkbox = null;
    private Button customField3Checkbox = null;

    private BgcBaseText customField1Text = null;
    private BgcBaseText customField2Text = null;
    private BgcBaseText customField3Text = null;

    private Button customValue1Checkbox = null;
    private Button customValue2Checkbox = null;
    private Button customValue3Checkbox = null;

    private BgcBaseText customValue1Text = null;
    private BgcBaseText customValue2Text = null;
    private BgcBaseText customValue3Text = null;

    private Button printBarcode1Checkbox = null;
    private Button printBarcode2Checkbox = null;
    private Button printBarcode3Checkbox = null;

    private Button savePdfButton = null;

    private Shell shell;

    private IPreferenceStore perferenceStore;

    private Template loadedTemplate;
    private TemplateStore templateStore;

    private boolean loggedIn = false;
    private ISourceProviderListener loginProvider = null;

    @Override
    protected void init() throws Exception {
        setPartName(Messages.PatientLabelEntryForm_main_title);
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
    public void setValues() throws Exception {
        clearFields();
    }

    private void clearFieldsConfirm() {
        if (BgcPlugin.openConfirm(
            Messages.PatientLabelEntryForm_reset_confirm_title,
            Messages.PatientLabelEntryForm_reset_confirm_msg)) {
            clearFields();
        }
    }

    private void clearFields() {
        patientNumText.setText(""); //$NON-NLS-1$
        labelCustomTextValue.setText(""); //$NON-NLS-1$

        customValue1Text.setText(""); //$NON-NLS-1$
        customValue2Text.setText(""); //$NON-NLS-1$
        customValue3Text.setText(""); //$NON-NLS-1$
    }

    @Override
    public boolean print() {
        PrintOperation printOperation = null;
        BarcodeViewGuiData guiData = null;
        try {
            guiData = new BarcodeViewGuiData();

            List<String> patientNumbers = SessionManager.getAppService()
                .executeGetSourceSpecimenUniqueInventoryIds(32);

            // print operation
            printOperation = new PrintOperation(guiData, patientNumbers);

            try {
                new ProgressMonitorDialog(shell)
                    .run(true, true, printOperation);
            } catch (InvocationTargetException e1) {
                printOperation.saveFailed();
                printOperation.setError(
                    Messages.PatientLabelEntryForm_print_error_title,
                    "InvocationTargetException: "//$NON-NLS-1$
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
            BgcPlugin.openAsyncError(
                Messages.PatientLabelEntryForm_specimenid_error_msg,
                e.getMessage());
        } catch (ApplicationException e) {
            BgcPlugin
                .openAsyncError(
                    Messages.PatientLabelEntryForm_server_error_msg,
                    e.getMessage());
        } catch (CBSRGuiVerificationException e1) {
            BgcPlugin.openAsyncError(
                Messages.PatientLabelEntryForm_validation_error_msg,
                e1.getMessage());
        } catch (InterruptedException e2) {
            // do nothing
        }
        return false;
    }

    @Override
    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addResetAction();
        addPrintAction();
        form.updateToolBar();
    }

    @Override
    protected void createFormContent() throws Exception {
        super.createFormContent();
        form.setText(Messages.PatientLabelEntryForm_form_title);
        form.setMessage(Messages.PatientLabelEntryForm_form_description,
            IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

        loadPreferenceStore();

        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        createTopSection();
        createPerLabelInfo();
        createPerSheetInfo();
        createActionButtonsGroup();

        if (loginProvider != null) {
            sessionSourceProvider.removeSourceProviderListener(loginProvider);
            loginProvider = null;
        }

        loginProvider = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceValue != null) {
                    loggedIn = sourceValue.equals(BgcSessionState.LOGGED_IN);
                    updateForm();
                }
            }

            @Override
            public void sourceChanged(int sourcePriority,
                @SuppressWarnings("rawtypes") Map sourceValuesByName) {
                // do nothing for now
            }
        };
        sessionSourceProvider.addSourceProviderListener(loginProvider);

        templateStore = null;
        updateForm();

    }

    @Override
    public void dispose() {
        if (loginProvider != null) {
            BgcPlugin.getSessionStateSourceProvider()
                .removeSourceProviderListener(loginProvider);
            loginProvider = null;
        }
        super.dispose();
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
            BgcPlugin.openAsyncError("Database Error", //$NON-NLS-1$
                "Error while updating form", e); //$NON-NLS-1$
        }
    }

    private void setEnable(boolean enable) {
        projectTitleText.setEnabled(enable);
        logoText.setEnabled(enable);
        logoButton.setEnabled(enable);
        customField1Text.setEnabled(enable);
        customValue1Text.setEnabled(enable);
        patientNumText.setEnabled(enable);
        customField2Text.setEnabled(enable);
        customValue2Text.setEnabled(enable);
        customField3Text.setEnabled(enable);
        customValue3Text.setEnabled(enable);
        labelCustomTextField.setEnabled(enable);
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
            logger.error("WARNING: preference store was NULL!"); //$NON-NLS-1$
            perferenceStore = new PreferenceStore("barcodegen.properties"); //$NON-NLS-1$
            PreferenceInitializer.setDefaults(perferenceStore);
        }
    }

    private void createTopSectionItems(Composite group3) {
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
        new Label(composite3, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_title_label);

        projectTitleText = new BgcBaseText(composite3, SWT.BORDER);
        projectTitleText.setLayoutData(gridData);
        projectTitleText.setTextLimit(12);
        projectTitleText.setText(perferenceStore
            .getString(PreferenceConstants.PROJECT_TITLE));

        new Label(composite3, SWT.NONE);
        new Label(composite3, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_logo_label);
        logoText = new BgcBaseText(composite3, SWT.BORDER);
        logoText.setEditable(false);
        logoText.setLayoutData(gridData1);
        logoText.setText(perferenceStore
            .getString(PreferenceConstants.LOGO_FILE_LOCATION));
        logoButton = new Button(composite3, SWT.NONE);
        logoButton.setText(Messages.PatientLabelEntryForm_browse);
        logoButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {

                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText(Messages.PatientLabelEntryForm_select_logo_msg);
                String[] filterExt = { "*.png" }; //$NON-NLS-1$
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

        new Label(composite3, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_template_label);
        templateCombo = new Combo(composite3, SWT.DROP_DOWN | SWT.BORDER
            | SWT.READ_ONLY);
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
        printerText = this.createReadOnlyLabelledField(composite3, SWT.NONE,
            Messages.PatientLabelEntryForm_intended_printer_label);

        new Label(composite3, SWT.NONE);
        new Label(composite3, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_printer_label);

        printerCombo = new Combo(composite3, SWT.DROP_DOWN | SWT.BORDER
            | SWT.READ_ONLY);
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
                BgcPlugin.openAsyncError(
                    Messages.PatientLabelEntryForm_verification_error_title,
                    NLS.bind(
                        Messages.PatientLabelEntryForm_verification_error_msg,
                        ee.getMessage()));
            }

            if (loadedTemplate != null)
                printerText.setText(loadedTemplate.getPrinterName());
        }

    }

    private void createPerSheetInfo() {

        Composite group1 = createSectionWithClient(
            Messages.PatientLabelEntryForm_patient_info_title, page);

        group1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        group1.setLayout(new GridLayout());

        createPerSheetItems(group1);
    }

    private void createCustomField1(Composite composite5) {
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_custom_field_1_label);

        customField1Checkbox = new Button(composite5, SWT.CHECK);
        customField1Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!customField1Checkbox.getSelection()) {
                    customField1Text.setText(""); //$NON-NLS-1$
                    customField1Checkbox.setEnabled(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        customField1Text = new BgcBaseText(composite5, SWT.BORDER);
        customField1Text.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, false));
        customField1Text.setTextLimit(12);
        customField1Text.setText(perferenceStore
            .getString(PreferenceConstants.LABEL_TEXT_1));

        customField1Checkbox.setSelection((customField1Text.getText() != null)
            && (customField1Text.getText().length() > 0));
        customField1Checkbox.setEnabled((customField1Text.getText() != null)
            && (customField1Text.getText().length() > 0));

        customField1Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((customField1Text.getText() != null)
                    && (customField1Text.getText().length() > 0)) {
                    customField1Checkbox.setEnabled(true);
                    customField1Checkbox.setSelection(true);
                } else {
                    customField1Checkbox.setEnabled(false);
                    customField1Checkbox.setSelection(false);
                }
            }

        });

        printBarcode1Checkbox = new Button(composite5, SWT.CHECK);
        printBarcode1Checkbox.setLayoutData(new GridData(GridData.CENTER,
            GridData.CENTER, false, false));
        printBarcode1Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.BARCODE_CHECKBOX_1));
        printBarcode1Checkbox.setEnabled(false);

        customValue1Checkbox = new Button(composite5, SWT.CHECK);
        customValue1Checkbox.setSelection(false);
        customValue1Checkbox.setEnabled(false);
        customValue1Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!customValue1Checkbox.getSelection()) {
                    customValue1Text.setEnabled(false);
                    customValue1Checkbox.setSelection(false);
                    customValue1Checkbox.setEnabled(false);
                    customValue1Text.setText(""); //$NON-NLS-1$
                    customValue1Text.setEnabled(true);
                    printBarcode1Checkbox.setEnabled(false);

                } else {
                    printBarcode1Checkbox.setEnabled(true);

                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        customValue1Text = new BgcBaseText(composite5, SWT.BORDER);
        customValue1Text.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, false));
        customValue1Text.setTextLimit(24);
        customValue1Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((customValue1Text.getText() != null)
                    && (customValue1Text.getText().length() > 0)) {
                    printBarcode1Checkbox.setEnabled(true);
                    customValue1Checkbox.setSelection(true);
                    customValue1Checkbox.setEnabled(true);

                } else {
                    printBarcode1Checkbox.setEnabled(false);
                    customValue1Checkbox.setSelection(false);
                    customValue1Checkbox.setEnabled(false);
                }
            }

        });

    }

    private void createCustomField2(Composite composite5) {

        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_custom_field_2_label);
        customField2Checkbox = new Button(composite5, SWT.CHECK);
        customField2Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!customField2Checkbox.getSelection()) {
                    customField2Text.setText(""); //$NON-NLS-1$
                    customField2Checkbox.setEnabled(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        customField2Text = new BgcBaseText(composite5, SWT.BORDER);
        customField2Text.setLayoutData(new GridData(GridData.FILL,
            GridData.CENTER, false, false));
        customField2Text.setTextLimit(12);
        customField2Text.setText(perferenceStore
            .getString(PreferenceConstants.LABEL_TEXT_2));

        customField2Checkbox.setSelection((customField2Text.getText() != null)
            && (customField2Text.getText().length() > 0));
        customField2Checkbox.setEnabled((customField2Text.getText() != null)
            && (customField2Text.getText().length() > 0));

        customField2Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((customField2Text.getText() != null)
                    && (customField2Text.getText().length() > 0)) {
                    customField2Checkbox.setEnabled(true);
                    customField2Checkbox.setSelection(true);
                } else {
                    customField2Checkbox.setEnabled(false);
                    customField2Checkbox.setSelection(false);
                }
            }

        });

        printBarcode2Checkbox = new Button(composite5, SWT.CHECK);
        printBarcode2Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.BARCODE_CHECKBOX_2));
        printBarcode2Checkbox.setLayoutData(new GridData(GridData.CENTER,
            GridData.CENTER, false, false));
        printBarcode2Checkbox.setEnabled(false);

        customValue2Checkbox = new Button(composite5, SWT.CHECK);
        customValue2Checkbox.setSelection(false);
        customValue2Checkbox.setEnabled(false);
        customValue2Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!customValue2Checkbox.getSelection()) {
                    customValue2Text.setEnabled(false);
                    customValue2Checkbox.setSelection(false);
                    customValue2Checkbox.setEnabled(false);
                    customValue2Text.setText(""); //$NON-NLS-1$
                    customValue2Text.setEnabled(true);
                    printBarcode2Checkbox.setEnabled(false);

                } else {
                    printBarcode2Checkbox.setEnabled(true);

                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        customValue2Text = new BgcBaseText(composite5, SWT.BORDER);
        customValue2Text.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, false));
        customValue2Text.setTextLimit(24);
        customValue2Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((customValue2Text.getText() != null)
                    && (customValue2Text.getText().length() > 0)) {
                    printBarcode2Checkbox.setEnabled(true);
                    customValue2Checkbox.setSelection(true);
                    customValue2Checkbox.setEnabled(true);

                } else {
                    printBarcode2Checkbox.setEnabled(false);
                    customValue2Checkbox.setSelection(false);
                    customValue2Checkbox.setEnabled(false);
                }
            }

        });

    }

    private void createCustomField3(Composite composite5) {
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_custom_field_3_label);
        customField3Checkbox = new Button(composite5, SWT.CHECK);
        customField3Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!customField3Checkbox.getSelection()) {
                    customField3Text.setText(""); //$NON-NLS-1$
                    customField3Checkbox.setEnabled(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        customField3Text = new BgcBaseText(composite5, SWT.BORDER);
        customField3Text.setLayoutData(new GridData(GridData.FILL,
            GridData.CENTER, false, false));
        customField3Text.setTextLimit(12);
        customField3Text.setText(perferenceStore
            .getString(PreferenceConstants.LABEL_TEXT_3));

        customField3Checkbox.setSelection((customField3Text.getText() != null)
            && (customField3Text.getText().length() > 0));
        customField3Checkbox.setEnabled((customField3Text.getText() != null)
            && (customField3Text.getText().length() > 0));

        customField3Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((customField3Text.getText() != null)
                    && (customField3Text.getText().length() > 0)) {
                    customField3Checkbox.setEnabled(true);
                    customField3Checkbox.setSelection(true);
                } else {
                    customField3Checkbox.setEnabled(false);
                    customField3Checkbox.setSelection(false);
                }
            }

        });

        printBarcode3Checkbox = new Button(composite5, SWT.CHECK);
        printBarcode3Checkbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.BARCODE_CHECKBOX_3));
        printBarcode3Checkbox.setLayoutData(new GridData(GridData.CENTER,
            GridData.CENTER, false, false));
        printBarcode3Checkbox.setEnabled(false);

        customValue3Checkbox = new Button(composite5, SWT.CHECK);
        customValue3Checkbox.setSelection(false);
        customValue3Checkbox.setEnabled(false);
        customValue3Checkbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!customValue3Checkbox.getSelection()) {
                    customValue3Text.setEnabled(false);
                    customValue3Checkbox.setSelection(false);
                    customValue3Checkbox.setEnabled(false);
                    customValue3Text.setText(""); //$NON-NLS-1$
                    customValue3Text.setEnabled(true);
                    printBarcode3Checkbox.setEnabled(false);

                } else {
                    printBarcode3Checkbox.setEnabled(true);

                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        customValue3Text = new BgcBaseText(composite5, SWT.BORDER);
        customValue3Text.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, false));
        customValue3Text.setTextLimit(24);
        customValue3Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((customValue3Text.getText() != null)
                    && (customValue3Text.getText().length() > 0)) {
                    printBarcode3Checkbox.setEnabled(true);
                    customValue3Checkbox.setSelection(true);
                    customValue3Checkbox.setEnabled(true);

                } else {
                    printBarcode3Checkbox.setEnabled(false);
                    customValue3Checkbox.setSelection(false);
                    customValue3Checkbox.setEnabled(false);
                }
            }

        });
    }

    private void createPerSheetItems(Composite group1) {
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 6;
        gridLayout2.makeColumnsEqualWidth = false;
        Composite composite5 = toolkit.createComposite(group1, SWT.NONE);
        composite5.setLayout(gridLayout2);
        composite5.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_fields_label);
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_enable_label);
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_field_name_label);
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_print_barcode_label);
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_enable_label);
        new Label(composite5, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_field_value_label);

        createCustomField1(composite5);
        createCustomField2(composite5);
        createCustomField3(composite5);

    }

    private void createPatientNumberText(Composite group1) {

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.widthHint = 150;

        new Label(group1, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_pnumber_label);
        patientNumText = new BgcBaseText(group1, SWT.BORDER);
        patientNumText.setTextLimit(14);
        patientNumText.setLayoutData(gridData);
    }

    private void createPerLabelInfo() {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.widthHint = 150;

        GridLayout gridLayout5 = new GridLayout();
        gridLayout5.numColumns = 2;
        gridLayout5.makeColumnsEqualWidth = false;

        Composite group2 = createSectionWithClient(
            Messages.PatientLabelEntryForm_more_config_title, page);
        group2.setLayoutData(gridData);
        group2.setLayout(gridLayout5);

        createPatientNumberText(group2);
        createCustomFieldText(group2);
        createBarcode2DTextCheckbox(group2);

    }

    private void createBarcode2DTextCheckbox(Composite group2) {
        new Label(group2, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_barcode2D);
        barcode2DTextCheckbox = new Button(group2, SWT.CHECK | SWT.LEFT);
        barcode2DTextCheckbox.setSelection(perferenceStore
            .getBoolean(PreferenceConstants.BARCODE_2D_TEXT_TYPE_CHECKBOX));

    }

    private void createCustomFieldText(Composite group2) {

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalAlignment = GridData.FILL;

        GridLayout gridLayout3 = new GridLayout();
        gridLayout3.verticalSpacing = 1;
        gridLayout3.numColumns = 4;
        gridLayout3.horizontalSpacing = 10;
        gridLayout3.marginWidth = 0;
        gridLayout3.makeColumnsEqualWidth = false;

        new Label(group2, SWT.NONE)
            .setText(Messages.PatientLabelEntryForm_spec_type_label);

        Composite composite6 = toolkit.createComposite(group2, SWT.NONE);
        composite6.setLayout(gridLayout3);
        composite6.setLayoutData(gridData);

        labelCustomFieldTypeCheckbox = new Button(composite6, SWT.CHECK
            | SWT.LEFT);
        labelCustomFieldTypeCheckbox
            .addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (!labelCustomFieldTypeCheckbox.getSelection()) {
                        labelCustomTextField.setText(""); //$NON-NLS-1$

                        labelCustomTextValue.setText(""); //$NON-NLS-1$
                        labelCustomTextValue.setEnabled(false);
                        labelCustomValueTypeCheckbox.setSelection(false);
                        labelCustomValueTypeCheckbox.setEnabled(false);
                    }

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });

        labelCustomTextField = new BgcBaseText(composite6, SWT.BORDER);
        labelCustomTextField.setText(perferenceStore
            .getString(PreferenceConstants.SPECIMEN_TYPE_TEXT));
        labelCustomTextField.setLayoutData(gridData);
        labelCustomTextField.setTextLimit(25);
        labelCustomTextField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((labelCustomTextField.getText() != null)
                    && (labelCustomTextField.getText().length() > 0)) {
                    labelCustomFieldTypeCheckbox.setSelection(true);
                    labelCustomFieldTypeCheckbox.setEnabled(true);

                    labelCustomTextValue.setEnabled(true);

                } else {
                    labelCustomFieldTypeCheckbox.setSelection(false);
                    labelCustomFieldTypeCheckbox.setEnabled(false);

                    labelCustomTextValue.setText(""); //$NON-NLS-1$
                    labelCustomTextValue.setEnabled(false);
                    labelCustomValueTypeCheckbox.setSelection(false);
                    labelCustomValueTypeCheckbox.setEnabled(false);
                }
            }

        });

        labelCustomFieldTypeCheckbox.setSelection((labelCustomTextField
            .getText() != null)
            && (labelCustomTextField.getText().length() > 0));
        labelCustomFieldTypeCheckbox
            .setEnabled((labelCustomTextField.getText() != null)
                && (labelCustomTextField.getText().length() > 0));

        // /////////////
        labelCustomValueTypeCheckbox = new Button(composite6, SWT.CHECK
            | SWT.LEFT);
        labelCustomValueTypeCheckbox
            .addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (!labelCustomValueTypeCheckbox.getSelection())
                        labelCustomTextValue.setText(""); //$NON-NLS-1$

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });

        labelCustomTextValue = new BgcBaseText(composite6, SWT.BORDER);
        labelCustomTextValue.setLayoutData(gridData);
        labelCustomTextValue.setTextLimit(25);
        labelCustomTextValue.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if ((labelCustomTextValue.getText() != null)
                    && (labelCustomTextValue.getText().length() > 0)) {
                    labelCustomValueTypeCheckbox.setSelection(true);
                    labelCustomValueTypeCheckbox.setEnabled(true);
                } else {
                    labelCustomValueTypeCheckbox.setSelection(false);
                    labelCustomValueTypeCheckbox.setEnabled(false);
                }
            }

        });

        labelCustomValueTypeCheckbox.setSelection((labelCustomTextValue
            .getText() != null)
            && (labelCustomTextValue.getText().length() > 0));
        labelCustomValueTypeCheckbox
            .setEnabled((labelCustomTextValue.getText() != null)
                && (labelCustomTextValue.getText().length() > 0));

    }

    private void createTopSection() {

        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
            false);

        GridLayout gridLayout3 = new GridLayout();
        gridLayout3.verticalSpacing = 1;
        gridLayout3.numColumns = 1;
        gridLayout3.makeColumnsEqualWidth = true;

        Composite group3 = createSectionWithClient(
            Messages.PatientLabelEntryForm_branding_label, page);
        group3.setLayoutData(gridData);
        group3.setLayout(gridLayout3);

        createTopSectionItems(group3);
    }

    private void createActionButtonsGroup() {
        GridLayout gridLayout5 = new GridLayout();
        gridLayout5.numColumns = 6;
        gridLayout5.makeColumnsEqualWidth = true;

        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
            false);

        GridData gridData7 = new GridData();
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.horizontalAlignment = GridData.FILL;

        Composite group4 = createSectionWithClient(
            Messages.PatientLabelEntryForm_actions_label, page);

        savePdfButton = new Button(group4, SWT.NONE);
        savePdfButton.setText(Messages.PatientLabelEntryForm_export_pdf_label);
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
            customField1Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.LABEL_CHECKBOX_2,
            customField2Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.LABEL_CHECKBOX_3,
            customField3Checkbox.getSelection());

        perferenceStore.setValue(PreferenceConstants.LABEL_TEXT_1,
            customField1Text.getText());
        perferenceStore.setValue(PreferenceConstants.LABEL_TEXT_2,
            customField2Text.getText());
        perferenceStore.setValue(PreferenceConstants.LABEL_TEXT_3,
            customField3Text.getText());

        perferenceStore.setValue(PreferenceConstants.VALUE_CHECKBOX_1,
            customValue1Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.VALUE_CHECKBOX_2,
            customValue2Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.VALUE_CHECKBOX_3,
            customValue3Checkbox.getSelection());

        perferenceStore.setValue(PreferenceConstants.BARCODE_CHECKBOX_1,
            printBarcode1Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.BARCODE_CHECKBOX_2,
            printBarcode2Checkbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.BARCODE_CHECKBOX_3,
            printBarcode3Checkbox.getSelection());

        perferenceStore.setValue(PreferenceConstants.SPECIMEN_TYPE_CHECKBOX,
            labelCustomFieldTypeCheckbox.getSelection());
        perferenceStore.setValue(PreferenceConstants.SPECIMEN_TYPE_TEXT,
            labelCustomTextField.getText());

    }

    public class BarcodeViewGuiData extends CBSRData {

        public BarcodeViewGuiData() throws CBSRGuiVerificationException {

            projectTileStr = projectTitleText.getText();

            if ((projectTileStr == null) || (projectTileStr.length() == 0)) {
                throw new CBSRGuiVerificationException(
                    Messages.PatientLabelEntryForm_title_error_title,
                    Messages.PatientLabelEntryForm_title_error_msg);
            }

            ByteArrayInputStream bis = null;
            try {
                BufferedImage logoImage;

                logoImage = ImageIO.read(new File(logoText.getText()));
                ByteArrayOutputStream binaryOutputStream =
                    new ByteArrayOutputStream();
                if (logoImage != null) {
                    ImageIO.write(logoImage, "PNG", binaryOutputStream); //$NON-NLS-1$
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
                fontName = ""; //$NON-NLS-1$

            patientNumberStr = patientNumText.getText();
            if ((patientNumberStr == null) || (patientNumberStr.length() == 0)) {
                throw new CBSRGuiVerificationException(
                    Messages.PatientLabelEntryForm_entry_error_title,
                    Messages.PatientLabelEntryForm_patient_error_msg);

            }
            // ------------ patient info start-----------------
            label1Str = null;
            if (customField1Checkbox.getSelection()) {
                label1Str = customField1Text.getText();

                if ((label1Str == null) || (label1Str.length() == 0)) {
                    throw new CBSRGuiVerificationException(
                        Messages.PatientLabelEntryForm_entry_error_title,
                        Messages.PatientLabelEntryForm_field1_name_error_msg);
                }

            }
            value1Str = null;
            barcode1Print = false;
            if (customValue1Checkbox.getSelection()) {
                value1Str = customValue1Text.getText();
                barcode1Print = printBarcode1Checkbox.getSelection();

                if ((value1Str == null) || (value1Str.length() == 0)) {
                    throw new CBSRGuiVerificationException(
                        Messages.PatientLabelEntryForm_entry_error_title,
                        Messages.PatientLabelEntryForm_field1_value_error_msg);
                }
            }

            label2Str = null;
            if (customField2Checkbox.getSelection()) {
                label2Str = customField2Text.getText();

                if ((label2Str == null) || (label2Str.length() == 0)) {
                    throw new CBSRGuiVerificationException(
                        Messages.PatientLabelEntryForm_entry_error_title,
                        Messages.PatientLabelEntryForm_field2_name_error_msg);
                }
            }

            value2Str = null;
            barcode2Print = false;
            if (customValue2Checkbox.getSelection()) {
                value2Str = customValue2Text.getText();
                barcode2Print = printBarcode2Checkbox.getSelection();

                if ((value2Str == null) || (value2Str.length() == 0)) {
                    throw new CBSRGuiVerificationException(
                        Messages.PatientLabelEntryForm_entry_error_title,
                        Messages.PatientLabelEntryForm_field2_value_error_msg);
                }

            }

            label3Str = null;
            if (customField3Checkbox.getSelection()) {
                label3Str = customField3Text.getText();

                if ((label3Str == null) || (label3Str.length() == 0)) {
                    throw new CBSRGuiVerificationException(
                        Messages.PatientLabelEntryForm_entry_error_title,
                        Messages.PatientLabelEntryForm_field3_name_error_msg);
                }
            }
            value3Str = null;
            barcode3Print = false;
            if (customValue3Checkbox.getSelection()) {
                value3Str = customValue3Text.getText();
                barcode3Print = printBarcode3Checkbox.getSelection();

                if ((value3Str == null) || (value3Str.length() == 0)) {
                    throw new CBSRGuiVerificationException(
                        Messages.PatientLabelEntryForm_entry_error_title,
                        Messages.PatientLabelEntryForm_field3_value_error_msg);
                }

            }
            // ------------ patient info end-----------------

            // only need if we are printing.
            if (printerCombo.getSelectionIndex() >= 0)
                printerNameStr = printerCombo.getItem(printerCombo
                    .getSelectionIndex());

            else
                printerNameStr = null;

            printBarcode2DTextBoolean = barcode2DTextCheckbox.getSelection();

            specimenTypeStr = null;
            if (labelCustomFieldTypeCheckbox.getSelection()) {
                specimenTypeStr = labelCustomTextField.getText();

                if (labelCustomValueTypeCheckbox.getSelection()) {
                    specimenTypeStr = specimenTypeStr + ": " //$NON-NLS-1$
                        + labelCustomTextValue.getText();
                }

            }

            template = loadedTemplate;

            if (template == null) {
                throw new CBSRGuiVerificationException(
                    Messages.PatientLabelEntryForm_verif_error_title,
                    Messages.PatientLabelEntryForm_load_template_error_msg);
            }

            if (!(template).jasperTemplateExists()) {
                throw new CBSRGuiVerificationException(
                    Messages.PatientLabelEntryForm_verif_error_title,
                    Messages.PatientLabelEntryForm_nojasper_file_error_msg);
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
                fileDialog.setFileName("default.pdf"); //$NON-NLS-1$
                String pdfFilePath = fileDialog.open();

                if (pdfFilePath == null)
                    return;

                List<String> patientNumbers = SessionManager.getAppService()
                    .executeGetSourceSpecimenUniqueInventoryIds(32);

                SaveOperation saveOperation = new SaveOperation(guiData,
                    patientNumbers, pdfFilePath);

                try {
                    new ProgressMonitorDialog(shell).run(true, true,
                        saveOperation);

                } catch (InvocationTargetException e1) {
                    saveOperation.saveFailed();
                    saveOperation.setError(
                        Messages.PatientLabelEntryForm_saveop_error_title,
                        "InvocationTargetException: " //$NON-NLS-1$
                            + e1.getCause().getMessage());

                } catch (InterruptedException e2) {
                    BgcPlugin.openAsyncError(
                        Messages.PatientLabelEntryForm_save_error_title, e2);
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
                BgcPlugin.openAsyncError(e1.title, e1.messsage);
                return;
            } catch (BiobankServerException e2) {
                BgcPlugin.openAsyncError(
                    Messages.PatientLabelEntryForm_specId_error_title,
                    e2.getMessage());
            } catch (ApplicationException e3) {
                BgcPlugin.openAsyncError(
                    Messages.PatientLabelEntryForm_server_error_title,
                    e3.getMessage());
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

}
