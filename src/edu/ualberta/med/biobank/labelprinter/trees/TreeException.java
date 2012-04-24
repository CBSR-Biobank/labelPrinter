package edu.ualberta.med.biobank.labelprinter.trees;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class TreeException extends Exception {

    private static final long serialVersionUID = -7571443033631791683L;
    private static final I18n i18n = I18nFactory.getI18n(TreeException.class);
    String mistake;

    @SuppressWarnings("nls")
    public TreeException() {
        super();
        mistake = i18n.trc("Error message", "not available");
    }

    public TreeException(String error) {
        super(error);
        mistake = error;
    }

    public String getError() {
        return mistake;
    }
}