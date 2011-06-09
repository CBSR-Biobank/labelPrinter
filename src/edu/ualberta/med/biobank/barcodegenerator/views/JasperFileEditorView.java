package edu.ualberta.med.biobank.barcodegenerator.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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
import org.apache.commons.io.FileUtils;

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

    private Map<String, JasperTemplateWrapper> templateMap;

    private JasperTemplateWrapper selectedTemplate = null;

    boolean jasperConfigDirty = false;

    private boolean loggedIn;

    @Override
    public void createPartControl(Composite parent) {
        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

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
    }

    private void updateForm() {
        if (templateMap == null) {
            templateMap = new HashMap<String, JasperTemplateWrapper>();
        }

        try {
            if (loggedIn) {
                for (JasperTemplateWrapper t : JasperTemplateWrapper
                    .getAllTemplates(SessionManager.getAppService())) {
                    String name = t.getName();
                    templateMap.put(name, t);
                    list.add(name);
                }
            } else {
                // TODO: unpopulate list, blannk out all widgets
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
     */
    private void createGroup() {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        group = new Group(top, SWT.NONE);
        group.setText("Jasper Configuration Editor");
        group.setLayoutData(gridData);
        createComposite();
        group.setLayout(new GridLayout());
        createComposite1();
    }

    /**
     * This method initializes composite
     * 
     */
    private void createComposite() {
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

        @SuppressWarnings("unused")
        Label filler22 = new Label(composite1, SWT.NONE);
        @SuppressWarnings("unused")
        Label filler21 = new Label(composite1, SWT.NONE);
        @SuppressWarnings("unused")
        Label filler2 = new Label(composite1, SWT.NONE);
        filler2 = new Label(composite1, SWT.NONE);

        saveButton = new Button(composite1, SWT.NONE);
        saveButton.setText("Save Jasper Config");
        saveButton.addSelectionListener(saveAllListener);
    }

    /**
     * This method initializes composite2
     * 
     */
    private void createComposite2() {
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
     */
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

    private SelectionListener listListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                String[] selectedItems = list.getSelection();
                if (selectedItems.length == 1) {
                    if (selectedTemplate != null) {
                        if (jasperConfigDirty) {
                            MessageBox messageBox = new MessageBox(PlatformUI
                                .getWorkbench().getActiveWorkbenchWindow()
                                .getShell(), SWT.ICON_QUESTION | SWT.YES
                                | SWT.NO);
                            messageBox
                                .setMessage("Jasper Configuration has been modified, do you want to save your changes?");
                            messageBox
                                .setText("Jasper Configuration Editor Saving");
                            int response = messageBox.open();
                            if (response == SWT.YES) {
                                selectedTemplate.persist();
                            } else {
                                selectedTemplate.reload();
                            }
                        }
                    }

                    setSelectedJasperConfig(templateMap.get(selectedItems[0]));
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

    private void updateJasperFileText(String selectedName) {

        if (selectedTemplate == null)
            return;

        String xml = selectedTemplate.getXml();

        if ((xml == null) || xml.isEmpty()) {
            jasperFileText.setText("Select a Jasper file.");
            jasperFileText
                .setBackground(new Color(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell().getDisplay(), 255,
                    0, 0));
        } else {
            if (selectedName == null) {
                selectedName = "Jasper file loaded";
            }
            jasperFileText.setText(selectedName);
            jasperFileText.setBackground(new Color(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell().getDisplay(), 255, 255,
                255));
        }
        jasperFileText.redraw();
    }

    private void setSelectedJasperConfig(JasperTemplateWrapper t) {
        if (t != null) {

            if (t == selectedTemplate)
                return;

            selectedTemplate = t;
            jasperNameTexty.setText(t.getName());
            jasperFileText.setEnabled(true);
            updateJasperFileText(null);

        } else {
            selectedTemplate = null;
            jasperNameTexty.setText("Select a Jasper Configuration.");
            jasperFileText.setText("");
            jasperFileText.setEnabled(false);
        }

        jasperConfigDirty = false;
    }

    /**
     * This method initializes composite4
     * 
     */
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
                    if (selectedTemplate != null) {
                        MessageBox messageBox = new MessageBox(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                        messageBox
                            .setMessage("Are you sure you want to delete "
                                + selectedTemplate.getName() + "?");
                        messageBox.setText("Deleting Jasper Configuration");

                        int response = messageBox.open();
                        if (response == SWT.YES) {
                            selectedTemplate.delete();
                            templateMap.remove(selectedTemplate.getName());
                            list.remove(selectedTemplate.getName());

                            int lastItemIndex = list.getItemCount() - 1;

                            if (lastItemIndex >= 0) {
                                list.deselectAll();
                                list.select(lastItemIndex);
                                setSelectedJasperConfig(templateMap.get(list
                                    .getItem(lastItemIndex)));
                            } else {
                                setSelectedJasperConfig(null);
                            }
                            list.redraw();
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
        newButton = new Button(composite4, SWT.NONE);
        newButton.setText("New");
        newButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                StringInputDialog dialog = new StringInputDialog(
                    "New Jasper Configuration Name",
                    "What is the name of this new Jasper Configuration?",
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), SWT.NONE);
                String jasperConfigName = dialog.open(null);

                if (!templateMap.containsKey(jasperConfigName)) {

                    JasperTemplateWrapper newTemplate = new JasperTemplateWrapper(
                        SessionManager.getAppService());

                    try {
                        newTemplate.setName(jasperConfigName);
                        newTemplate.persist();

                        templateMap.put(jasperConfigName, newTemplate);
                        list.add(jasperConfigName);
                        list.redraw();
                    } catch (Exception e1) {
                        Error(
                            "Failed to Save",
                            "Faile to save newly created template: "
                                + e1.getMessage());
                    }

                } else {
                    Error("Jasper Configuration Exists",
                        "Your new Jasper Configuration must have a unique name.");
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
                if (selectedTemplate == null)
                    return;

                FileDialog fd = new FileDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), SWT.OPEN);
                fd.setText("Select Jasper File");
                String[] filterExt = { "*.jrxml" };
                fd.setFilterExtensions(filterExt);
                String path = fd.open();
                if (path != null) {
                    try {
                        File f = new File(path);
                        selectedTemplate.setXml(FileUtils.readFileToString(f));
                        updateJasperFileText(path);
                        jasperConfigDirty = true;
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

    public static byte[] fileToBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];

        int offset = 0;
        int readCount = 0;
        while (offset < bytes.length
            && (readCount = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += readCount;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "
                + file.getName());
        }

        is.close();
        return bytes;
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
    }

    private void Error(String title, String message) {
        MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR);
        messageBox.setMessage(message);
        messageBox.setText(title);
        messageBox.open();
    }

    private boolean saveCurrentJasperConfig() throws Exception {

        if (selectedTemplate == null) {
            Error("No Jasper Configuration Selected",
                "Cannot save jasper configuration. Please select one first.");
            return false;
        }
        selectedTemplate.persist();
        jasperConfigDirty = false;
        return true;
    }

    private SelectionListener saveAllListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                if (saveCurrentJasperConfig()) {
                    MessageBox messageBox = new MessageBox(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.ICON_INFORMATION | SWT.OK);
                    messageBox
                        .setMessage("Jasper Configuration has been successfully saved.");
                    messageBox.setText("Jasper Configuration Saved");
                    messageBox.open();
                }
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
