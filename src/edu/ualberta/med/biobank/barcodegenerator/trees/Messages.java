package edu.ualberta.med.biobank.barcodegenerator.trees;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.trees.messages"; //$NON-NLS-1$
    public static String ConfigurationTree_height_column;
    public static String ConfigurationTree_horizontal_column;
    public static String ConfigurationTree_settings_tree_column;
    public static String ConfigurationTree_valid_config_settings_error;
    public static String ConfigurationTree_vertical_column;
    public static String ConfigurationTree_width_column;
    public static String TreeException_mistake_notavailable_text;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
