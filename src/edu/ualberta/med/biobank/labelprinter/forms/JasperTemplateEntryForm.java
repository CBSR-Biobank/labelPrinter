package edu.ualberta.med.biobank.labelprinter.forms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.labelPrinter.JasperTemplateDeleteAction;
import edu.ualberta.med.biobank.common.action.labelPrinter.JasperTemplateGetAllAction;
import edu.ualberta.med.biobank.common.action.labelPrinter.JasperTemplateSaveAction;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryForm;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.labelprinter.dialogs.StringInputDialog;
import edu.ualberta.med.biobank.model.JasperTemplate;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * View for adding new jasper files that are mapped to a user chosen name.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class JasperTemplateEntryForm extends BgcEntryForm implements
    SelectionListener {

    private static final I18n i18n = I18nFactory
        .getI18n(JasperTemplateEntryForm.class);

    @SuppressWarnings("nls")
    private static final String JASPER_EXTENSION = "*.jrxml";

    public static final String ID =
        "edu.ualberta.med.biobank.labelprinter.forms.JasperTemplateEntryForm"; //$NON-NLS-1$

    private Button deleteButton = null;
    private Button newButton = null;
    private Button browseButton = null;

    private BgcBaseText jasperNameText = null;
    private BgcBaseText jasperConfigText = null;

    private List jasperTemplateList = null;

    private String loadedJasperFileXml = null;
    private String prevJasperName = null;

    private Map<String, JasperTemplateWrapper> templateMap = null;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        setPartName(i18n.tr("Jasper Templates"));
    }

    @Override
    protected Image getFormImage() {
        return null;
    }

    @SuppressWarnings("nls")
    protected String getOkMessage() {
        return i18n
            .tr("Used to configure jasper files for printer label templates");
    }

    @Override
    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addConfirmAction();
        form.updateToolBar();
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        super.createFormContent();
        form.setText(i18n.tr("Jasper Configuration Templates"));
        form.setMessage(
            i18n.tr("Add Jasper configurations for different printer labels"),
            IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMasterDetail();
        updateForm();
    }

    @Override
    public void setFocus() {
        // do nothing for now
    }

    @SuppressWarnings("nls")
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
            i18n.tr("Jasper Configurations"), masterComp);
        client.setLayout(new GridLayout());

        jasperTemplateList = new List(client, SWT.BORDER | SWT.V_SCROLL);
        jasperTemplateList.addSelectionListener(listListener);
        GridData gd = new GridData();
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.heightHint = 200;
        gd.widthHint = 125;
        jasperTemplateList.setLayoutData(gd);

        Composite buttonComp = toolkit.createComposite(client);
        buttonComp.setLayout(new GridLayout(2, true));
        buttonComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        newButton = toolkit.createButton(buttonComp,
            i18n.tr("New"), SWT.NONE);
        newButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));
        newButton.addSelectionListener(this);

        deleteButton = toolkit.createButton(buttonComp,
            i18n.tr("Delete"), SWT.NONE);
        deleteButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));
        deleteButton.addSelectionListener(this);

        createConfigDetailsSection();
    }

    @SuppressWarnings("nls")
    private void createConfigDetailsSection() {
        Composite detailsComp = toolkit.createComposite(page);
        detailsComp.setLayout(new GridLayout(1, false));
        detailsComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        Composite client = createSectionWithClient(
            i18n.tr("Jasper Configuration Details"),
            detailsComp);
        client.setLayout(new GridLayout(3, false));
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL
            | SWT.TOP, true, true));

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;

        jasperNameText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE,
            i18n.tr("Configuration Name"));
        jasperNameText.setLayoutData(gd);
        jasperNameText.setEditable(false);

        jasperConfigText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE,
            i18n.tr("Jasper File"));
        jasperConfigText.setEditable(false);
        jasperConfigText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseButton = toolkit.createButton(client,
            i18n.tr("Browse..."), SWT.PUSH);
        browseButton.addSelectionListener(this);
    }

    private void setEnable(boolean enable) {
        jasperTemplateList.setEnabled(enable);
        deleteButton.setEnabled(enable);
        browseButton.setEnabled(enable);
        newButton.setEnabled(enable);
    }

    @SuppressWarnings("nls")
    private void updateForm() {
        if (templateMap == null) {
            templateMap = new HashMap<String, JasperTemplateWrapper>();
        }

        try {
            setEnable(true);

            jasperTemplateList.setEnabled(true);

            java.util.List<JasperTemplate> jTemplates =
                SessionManager.getAppService().doAction(
                    new JasperTemplateGetAllAction()).getList();

            for (JasperTemplate t : jTemplates) {
                String name = t.getName();
                templateMap.put(name, new JasperTemplateWrapper(
                    SessionManager.getAppService(), t));
                jasperTemplateList.add(name);
            }
            jasperTemplateList.redraw();
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                i18n.tr("Database Error"),
                i18n.tr("Error while updating form"), e);
        }
    }

    /**
     * Sets the name of the selected jasper template list to prevJasperName.
     * 
     */
    private SelectionListener listListener = new SelectionListener() {
        @SuppressWarnings("nls")
        @Override
        public void widgetSelected(SelectionEvent e) {
            try {
                String[] selectedItems = jasperTemplateList.getSelection();
                if (selectedItems.length == 1) {
                    save(false);

                    if (selectedItems[0] != null) {
                        JasperTemplateWrapper selectedTemplate = (templateMap
                            .get(selectedItems[0]));
                        jasperNameText.setText(selectedTemplate.getName());
                        if ((selectedTemplate.getXml() == null)
                            || selectedTemplate.getXml().isEmpty()) {
                            jasperConfigText
                                .setText(i18n.tr("Please select a Jasper file"));
                        } else
                            jasperConfigText
                                .setText(i18n.tr("Jasper file loaded"));

                        prevJasperName = selectedItems[0];

                    } else {
                        jasperNameText
                            .setText(i18n.tr("Please select a template"));
                        jasperConfigText.setText("");
                        prevJasperName = null;
                    }

                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError(
                    i18n.tr("Jasper Template Save Error"),
                    i18n.tr("Could not save template to database"), e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    @SuppressWarnings("nls")
    public boolean save(boolean isConfirmButton) {
        try {
            if (prevJasperName != null) {
                if (isDirty()) {

                    JasperTemplateWrapper selectedTemplate = (templateMap
                        .get(prevJasperName));

                    if (isConfirmButton
                        || BgcPlugin
                            .openConfirm(
                                i18n.tr("Jasper Configuration Editor Saving"),
                                i18n.tr("Jasper Configuration has been modified, do you want to save your changes?"))) {

                        if (loadedJasperFileXml != null) {
                            selectedTemplate.setXml(loadedJasperFileXml);
                            loadedJasperFileXml = null;
                        }

                        if ((selectedTemplate.getXml() != null)
                            && !selectedTemplate.getXml().isEmpty()) {
                            SessionManager.getAppService().doAction(
                                new JasperTemplateSaveAction(selectedTemplate
                                    .getWrappedObject()));
                            jasperConfigText
                                .setText(i18n.tr("Jasper file loaded"));
                            setDirty(false);
                            return true;

                        } else {
                            jasperConfigText
                                .setText(i18n.tr("Please select a Jasper file"));
                            throw new Exception(
                                i18n.tr("Jasper file was not selected"));
                        }

                    }
                    setDirty(false);
                }
            }
        } catch (Exception e1) {
            BgcPlugin.openAsyncError(
                i18n.tr("Template Save Error"),
                i18n.tr("Could not save the template to the database", e1));
        }
        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public void confirm() {
        if (save(true)) {
            BgcPlugin.openInformation(
                i18n.tr("Template Saved"),
                i18n.tr("Template has been sucessfully saved."));
        }
    }

    @SuppressWarnings("nls")
    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == newButton) {
            StringInputDialog dialog = new StringInputDialog(
                i18n.tr("New Jasper Configuration Name"),
                i18n.tr("What is the name of this new Jasper Configuration?"),
                i18n.tr("Name"), PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell());
            if (dialog.open() == Dialog.OK) {
                String jasperConfigName = dialog.getValue();

                if (!templateMap.containsKey(jasperConfigName)) {
                    JasperTemplateWrapper newTemplate =
                        new JasperTemplateWrapper(
                            SessionManager.getAppService());

                    try {
                        newTemplate.setName(jasperConfigName);
                        templateMap.put(jasperConfigName, newTemplate);
                        jasperTemplateList.add(jasperConfigName);

                        jasperTemplateList.redraw();
                    } catch (Exception e1) {
                        BgcPlugin
                            .openAsyncError(
                                i18n.tr("Failed to Save"),
                                i18n.tr(
                                    "Faile to save newly created template: {0} ",
                                    e1.getMessage()));
                    }

                } else {
                    BgcPlugin
                        .openAsyncError(
                            i18n.tr("Jasper Configuration Exists"),
                            i18n.tr("Your new Jasper Configuration must have a unique name."));
                }
            }
        } else if (e.getSource() == deleteButton) {
            try {

                if ((jasperTemplateList.getSelectionCount() == 1)
                    && (prevJasperName != null)) {
                    JasperTemplateWrapper selected = templateMap
                        .get(prevJasperName);

                    if (selected != null) {
                        MessageBox messageBox = new MessageBox(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                        messageBox
                            .setMessage(NLS
                                .bind(
                                    i18n.tr("Are you sure you want to delete {0}?"),
                                    selected.getName()));
                        messageBox
                            .setText(i18n.tr("Deleting Jasper Configuration"));

                        int response = messageBox.open();
                        if (response == SWT.YES) {

                            if (!selected.isNew()) {
                                SessionManager.getAppService().doAction(
                                    new JasperTemplateDeleteAction(selected
                                        .getWrappedObject()));
                            }
                            templateMap.remove(prevJasperName);
                            jasperTemplateList.remove(prevJasperName);

                            jasperNameText
                                .setText(i18n.tr("Please select a template"));
                            jasperConfigText.setText("");
                            prevJasperName = null;

                            jasperTemplateList.deselectAll();
                            jasperTemplateList.redraw();
                        }
                    }
                }
            } catch (Exception e1) {
                BgcPlugin
                    .openAsyncError(
                        i18n.tr("Template Delete Error"),
                        i18n.tr("Could not delete template. A printer template is using this jasper configuration."),
                        e1);
            }
        } else if (e.getSource() == browseButton) {
            FileDialog fd = new FileDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.OPEN);
            fd.setText(i18n.tr("Select Jasper File"));
            String[] filterExt = { JASPER_EXTENSION };
            fd.setFilterExtensions(filterExt);
            String path = fd.open();
            if (path != null) {
                try {
                    File f = new File(path);
                    if (f.exists()) {
                        loadedJasperFileXml = FileUtils.readFileToString(f);
                        jasperConfigText.setText(path);
                        setDirty(true);
                    } else {
                        BgcPlugin
                            .openAsyncError(
                                i18n.tr("File Not Found"),
                                i18n.tr("File selected does not exist."));
                    }

                } catch (IOException e1) {
                    BgcPlugin
                        .openAsyncError(
                            i18n.tr("Template Read Error"),
                            i18n.tr("Could not open the template file for reading"),
                            e1);
                }
            }
        } else {
            BgcPlugin.openAsyncError(i18n.tr("Invalid selection event"),
                i18n.tr("invalid selection source"));
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }

}
