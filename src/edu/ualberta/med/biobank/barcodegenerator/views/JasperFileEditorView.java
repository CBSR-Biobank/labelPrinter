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
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;

/**
 * View for adding new jasper files that are mapped to a user chosen name.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class JasperFileEditorView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.views.JasperFileEditorView";
    private Composite top = null;
    private Group topGroup = null;
    private Composite composite = null;
    private Composite actionComposite = null;
    private Composite composite2 = null;
    private Composite composite3 = null;
    private Group jasperConfigGroup = null;
    private Composite composite4 = null;
    private Button deleteButton = null;
    private Button newButton = null;
    private Button saveButton = null;
    private Composite jasperConfigFieldsComposite = null;
    private Label label = null;
    private Text jasperNameTexty = null;
    private Label jasperFileLabel = null;
    private Combo templateTypeCombo = null;
    private Text jasperFileText = null;
    private Button browseButton = null;
    private List list = null;

    private Shell shell;

    private JasperConfiguration jasperConfigSelected = null;
    boolean jasperConfigDirty = false;

    GridData fillGridData;
    GridData horizontalGridData;
    GridData verticalGridData;

    @Override
    public void createPartControl(Composite parent) {

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

        createTopGroup();

    }

    @Override
    public void setFocus() {
    }

    private void createTopGroup() {
        topGroup = new Group(top, SWT.NONE);
        topGroup.setText("Jasper Configuration Editor");
        topGroup.setLayoutData(fillGridData);
        createComposite();
        topGroup.setLayout(new GridLayout());
        createActionComposite();
    }

    private void createComposite() {
        GridLayout grid3Layout = new GridLayout();
        grid3Layout.numColumns = 3;

        composite = new Composite(topGroup, SWT.NONE);
        composite.setLayoutData(fillGridData);
        composite.setLayout(grid3Layout);

        createComposite2();
        new Label(composite, SWT.NONE);
        createComposite3();
    }

    private void createActionComposite() {

        actionComposite = new Composite(topGroup, SWT.NONE);
        actionComposite.setLayoutData(horizontalGridData);
        actionComposite.setLayout(new FillLayout());

        new Label(actionComposite, SWT.NONE);
        new Label(actionComposite, SWT.NONE);
        new Label(actionComposite, SWT.NONE);
        new Label(actionComposite, SWT.NONE);

        saveButton = new Button(actionComposite, SWT.NONE);
        saveButton.setText("Save Jasper Config");
        saveButton.addSelectionListener(saveAllListener);
    }

    private void createComposite2() {
        composite2 = new Composite(composite, SWT.NONE);
        composite2.setLayout(new GridLayout());
        composite2.setLayoutData(verticalGridData);

        createJasperConfigGroup();
        createComposite4();
    }

    private void createComposite3() {
        composite3 = new Composite(composite, SWT.NONE);
        composite3.setLayoutData(fillGridData);
        composite3.setLayout(new GridLayout());

        createComposite5();
    }

    private void createJasperConfigGroup() {

        GridData specificFillGrid = new GridData();
        specificFillGrid.grabExcessVerticalSpace = true;
        specificFillGrid.verticalAlignment = GridData.FILL;
        specificFillGrid.grabExcessHorizontalSpace = true;
        specificFillGrid.horizontalAlignment = GridData.FILL;
        specificFillGrid.heightHint = 200;
        specificFillGrid.widthHint = 125;

        FillLayout verticalFillLayout = new FillLayout();
        verticalFillLayout.type = org.eclipse.swt.SWT.VERTICAL;

        jasperConfigGroup = new Group(composite2, SWT.NONE);
        jasperConfigGroup.setLayoutData(specificFillGrid);
        jasperConfigGroup.setLayout(verticalFillLayout);
        jasperConfigGroup.setText("Jasper Configurations");

        list = new List(jasperConfigGroup, SWT.BORDER | SWT.V_SCROLL);
        list.addSelectionListener(listListener);
        for (String s : JasperFileStore.getNames())
            list.add(s);
        list.redraw();

    }

    private void createComposite4() {

        composite4 = new Composite(composite2, SWT.NONE);
        composite4.setLayout(new RowLayout());
        new Label(composite2, SWT.NONE);
        deleteButton = new Button(composite4, SWT.NONE);
        deleteButton.setText("Delete ");
        deleteButton.addSelectionListener(deleteListener);
        newButton = new Button(composite4, SWT.NONE);
        newButton.setText("New");
        newButton.addSelectionListener(newListener);
    }

    private void createComposite5() {

        GridData horizontalGrabExcess = new GridData();
        horizontalGrabExcess.horizontalAlignment = GridData.FILL;
        horizontalGrabExcess.grabExcessHorizontalSpace = true;

        GridLayout layoutColumn3 = new GridLayout();
        layoutColumn3.numColumns = 3;

        jasperConfigFieldsComposite = new Composite(composite3, SWT.NONE);
        jasperConfigFieldsComposite.setLayout(layoutColumn3);
        jasperConfigFieldsComposite.setLayoutData(horizontalGrabExcess);

        label = new Label(jasperConfigFieldsComposite, SWT.NONE);
        label.setText("Configuration Name:");

        jasperNameTexty = new Text(jasperConfigFieldsComposite, SWT.BORDER);
        jasperNameTexty.setEditable(false);
        jasperNameTexty.setLayoutData(horizontalGrabExcess);

        new Label(jasperConfigFieldsComposite, SWT.NONE);
        label = new Label(jasperConfigFieldsComposite, SWT.NONE);
        label.setText("Intended Template:");

        templateTypeCombo = new Combo(jasperConfigFieldsComposite, SWT.BORDER);
        templateTypeCombo.setLayoutData(horizontalGrabExcess);
        templateTypeCombo.addSelectionListener(templateComboListener);
        templateTypeCombo.setEnabled(false);
        for (String s : JasperFileStore.TEMPLATE_TYPES) {
            templateTypeCombo.add(s);
        }

        new Label(jasperConfigFieldsComposite, SWT.NONE);
        jasperFileLabel = new Label(jasperConfigFieldsComposite, SWT.NONE);
        jasperFileLabel.setText("Jasper File:");

        jasperFileText = new Text(jasperConfigFieldsComposite, SWT.BORDER);
        jasperFileText.setEditable(false);
        jasperFileText.setLayoutData(horizontalGrabExcess);

        browseButton = new Button(jasperConfigFieldsComposite, SWT.NONE);
        browseButton.setText("Browse...");
        browseButton.addSelectionListener(browserListener);
    }

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

    private boolean saveCurrentJasperConfig() {

        if (jasperConfigSelected == null) {
            BiobankGuiCommonPlugin.openAsyncError(
                "No Jasper Configuration Selected",
                "Cannot save jasper configuration. Please select one first.");
            return false;
        }
        JasperFileStore.update(jasperConfigSelected);
        jasperConfigDirty = false;
        return true;
    }

    private SelectionListener deleteListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (jasperConfigSelected != null) {
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
                    | SWT.YES | SWT.NO);
                messageBox.setMessage("Are you sure you want to delete "
                    + jasperConfigSelected.name + "?");
                messageBox.setText("Deleting Jasper Configuration");

                int response = messageBox.open();
                if (response == SWT.YES) {
                    if (JasperFileStore.removeConfig(jasperConfigSelected.name)) {
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
                        BiobankGuiCommonPlugin
                            .openAsyncError("Jasper Configuration not found.",
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
    };

    private SelectionListener newListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {

            StringInputDialog dialog = new StringInputDialog(
                "New Jasper Configuration Name",
                "What is the name of this new Jasper Configuration?", shell,
                SWT.NONE);
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
                    BiobankGuiCommonPlugin
                        .openAsyncError("Jasper Configuration Exists",
                            "Your new Jasper Configuration must have a unique name.");
                }
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    private SelectionListener templateComboListener = new SelectionListener() {

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
    };

    private SelectionListener browserListener = new SelectionListener() {
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
                    BiobankGuiCommonPlugin.openAsyncError(
                        "Jasper File Non-existant",
                        "Could not find the selected Jasper file.");
                    return;
                }
                byte[] jasperFileData;
                try {
                    jasperFileData = fileToBytes(selectedFile);
                } catch (IOException e) {
                    BiobankGuiCommonPlugin.openAsyncError(
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
    };

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

    // FIXME replace all of this with the biobank-based wrapper equivalent.
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
}
