package edu.ualberta.med.biobank.barcodegenerator.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.RowLayout;

import edu.ualberta.med.biobank.barcodegenerator.dialogs.StringInputDialog;

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
    private Label label1 = null;
    private Combo templateTypeCombo = null;
    private Text jasperFileText = null;
    private Button browseButton = null;
    private List list = null;

    private Shell shell;

    private JasperConfiguration jasperConfigSelected = null;
    boolean jasperConfigDirty = false;

    @Override
    public void createPartControl(Composite parent) {
        shell = parent.getShell();

        top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());

        createGroup();
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

    static class JasperFileStore {
        public static ArrayList<JasperConfiguration> configs = new ArrayList<JasperConfiguration>();

        public static boolean addConfig(JasperConfiguration jc) {
            for (String name : JasperFileStore.getNames()) {
                if (name.equals(jc.name)) {
                    return false;
                }
            }
            return configs.add(jc);
        }

        public static JasperConfiguration getConfig(String name) {
            for (JasperConfiguration t : configs) {
                if (name.equals(t.name))
                    return t;
            }
            return null;
        }

        public static boolean removeConfig(String name) {
            JasperConfiguration old = getConfig(name);

            if (old != null) {
                configs.remove(old);
                return true;
            }

            return false;
        }

        public static boolean update(JasperConfiguration updated) {

            JasperConfiguration old = getConfig(updated.name);

            if (old == null)
                return false;

            removeConfig(updated.name);
            return addConfig(updated);
        }

        // get all of the names in the name columns
        public static String[] getNames() {
            String[] jasperConfigNames = new String[configs.size()];

            int i = 0;
            for (JasperConfiguration t : JasperFileStore.configs) {
                jasperConfigNames[i] = t.name;
                i++;
            }

            if (i == 0) {
                jasperConfigNames = new String[] { "Apple", "Seed" };
            }

            return jasperConfigNames;
        }

        public static void clone(JasperConfiguration o, JasperConfiguration n) {
            n.name = o.name;
            n.typeIndex = o.typeIndex;
            if (o.jasperFileData != null) {
                n.jasperFileData = new byte[o.jasperFileData.length];

                System.arraycopy(o.jasperFileData, 0, n.jasperFileData, 0,
                    o.jasperFileData.length);
            }
        }

        public static String[] TEMPLATE_TYPES = { "CBSR", "XKCD" };
    };

    class JasperConfiguration {

        public String name = "default";
        public byte[] jasperFileData = null;
        public int typeIndex = 0;

    };

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
        for (String s : JasperFileStore.getNames())
            list.add(s);
        list.redraw();

    }

    private SelectionListener listListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            String[] selectedItems = list.getSelection();
            if (selectedItems.length == 1) {

                if (jasperConfigSelected != null) {

                    if (jasperConfigDirty) {

                        MessageBox messageBox = new MessageBox(shell,
                            SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                        messageBox
                            .setMessage("Jasper Configuration has been modified, do you want to save your changes?");
                        messageBox
                            .setText("Jasper Configuration Editor Saving");
                        int response = messageBox.open();
                        if (response == SWT.YES) {
                            JasperFileStore.update(jasperConfigSelected);
                        }
                    }
                }

                JasperConfiguration t = JasperFileStore
                    .getConfig(selectedItems[0]);

                setSelectedJasperConifg(t);
            } else {
                setSelectedJasperConifg(null);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    private void updateJasperFileText(String selectedName) {

        if (jasperConfigSelected == null)
            return;

        if (jasperConfigSelected.jasperFileData == null) {
            jasperFileText.setText("Select a Jasper file.");
            jasperFileText.setBackground(new Color(shell.getDisplay(), 255, 0,
                0));
        } else {
            if (selectedName == null) {
                selectedName = "Jasper file loaded";
            }
            jasperFileText.setText(selectedName);
            jasperFileText.setBackground(new Color(shell.getDisplay(), 255,
                255, 255));
        }
        jasperFileText.redraw();
    }

    private void setSelectedJasperConifg(JasperConfiguration t) {

        if (t != null) {

            jasperConfigSelected = new JasperConfiguration();

            jasperConfigSelected = new JasperConfiguration();
            JasperFileStore.clone(t, jasperConfigSelected);

            jasperNameTexty.setText(t.name);

            int i = 0;
            for (String s : templateTypeCombo.getItems()) {
                if (s.equals(JasperFileStore.TEMPLATE_TYPES[t.typeIndex])) {
                    templateTypeCombo.select(i);
                }
                i++;
            }

            templateTypeCombo.getItem(templateTypeCombo.getSelectionIndex());

            updateJasperFileText(null);
            templateTypeCombo.setEnabled(true);

        } else {
            templateTypeCombo.deselectAll();
            jasperConfigSelected = null;
            jasperNameTexty.setText("Select a Jasper Configuration.");
            jasperFileText.setText("");
            jasperFileText.setBackground(new Color(shell.getDisplay(), 255,
                255, 255));
            templateTypeCombo.setEnabled(false);
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
                if (jasperConfigSelected != null) {
                    MessageBox messageBox = new MessageBox(shell,
                        SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    messageBox.setMessage("Are you sure you want to delete "
                        + jasperConfigSelected.name + "?");
                    messageBox.setText("Deleting Jasper Configuration");

                    int response = messageBox.open();
                    if (response == SWT.YES) {
                        if (JasperFileStore
                            .removeConfig(jasperConfigSelected.name)) {
                            list.remove(jasperConfigSelected.name);

                            if (list.getItemCount() > 0) {
                                list.deselectAll();

                                int lastItemIndex = list.getItemCount() - 1;

                                if (lastItemIndex >= 0) {
                                    list.select(lastItemIndex);
                                    setSelectedJasperConifg(JasperFileStore
                                        .getConfig(list.getItem(lastItemIndex)));
                                }

                            } else {
                                setSelectedJasperConifg(null);
                            }

                        } else {
                            Error("Jasper Configuration not found.",
                                "Jasper Configuration does not exist, already deleted.");
                            return;
                        }
                    }
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
                    shell, SWT.NONE);
                String jasperConfigName = dialog.open(null);

                if (jasperConfigName != null) {
                    JasperConfiguration jc = new JasperConfiguration();
                    jc.jasperFileData = null;
                    jc.name = jasperConfigName;
                    jc.typeIndex = 0;

                    if (JasperFileStore.addConfig(jc)) {
                        list.add(jc.name);
                        list.redraw();
                    } else {
                        Error("Jasper Configuration Exists",
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
        label.setText("Configuration Name:");
        jasperNameTexty = new Text(composite5, SWT.BORDER);
        jasperNameTexty.setEditable(false);
        jasperNameTexty.setLayoutData(gridData7);
        @SuppressWarnings("unused")
        Label filler7 = new Label(composite5, SWT.NONE);
        label = new Label(composite5, SWT.NONE);
        label.setText("Intended Template:");
        templateTypeCombo = new Combo(composite5, SWT.BORDER);
        templateTypeCombo.setLayoutData(gridData7);

        for (String s : JasperFileStore.TEMPLATE_TYPES) {
            templateTypeCombo.add(s);
        }
        templateTypeCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                jasperConfigDirty = true;
                if (jasperConfigSelected != null) {

                    // FIXME make sure this stays in sync.
                    jasperConfigSelected.typeIndex = templateTypeCombo
                        .getSelectionIndex();
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        templateTypeCombo.setEnabled(false);

        filler7 = new Label(composite5, SWT.NONE);
        label1 = new Label(composite5, SWT.NONE);
        label1.setText("Jasper File:");
        jasperFileText = new Text(composite5, SWT.BORDER);
        jasperFileText.setEditable(false);
        jasperFileText.setLayoutData(gridData8);
        browseButton = new Button(composite5, SWT.NONE);
        browseButton.setText("Browse...");
        browseButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (jasperConfigSelected == null)
                    return;

                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Select Jasper File");
                String[] filterExt = { "*.jrxml" };
                fd.setFilterExtensions(filterExt);
                String selected = fd.open();
                if (selected != null) {

                    File selectedFile = new File(selected);
                    if (!selectedFile.exists()) {
                        Error("Jasper File Non-existant",
                            "Could not find the selected Jasper file.");
                        return;
                    }
                    byte[] jasperFileData;
                    try {
                        jasperFileData = fileToBytes(selectedFile);
                    } catch (IOException e) {
                        Error(
                            "Loading Jasper File",
                            "Could not read the specified jasper file.\n\n"
                                + e.getMessage());
                        return;
                    }
                    (jasperConfigSelected).jasperFileData = jasperFileData;
                    updateJasperFileText(selected);
                    jasperConfigDirty = true;
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
        MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
        messageBox.setMessage(message);
        messageBox.setText(title);
        messageBox.open();
    }

    private boolean saveCurrentJasperConfig() {

        if (jasperConfigSelected == null) {
            Error("No Jasper Configuration Selected",
                "Cannot save jasper configuration. Please select one first.");
            return false;
        }
        JasperFileStore.update(jasperConfigSelected);
        jasperConfigDirty = false;
        return true;
    }

    private SelectionListener saveAllListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (saveCurrentJasperConfig()) {
                MessageBox messageBox = new MessageBox(shell,
                    SWT.ICON_INFORMATION | SWT.OK);
                messageBox
                    .setMessage("Jasper Configuration has been successfully saved.");
                messageBox.setText("Jasper Configuration Saved");
                messageBox.open();
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

}
