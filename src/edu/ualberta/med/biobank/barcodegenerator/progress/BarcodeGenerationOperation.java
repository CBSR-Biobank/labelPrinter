package edu.ualberta.med.biobank.barcodegenerator.progress;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.ualberta.med.biobank.barcodegenerator.forms.SpecimanLabelEntryForm.BarcodeViewGuiData;

/**
 * Base class for generating label prints. After the class is run, check
 * isSuccessful() if the operation succeeded. If isSuccessful() fails then
 * obtain the error message with getError().
 * 
 * @author Thomas Polasek 2011
 * 
 */
abstract class BarcodeGenerationOperation implements IRunnableWithProgress {
    protected BarcodeViewGuiData guiData = null;
    protected List<String> patientIDs = null;
    protected boolean successful = false;
    protected String errorTitle = null;
    protected String errorMessage = null;

    public BarcodeGenerationOperation(BarcodeViewGuiData guiData,
        List<String> patientIDs) {
        this.guiData = guiData;
        this.patientIDs = patientIDs;
        successful = false;
    }

    public void saveFailed() {
        successful = false;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public List<String> getPatientIDsUsed() {
        return patientIDs;
    }

    public void setError(String title, String msg) {
        errorTitle = title;
        errorMessage = msg;
    }

    public boolean errorExists() {
        return (getError()[0] != null) || (getError()[1] != null);
    }

    public String[] getError() {
        return new String[] { errorTitle, errorMessage };
    }

    @Override
    public abstract void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException;

};