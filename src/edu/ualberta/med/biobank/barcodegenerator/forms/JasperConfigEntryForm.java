package edu.ualberta.med.biobank.barcodegenerator.forms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.barcodegenerator.dialogs.StringInputDialog;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.gui.common.forms.BgcFormBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * View for adding new jasper files that are mapped to a user chosen name.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class JasperConfigEntryForm extends BgcFormBase {

    public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.views.JasperFileEditorView";
    private Button deleteButton = null;
    private Button newButton = null;
    private Button saveButton = null;
    private Button browseButton = null;
    private Text jasperNameTexty = null;
    private Text jasperConfigText = null;
    private List list = null;

    private String loadedJasperFileXml = null;
    private String prevJasperName = null;

    private Map<String, JasperTemplateWrapper> templateMap = null;

    boolean jasperConfigDirty = false;

    private boolean loggedIn = false;

    @Override
    protected void init() throws Exception {
        setPartName("Jasper Configuration Templates");
    }

    @Override
    protected void performDoubleClick(DoubleClickEvent event) {
    }

    @Override
    protected Image getFormImage() {
        return null;
    }

    protected String getOkMessage() {
        return "Used to configure jasper files for printer label templates";
    }

    @Override
    protected void createFormContent() throws Exception {

        form.setText("Jasper Configuration Templates");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

        Composite top = toolkit.createComposite(page, SWT.NONE);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        createGroup(top);

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

    @Override
    public void setFocus() {
    }

    private void createGroup(Composite top) {
        Composite group = createSectionWithClient(
            "Jasper Configuration Editor", top);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        createComposite(group);
        createComposite1(group);
    }

    private void createComposite(Composite group) {

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        Composite composite = toolkit.createComposite(group, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        composite.setLayout(gridLayout);

        createComposite2(composite);
        new Label(composite, SWT.NONE);
        createComposite3(composite);
    }

    private void createComposite1(Composite group) {
        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = false;
        gridData2.verticalAlignment = GridData.CENTER;

        Composite composite1 = toolkit.createComposite(group, SWT.NONE);
        composite1.setLayoutData(gridData2);
        composite1.setLayout(new FillLayout());

        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);

        saveButton = new Button(composite1, SWT.NONE);
        saveButton.setText("Save Jasper Config");
        saveButton.addSelectionListener(saveAllListener);
    }

    private void createComposite2(Composite composite) {
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.BEGINNING;
        gridData3.grabExcessVerticalSpace = true;
        gridData3.grabExcessHorizontalSpace = false;
        gridData3.verticalAlignment = GridData.FILL;
        Composite composite2 = toolkit.createComposite(composite, SWT.NONE);
        composite2.setLayout(new GridLayout());
        composite2.setLayoutData(gridData3);
        createGroup1(composite2);
        createComposite4(composite2);
    }

    private void createComposite3(Composite composite) {
        GridData gridData4 = new GridData();
        gridData4.horizontalAlignment = GridData.FILL;
        gridData4.grabExcessHorizontalSpace = true;
        gridData4.grabExcessVerticalSpace = true;
        gridData4.verticalAlignment = GridData.FILL;
        Composite composite3 = toolkit.createComposite(composite, SWT.NONE);
        composite3.setLayoutData(gridData4);
        composite3.setLayout(new GridLayout());
        createComposite5(composite3);
    }

    private void createGroup1(Composite composite2) {
        GridData gridData6 = new GridData();
        gridData6.grabExcessVerticalSpace = true;
        gridData6.verticalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.heightHint = 500;
        gridData6.widthHint = 125;

        //FIXME make template list longer vertically
        Composite group1 = createSectionWithClient("Jasper Configurations",
            composite2);
        group1.setLayoutData(gridData6);
        list = new List(group1, SWT.BORDER | SWT.V_SCROLL);
        list.addSelectionListener(listListener);
        list.redraw();
        list.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
    }

    private void setEnable(boolean enable) {

        deleteButton.setEnabled(enable);
        browseButton.setEnabled(enable);
        newButton.setEnabled(enable);
        saveButton.setEnabled(enable);
    }

    private void updateForm() {
        if (templateMap == null) {
            templateMap = new HashMap<String, JasperTemplateWrapper>();
        }

        try {

            if (loggedIn) {
                setEnable(true);

                list.setEnabled(true);

                for (JasperTemplateWrapper t : JasperTemplateWrapper
                    .getAllTemplates(SessionManager.getAppService())) {
                    String name = t.getName();
                    templateMap.put(name, t);
                    list.add(name);
                }
                list.redraw();
            } else {

                setEnable(false);

                templateMap.clear();

                list.removeAll();
                list.setEnabled(false);
                list.redraw();
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Database Error",
                "Error while updating form", e);
        }
    }

    /**
     * Sets the name of the selected jasper template list to prevJasperName.
     * 
     */
    private SelectionListener listListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                String[] selectedItems = list.getSelection();
                if (selectedItems.length == 1) {
                    saveTemplate();

                    if (selectedItems[0] != null) {
                        JasperTemplateWrapper selectedTemplate = (templateMap
                            .get(selectedItems[0]));
                        jasperNameTexty.setText(selectedTemplate.getName());
                        if (selectedTemplate.getXml() == null
                            || selectedTemplate.getXml().isEmpty()) {
                            jasperConfigText
                                .setText("Please select a Jasper file");
                        } else
                            jasperConfigText.setText("Jasper file loaded");

                        prevJasperName = selectedItems[0];
                        jasperConfigDirty = false;

                    } else {
                        jasperNameTexty.setText("Please select a template");
                        jasperConfigText.setText("");
                        prevJasperName = null;
                        jasperConfigDirty = false;
                    }

                } else {
                    BgcPlugin.openAsyncError("Selection Listener Error",
                        "invalid selected items length: "
                            + selectedItems.length);
                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError("Jasper Template Save Error",
                    "could not save template to database", e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    private void saveTemplate() throws Exception {
        if (prevJasperName != null) {
            if (jasperConfigDirty) {
                if (BgcPlugin
                    .openConfirm("Jasper Configuration Editor Saving",
                        "Jasper Configuration has been modified, do you want to save your changes?")) {

                    JasperTemplateWrapper selectedTemplate = (templateMap
                        .get(prevJasperName));

                    if (loadedJasperFileXml != null) {
                        selectedTemplate.setXml(loadedJasperFileXml);
                        loadedJasperFileXml = null;
                    }

                    if (selectedTemplate.getXml() != null
                        && !selectedTemplate.getXml().isEmpty()) {
                        selectedTemplate.persist();
                        jasperConfigText.setText("Jasper file loaded");

                    } else {
                        jasperConfigText.setText("Please select a Jasper file");
                        throw new Exception("Jasper file was not selected");
                    }
                }
                jasperConfigDirty = false;
            }
        }
    }

    private void createComposite4(Composite composite2) {

        Composite composite4 = toolkit.createComposite(composite2, SWT.NONE);
        composite4.setLayout(new RowLayout());
        new Label(composite2, SWT.NONE);
        newButton = new Button(composite4, SWT.NONE);
        newButton.setText("New");
        newButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                StringInputDialog dialog = new StringInputDialog(
                    "New Jasper Configuration Name",
                    "What is the name of this new Jasper Configuration?",
                    "Name", PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell());
                if (dialog.open() == Dialog.OK) {
                    String jasperConfigName = dialog.getValue();

                    if (!templateMap.containsKey(jasperConfigName)) {
                        JasperTemplateWrapper newTemplate = new JasperTemplateWrapper(
                            SessionManager.getAppService());

                        try {
                            newTemplate.setName(jasperConfigName);
                            templateMap.put(jasperConfigName, newTemplate);
                            list.add(jasperConfigName);

                            list.redraw();
                        } catch (Exception e1) {
                            BgcPlugin.openAsyncError(
                                "Failed to Save",
                                "Faile to save newly created template: "
                                    + e1.getMessage());
                        }

                    } else {
                        BgcPlugin
                            .openAsyncError("Jasper Configuration Exists",
                                "Your new Jasper Configuration must have a unique name.");
                    }
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

                    if (list.getSelectionCount() == 1 && prevJasperName != null) {
                        JasperTemplateWrapper selected = templateMap
                            .get(prevJasperName);

                        if (selected != null) {
                            MessageBox messageBox = new MessageBox(PlatformUI
                                .getWorkbench().getActiveWorkbenchWindow()
                                .getShell(), SWT.ICON_QUESTION | SWT.YES
                                | SWT.NO);
                            messageBox
                                .setMessage("Are you sure you want to delete "
                                    + selected.getName() + "?");
                            messageBox.setText("Deleting Jasper Configuration");

                            int response = messageBox.open();
                            if (response == SWT.YES) {

                                if (!selected.isNew())
                                    selected.delete();
                                templateMap.remove(prevJasperName);
                                list.remove(prevJasperName);

                                jasperNameTexty
                                    .setText("Please select a template");
                                jasperConfigText.setText("");
                                prevJasperName = null;
                                jasperConfigDirty = false;

                                list.deselectAll();
                                list.redraw();
                            }
                        }
                    }
                } catch (Exception e1) {
                    BgcPlugin
                        .openAsyncError(
                            "Template Delete Error",
                            "Could not delete template. A printer template is using this jasper configuration.",
                            e1);
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
    private void createComposite5(Composite composite3) {
        Composite composite5 = toolkit.createComposite(composite3, SWT.NONE);
        composite5.setLayout(new GridLayout(3, false));
        composite5.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite5, SWT.NONE).setText("Configuration Name:");

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;

        jasperNameTexty = new Text(composite5, SWT.BORDER);
        jasperNameTexty.setLayoutData(gd);
        jasperNameTexty.setEditable(false);

        new Label(composite5, SWT.NONE).setText("Jasper File:");

        jasperConfigText = new Text(composite5, SWT.BORDER);
        jasperConfigText.setEditable(false);
        jasperConfigText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseButton = new Button(composite5, SWT.NONE);
        browseButton.setText("Browse...");
        browseButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                FileDialog fd = new FileDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), SWT.OPEN);
                fd.setText("Select Jasper File");
                String[] filterExt = { "*.jrxml" };
                fd.setFilterExtensions(filterExt);
                String path = fd.open();
                if (path != null) {
                    try {
                        File f = new File(path);
                        if (f.exists()) {
                            loadedJasperFileXml = FileUtils.readFileToString(f);
                            jasperConfigText.setText(path);
                            jasperConfigDirty = true;
                        } else {
                            BgcPlugin.openAsyncError("File Not Found",
                                "File selected does not exist.");
                        }

                    } catch (IOException e) {
                        BgcPlugin.openAsyncError("Template Read Error",
                            "Could not open the template file for reading", e);
                    }
                }

            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
    }

    private SelectionListener saveAllListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                saveTemplate();
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

}
