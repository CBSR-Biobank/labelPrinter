package edu.ualberta.med.biobank.barcodegenerator.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.barcodegenerator.BarcodeGenPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BarcodeGenPlugin.getDefault().getPreferenceStore();

        setDefaults(store);
    }

    public static void setDefaults(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.PROJECT_TITLE, "BBPSP");
        store.setDefault(PreferenceConstants.LOGO_FILE_LOCATION, "");
        store.setDefault(PreferenceConstants.TEMPLATE_NAME, "");
        store.setDefault(PreferenceConstants.PRINTER_NAME, "");

        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.LABEL_TEXT_1, "Patient Name");
        store.setDefault(PreferenceConstants.LABEL_TEXT_2, "PHN");
        store.setDefault(PreferenceConstants.LABEL_TEXT_3, "Patient Type");

        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_1, false);
        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_2, false);
        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_3, false);

        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.SAMPLETYPE_CHECKBOX, true);
        store.setDefault(PreferenceConstants.SAMPLETYPE_TEXT, "Pt Type___________");
        store.setDefault(PreferenceConstants.PDF_DIRECTORY_PATH, "");
    }

}
