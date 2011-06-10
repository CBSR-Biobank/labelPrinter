package edu.ualberta.med.biobank.barcodegenerator.views;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.barcodegenerator.dialogs.StringInputDialog;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * View for adding new jasper files that are mapped to a user chosen name.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class JasperFileEditorView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.views.JasperFileEditorView";
    private Composite top = null;
    private Group group = null;
    private Composite composite = null;
    private Composite composite1 = null;
    private Composite composite2 = null;
    private Composite composite3 = null;
    private Group group1 = null;
    private Composite composite4 = null;
    private Button deleteButton = null;
    private Button newButton = null;
    private Button saveButton = null;
    private Composite composite5 = null;
    private Label label = null;
    private Text jasperNameTexty = null;
    private Text jasperFileText = null;
    private Button browseButton = null;
    private List list = null;

    private String loadedJasperFileXml = null;
    private String prevJasperName = null;

    private Map<String, JasperTemplateWrapper> templateMap;

    boolean jasperConfigDirty = false;

    private boolean loggedIn = false;

    private GridData gridFill = null;

    @Override
    public void setFocus() {
    }

    private void createGroup() {

        group = new Group(top, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setLayoutData(gridFill);
        group.setText("Jasper Configuration Editor");

        createComposite();
        createComposite1();
    }

    private void createComposite() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        composite = new Composite(group, SWT.NONE);
        createComposite2();
        composite.setLayoutData(gridFill);
        composite.setLayout(gridLayout);
        @SuppressWarnings("unused")
        Label filler = new Label(composite, SWT.NONE);
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

        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);
        new Label(composite1, SWT.NONE);

        saveButton = new Button(composite1, SWT.NONE);
        saveButton.setText("Save Jasper Config");
        saveButton.addSelectionListener(saveAllListener);
    }

    private void createComposite2() {
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
        composite3.setLayoutData(gridData4);
        composite3.setLayout(new GridLayout());
        createComposite5();
    }

    private void createGroup1() {
        GridData gridData6 = new GridData();
        gridData6.grabExcessVerticalSpace = true;
        gridData6.verticalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.heightHint = 200;
        gridData6.widthHint = 125;
        FillLayout fillLayout1 = new FillLayout();
        fillLayout1.type = org.eclipse.swt.SWT.VERTICAL;

        group1 = new Group(composite2, SWT.NONE);
        group1.setText("Jasper Configurations");
        group1.setLayoutData(gridData6);
        group1.setLayout(fillLayout1);
        list = new List(group1, SWT.BORDER | SWT.V_SCROLL);
        list.addSelectionListener(listListener);
        list.redraw();
    }

    @Override
    public void createPartControl(Composite parent) {
        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

        top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());

        gridFill = new GridData();
        gridFill.horizontalAlignment = GridData.FILL;
        gridFill.grabExcessHorizontalSpace = true;
        gridFill.grabExcessVerticalSpace = true;
        gridFill.verticalAlignment = GridData.FILL;

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

    private void updateForm() {
        if (templateMap == null) {
            templateMap = new HashMap<String, JasperTemplateWrapper>();
        }

        try {

            if (loggedIn) {
                list.setEnabled(true);
                for (JasperTemplateWrapper t : JasperTemplateWrapper
                    .getAllTemplates(SessionManager.getAppService())) {
                    String name = t.getName();
                    templateMap.put(name, t);
                    list.add(name);
                }
                list.redraw();
            } else {
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
                            jasperFileText
                                .setText("Please select a Jasper file");
                        } else
                            jasperFileText.setText("Jasper file loaded");

                        prevJasperName = selectedItems[0];

                    } else {
                        jasperNameTexty.setText("Please select a template");
                        jasperFileText.setText("");
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
                        jasperFileText.setText("Jasper file loaded");

                    } else {
                        jasperFileText.setText("Please select a Jasper file");
                        throw new Exception("Jasper file was not selected");
                    }
                }
                jasperConfigDirty = false;
            }
        }
    }

    private void createComposite4() {

        composite4 = new Composite(composite2, SWT.NONE);
        composite4.setLayout(new RowLayout());
        new Label(composite2, SWT.NONE);
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
                                jasperFileText.setText("");
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
                            "Could not delete template. A printer template may be using this jasper configuration.",
                            e1);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
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
    }

    /**
     * This method initializes composite5
     * 
     */
    private void createComposite5() {
        composite5 = new Composite(composite3, SWT.NONE);
        composite5.setLayout(new GridLayout(3, false));
        composite5.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        label = new Label(composite5, SWT.NONE);
        label.setText("Configuration Name:");
        jasperNameTexty = new Text(composite5, SWT.BORDER);
        jasperNameTexty.setEditable(false);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        jasperNameTexty.setLayoutData(gd);

        Label label = new Label(composite5, SWT.NONE);
        label.setText("Jasper File:");
        jasperFileText = new Text(composite5, SWT.BORDER);
        jasperFileText.setEditable(false);
        jasperFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
                            jasperFileText.setText(path);
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
