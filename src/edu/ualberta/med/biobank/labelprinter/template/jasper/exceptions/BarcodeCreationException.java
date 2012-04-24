package edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class BarcodeCreationException extends Exception {

    private static final long serialVersionUID = -8668642570419427542L;

    private static final I18n i18n = I18nFactory
        .getI18n(BarcodeCreationException.class);

    String mistake;

    @SuppressWarnings("nls")
    public BarcodeCreationException() {
        super();
        mistake = i18n.tr("not available ");
    }

    public BarcodeCreationException(String error) {
        super(error);
        mistake = error;
    }

    public String getError() {
        return mistake;
    }
}
