package edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.messages"; //$NON-NLS-1$
    public static String BarcodeCreationException_mistake_text_notavailable;
    public static String ElementCreationException_mistake_notavailable_text;
    public static String JasperFillException_mistake_notavailable_text;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
