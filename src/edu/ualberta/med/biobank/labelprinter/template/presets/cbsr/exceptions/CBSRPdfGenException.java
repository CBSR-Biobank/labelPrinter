package edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.exceptions;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class CBSRPdfGenException extends Exception {

    private static final long serialVersionUID = -6225763254642894265L;
    private static final I18n i18n = I18nFactory
        .getI18n(CBSRPdfGenException.class);

    String mistake;

    @SuppressWarnings("nls")
    public CBSRPdfGenException() {
        super();
        mistake = i18n.trc("Error message", "not available");
    }

    public CBSRPdfGenException(String error) {
        super(error);
        mistake = error;
    }

    public String getError() {
        return mistake;
    }
}
