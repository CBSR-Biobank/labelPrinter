package edu.ualberta.med.biobank.labelprinter.forms;

import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.LoginSessionState;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryForm;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.labelprinter.dialogs.ComboInputDialog;
import edu.ualberta.med.biobank.labelprinter.dialogs.ComboInputDialog.InvalidOptionsException;
import edu.ualberta.med.biobank.labelprinter.dialogs.StringInputDialog;
import edu.ualberta.med.biobank.labelprinter.template.Template;
import edu.ualberta.med.biobank.labelprinter.template.TemplateStore;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.CBSRLabelMaker;
import edu.ualberta.med.biobank.labelprinter.trees.ConfigurationTree;
import edu.ualberta.med.biobank.labelprinter.trees.TreeException;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * 
 * View for making templates. Consists of a configuration tree editor to edit
 * jasper maker specific configuration settings.
 * 
 * @author Thomas Polasek 2011
 * 
 */

public class LabelTemplateEntryForm extends BgcEntryForm implements
    SelectionListener {

    private static final String CBSR_CONFIG_NAME = "CBSR"; //$NON-NLS-1$

    private static final String PRINTER_DEFAULT_NAME = "default"; //$NON-NLS-1$

    public static final String ID =
        "edu.ualberta.med.biobank.labelprinter.forms.TemplateEntryForm"; //$NON-NLS-1$

    private Button deleteButton = null;
    private Button copyButton = null;
    private Button newButton = null;

    private BgcBaseText templateNameText = null;
    private BgcBaseText printerNameText = null;
    private BgcBaseText jasperConfigText = null;

    private List templateNamesList = null;
    private ConfigurationTree configTree = null;
    private String prevTemplateName = null;

    private Shell shell = null;

    private TemplateStore templateStore = null;

    private boolean loggedIn = false;

    private ISourceProviderListener loginProvider = null;

    @Override
    protected void init() throws Exception {
        setPartName(Messages.LabelTemplateEntryForm_title);
    }

    @Override
    protected void performDoubleClick(DoubleClickEvent event) {
        // do nothing
    }

    @Override
    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addConfirmAction();
        form.updateToolBar();
    }

    @Override
    protected void createFormContent() throws Exception {
        super.createFormContent();
        form.setText(Messages.LabelTemplateEntryForm_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        LoginSessionState sessionSourceProvider = BgcPlugin
            .getLoginStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(LoginSessionState.LOGIN_STATE_SOURCE_NAME)
            .equals(LoginSessionState.LOGGED_IN);

        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        createMasterDetail();

        if (loginProvider != null) {
            sessionSourceProvider.removeSourceProviderListener(loginProvider);
            loginProvider = null;
        }

        loginProvider = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceValue != null) {
                    loggedIn = sourceValue.equals(LoginSessionState.LOGGED_IN);
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
        updateForm();
    }

    @Override
    public void dispose() {
        if (loginProvider != null) {
            BgcPlugin.getLoginStateSourceProvider()
                .removeSourceProviderListener(loginProvider);
            loginProvider = null;
        }
        super.dispose();
    }

    protected String getOkMessage() {
        return Messages.LabelTemplateEntryForm_okmsg;
    }

    @Override
    public void setFocus() {
        // do nothing for now
    }

    private void createMasterDetail() {
        page.setLayout(new GridLayout(2, false));
        page.setLayoutData(new GridData(GridData.FILL, GridData.FILL | SWT.TOP,
            true, true));

        // master section
        Composite masterComp = toolkit.createComposite(page);
        masterComp.setLayout(new GridLayout(1, false));
        masterComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        Composite client = createSectionWithClient(
            Messages.LabelTemplateEntryForm_templates_title, masterComp);
        client.setLayout(new GridLayout());
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            true));

        templateNamesList = new List(client, SWT.BORDER | SWT.V_SCROLL);
        templateNamesList.addSelectionListener(listListener);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
        gd.heightHint = 300;
        templateNamesList.setLayoutData(gd);

        Composite composite4 = new Composite(client, SWT.NONE);
        composite4.setLayout(new GridLayout(3, true));
        composite4.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        newButton = toolkit.createButton(composite4,
            Messages.LabelTemplateEntryForm_new_btn, SWT.PUSH);
        newButton.addSelectionListener(this);
        newButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        copyButton = toolkit.createButton(composite4,
            Messages.LabelTemplateEntryForm_copy_btn, SWT.PUSH);
        copyButton.addSelectionListener(this);
        copyButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        deleteButton = toolkit.createButton(composite4,
            Messages.LabelTemplateEntryForm_delete_btn, SWT.PUSH);
        deleteButton.addSelectionListener(this);
        deleteButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        createComposite3();
    }

    private void createComposite3() {
        Composite detailsComp = toolkit.createComposite(page);
        detailsComp.setLayout(new GridLayout(1, false));
        detailsComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        Composite client = createSectionWithClient(
            Messages.LabelTemplateEntryForm_details_label, detailsComp);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL,
            GridData.VERTICAL_ALIGN_BEGINNING, true, true));

        templateNameText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE,
            Messages.LabelTemplateEntryForm_template_name_label);
        templateNameText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, true));
        templateNameText.setEditable(false);
        templateNameText.setEnabled(true);

        jasperConfigText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE,
            Messages.LabelTemplateEntryForm_jasper_config_label);
        jasperConfigText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, true));
        jasperConfigText.setEditable(false);
        jasperConfigText.setEnabled(true);

        printerNameText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE,
            Messages.LabelTemplateEntryForm_intended_printer_label);
        printerNameText.setEditable(true);
        printerNameText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, true));
        printerNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });

        Label l = toolkit.createLabel(client,
            Messages.LabelTemplateEntryForm_config_label);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
        gd.horizontalSpan = 2;
        l.setLayoutData(gd);

        configTree = new ConfigurationTree(client, SWT.NONE);
        configTree.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });
    }

    private void setEnable(boolean enable) {
        configTree.setEnabled(enable);
        deleteButton.setEnabled(enable);
        copyButton.setEnabled(enable);
        newButton.setEnabled(enable);
        printerNameText.setEnabled(enable);
    }

    /**
     * 
     * Updates the template name list and jasper file combo.
     * 
     */
    private void updateForm() {
        try {
            if (loggedIn) {
                if (templateStore == null) {
                    templateStore = new TemplateStore();
                }

                setEnable(true);

                for (String s : templateStore.getTemplateNames())
                    templateNamesList.add(s);

                templateNamesList.redraw();

            } else {
                setEnable(false);
                templateNamesList.removeAll();
                templateNamesList.redraw();

            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.LabelTemplateEntryForm_db_error_title,
                Messages.LabelTemplateEntryForm_update_error_msg, e);
        }
    }

    private SelectionListener listListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {

            String[] selectedItems = templateNamesList.getSelection();

            if ((selectedItems.length == 1)) {

                // ask to save and select the template if its not one that is
                // already selected.
                if (((prevTemplateName == null) || (!prevTemplateName
                    .equals(selectedItems[0])))) {

                    try {
                        save(false);
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError(
                            Messages.LabelTemplateEntryForm_saving_title,
                            NLS.bind(
                                Messages.LabelTemplateEntryForm_save_error_msg,
                                e1.getMessage()));
                        return;
                    }

                    if (selectedItems[0] != null) {
                        Template selectedTemplate;
                        try {
                            selectedTemplate = templateStore
                                .getTemplate(selectedItems[0]);
                        } catch (Exception e1) {
                            BgcPlugin
                                .openAsyncError(
                                    Messages.LabelTemplateEntryForm_template_error_title,
                                    Messages.LabelTemplateEntryForm_template_load_error_msg);
                            return;
                        }

                        if (!selectedTemplate.getName()
                            .equals(selectedItems[0])) {
                            BgcPlugin
                                .openAsyncError(
                                    Messages.LabelTemplateEntryForm_name_error_title,
                                    Messages.LabelTemplateEntryForm_name_error_msg);
                            return;
                        }

                        // load selected template into gui elements
                        try {
                            jasperConfigText.setText(selectedTemplate
                                .getJasperTemplateName());
                        } catch (Exception e1) {
                            jasperConfigText.setText(""); //$NON-NLS-1$
                            BgcPlugin
                                .openAsyncError(
                                    Messages.LabelTemplateEntryForm_template_error_title,
                                    Messages.LabelTemplateEntryForm_jasper_config_error_msg);
                            try {
                                selectedTemplate.delete();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            return;
                        }

                        try {
                            configTree.populateTree(selectedTemplate
                                .getConfiguration());
                        } catch (Exception ee) {
                            jasperConfigText.setText(""); //$NON-NLS-1$
                            BgcPlugin
                                .openAsyncError(
                                    Messages.LabelTemplateEntryForm_template_tree_error_title,
                                    ee.getMessage());
                            try {
                                configTree.populateTree(null);
                            } catch (TreeException e1) {
                                return;
                            }
                        }

                        templateNameText.setText(selectedTemplate.getName());
                        prevTemplateName = selectedTemplate.getName();
                        printerNameText.setText(selectedTemplate
                            .getPrinterName());
                        printerNameText.setEnabled(true);
                        setDirty(selectedTemplate.isNew());

                    } else {
                        prevTemplateName = null;
                        templateNameText
                            .setText(Messages.LabelTemplateEntryForm_select_template_msg);
                        printerNameText.setText(""); //$NON-NLS-1$
                        printerNameText.setEnabled(false);
                        jasperConfigText.setText(""); //$NON-NLS-1$
                        try {
                            configTree.populateTree(null);
                        } catch (TreeException e1) {
                            BgcPlugin
                                .openAsyncError(
                                    Messages.LabelTemplateEntryForm_template_tree_clear_error_title,
                                    e1.getError());
                        }
                    }
                }
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    @Override
    public void confirm() {
        if (save(true)) {
            BgcPlugin.openInformation(
                Messages.LabelTemplateEntryForm_saved_title,
                Messages.LabelTemplateEntryForm_saved_msg);
        }
    }

    public boolean save(boolean isConfirmButton) {
        try {
            try {
                configTree.resetEditor();
            } catch (TreeException e1) {
                BgcPlugin.openAsyncError("Tree Editor", //$NON-NLS-1$
                    "Failed to reset tree editor"); //$NON-NLS-1$
            }

            if (prevTemplateName != null) {
                if (isDirty()) {

                    Template selectedTemplate = templateStore
                        .getTemplate(prevTemplateName);

                    if (isConfirmButton
                        || BgcPlugin
                            .openConfirm(
                                Messages.LabelTemplateEntryForm_saving_confirm_title,
                                NLS.bind(
                                    Messages.LabelTemplateEntryForm_saving_confirm_msg,
                                    selectedTemplate.getName()))) {

                        String printerName = printerNameText.getText();
                        if ((printerName == null)
                            || (printerName.length() == 0)) {
                            selectedTemplate
                                .setPrinterName(PRINTER_DEFAULT_NAME);
                        } else {
                            if (!printerName.equals(selectedTemplate
                                .getPrinterName())) {
                                selectedTemplate.setPrinterName(printerName);
                            }
                        }
                        selectedTemplate.setConfiguration(configTree
                            .getConfiguration());

                        selectedTemplate.persist();
                        setDirty(false);
                        return true;
                    } else {
                        // restore original saved version
                        if (!selectedTemplate.isNew())
                            templateStore.reloadTemplate(prevTemplateName);
                    }

                    setDirty(false);
                }

            }
        } catch (Exception e1) {
            BgcPlugin.openAsyncError(
                Messages.LabelTemplateEntryForm_save_error_title,
                Messages.LabelTemplateEntryForm_save_db_error_msg, e1);
        }
        return false;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == newButton) {
            newButtonSelected(e);
        } else if (e.getSource() == copyButton) {
            copyButtonSelected(e);
        } else if (e.getSource() == deleteButton) {
            deleteButtonSelected(e);
        } else {
            BgcPlugin.openAsyncError("Invalid selection event", //$NON-NLS-1$
                "invalid selection source"); //$NON-NLS-1$
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }

    private void templatesNamesSelectLast() {
        if ((templateNamesList != null)
            && ((templateNamesList.getItemCount() - 1) >= 0)) {
            templateNamesList.select(templateNamesList.getItemCount() - 1);
            templateNamesList.notifyListeners(SWT.Selection, new Event());
        }
    }

    private void newButtonSelected(SelectionEvent e) {
        try {
            StringInputDialog dialog = new StringInputDialog(
                Messages.LabelTemplateEntryForm_new_label,
                Messages.LabelTemplateEntryForm_new_description,
                Messages.LabelTemplateEntryForm_name_label, shell);
            if (dialog.open() == Dialog.OK) {

                String newTemplateName = dialog.getValue();

                if (!templateStore.getTemplateNames().contains(newTemplateName)) {

                    // jasper config combo selection
                    ComboInputDialog jasperComboDialog =
                        new ComboInputDialog(
                            Messages.LabelTemplateEntryForm_jasper_config_selection,
                            Messages.LabelTemplateEntryForm_jasper_config_selection_msg,
                            JasperTemplateWrapper
                                .getTemplateNames(SessionManager
                                    .getAppService()), null, shell);
                    jasperComboDialog.open();

                    String selectedJasperConfig = jasperComboDialog.getValue();
                    if ((selectedJasperConfig == null)
                        || (selectedJasperConfig.length() == 0))
                        return;

                    Template ct = new Template();

                    ct.setName(newTemplateName);
                    ct.setPrinterName(PRINTER_DEFAULT_NAME);
                    ct.setJasperTemplate(JasperTemplateWrapper
                        .getTemplateByName(SessionManager.getAppService(),
                            selectedJasperConfig));

                    if (selectedJasperConfig.equals(CBSR_CONFIG_NAME)) {
                        ct.setConfiguration(CBSRLabelMaker
                            .getDefaultConfiguration());

                    } else {
                        ct.setConfiguration(CBSRLabelMaker
                            .getDefaultConfiguration());
                    }

                    try {
                        templateStore.addTemplate(ct);
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError(
                            Messages.LabelTemplateEntryForm_add_error_title,
                            Messages.LabelTemplateEntryForm_add_error_msg);
                        return;
                    }

                    templateNamesList.add(ct.getName());
                    templatesNamesSelectLast();
                    templateNamesList.redraw();

                } else {
                    BgcPlugin.openAsyncError(
                        Messages.LabelTemplateEntryForm_exists_error_title,
                        Messages.LabelTemplateEntryForm_exists_error_msg);
                    return;
                }
            }
        } catch (ApplicationException e1) {
            BgcPlugin.openAsyncError(
                Messages.LabelTemplateEntryForm_creation_error_title,
                Messages.LabelTemplateEntryForm_creation_error_msg, e1);
        } catch (InvalidOptionsException e1) {
            BgcPlugin
                .openAsyncError(
                    Messages.LabelTemplateEntryForm_creation_error_title,
                    Messages.LabelTemplateEntryForm_creation_no_jasper_config_error_msg,
                    e1);
        } catch (JAXBException e1) {
            BgcPlugin.openAsyncError(
                Messages.LabelTemplateEntryForm_creation_error_title,
                Messages.LabelTemplateEntryForm_creation_config_data_error_msg,
                e1);
        }

    }

    private void copyButtonSelected(SelectionEvent e) {
        try {
            if (prevTemplateName == null)
                return;

            StringInputDialog dialog = new StringInputDialog(
                Messages.LabelTemplateEntryForm_clone_dialog_title,
                Messages.LabelTemplateEntryForm_clone_dialog_msg,
                Messages.LabelTemplateEntryForm_name_label, shell);
            dialog.setValue(prevTemplateName + " copy"); //$NON-NLS-1$

            if (dialog.open() == Dialog.OK) {

                Template selectedTemplate = templateStore
                    .getTemplate(prevTemplateName);

                String newTemplateName = dialog.getValue();

                if (!templateStore.getTemplateNames().contains(newTemplateName)) {

                    Template cloned = selectedTemplate.clone();

                    if (cloned != null) {

                        cloned.setName(newTemplateName);
                        cloned.persist();

                        templateStore.addTemplate(cloned);

                        templateNamesList.add(newTemplateName);
                        templatesNamesSelectLast();
                        templateNamesList.redraw();
                    } else {
                        BgcPlugin.openAsyncError(
                            Messages.LabelTemplateEntryForm_copy_error_title,
                            Messages.LabelTemplateEntryForm_copy_error_msg);
                        return;
                    }
                } else {
                    BgcPlugin.openAsyncError(
                        Messages.LabelTemplateEntryForm_exists_error_title,
                        Messages.LabelTemplateEntryForm_exists_error_msg);
                    return;
                }
            }
        } catch (Exception e1) {
            BgcPlugin.openAsyncError(
                Messages.LabelTemplateEntryForm_create_error_title,
                Messages.LabelTemplateEntryForm_create_error_msg, e1);
        }
    }

    private void deleteButtonSelected(SelectionEvent e) {
        try {

            if (prevTemplateName != null) {
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
                    | SWT.YES | SWT.NO);
                messageBox.setMessage(NLS.bind(
                    Messages.LabelTemplateEntryForm_delete_confirm_msg,
                    prevTemplateName));
                messageBox
                    .setText(Messages.LabelTemplateEntryForm_deleting_title);

                int response = messageBox.open();
                if (response == SWT.YES) {

                    Template selectedTemplate = templateStore
                        .getTemplate(prevTemplateName);

                    templateNamesList.remove(prevTemplateName);
                    templateStore.deleteTemplate(prevTemplateName);

                    if (!selectedTemplate.isNew()) {
                        selectedTemplate.delete();
                    }

                    templateNameText
                        .setText(Messages.LabelTemplateEntryForm_select_msg);

                    prevTemplateName = null;
                    printerNameText.setText(""); //$NON-NLS-1$
                    printerNameText.setEnabled(false);

                    jasperConfigText.setText(""); //$NON-NLS-1$

                    configTree.populateTree(null);

                    templateNamesList.deselectAll();
                    setDirty(false);

                    templatesNamesSelectLast();
                    templateNamesList.redraw();
                }
            }
        } catch (Exception e1) {
            BgcPlugin.openAsyncError(
                Messages.LabelTemplateEntryForm_delete_error_title,
                Messages.LabelTemplateEntryForm_delete_error_msg, e1);
        }
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }
}
