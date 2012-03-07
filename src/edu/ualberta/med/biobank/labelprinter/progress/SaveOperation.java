package edu.ualberta.med.biobank.labelprinter.progress;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import edu.ualberta.med.biobank.labelprinter.forms.PatientLabelEntryForm.BarcodeViewGuiData;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.CBSRLabelMaker;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.exceptions.CBSRPdfGenException;

/**
 * 
 * Loads the guiData and template data into the appropiate jasper maker class.
 * After the jasper operation is completed, a pdf is created and saved.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class SaveOperation extends BarcodeGenerationOperation {

    private String pdfFilePath = ""; //$NON-NLS-1$

    public SaveOperation(BarcodeViewGuiData guiData,
        List<String> patientNumbers, String pdfFilePath) {
        super(guiData, patientNumbers);
        this.pdfFilePath = pdfFilePath;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InterruptedException {

        byte[] pdfdata = null;

        successful = false;
        monitor.beginTask(Messages.SaveOperation_saving_barcode_pdf_task,
            IProgressMonitor.UNKNOWN);

        try {
            monitor.subTask(Messages.SaveOperation_generating_pdf_subtask);
            pdfdata = CBSRLabelMaker.generatePdfCBSR(guiData, patientNumbers);

        } catch (CBSRPdfGenException e1) {
            monitor.done();
            successful = false;
            setError(Messages.SaveOperation_validation_error_title,
                e1.getError());
        } catch (JAXBException e2) {
            monitor.done();
            successful = false;
            setError(Messages.SaveOperation_validation_error_title,
                e2.getMessage());
        }

        if (pdfdata != null) {
            FileOutputStream fos;
            try {
                monitor.subTask(Messages.SaveOperation_saving_pdf_subtask);
                fos = new FileOutputStream(pdfFilePath);
                fos.write(pdfdata);
                fos.close();
                successful = true;

            } catch (Exception e1) {
                monitor.done();
                successful = false;
                setError(
                    Messages.SaveOperation_saving_pdf_error_title,
                    NLS.bind(Messages.SaveOperation_saving_pdf_error_msg,
                        e1.getMessage()));
                return;

            }
        }
        monitor.done();

        if (monitor.isCanceled()) {
            successful = false;
            new File(pdfFilePath).delete();
        }
    }
}