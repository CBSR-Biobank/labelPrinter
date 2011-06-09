package edu.ualberta.med.biobank.barcodegenerator.views;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBException;

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

import edu.ualberta.med.biobank.barcodegenerator.dialogs.StringInputDialog;
import edu.ualberta.med.biobank.barcodegenerator.template.Template;
import edu.ualberta.med.biobank.barcodegenerator.template.TemplateStore;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.CBSRLabelMaker;
import edu.ualberta.med.biobank.barcodegenerator.trees.ConfigurationTree;
import edu.ualberta.med.biobank.barcodegenerator.trees.TreeException;
import edu.ualberta.med.biobank.barcodegenerator.views.JasperFileEditorView.JasperFileStore;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.gui.common.GuiCommonSessionState;
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
    private Group templateEditorGroup = null;
    private Composite composite = null;
    private Composite actionComposite = null;
    private Composite composite2 = null;
    private Composite rightSideComposite = null;
    private Group group1 = null;
    private Composite templateActionsComposite = null;
    private Button deleteButton = null;
    private Button copyButton = null;
    private Button newButton = null;
    private Button helpButton = null;
    private Button saveButton = null;
    private Composite templateFieldsComposite = null;
    private Text templateNameText = null;
    private Label jasperConfigLabel = null;
    private Text printerNameText = null;
    private Combo jasperFileCombo = null;
    private List list = null;
    private Group configTreeComposite = null;
    private ConfigurationTree configTree = null;
    private Shell shell = null;

    GridData fillGridData = null;
    GridData horizontalGridData = null;
    GridData verticalGridData = null;

    private TemplateStore templateStore;
    private Template templateSelected = null;
    boolean templateDirty = false;
    private boolean loggedIn;

    // constants
    final private String HELP_URL = "http://www.example.com";

    @Override
    public void createPartControl(Composite parent) {
        try {
            GuiCommonSessionState sessionSourceProvider = BiobankGuiCommonPlugin
                .getSessionStateSourceProvider();

            loggedIn = sessionSourceProvider.getCurrentState()
                .get(GuiCommonSessionState.SESSION_STATE_SOURCE_NAME)
                .equals(GuiCommonSessionState.LOGGED_IN);

            shell = parent.getShell();
            top = new Composite(parent, SWT.NONE);
            top.setLayout(new GridLayout());

            fillGridData = new GridData();
            fillGridData.horizontalAlignment = GridData.FILL;
            fillGridData.grabExcessHorizontalSpace = true;
            fillGridData.grabExcessVerticalSpace = true;
            fillGridData.verticalAlignment = GridData.FILL;

            horizontalGridData = new GridData();
            horizontalGridData.horizontalAlignment = GridData.FILL;
            horizontalGridData.grabExcessHorizontalSpace = true;
            horizontalGridData.grabExcessVerticalSpace = false;
            horizontalGridData.verticalAlignment = GridData.CENTER;

            verticalGridData = new GridData();
            verticalGridData.horizontalAlignment = GridData.FILL;
            verticalGridData.grabExcessVerticalSpace = true;
            verticalGridData.grabExcessHorizontalSpace = false;
            verticalGridData.verticalAlignment = GridData.FILL;

            createGroup();

            sessionSourceProvider
                .addSourceProviderListener(new ISourceProviderListener() {
                    @Override
                    public void sourceChanged(int sourcePriority,
                        String sourceName, Object sourceValue) {
                        if (sourceValue != null) {
                            loggedIn = sourceValue
                                .equals(GuiCommonSessionState.LOGGED_IN);
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
            BiobankGuiCommonPlugin.openAsyncError("Form Creation Error",
                "Error while creating form", e);
        }
    }

    @Override
    public void setFocus() {
    }

    private void createGroup() throws ApplicationException {
        templateEditorGroup = new Group(top, SWT.NONE);
        templateEditorGroup.setText("Templates Editor");
        templateEditorGroup.setLayoutData(fillGridData);
        templateEditorGroup.setLayout(new GridLayout());

        createComposite();
        createActionComposite();
    }

    private void createComposite() throws ApplicationException {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        composite = new Composite(templateEditorGroup, SWT.NONE);
        composite.setLayoutData(fillGridData);
        composite.setLayout(gridLayout);

        createComposite2();
        new Label(composite, SWT.NONE);
        createComposite3();
    }

    private void createActionComposite() {

        actionComposite = new Composite(templateEditorGroup, SWT.NONE);
        actionComposite.setLayoutData(horizontalGridData);
        actionComposite.setLayout(new FillLayout());

        helpButton = new Button(actionComposite, SWT.NONE);
        helpButton.addSelectionListener(helpListener);
        helpButton.setText("Help");

        new Label(actionComposite, SWT.NONE);
        new Label(actionComposite, SWT.NONE);
        new Label(actionComposite, SWT.NONE);
        new Label(actionComposite, SWT.NONE);

        saveButton = new Button(actionComposite, SWT.NONE);
        saveButton.addSelectionListener(saveAllListener);
        saveButton.setText("Save Template");
    }

    private void createComposite2() throws ApplicationException {
        composite2 = new Composite(composite, SWT.NONE);
        composite2.setLayout(new GridLayout());
        composite2.setLayoutData(verticalGridData);

        createGroup1();
        createTemplateListActionsComposite();
    }

    private void createComposite3() {
        rightSideComposite = new Composite(composite, SWT.NONE);
        rightSideComposite.setLayoutData(fillGridData);
        rightSideComposite.setLayout(new GridLayout());

        createTemplateFieldsComposite();
        createConfigTreeComposite();
    }

    private void createGroup1() throws ApplicationException {
        FillLayout fillLayout1 = new FillLayout();
        fillLayout1.type = org.eclipse.swt.SWT.VERTICAL;

        group1 = new Group(composite2, SWT.NONE);
        group1.setLayoutData(fillGridData);
        group1.setLayout(fillLayout1);
        group1.setText("Templates");

        list = new List(group1, SWT.BORDER | SWT.V_SCROLL);
        list.addSelectionListener(listListener);

    }

    private void createTemplateFieldsComposite() {
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = GridData.FILL;
        gridData11.grabExcessHorizontalSpace = true;
        gridData11.verticalAlignment = GridData.CENTER;
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 3;

        templateFieldsComposite = new Composite(rightSideComposite, SWT.NONE);
        templateFieldsComposite.setLayout(gridLayout2);
        templateFieldsComposite.setLayoutData(gridData11);

        Label templateNameLabel = new Label(templateFieldsComposite, SWT.NONE);
        templateNameLabel.setText("Template Name:");

        templateNameText = new Text(templateFieldsComposite, SWT.BORDER);
        templateNameText.setLayoutData(gridData11);
        templateNameText.setEditable(false);

        new Label(templateFieldsComposite, SWT.NONE);

        Label intendedPrinterLabel = new Label(templateFieldsComposite,
            SWT.NONE);
        intendedPrinterLabel.setText("Intended Printer:");

        printerNameText = new Text(templateFieldsComposite, SWT.BORDER);
        printerNameText.setLayoutData(gridData11);
        printerNameText.setEditable(true);
        printerNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (templateSelected == null || e.widget == null)
                    return;
                templateSelected.setPrinterName(((Text) e.widget).getText());
                templateDirty = true;
            }
        });

        new Label(templateFieldsComposite, SWT.NONE);

        jasperConfigLabel = new Label(templateFieldsComposite, SWT.NONE);
        jasperConfigLabel.setText("Jasper Configuration:");

        jasperFileCombo = new Combo(templateFieldsComposite, SWT.BORDER);
        jasperFileCombo.setLayoutData(gridData11);
        jasperFileCombo.setEnabled(false);
        for (String s : JasperFileStore.getNames()) {
            jasperFileCombo.add(s);
        }
        jasperFileCombo.deselectAll();

    }

    private void createConfigTreeComposite() {
        configTreeComposite = new Group(rightSideComposite, SWT.NONE);
        configTreeComposite.setLayoutData(fillGridData);
        configTreeComposite.setLayout(new GridLayout());
        configTreeComposite.setText("Configuration");

        configTree = new ConfigurationTree(configTreeComposite, SWT.NONE);
    }

    private void createTemplateListActionsComposite() {

        templateActionsComposite = new Composite(composite2, SWT.NONE);
        templateActionsComposite.setLayout(new RowLayout());

        newButton = new Button(templateActionsComposite, SWT.NONE);
        newButton.addSelectionListener(newListener);
        newButton.setText("New");

        copyButton = new Button(templateActionsComposite, SWT.NONE);
        copyButton.addSelectionListener(copyListner);
        copyButton.setText("Copy ");

        deleteButton = new Button(templateActionsComposite, SWT.NONE);
        deleteButton.addSelectionListener(deleteListner);
        deleteButton.setText("Delete ");

    }

    private void updatejasperFileCombo() {

        jasperFileCombo.setEnabled(false);
        jasperFileCombo.deselectAll();

        if (templateSelected == null)
            return;

        // FIXME make jasper file combo box select the correct name
        // from the template selected.

        jasperFileCombo.setEnabled(true);

        /*
         * if (!templateSelected.jasperFileDataExists()) {
         * jasperFileCombo.deselectAll(); } else {
         * jasperFileCombo.setBackground(new Color(shell.getDisplay(), 255, 255,
         * 255)); }
         */
    }

    private void setSelectedTemplate(Template t) {
        templateDirty = false;
        if (t == null) {
            templateSelected = null;
            templateNameText.setText("Select a template.");
            printerNameText.setText("");
            jasperFileCombo.setEnabled(false);
            jasperFileCombo.deselectAll();

            try {
                configTree.populateTree(null);
            } catch (TreeException e) {
                BiobankGuiCommonPlugin.openAsyncError("Set Templat Error",
                    e.getError());
            }
            return;
        }

        if (templateSelected != t) {
            templateSelected = t;
            templateNameText.setText(t.getName());
            String printerName = t.getPrinterName();
            printerNameText.setText(printerName != null ? printerName : "");
            updatejasperFileCombo();

            try {
                configTree.populateTree(templateSelected.getConfiguration());
            } catch (TreeException e) {
                BiobankGuiCommonPlugin.openAsyncError("Set Template Error",
                    e.getError());
            } catch (JAXBException e2) {
                BiobankGuiCommonPlugin.openAsyncError("Set Template Error",
                    e2.getMessage());
            }
        }
    }

    private SelectionListener copyListner = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                if (templateSelected != null) {

                    StringInputDialog dialog = new StringInputDialog(
                        "Cloned Template Name",
                        "What is the name of the cloned template?", shell,
                        SWT.NONE);
                    String cloneName = dialog.open(templateSelected.getName()
                        + " copy");

                    if (cloneName != null) {

                        if (templateStore.getTemplateNames()
                            .contains(cloneName)) {
                            BiobankGuiCommonPlugin.openAsyncError(
                                "Template Exists",
                                "Your new template must have a unique name.");
                            return;
                        }

                        Template clone = templateSelected.clone();
                        clone.setName(cloneName);
                        templateStore.addTemplate(clone);
                        list.add(clone.getName());
                        list.redraw();
                    }
                }
            } catch (Exception e1) {
                BiobankGuiCommonPlugin.openAsyncError("Template Copy Error",
                    "Could not copy template", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };
    private SelectionListener newListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {

                StringInputDialog dialog = new StringInputDialog(
                    "New Template Name",
                    "What is the name of this new template?", shell, SWT.NONE);
                String newTemplateName = dialog.open(null);

                if (newTemplateName != null) {

                    if (templateStore.getTemplateNames().contains(
                        newTemplateName)) {
                        BiobankGuiCommonPlugin.openAsyncError(
                            "Template Exists",
                            "Your new template must have a unique name.");
                        return;
                    }

                    Template ct = new Template();
                    ct.setJasperFileData(null);
                    ct.setConfiguration(CBSRLabelMaker
                        .getDefaultConfiguration());
                    ct.setName(newTemplateName);
                    templateStore.addTemplate(ct);
                    list.add(ct.getName());
                    list.redraw();
                }
            } catch (Exception e1) {
                BiobankGuiCommonPlugin.openAsyncError("Template Create Error",
                    "Could not create template", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };
    private SelectionListener deleteListner = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                if (templateSelected != null) {
                    MessageBox messageBox = new MessageBox(shell,
                        SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    messageBox.setMessage("Are you sure you want to delete "
                        + templateSelected.getName() + "?");
                    messageBox.setText("Deleting Template");

                    int response = messageBox.open();
                    if (response == SWT.YES) {
                        templateSelected.delete();
                        templateStore.deleteTemplate(templateSelected);
                        list.remove(templateSelected.getName());

                        if (list.getItemCount() > 0) {
                            list.deselectAll();

                            int lastItemIndex = list.getItemCount() - 1;

                            if (lastItemIndex >= 0) {
                                list.select(lastItemIndex);
                                setSelectedTemplate(Template
                                    .getTemplateByName(list
                                        .getItem(lastItemIndex)));
                            }

                            setSelectedTemplate(templateStore.getTemplate(list
                                .getItem(lastItemIndex)));

                        } else {
                            BiobankGuiCommonPlugin.openAsyncError(
                                "Template not in Template Store.",
                                "Template does not exist, already deleted.");
                            return;
                        }
                    }
                }
            } catch (Exception e1) {
                BiobankGuiCommonPlugin.openAsyncError("Template Delete Error",
                    "Could not delete template", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    private SelectionListener listListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            String[] selectedItems = list.getSelection();
            if (selectedItems.length == 1) {

                if (templateSelected != null) {

                    if (templateDirty || configTree.isDirty()) {

                        MessageBox messageBox = new MessageBox(shell,
                            SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                        messageBox
                            .setMessage("Template has been modified, do you want to save your changes?");
                        messageBox.setText("Template Editor Saving");
                        int response = messageBox.open();
                        if (response == SWT.YES) {
                            // FIXME check if user selected a valid jasper
                            // config
                            // from the jaspercombo
                            if (templateSelected.getJasperFileData() == null) {
                                BiobankGuiCommonPlugin
                                    .openError("Cannot Save Template",
                                        "A Jasper Configuration has not been selected.");
                                return;
                            }

                            try {
                                templateSelected.persist();
                            } catch (Exception e2) {
                                BiobankGuiCommonPlugin.openAsyncError(
                                    "Template Save Error",
                                    "Error occured saving template", e2);
                            }
                        }
                    }
                }

                try {
                    setSelectedTemplate(templateStore
                        .getTemplate(selectedItems[0]));
                } catch (Exception e1) {
                    BiobankGuiCommonPlugin.openAsyncError(
                        "Template Retrieval Error",
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

    private SelectionListener helpListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                // TODO make a valid documentation page for help.
                PlatformUI.getWorkbench().getBrowserSupport()
                    .getExternalBrowser().openURL(new URL(HELP_URL));
            } catch (Exception e1) {
                BiobankGuiCommonPlugin.openAsyncError("Open URL Problem",
                    "Could not open help url.\n\n" + e1.getMessage());
                return;
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    private SelectionListener saveAllListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                if (saveCurrentTemplate()) {
                    MessageBox messageBox = new MessageBox(shell,
                        SWT.ICON_INFORMATION | SWT.OK);
                    messageBox
                        .setMessage("Template has been successfully saved.");
                    messageBox.setText("Template Saved");
                    messageBox.open();
                }
            } catch (Exception e1) {
                BiobankGuiCommonPlugin.openAsyncError("Save Template Error",
                    "Could not save template: " + e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    private boolean saveCurrentTemplate() throws Exception {

        try {
            configTree.resetEditor();
        } catch (TreeException e2) {
            BiobankGuiCommonPlugin.openAsyncError("Editor Error",
                "Could not reset editor: " + e2.getError());
            return false;
        }
        if (!templateDirty && !configTree.isDirty()) {
            return true;
        }

        if (templateSelected == null) {
            BiobankGuiCommonPlugin.openAsyncError("No Template Selected",
                "Cannot save template. Please select a template first.");
            return false;
        }
        // FIXME
        if (templateSelected.getJasperFileData() == null) {
            BiobankGuiCommonPlugin.openError("Cannot Save Template",
                "A Jasper Configuration has not been selected.");
            return false;
        }

        try {
            templateSelected.persist();
        } catch (IOException e1) {
            BiobankGuiCommonPlugin.openAsyncError("Save Template Error",
                "Could not save template: " + e1);
            return false;
        }
        templateDirty = false;
        return true;
    }

    private void updateForm() {
        try {
            if (loggedIn) {
                if (templateStore == null) {
                    templateStore = new TemplateStore();
                }

                for (String s : templateStore.getTemplateNames())
                    list.add(s);
                list.redraw();
            } else {
                // TODO: unpopulate list, blannk out all widgets
            }
        } catch (ApplicationException e) {
            BiobankGuiCommonPlugin.openAsyncError("Database Error",
                "Error while updating form", e);
        }
    }
}
