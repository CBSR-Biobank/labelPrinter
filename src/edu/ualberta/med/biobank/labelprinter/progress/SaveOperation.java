package edu.ualberta.med.biobank.labelprinter.progress;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
    private static final I18n i18n = I18nFactory.getI18n(SaveOperation.class);

    public SaveOperation(BarcodeViewGuiData guiData,
        List<String> patientNumbers, String pdfFilePath) {
        super(guiData, patientNumbers);
        this.pdfFilePath = pdfFilePath;
    }

    @SuppressWarnings("nls")
    @Override
    public void run(IProgressMonitor monitor) throws InterruptedException {

        byte[] pdfdata = null;

        successful = false;
        monitor.beginTask(i18n.tr("Saving Barcode Labels PDF"),
            IProgressMonitor.UNKNOWN);

        try {
            monitor.subTask(i18n.tr("Generating PDF"));
            pdfdata = CBSRLabelMaker.generatePdfCBSR(guiData, patientNumbers);

        } catch (CBSRPdfGenException e1) {
            monitor.done();
            successful = false;
            setError(i18n.tr("Gui Validation"),
                e1.getError());
        } catch (JAXBException e2) {
            monitor.done();
            successful = false;
            setError(i18n.tr("Gui Validation"),
                e2.getMessage());
        }

        if (pdfdata != null) {
            FileOutputStream fos;
            try {
                monitor.subTask(i18n.tr("Saving PDF"));
                fos = new FileOutputStream(pdfFilePath);
                fos.write(pdfdata);
                fos.close();
                successful = true;

            } catch (Exception e1) {
                monitor.done();
                successful = false;
                setError(
                    i18n.tr("Saving PDF"),
                    i18n.tr("Problem saving file: {0}",
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