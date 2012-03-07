package edu.ualberta.med.biobank.labelprinter.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.preferences.messages"; //$NON-NLS-1$
    public static String PreferenceInitializer_patient_name;
    public static String PreferenceInitializer_patient_type;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
