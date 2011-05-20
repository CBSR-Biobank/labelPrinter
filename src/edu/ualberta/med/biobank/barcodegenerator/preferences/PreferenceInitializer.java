package edu.ualberta.med.biobank.barcodegenerator.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.barcodegenerator.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		setDefaults(store);
	}
	
	public static void setDefaults(IPreferenceStore store ){
		store.setDefault(PreferenceConstants.PROJECT_TITLE, "BBPSP");
		store.setDefault(PreferenceConstants.LOGO_FILE_LOCATION, "");

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

		store.setDefault(PreferenceConstants.SAMPLETYPE_CHECKBOX, false);
		store.setDefault(PreferenceConstants.SAMPLETYPE_TEXT, "Pt Type_____");
	}
	
}