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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
    private Composite top = null;
    private Group group = null;
    private Composite composite = null;
    private Composite composite1 = null;
    private Composite composite2 = null;
    private Composite composite3 = null;
    private Group group1 = null;
    private Composite composite4 = null;
    private Button deleteButton = null;
    private Button copyButton = null;
    private Button newButton = null;
    private Button helpButton = null;
    private Button saveButton = null;
    private Composite composite5 = null;
    private Text templateNameText = null;
    private Text printerNameText = null;
    private Text jasperConfigText = null;

    private List templateNamesList = null;
    private Group composite6 = null;
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
        setPartName("replace with this form's tab name");
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
        form.setText("replace with this form's text");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        top = new Composite(page, SWT.NONE);
        top.setLayout(new GridLayout());

        createGroup();

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
        return "fix this OK message";
    }

    @Override
    public void setFocus() {
    }

    private void createGroup() throws ApplicationException {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        group = new Group(top, SWT.NONE);
        group.setText("Templates Editor");
        group.setLayout(new GridLayout());
        group.setLayoutData(gridData);

        createComposite();
        createComposite1();
    }

    private void createComposite() throws ApplicationException {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        gridData1.verticalAlignment = GridData.FILL;

        composite = new Composite(group, SWT.NONE);
        composite.setLayoutData(gridData1);
        composite.setLayout(gridLayout);

        createComposite2();
        new Label(composite, SWT.NONE);
        createComposite3();
    }

    private void createComposite1() {
        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = false;
        gridData2.verticalAlignment = GridData.CENTER;

        composite1 = new Composite(group, SWT.NONE);
        composite1.setLayoutData(gridData2);
        composite1.setLayout(new FillLayout());

        helpButton = new Button(composite1, SWT.NONE);
        helpButton.setText("Help");
        helpButton.addSelectionListener(helpListener);

        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);

        saveButton = new Button(composite1, SWT.NONE);
        saveButton.setText("Save Template");
        saveButton.addSelectionListener(saveAllListener);
    }

    private void createComposite2() throws ApplicationException {
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.BEGINNING;
        gridData3.grabExcessVerticalSpace = true;
        gridData3.grabExcessHorizontalSpace = false;
        gridData3.verticalAlignment = GridData.FILL;

        composite2 = new Composite(composite, SWT.NONE);
        composite2.setLayout(new GridLayout());
        composite2.setLayoutData(gridData3);

        createGroup1();
        createComposite4();
    }

    private void createComposite3() {
        GridData gridData4 = new GridData();
        gridData4.horizontalAlignment = GridData.FILL;
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.grabExcessVerticalSpace = true;
        gridData4.verticalAlignment = GridData.FILL;
        composite3 = new Composite(composite, SWT.NONE);
        composite3.setLayout(new GridLayout());
        composite3.setLayoutData(gridData4);

        createComposite5();
        createComposite62();

    }

    private void createGroup1() throws ApplicationException {
        GridData gridData6 = new GridData();
        gridData6.grabExcessVerticalSpace = true;
        gridData6.verticalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.horizontalAlignment = GridData.FILL;
        FillLayout fillLayout1 = new FillLayout();
        fillLayout1.type = org.eclipse.swt.SWT.VERTICAL;
        group1 = new Group(composite2, SWT.NONE);
        group1.setLayoutData(gridData6);
        group1.setLayout(fillLayout1);
        group1.setText("Templates");
        templateNamesList = new List(group1, SWT.BORDER | SWT.V_SCROLL);
        templateNamesList.addSelectionListener(listListener);

    }

    private void createComposite62() {
        GridData gridData10 = new GridData();
        gridData10.horizontalAlignment = GridData.FILL;
        gridData10.grabExcessVerticalSpace = true;
        gridData10.verticalAlignment = GridData.FILL;
        GridData gridData9 = new GridData();
        gridData9.grabExcessHorizontalSpace = true;
        gridData9.verticalAlignment = GridData.FILL;
        gridData9.grabExcessVerticalSpace = true;
        gridData9.widthHint = -1;
        gridData9.horizontalAlignment = GridData.FILL;

        composite6 = new Group(composite3, SWT.NONE);
        composite6.setLayout(new GridLayout());
        composite6.setLayoutData(gridData10);
        composite6.setText("Configuration");

        configTree = new ConfigurationTree(composite6, SWT.NONE);
    }

    private void createComposite4() {

        composite4 = new Composite(composite2, SWT.NONE);
        composite4.setLayout(new RowLayout());

        newButton = new Button(composite4, SWT.NONE);
        newButton.setText("New");
        newButton.addSelectionListener(newListener);

        copyButton = new Button(composite4, SWT.NONE);
        copyButton.setText("Copy ");
        copyButton.addSelectionListener(copyListener);

        deleteButton = new Button(composite4, SWT.NONE);
        deleteButton.setText("Delete ");
        deleteButton.addSelectionListener(deleteListener);
    }

    private void createComposite5() {
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = GridData.FILL;
        gridData11.grabExcessHorizontalSpace = true;
        gridData11.verticalAlignment = GridData.CENTER;

        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 3;

        composite5 = new Composite(composite3, SWT.NONE);
        composite5.setLayout(gridLayout2);
        composite5.setLayoutData(gridData11);

        new Label(composite5, SWT.NONE).setText("Template Name:");

        templateNameText = new Text(composite5, SWT.BORDER);
        templateNameText.setLayoutData(gridData11);
        templateNameText.setEditable(false);
        templateNameText.setEnabled(true);
        // TODO color

        new Label(composite5, SWT.NONE);
        new Label(composite5, SWT.NONE).setText("Jasper Configuration:");

        jasperConfigText = new Text(composite5, SWT.BORDER);
        jasperConfigText.setLayoutData(gridData11);
        jasperConfigText.setEditable(false);
        jasperConfigText.setEnabled(true);

        new Label(composite5, SWT.NONE);
        new Label(composite5, SWT.NONE).setText("Intended Printer:");

        printerNameText = new Text(composite5, SWT.BORDER);
        printerNameText.setEditable(true);
        printerNameText.setLayoutData(gridData11);
        printerNameText.addModifyListener(printerNameModifyListener);

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
