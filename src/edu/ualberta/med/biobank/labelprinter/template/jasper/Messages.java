package edu.ualberta.med.biobank.labelprinter.template.jasper;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.labelprinter.template.jasper.messages"; //$NON-NLS-1$
    public static String JasperFiller_barcode_img_draw_error;
    public static String JasperFiller_barcode_img_error;
    public static String JasperFiller_barcode_img_failed_error_msg;
    public static String JasperFiller_barcode_info_error_msg;
    public static String JasperFiller_configuration_error_msg;
    public static String JasperFiller_image_s_size_error_msg;
    public static String JasperFiller_jasper_reset_error_msg;
    public static String JasperFiller_load_jasper_error_msg;
    public static String JasperFiller_patient_img_draw_error;
    public static String JasperFiller_patient_img_failed_error_msg;
    public static String JasperFiller_pdf_create_error_msg;
    public static String JasperFiller_pdf_error_msg;
    public static String JasperFiller_print_error_msg;
    public static String JasperFiller_printer_find_error_msg;
    public static String JasperFiller_printer_selection_error_msg;
    public static String JasperFiller_printer_service_error_msg;
    public static String JasperFiller_reset_error_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
