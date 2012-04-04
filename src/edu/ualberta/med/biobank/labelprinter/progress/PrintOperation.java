package edu.ualberta.med.biobank.labelprinter.progress;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import edu.ualberta.med.biobank.labelprinter.forms.PatientLabelEntryForm.BarcodeViewGuiData;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.CBSRLabelMaker;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.exceptions.CBSRPdfGenException;

/**
 * 
 * Loads the guiData and template data into the appropiate jasper maker class.
 * After the jasper operation is completed, the file is sent to a printer .
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class PrintOperation extends BarcodeGenerationOperation {

    public PrintOperation(BarcodeViewGuiData guiData,
        List<String> patientNumbers) {
        super(guiData, patientNumbers);
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
        InterruptedException {

        successful = false;

        monitor.beginTask("Printing Barcode Labels",
            IProgressMonitor.UNKNOWN);

        try {
            monitor.subTask("Sending Data to Printer");
            CBSRLabelMaker.printLabelsCBSR(guiData, patientNumbers);
            successful = true;

        } catch (CBSRPdfGenException e1) {
            monitor.done();
            successful = false;
            setError("Gui Validation",
                e1.getError());
            return;
        }

        monitor.done();

        // since the cancel operation does nothing, warn the user that
        // they should destroy any recent pages that were printed with
        // by this method.
        if (monitor.isCanceled()) {
            setError(
                "Printing Operation Cancel",
                "The current set of prints are invalid, please shred any sheets that were printed from this operation.");
        }
    }
}