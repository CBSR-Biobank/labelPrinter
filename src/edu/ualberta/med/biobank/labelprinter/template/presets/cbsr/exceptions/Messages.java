package edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.exceptions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions.messages"; //$NON-NLS-1$
    public static String CBSRPdfGenException_mistake_notavailable_text;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
