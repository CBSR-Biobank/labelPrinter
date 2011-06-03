package edu.ualberta.med.biobank.barcodegenerator.progress;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.CBSROutlineMaker;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions.CBSRPdfGenException;
import edu.ualberta.med.biobank.barcodegenerator.views.LabelPrinterView.BarcodeViewGuiData;

public class PrintOperation extends BarcodeGenerationOperation {

    public PrintOperation(BarcodeViewGuiData guiData,
        ArrayList<String> patientIDs) {
        super(guiData, patientIDs);
    }

    public void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException {

        successfulSave = false;

        monitor.beginTask("Printing Barcode Labels",
            IProgressMonitor.UNKNOWN);

        try {
            monitor.subTask("Sending Data to Printer");
            CBSROutlineMaker.printLabelsCBSR(guiData, patientIDs);
            successfulSave = true;

        } catch (CBSRPdfGenException e1) {
            monitor.done();
            successfulSave = false;
            setError("Gui Validation", e1.getError());
            return;
        }

        monitor.done();

        // since the cancle operation does nothing, warn the user that
        // they should destroy any recent pags that were printed with
        // by this method.
        if (monitor.isCanceled()) {
            setError("Printing Operation Cancle",
                "The current set of prints are invalid, please shred any "
                    + "sheets that were printed from this operation.");
        }
    }
}