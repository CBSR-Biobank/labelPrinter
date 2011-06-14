package edu.ualberta.med.biobank.barcodegenerator.forms;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.barcodegenerator.dialogs.ComboInputDialog;
import edu.ualberta.med.biobank.barcodegenerator.dialogs.StringInputDialog;
import edu.ualberta.med.biobank.barcodegenerator.template.Template;
import edu.ualberta.med.biobank.barcodegenerator.template.TemplateStore;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.CBSRLabelMaker;
import edu.ualberta.med.biobank.barcodegenerator.trees.ConfigurationTree;
import edu.ualberta.med.biobank.barcodegenerator.trees.TreeException;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.gui.common.forms.BgcFormBase;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * 
 * View for making templates. Consists of a configuration tree editor to edit
 * jasper maker specific configuration settings.
 * 
 * @author Thomas Polasek 2011
 * 
 */

// FIXME add form close listener
public class TemplateEntryForm extends BgcFormBase {

    public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.forms.TemplateEntryForm";
    private Button deleteButton = null;
    private Button copyButton = null;
    private Button newButton = null;
    private Button helpButton = null;
    private Button saveButton = null;

    private BgcBaseText templateNameText = null;

    private BgcBaseText printerNameText = null;

    private BgcBaseText jasperConfigText = null;

    private List templateNamesList = null;
    private ConfigurationTree configTree = null;
    private String prevTemplateName = null;

    private Shell shell;

    boolean templateDirty = false;
    private TemplateStore templateStore;

    private boolean loggedIn = false;

    // constants
    final private String HELP_URL = "http://www.example.com";

    @Override
    protected void init() throws Exception {
        setPartName("Label Templates");
    }

    @Override
    protected void performDoubleClick(DoubleClickEvent event) {
        // do nothing
    }

    @Override
    protected Image getFormImage() {
        // TODO: select an image for this form
        return null;
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Label Templates");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        createMasterDetail();
        createFormButtons();

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
                }
            });
        updateForm();
    }

    protected String getOkMessage() {
        return "Used to create a new template for a printer label";
    }

    @Override
    public void setFocus() {
    }

    private void createMasterDetail() throws ApplicationException {
        Composite topComp = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        topComp.setLayout(layout);
        topComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL
            | SWT.TOP, true, true));

        // master section
        Composite masterComp = toolkit.createComposite(topComp);
        masterComp.setLayout(new GridLayout(1, false));
        masterComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        Composite client = createSectionWithClient("Label Templates",
            masterComp);
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

        newButton = toolkit.createButton(composite4, "New", SWT.PUSH);
        newButton.addSelectionListener(newListener);
        newButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        copyButton = toolkit.createButton(composite4, "Copy", SWT.PUSH);
        copyButton.addSelectionListener(copyListener);
        copyButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        deleteButton = toolkit.createButton(composite4, "Delete", SWT.PUSH);
        deleteButton.addSelectionListener(deleteListener);
        deleteButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        createComposite3(topComp);
    }

    private void createComposite3(Composite parent) {
        Composite detailsComp = toolkit.createComposite(parent);
        detailsComp.setLayout(new GridLayout(1, false));
        detailsComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        Composite client = createSectionWithClient("Templates Details",
            detailsComp);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL,
            GridData.VERTICAL_ALIGN_BEGINNING, true, true));

        templateNameText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE, "Template Name");
        templateNameText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, true));
        templateNameText.setEditable(false);
        templateNameText.setEnabled(true);

        jasperConfigText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE, "Jasper Configuration");
        jasperConfigText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, true));
        jasperConfigText.setEditable(false);
        jasperConfigText.setEnabled(true);

        printerNameText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE, "Intended Printer");
        printerNameText.setEditable(true);
        printerNameText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, true));
        printerNameText.addModifyListener(printerNameModifyListener);

        Label l = toolkit.createLabel(client, "Configuration:");
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
        gd.horizontalSpan = 2;
        l.setLayoutData(gd);

        configTree = new ConfigurationTree(client, SWT.NONE);
    }

    private void createFormButtons() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(5, true));
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        helpButton = toolkit.createButton(composite, "Help", SWT.PUSH);
        helpButton.addSelectionListener(helpListener);
        helpButton.setLayoutData(new GridData(GridData.FILL,
            GridData.BEGINNING, true, true));

        Composite spacer = toolkit.createComposite(composite);
        GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, true,
            true);
        gd.horizontalSpan = 3;
        spacer.setLayoutData(gd);

        saveButton = toolkit.createButton(composite, "Save Template", SWT.PUSH);
        saveButton.addSelectionListener(saveAllListener);
        saveButton.setLayoutData(new GridData(GridData.FILL,
            GridData.BEGINNING, true, true));
    }

    private void setEnable(boolean enable) {
        configTree.setEnabled(enable);
        deleteButton.setEnabled(enable);
        copyButton.setEnabled(enable);
        newButton.setEnabled(enable);
        helpButton.setEnabled(enable);
        saveButton.setEnabled(enable);
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
            BgcPlugin.openAsyncError("Database Error",
                "Error while updating form", e);
        }
    }

    // if (templateDirty || configTree.isDirty()) {

    private SelectionListener listListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            String[] selectedItems = templateNamesList.getSelection();
            if (selectedItems.length == 1) {

                // TODO call resetEditor in the appropiate locations
                try {
                    configTree.resetEditor();
                } catch (TreeException e1) {
                    BgcPlugin.openAsyncError("Tree Editor",
                        "Failed to reset tree editor");
                    return;
                }

                try {
                    saveCurrentTemplate();
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError("Template Saving",
                        "Failed to save template: " + e1.getMessage());
                    return;
                }

                if (selectedItems[0] != null) {
                    Template selectedTemplate;
                    try {
                        selectedTemplate = templateStore
                            .getTemplate(selectedItems[0]);
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError("Template Selection",
                            "Failed to load the selected template");
                        return;
                    }

                    // TODO check that template name matches selectedItem

                    templateNameText.setText(selectedTemplate.getName());
                    prevTemplateName = selectedTemplate.getName();

                    printerNameText.setText(selectedTemplate.getPrinterName());

                    try {
                        jasperConfigText.setText(selectedTemplate
                            .getJasperTemplateName());
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError("Template Selection",
                            "Failed to find the jasper configuration name.");
                    }
                    printerNameText.setEnabled(true);

                    // TODO handle errors with correct branching.

                    try {
                        configTree.populateTree(selectedTemplate
                            .getConfiguration());
                    } catch (Exception ee) {
                        BgcPlugin.openAsyncError(
                            "Error: Could not set Template Configuration Tree",
                            ee.getMessage());
                        return;
                    }
                    templateDirty = selectedTemplate.isNew();

                } else {
                    prevTemplateName = null;
                    templateNameText.setText("Please select a template");
                    printerNameText.setText("");
                    printerNameText.setEnabled(false);
                    jasperConfigText.setText("");
                    try {
                        configTree.populateTree(null);
                    } catch (TreeException e1) {
                        BgcPlugin
                            .openAsyncError(
                                "Error: Could not clear the Template Configuration Tree",
                                e1.getError());
                    }
                    templateDirty = false;

                }

            } else {
                BgcPlugin.openAsyncError("Selection Listener Error",
                    "invalid selected items length: " + selectedItems.length);
                return;
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    // FIXME get the selected jaspertemplatewrapper correctly.
    private JasperTemplateWrapper getJasperTemplateWrapper(String name)
        throws ApplicationException {

        for (JasperTemplateWrapper t : JasperTemplateWrapper
            .getAllTemplates(SessionManager.getAppService())) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    private void saveCurrentTemplate() throws Exception {

        if (prevTemplateName != null) {

            if (templateDirty || configTree.isDirty()) {
                if (BgcPlugin
                    .openConfirm("Template Saving",
                        "Template has been modified, do you want to save your changes?")) {

                    Template selectedTemplate = templateStore
                        .getTemplate(prevTemplateName);

                    String printerName = printerNameText.getText();
                    if (printerName == null || printerName.length() == 0) {
                        selectedTemplate.setPrinterName("default");
                    } else {
                        if (!printerName.equals(selectedTemplate
                            .getPrinterName())) {
                            selectedTemplate.setPrinterName(printerName);
                        }
                    }

                    if (configTree.isDirty()) {
                        selectedTemplate.setConfiguration(configTree
                            .getConfiguration());
                    }
                    selectedTemplate.persist();

                }

            }
            templateDirty = false;
            configTree.unDirty();

        }
    }

    private SelectionListener newListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {

                StringInputDialog dialog = new StringInputDialog(
                    "New Template",
                    "Please enter the name for the new template", "Name", shell);
                if (dialog.open() == Dialog.OK) {

                    String newTemplateName = dialog.getValue();

                    if (!templateStore.getTemplateNames().contains(
                        newTemplateName)) {

                        // jasper config combo selection
                        ComboInputDialog jasperComboDialog = new ComboInputDialog(
                            "Jasper Configuration Selection",
                            "Please select a Jasper Configuration that you would like to base this template on.",
                            JasperTemplateWrapper
                                .getTemplateNames(SessionManager
                                    .getAppService()), null, shell);
                        jasperComboDialog.open();

                        String selectedJasperConfig = jasperComboDialog
                            .getValue();
                        if (selectedJasperConfig == null
                            || selectedJasperConfig.length() == 0)
                            return;

                        Template ct = new Template();

                        ct.setName(newTemplateName);
                        ct.setPrinterName("default");
                        ct.setJasperTemplate(getJasperTemplateWrapper(selectedJasperConfig));

                        if (selectedJasperConfig.equals("CBSR")) {
                            ct.setConfiguration(CBSRLabelMaker
                                .getDefaultConfiguration());

                        } else {
                            ct.setConfiguration(CBSRLabelMaker
                                .getDefaultConfiguration());
                        }

                        templateStore.addTemplate(ct);
                        templateNamesList.add(ct.getName());
                        templateNamesList.redraw();
                    } else {
                        BgcPlugin.openAsyncError("Template Exists",
                            "Your new template must have a unique name.");
                        return;
                    }
                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError("Template Create Error",
                    "Could not create template", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    // FIXME make cloning work.
    private SelectionListener copyListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                if (prevTemplateName == null)
                    return;

                StringInputDialog dialog = new StringInputDialog(
                    "Cloned Template Name",
                    "What is the name of the cloned template?", "Name", shell);
                dialog.setValue(prevTemplateName + " copy");

                if (dialog.open() == Dialog.OK) {

                    Template selectedTemplate = templateStore
                        .getTemplate(prevTemplateName);

                    String newTemplateName = dialog.getValue();

                    if (!templateStore.getTemplateNames().contains(
                        newTemplateName)) {

                        Template cloned = selectedTemplate.clone();

                        if (cloned != null) {

                            cloned.setName(newTemplateName);
                            cloned.persist();

                            templateStore.addTemplate(cloned);
                            templateNamesList.add(newTemplateName);
                            templateNamesList.redraw();
                        } else {
                            BgcPlugin.openAsyncError("Copy Template Error",
                                "Could not copy template. An error occured.");
                            return;
                        }
                    } else {
                        BgcPlugin.openAsyncError("Template Exists",
                            "Your new template must have a unique name.");
                        return;
                    }
                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError("Template Create Error",
                    "Could not create template", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    private SelectionListener deleteListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {

                if (prevTemplateName != null) {
                    MessageBox messageBox = new MessageBox(shell,
                        SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    messageBox.setMessage("Are you sure you want to delete "
                        + prevTemplateName + "?");
                    messageBox.setText("Deleting Template");

                    int response = messageBox.open();
                    if (response == SWT.YES) {

                        Template selectedTemplate = templateStore
                            .getTemplate(prevTemplateName);

                        templateStore.deleteTemplate(prevTemplateName);
                        templateNamesList.remove(selectedTemplate.getName());

                        if (!selectedTemplate.isNew()) {
                            selectedTemplate.delete();
                        }

                        templateNameText.setText("Please select a template");

                        templateNamesList.deselectAll();
                        templateNamesList.redraw();

                        prevTemplateName = null;
                        printerNameText.setText("");
                        printerNameText.setEnabled(false);

                        jasperConfigText.setText("");

                        configTree.populateTree(null);

                    }
                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError("Template Delete Error",
                    "Could not delete template", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    private ModifyListener printerNameModifyListener = new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
            templateDirty = true;
        }
    };

    private SelectionListener saveAllListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                saveCurrentTemplate();
            } catch (Exception e1) {
                BgcPlugin.openAsyncError("Template Save Error",
                    "Could not save the template to the database", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    private SelectionListener helpListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                // TODO make a valid documentation page for help.
                PlatformUI.getWorkbench().getBrowserSupport()
                    .getExternalBrowser().openURL(new URL(HELP_URL));
            } catch (Exception e1) {
                BgcPlugin.openAsyncError("Open URL Problem",
                    "Could not open help url.\n\n" + e1.getMessage());
                return;
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

}
