package edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.messages"; //$NON-NLS-1$
    public static String CBSRLabelMaker_barcode_characters_error;
    public static String CBSRLabelMaker_barcodes_size_error;
    public static String CBSRLabelMaker_config_data_invalid_error;
    public static String CBSRLabelMaker_empty_barcode_error;
    public static String CBSRLabelMaker_fill_config_failed;
    public static String CBSRLabelMaker_fill_config_print_failed;
    public static String CBSRLabelMaker_info_box_create_error;
    public static String CBSRLabelMaker_load_config_error;
    public static String CBSRLabelMaker_load_config_settings_error;
    public static String CBSRLabelMaker_null_title_error;
    public static String CBSRLabelMaker_patient_elt_create_error;
    public static String CBSRLabelMaker_patient_info_box_create_error;
    public static String CBSRLabelMaker_valid_jasper_error;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
