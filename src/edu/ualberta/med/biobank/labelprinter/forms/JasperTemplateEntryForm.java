package edu.ualberta.med.biobank.labelprinter.forms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
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
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryForm;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.labelprinter.dialogs.StringInputDialog;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * View for adding new jasper files that are mapped to a user chosen name.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class JasperTemplateEntryForm extends BgcEntryForm implements
    SelectionListener {

    private static final String JASPER_EXTENSION = "*.jrxml"; //$NON-NLS-1$

    public static final String ID =
        "edu.ualberta.med.biobank.barcodegenerator.forms.JasperTemplateEntryForm"; //$NON-NLS-1$

    private Button deleteButton = null;
    private Button newButton = null;
    private Button browseButton = null;

    private BgcBaseText jasperNameText = null;
    private BgcBaseText jasperConfigText = null;

    private List jasperTemplateList = null;

    private String loadedJasperFileXml = null;
    private String prevJasperName = null;

    private Map<String, JasperTemplateWrapper> templateMap = null;

    private boolean loggedIn = false;
    private ISourceProviderListener loginProvider = null;

    @Override
    protected void init() throws Exception {
        setPartName(Messages.JasperTemplateEntryForm_title);
    }

    @Override
    protected void performDoubleClick(DoubleClickEvent event) {
        // do nothing for now
    }

    @Override
    protected Image getFormImage() {
        return null;
    }

    protected String getOkMessage() {
        return Messages.JasperTemplateEntryForm_ok_msg;
    }

    @Override
    public void dispose() {
        if (loginProvider != null) {
            BgcPlugin.getSessionStateSourceProvider()
                .removeSourceProviderListener(loginProvider);
            loginProvider = null;
        }
        super.dispose();
    }

    @Override
    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addConfirmAction();
        form.updateToolBar();
    }

    @Override
    protected void createFormContent() throws Exception {
        super.createFormContent();
        form.setText(Messages.JasperTemplateEntryForm_form_title);
        form.setMessage(Messages.JasperTemplateEntryForm_description,
            IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        BgcSessionState sessionSourceProvider = BgcPlugin
            .getSessionStateSourceProvider();

        loggedIn = sessionSourceProvider.getCurrentState()
            .get(BgcSessionState.SESSION_STATE_SOURCE_NAME)
            .equals(BgcSessionState.LOGGED_IN);

        createMasterDetail();

        if (loginProvider != null) {
            sessionSourceProvider.removeSourceProviderListener(loginProvider);
            loginProvider = null;
        }

        loginProvider = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceValue != null) {
                    loggedIn = sourceValue.equals(BgcSessionState.LOGGED_IN);
                    updateForm();
                }
            }

            @Override
            public void sourceChanged(int sourcePriority,
                @SuppressWarnings("rawtypes") Map sourceValuesByName) {
                // do nothing for now
            }
        };
        sessionSourceProvider.addSourceProviderListener(loginProvider);

        updateForm();
    }

    @Override
    public void setFocus() {
        // do nothing for now
    }

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
            Messages.JasperTemplateEntryForm_jasper_configs_label, masterComp);
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
            Messages.JasperTemplateEntryForm_new_btn, SWT.NONE);
        newButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));
        newButton.addSelectionListener(this);

        deleteButton = toolkit.createButton(buttonComp,
            Messages.JasperTemplateEntryForm_delete_btn, SWT.NONE);
        deleteButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));
        deleteButton.addSelectionListener(this);

        createConfigDetailsSection();
    }

    private void createConfigDetailsSection() {
        Composite detailsComp = toolkit.createComposite(page);
        detailsComp.setLayout(new GridLayout(1, false));
        detailsComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, true));

        Composite client = createSectionWithClient(
            Messages.JasperTemplateEntryForm_jasper_config_details_label,
            detailsComp);
        client.setLayout(new GridLayout(3, false));
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL
            | SWT.TOP, true, true));

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;

        jasperNameText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE,
            Messages.JasperTemplateEntryForm_config_name_label);
        jasperNameText.setLayoutData(gd);
        jasperNameText.setEditable(false);

        jasperConfigText = (BgcBaseText) createLabelledWidget(client,
            BgcBaseText.class, SWT.NONE,
            Messages.JasperTemplateEntryForm_jasper_file_label);
        jasperConfigText.setEditable(false);
        jasperConfigText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseButton = toolkit.createButton(client,
            Messages.JasperTemplateEntryForm_browse, SWT.PUSH);
        browseButton.addSelectionListener(this);
    }

    private void setEnable(boolean enable) {
        jasperTemplateList.setEnabled(enable);
        deleteButton.setEnabled(enable);
        browseButton.setEnabled(enable);
        newButton.setEnabled(enable);
    }

    private void updateForm() {
        if (templateMap == null) {
            templateMap = new HashMap<String, JasperTemplateWrapper>();
        }

        try {

            if (loggedIn) {
                setEnable(true);

                jasperTemplateList.setEnabled(true);

                for (JasperTemplateWrapper t : JasperTemplateWrapper
                    .getAllTemplates(SessionManager.getAppService())) {
                    String name = t.getName();
                    templateMap.put(name, t);
                    jasperTemplateList.add(name);
                }
                jasperTemplateList.redraw();
            } else {

                setEnable(false);

                templateMap.clear();

                jasperTemplateList.removeAll();
                jasperTemplateList.setEnabled(false);
                jasperTemplateList.redraw();
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.JasperTemplateEntryForm_update_error_title,
                Messages.JasperTemplateEntryForm_update_error_msg, e);
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
                                .setText(Messages.JasperTemplateEntryForm_select_file_msg);
                        } else
                            jasperConfigText
                                .setText(Messages.JasperTemplateEntryForm_loaded_msg);

                        prevJasperName = selectedItems[0];

                    } else {
                        jasperNameText
                            .setText(Messages.JasperTemplateEntryForm_select_template_msg);
                        jasperConfigText.setText(""); //$NON-NLS-1$
                        prevJasperName = null;
                    }

                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError(
                    Messages.JasperTemplateEntryForm_save_error_title,
                    Messages.JasperTemplateEntryForm_save_error_msg, e1);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);

        }
    };

    public boolean save(boolean isConfirmButton) {
        try {
            if (prevJasperName != null) {
                if (isDirty()) {

                    JasperTemplateWrapper selectedTemplate = (templateMap
                        .get(prevJasperName));

                    if (isConfirmButton
                        || BgcPlugin
                            .openConfirm(
                                Messages.JasperTemplateEntryForm_save_confirm_title,
                                Messages.JasperTemplateEntryForm_save_confirm_msg)) {

                        if (loadedJasperFileXml != null) {
                            selectedTemplate.setXml(loadedJasperFileXml);
                            loadedJasperFileXml = null;
                        }

                        if ((selectedTemplate.getXml() != null)
                            && !selectedTemplate.getXml().isEmpty()) {
                            selectedTemplate.persist();
                            jasperConfigText
                                .setText(Messages.JasperTemplateEntryForm_loaded_msg);
                            setDirty(false);
                            return true;

                        } else {
                            jasperConfigText
                                .setText(Messages.JasperTemplateEntryForm_select_file_msg);
                            throw new Exception(
                                Messages.JasperTemplateEntryForm_not_selected_msg);
                        }

                    }
                    setDirty(false);
                }
            }
        } catch (Exception e1) {
            BgcPlugin.openAsyncError(
                Messages.JasperTemplateEntryForm_template_save_error_title,
                Messages.JasperTemplateEntryForm_template_save_error_msg, e1);
        }
        return false;
    }

    @Override
    public void confirm() {
        if (save(true)) {
            BgcPlugin.openInformation(
                Messages.JasperTemplateEntryForm_saved_msg,
                Messages.JasperTemplateEntryForm_save_success_msg);
        }
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == newButton) {
            StringInputDialog dialog = new StringInputDialog(
                Messages.JasperTemplateEntryForm_input_config_name_title,
                Messages.JasperTemplateEntryForm_input_config_name_msg,
                Messages.JasperTemplateEntryForm_name_label, PlatformUI
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
                                Messages.JasperTemplateEntryForm_save_failed_title,
                                NLS.bind(
                                    Messages.JasperTemplateEntryForm_save_failed_msg,
                                    e1.getMessage()));
                    }

                } else {
                    BgcPlugin
                        .openAsyncError(
                            Messages.JasperTemplateEntryForm_name_exists_error_title,
                            Messages.JasperTemplateEntryForm_name_exists_error_msg);
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
                                    Messages.JasperTemplateEntryForm_delete_confirm_msg,
                                    selected.getName()));
                        messageBox
                            .setText(Messages.JasperTemplateEntryForm_deleting);

                        int response = messageBox.open();
                        if (response == SWT.YES) {

                            if (!selected.isNew())
                                selected.delete();
                            templateMap.remove(prevJasperName);
                            jasperTemplateList.remove(prevJasperName);

                            jasperNameText
                                .setText(Messages.JasperTemplateEntryForm_select_template_msg);
                            jasperConfigText.setText(""); //$NON-NLS-1$
                            prevJasperName = null;

                            jasperTemplateList.deselectAll();
                            jasperTemplateList.redraw();
                        }
                    }
                }
            } catch (Exception e1) {
                BgcPlugin.openAsyncError(
                    Messages.JasperTemplateEntryForm_delete_error_title,
                    Messages.JasperTemplateEntryForm_delete_error_msg, e1);
            }
        } else if (e.getSource() == browseButton) {
            FileDialog fd = new FileDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.OPEN);
            fd.setText(Messages.JasperTemplateEntryForm_select_file_label);
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
                                Messages.JasperTemplateEntryForm_file_not_found_title,
                                Messages.JasperTemplateEntryForm_file_not_found_msg);
                    }

                } catch (IOException e1) {
                    BgcPlugin.openAsyncError(
                        Messages.JasperTemplateEntryForm_read_error_title,
                        Messages.JasperTemplateEntryForm_read_error_msg, e1);
                }
            }
        } else {
            BgcPlugin.openAsyncError("Invalid selection event", //$NON-NLS-1$
                "invalid selection source"); //$NON-NLS-1$
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
