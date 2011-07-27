package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.messages"; //$NON-NLS-1$
    public static String FieldGenerator_null_dim_error;
    public static String FieldGenerator_null_font_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
