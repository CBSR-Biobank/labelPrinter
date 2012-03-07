package edu.ualberta.med.biobank.labelprinter.template.jasper.element.barcodes;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.labelprinter.template.jasper.element.barcodes.messages"; //$NON-NLS-1$
    public static String Barcode1D_empty_msg;
    public static String Barcode1D_img_buffer_error;
    public static String Barcode1D_null_dimensions_msg;
    public static String Barcode1D_null_font_msg;
    public static String Barcode2D_characters_error;
    public static String Barcode2D_img_buffer_error;
    public static String Barcode2D_null_dim_error;
    public static String BarcodeGenerator_dpi_error;
    public static String BarcodeGenerator_empty_msg_1D_error;
    public static String BarcodeGenerator_empty_msg_2D_error;
    public static String BarcodeGenerator_font_error;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
