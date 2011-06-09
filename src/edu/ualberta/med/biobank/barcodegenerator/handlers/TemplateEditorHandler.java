package edu.ualberta.med.biobank.barcodegenerator.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.barcodegenerator.BarcodeGenPlugin;
import edu.ualberta.med.biobank.barcodegenerator.views.TemplateEditorView;

public class TemplateEditorHandler extends AbstractHandler implements IHandler {

    public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.handlers.TemplateEditorHandler";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BarcodeGenPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true)) {
                // workbench.showPerspective(TemplateEditorPerspective.ID,
                // workbench.getActiveWorkbenchWindow());

                workbench.showPerspective(
                    "barcodeGenerator.perspective.templateeditor",
                    workbench.getActiveWorkbenchWindow());

                IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
                    .getActivePage();
                page.showView(TemplateEditorView.ID);
            }
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Could not open label printer view : ", e);
        }
        return null;
    }

}
