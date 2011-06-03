package edu.ualberta.med.biobank.barcodegenerator.progress;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.ualberta.med.biobank.barcodegenerator.views.LabelPrinterView.BarcodeViewGuiData;

abstract class BarcodeGenerationOperation implements IRunnableWithProgress {
    protected BarcodeViewGuiData guiData = null;
    protected ArrayList<String> patientIDs = null;
    protected boolean successfulSave = false;
    protected String errorTitle = null;
    protected String errorMessage = null;

    public BarcodeGenerationOperation(BarcodeViewGuiData guiData,
        ArrayList<String> patientIDs) {
        this.guiData = guiData;
        this.patientIDs = patientIDs;
        successfulSave = false;
    }

    public void saveFailed() {
        successfulSave = false;
    }

    public boolean isSuccessfulSave() {
        return successfulSave;
    }

    public ArrayList<String> getPatientIDsUsed() {
        return patientIDs;
    }

    public void setError(String title, String msg) {
        errorTitle = title;
        errorMessage = msg;
    }

    public boolean errorExists() {
        return getError()[0] != null || getError()[1] != null;
    }

    public String[] getError() {
        return new String[] { errorTitle, errorMessage };
    }

    public abstract void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException;

};