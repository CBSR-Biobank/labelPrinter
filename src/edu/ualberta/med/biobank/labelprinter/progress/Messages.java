package edu.ualberta.med.biobank.labelprinter.progress;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.barcodegenerator.progress.messages"; //$NON-NLS-1$
    public static String PrintOperation_cancel_title;
    public static String PrintOperation_print_cancel_msg;
    public static String PrintOperation_printing_task;
    public static String PrintOperation_sending_data_subtask;
    public static String PrintOperation_validation_error_title;
    public static String SaveOperation_generating_pdf_subtask;
    public static String SaveOperation_saving_barcode_pdf_task;
    public static String SaveOperation_saving_pdf_error_msg;
    public static String SaveOperation_saving_pdf_error_title;
    public static String SaveOperation_saving_pdf_subtask;
    public static String SaveOperation_validation_error_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
