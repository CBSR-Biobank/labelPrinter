package edu.ualberta.med.biobank.barcodegenerator.views;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.barcodegenerator.dialogs.StringInputDialog;
import edu.ualberta.med.biobank.barcodegenerator.template.Template;
import edu.ualberta.med.biobank.barcodegenerator.template.TemplateStore;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.CBSRLabelMaker;
import edu.ualberta.med.biobank.barcodegenerator.trees.ConfigurationTree;
import edu.ualberta.med.biobank.barcodegenerator.trees.TreeException;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * 
 * View for making templates. Consists of a configuration tree editor to edit
 * jasper maker specific configuration settings.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class TemplateEditorView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.views.TemplateEditorView";
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
    private Label label = null;
    private Text templateNameText = null;
    private Label label1 = null;
    private Text printerNameText = null;
    private Combo jasperFileCombo = null;
    private List templateNamesList = null;
    private Group composite6 = null;
    private ConfigurationTree configTree = null;

    private Shell shell;

    private Template selectedTemplate = null;

    boolean templateDirty = false;

    private TemplateStore templateStore;

    private boolean loggedIn;

    // constants
    final private String HELP_URL = "http://www.example.com";

    @Override
    public void createPartControl(Composite parent) {
        try {
            BgcSessionState sessionSourceProvider = BgcPlugin
                .getSessionStateSourceProvider();

            loggedIn = sessionSourceProvider.getCurrentState()
                .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
                .equals(BgcSessionState.LOGGED_IN);

            shell = parent.getShell();
            top = new Composite(parent, SWT.NONE);
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
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Form Creation Error",
                "Error while creating form", e);
        }
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

                for (String s : templateStore.getTemplateNames())
                    templateNamesList.add(s);
                templateNamesList.setEnabled(true);
                templateNamesList.redraw();

                for (String s : JasperTemplateWrapper
                    .getTemplateNames(SessionManager.getAppService())) {
                    jasperFileCombo.add(s);
                }
                jasperFileCombo.setEnabled(true);
                jasperFileCombo.redraw();

            } else {
                templateNamesList.removeAll();
                templateNamesList.setEnabled(false);
                templateNamesList.redraw();

                jasperFileCombo.removeAll();
                jasperFileCombo.setEnabled(false);
                jasperFileCombo.redraw();

            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Database Error",
                "Error while updating form", e);
        }
    }

    @Override
    public void setFocus() {
    }

    /**
     * This method initializes group
     * 
     * @throws ApplicationException
     * 
     */
    private void createGroup() throws ApplicationException {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        group = new Group(top, SWT.NONE);
        group.setText("Templates Editor");
        group.setLayoutData(gridData);
        createComposite();
        group.setLayout(new GridLayout());
        createComposite1();
    }

    /**
     * This method initializes composite
     * 
     * @throws ApplicationException
     * 
     */
    private void createComposite() throws ApplicationException {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        gridData1.verticalAlignment = GridData.FILL;
        composite = new Composite(group, SWT.NONE);
        createComposite2();
        composite.setLayoutData(gridData1);
        composite.setLayout(gridLayout);
        @SuppressWarnings("unused")
        Label filler = new Label(composite, SWT.NONE);
        createComposite3();
    }

    /**
     * This method initializes composite1
     * 
     */
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

        @SuppressWarnings("unused")
        Label filler22 = new Label(composite1, SWT.NONE);
        @SuppressWarnings("unused")
        Label filler21 = new Label(composite1, SWT.NONE);
        @SuppressWarnings("unused")
        Label filler2 = new Label(composite1, SWT.NONE);
        filler2 = new Label(composite1, SWT.NONE);

        saveButton = new Button(composite1, SWT.NONE);
        saveButton.setText("Save Template");
        saveButton.addSelectionListener(saveAllListener);
    }

    /**
     * This method initializes composite2
     * 
     * @throws ApplicationException
     * 
     */
    private void createComposite2() throws ApplicationException {
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.BEGINNING;
        gridData3.grabExcessVerticalSpace = true;
        gridData3.grabExcessHorizontalSpace = false;
        gridData3.verticalAlignment = GridData.FILL;
        composite2 = new Composite(composite, SWT.NONE);
        createGroup1();
        composite2.setLayout(new GridLayout());
        composite2.setLayoutData(gridData3);
        createComposite4();
    }

    /**
     * This method initializes composite3
     * 
     */
    private void createComposite3() {
        GridData gridData4 = new GridData();
        gridData4.horizontalAlignment = GridData.FILL;
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.grabExcessVerticalSpace = true;
        gridData4.verticalAlignment = GridData.FILL;
        composite3 = new Composite(composite, SWT.NONE);
        createComposite5();
        composite3.setLayoutData(gridData4);
        createComposite62();
        composite3.setLayout(new GridLayout());
    }

    /**
     * This method initializes group1
     * 
     * @throws ApplicationException
     * 
     */
    private void createGroup1() throws ApplicationException {
        GridData gridData6 = new GridData();
        gridData6.grabExcessVerticalSpace = true;
        gridData6.verticalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.horizontalAlignment = GridData.FILL;
        FillLayout fillLayout1 = new FillLayout();
        fillLayout1.type = org.eclipse.swt.SWT.VERTICAL;
        group1 = new Group(composite2, SWT.NONE);
        group1.setText("Templates");
        group1.setLayoutData(gridData6);
        group1.setLayout(fillLayout1);
        templateNamesList = new List(group1, SWT.BORDER | SWT.V_SCROLL);
        templateNamesList.addSelectionListener(listListener);

    }

    private SelectionListener listListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            String[] selectedItems = templateNamesList.getSelection();
            if (selectedItems.length == 1) {

                if (selectedTemplate != null) {

                    if (templateDirty || configTree.isDirty()) {

                        if (BgcPlugin
                            .openConfirm("Template Editor Saving",
                                "Template has been modified, do you want to save your changes?")) {

                            if (!selectedTemplate.jasperTemplateExists()) {
                                BgcPlugin
                                    .openInformation(
                                        "Template does not have a configuration",
                                        "It is essiential that you select a jasper configuration for this template.");
                            }
                            // FIXME check if user selected a valid jasper
                            // config
                            // from the jaspercombo

                            try {
                                selectedTemplate.persist();
                            } catch (Exception e2) {
                                BgcPlugin.openAsyncError("Template Save Error",
                                    "Error occured saving template", e2);
                            }
                        } else {
                            try {
                                selectedTemplate.reload();
                            } catch (Exception e2) {
                                BgcPlugin
                                    .openAsyncError(
                                        "Template Reload Error",
                                        "Could not restore original template version",
                                        e2);
                            }
                        }
                    }
                }

                try {
                    setSelectedTemplate(templateStore
                        .getTemplate(selectedItems[0]));
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError("Template Retrieval Error",
                        "Error occured retrieving template: ", e1);
                }
            } else {
                setSelectedTemplate(null);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    private void updatejasperFileCombo() {

        jasperFileCombo.setEnabled(false);
        jasperFileCombo.deselectAll();

        if (selectedTemplate == null)
            return;

        jasperFileCombo.setEnabled(true);

        String selectedJasperName;
        try {
            selectedJasperName = selectedTemplate.getJasperTemplate();
        } catch (Exception e) {
            selectedJasperName = null;
        }

        if (selectedJasperName == null)
            return;

        int i = 0;
        for (String s : jasperFileCombo.getItems()) {
            if (s.equals(selectedJasperName)) {
                jasperFileCombo.select(i);
                break;
            }
            i++;
        }

    }

    private void setSelectedTemplate(Template t) {

        if (t != null) {

            if (t == selectedTemplate)
                return;

            selectedTemplate = t;

            templateNameText.setText(t.getName());
            printerNameText.setText(t.getPrinterName() != null ? t
                .getPrinterName() : "");

            updatejasperFileCombo();

            try {
                configTree.populateTree(selectedTemplate.getConfiguration());
            } catch (TreeException e) {
                BgcPlugin.openAsyncError("Set Template Error", e.getError());
            } catch (JAXBException e2) {
                BgcPlugin.openAsyncError("Set Template Error", e2.getMessage());
            }
        } else {
            selectedTemplate = null;
            templateNameText.setText("Select a template.");
            printerNameText.setText("");
            jasperFileCombo.setEnabled(false);
            jasperFileCombo.deselectAll();

            try {
                configTree.populateTree(null);
            } catch (TreeException e) {
                BgcPlugin.openAsyncError("Set Template Tree Error",
                    e.getError());
            }
        }
    }

    /**
     * This method initializes composite4
     * 
     */
    private void createComposite4() {

        composite4 = new Composite(composite2, SWT.NONE);
        composite4.setLayout(new RowLayout());
        newButton = new Button(composite4, SWT.NONE);
        newButton.setText("New");
        newButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {

                    StringInputDialog dialog = new StringInputDialog(
                        "New Template",
                        "Please enter the name for the new template", "Name",
                        shell);
                    if (dialog.open() == Dialog.OK) {

                        String newTemplateName = dialog.getValue();

                        if (newTemplateName != null) {

                            if (templateStore.getTemplateNames().contains(
                                newTemplateName)) {
                                BgcPlugin
                                    .openAsyncError("Template Exists",
                                        "Your new template must have a unique name.");
                                return;
                            }

                            Template ct = new Template();

                            // FIXME implement a switch statement that is based
                            // on jasper config names.
                            ct.setConfiguration(CBSRLabelMaker
                                .getDefaultConfiguration());

                            ct.setName(newTemplateName);

                            templateStore.addTemplate(ct);
                            templateNamesList.add(ct.getName());
                            templateNamesList.redraw();
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
        });

        copyButton = new Button(composite4, SWT.NONE);
        copyButton.setText("Copy ");
        copyButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (selectedTemplate != null) {

                        StringInputDialog dialog = new StringInputDialog(
                            "Cloned Template Name",
                            "What is the name of the cloned template?", "Name",
                            shell);
                        dialog.setValue(selectedTemplate.getName() + " copy");
                        if (dialog.open() == Dialog.OK) {
                            String cloneName = dialog.getValue();

                            if (cloneName != null) {

                                if (templateStore.getTemplateNames().contains(
                                    cloneName)) {
                                    BgcPlugin
                                        .openAsyncError("Template Exists",
                                            "Your new template must have a unique name.");
                                    return;
                                }

                                Template clone = selectedTemplate.clone();
                                clone.setName(cloneName);
                                templateStore.addTemplate(clone);
                                templateNamesList.add(clone.getName());
                                templateNamesList.redraw();
                            }
                        }
                    }
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError("Template Copy Error",
                        "Could not copy template", e1);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        deleteButton = new Button(composite4, SWT.NONE);
        deleteButton.setText("Delete ");
        deleteButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (selectedTemplate != null) {
                        MessageBox messageBox = new MessageBox(shell,
                            SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                        messageBox
                            .setMessage("Are you sure you want to delete "
                                + selectedTemplate.getName() + "?");
                        messageBox.setText("Deleting Template");

                        int response = messageBox.open();
                        if (response == SWT.YES) {
                            selectedTemplate.delete();
                            templateStore.deleteTemplate(selectedTemplate);
                            templateNamesList.remove(selectedTemplate.getName());

                            if (templateNamesList.getItemCount() > 0) {
                                templateNamesList.deselectAll();

                                int lastItemIndex = templateNamesList
                                    .getItemCount() - 1;

                                if (lastItemIndex >= 0) {
                                    templateNamesList.select(lastItemIndex);
                                    setSelectedTemplate(Template
                                        .getTemplateByName(templateNamesList
                                            .getItem(lastItemIndex)));
                                } else {
                                    setSelectedTemplate(null);
                                }

                            } else {
                                BgcPlugin
                                    .openAsyncError(
                                        "Template not in Template Store.",
                                        "Template does not exist, already deleted.");
                                return;
                            }
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
        });
    }

    /**
     * This method initializes composite5
     * 
     */
    private void createComposite5() {
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = GridData.FILL;
        gridData11.grabExcessHorizontalSpace = true;
        gridData11.verticalAlignment = GridData.CENTER;
        GridData gridData8 = new GridData();
        gridData8.horizontalAlignment = GridData.FILL;
        gridData8.grabExcessHorizontalSpace = true;
        gridData8.verticalAlignment = GridData.CENTER;
        GridData gridData7 = new GridData();
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.verticalAlignment = GridData.CENTER;
        gridData7.horizontalAlignment = GridData.FILL;
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 3;
        composite5 = new Composite(composite3, SWT.NONE);
        composite5.setLayout(gridLayout2);
        composite5.setLayoutData(gridData11);
        label = new Label(composite5, SWT.NONE);
        label.setText("Template Name:");
        templateNameText = new Text(composite5, SWT.BORDER);
        templateNameText.setLayoutData(gridData7);
        templateNameText.setEditable(false);

        @SuppressWarnings("unused")
        Label filler7 = new Label(composite5, SWT.NONE);
        label = new Label(composite5, SWT.NONE);
        label.setText("Intended Printer:");
        printerNameText = new Text(composite5, SWT.BORDER);
        printerNameText.setEditable(true);
        printerNameText.setLayoutData(gridData7);
        printerNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (selectedTemplate == null || e.widget == null)
                    return;
                selectedTemplate.setPrinterName(((Text) e.widget).getText());
                templateDirty = true;
            }
        });

        filler7 = new Label(composite5, SWT.NONE);
        label1 = new Label(composite5, SWT.NONE);
        label1.setText("Jasper Configuration:");
        jasperFileCombo = new Combo(composite5, SWT.BORDER);
        jasperFileCombo.setLayoutData(gridData8);
        jasperFileCombo.deselectAll();
        jasperFileCombo.setEnabled(false);
        jasperFileCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (selectedTemplate != null) {
                    // templateSelected.setJasperTemplate();

                    // FIXME wasteful... do not set again if it is the same
                    // jasper file.
                    if (jasperFileCombo.getSelectionIndex() >= 0) {
                        String selectedName = jasperFileCombo
                            .getItem(jasperFileCombo.getSelectionIndex());

                        try {
                            selectedTemplate
                                .setJasperTemplate(getJasperTemplateWrapper(selectedName));
                        } catch (ApplicationException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

        });

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
        composite6.setText("Configuration");
        composite6.setLayoutData(gridData10);
        configTree = new ConfigurationTree(composite6, SWT.NONE);
    }

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

    // FIXME get the selected jaspertemplatewrapper correctly.
    private JasperTemplateWrapper getJasperTemplateWrapper(String name)
        throws ApplicationException {

        if (jasperFileCombo.getSelectionIndex() >= 0) {

            for (JasperTemplateWrapper t : JasperTemplateWrapper
                .getAllTemplates(SessionManager.getAppService())) {
                if (t.getName().equals(name)) {
                    return t;
                }
            }
        }
        return null;
    }

    private boolean saveCurrentTemplate() throws Exception {

        try {
            configTree.resetEditor();
        } catch (TreeException e2) {
            BgcPlugin.openAsyncError("Editor Error",
                "Could not reset editor: ", e2);
        }
        if (!templateDirty && !configTree.isDirty()) {
            return true;
        }

        if (selectedTemplate == null) {
            BgcPlugin.openAsyncError("No Template Selected",
                "Cannot save template. Please select a template first.");
            return false;
        }

        if (!selectedTemplate.jasperTemplateExists()) {
            BgcPlugin
                .openInformation("Template Configuration Missing",
                    "It is essiential that you select a jasper configuration for this template.");
        }

        try {
            selectedTemplate.persist();
        } catch (IOException e1) {
            BgcPlugin.openAsyncError("Save Template Error",
                "Could not save template: ", e1);
            return false;
        }
        templateDirty = false;

        return true;
    }

    private SelectionListener saveAllListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                if (saveCurrentTemplate()) {
                    BgcPlugin.openInformation("Template Saved",
                        "Template has been successfully saved.");
                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError("Save Template Error",
                    "Could not save template: ", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

}
