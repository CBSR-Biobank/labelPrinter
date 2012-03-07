package edu.ualberta.med.biobank.labelprinter.template.jasper.element.text;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.labelprinter.template.jasper.element.text.messages"; //$NON-NLS-1$
    public static String Text_empty_msg_error;
    public static String Text_null_dim_error;
    public static String Text_null_font_error;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
