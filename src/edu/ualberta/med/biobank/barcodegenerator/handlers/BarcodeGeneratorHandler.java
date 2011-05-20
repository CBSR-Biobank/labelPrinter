package edu.ualberta.med.biobank.barcodegenerator.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;


import edu.ualberta.med.biobank.barcodegenerator.Activator;
import edu.ualberta.med.biobank.barcodegenerator.views.LabelPrinterView;

public class BarcodeGeneratorHandler extends AbstractHandler implements IHandler {

	public static final String ID = "edu.ualberta.med.biobank.barcodegenerator.handlers.BarcodeGeneratorHandler";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("CLICKED GENERATE BARCODE.");
		
        IWorkbench workbench = Activator.getDefault().getWorkbench();
        IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        try {
            page.showView(LabelPrinterView.ID);
        } catch (PartInitException e) {
            throw new ExecutionException("View cannot be opened", e);
        }
        return null;
	}


}

