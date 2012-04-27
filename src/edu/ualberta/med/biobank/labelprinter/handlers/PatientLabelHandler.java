package edu.ualberta.med.biobank.labelprinter.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.gui.common.forms.BgcFormInput;
import edu.ualberta.med.biobank.labelprinter.BarcodeGenPlugin;
import edu.ualberta.med.biobank.labelprinter.forms.PatientLabelEntryForm;
import edu.ualberta.med.biobank.labelprinter.perspective.PatientLabelPerspective;

public class PatientLabelHandler extends AbstractHandler {

    public static final String ID =
        "edu.ualberta.med.biobank.labelprinter.handlers.patientlabelHandler"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BarcodeGenPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true)) {
                workbench.showPerspective(PatientLabelPerspective.ID,
                    workbench.getActiveWorkbenchWindow());

                PlatformUI
                    .getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(
                        new BgcFormInput(PatientLabelEntryForm.ID,
                            PatientLabelEntryForm.ID),
                        PatientLabelEntryForm.ID, true);

            }
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Could not open label printer view : ", e); //$NON-NLS-1$
        }
        return null;
    }

}
