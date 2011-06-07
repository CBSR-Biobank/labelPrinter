package edu.ualberta.med.biobank.barcodegenerator.progress;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.CBSRLabelMaker;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions.CBSRPdfGenException;
import edu.ualberta.med.biobank.barcodegenerator.views.LabelPrinterView.BarcodeViewGuiData;

public class SaveOperation extends BarcodeGenerationOperation {

    private String pdfFilePath = "";

    public SaveOperation(BarcodeViewGuiData guiData,
        ArrayList<String> patientIDs, String pdfFilePath) {
        super(guiData, patientIDs);
        this.pdfFilePath = pdfFilePath;
    }

    public void run(IProgressMonitor monitor) throws InterruptedException {

        byte[] pdfdata = null;

        successfulSave = false;
        monitor
            .beginTask("Saving Barcode Labels PDF", IProgressMonitor.UNKNOWN);

        try {
            monitor.subTask("Generating PDF");
            pdfdata = CBSRLabelMaker.generatePdfCBSR(guiData, patientIDs);

        } catch (CBSRPdfGenException e1) {
            monitor.done();
            successfulSave = false;
            setError("Gui Validation", e1.getError());
            return;
        }

        if (pdfdata != null) {
            FileOutputStream fos;
            try {
                monitor.subTask("Saving PDF");
                fos = new FileOutputStream(pdfFilePath);
                fos.write(pdfdata);
                fos.close();
                successfulSave = true;

            } catch (Exception e1) {
                monitor.done();
                successfulSave = false;
                setError("Saving Pdf",
                    "Problem saving file: " + e1.getMessage());
                return;

            }
        }
        monitor.done();

        if (monitor.isCanceled()) {
            successfulSave = false;
            new File(pdfFilePath).delete();
        }
    }
}