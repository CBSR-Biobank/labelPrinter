package edu.ualberta.med.biobank.labelprinter.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.dialogs.messages"; //$NON-NLS-1$
    public static String ComboInputDialog_config_label;
    public static String ComboInputDialog_config_validator_msg;
    public static String ComboInputDialog_nooptions_msg;
    public static String StringInputDialog_value_required_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
