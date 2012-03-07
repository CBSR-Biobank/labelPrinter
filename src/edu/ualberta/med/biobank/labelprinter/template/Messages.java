package edu.ualberta.med.biobank.labelprinter.template;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.template.messages"; //$NON-NLS-1$
    public static String Template_template_set_error;
    public static String TemplateStore_notfound_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
