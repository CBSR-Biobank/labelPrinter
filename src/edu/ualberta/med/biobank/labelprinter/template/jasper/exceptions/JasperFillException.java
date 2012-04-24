package edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class JasperFillException extends Exception {

    private static final long serialVersionUID = -1203245512937171890L;

    String mistake;

    private static final I18n i18n = I18nFactory
        .getI18n(JasperFillException.class);

    @SuppressWarnings("nls")
    public JasperFillException() {
        super();
        mistake = i18n.trc("Error message", "not available");
    }

    public JasperFillException(String error) {
        super(error);
        mistake = error;
    }

    public String getError() {
        return mistake;
    }
}
