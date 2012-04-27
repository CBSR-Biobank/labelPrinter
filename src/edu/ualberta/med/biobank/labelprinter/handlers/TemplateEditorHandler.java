package edu.ualberta.med.biobank.labelprinter.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.gui.common.forms.BgcFormInput;
import edu.ualberta.med.biobank.labelprinter.BarcodeGenPlugin;
import edu.ualberta.med.biobank.labelprinter.forms.LabelTemplateEntryForm;
import edu.ualberta.med.biobank.labelprinter.perspective.TemplateEditorPerspective;

public class TemplateEditorHandler extends AbstractHandler {

    public static final String ID =
        "edu.ualberta.med.biobank.labelprinter.handlers.TemplateEditorHandler"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BarcodeGenPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true)) {
                workbench.showPerspective(TemplateEditorPerspective.ID,
                    workbench.getActiveWorkbenchWindow());

                PlatformUI
                    .getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(
                        new BgcFormInput(LabelTemplateEntryForm.ID,
                            LabelTemplateEntryForm.ID),
                        LabelTemplateEntryForm.ID, true);
            }
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Could not open label printer view : ", e); //$NON-NLS-1$
        }
        return null;
    }

}
