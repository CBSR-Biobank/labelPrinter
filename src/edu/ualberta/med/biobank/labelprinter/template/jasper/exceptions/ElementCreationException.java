package edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions;

public class ElementCreationException extends BarcodeCreationException {

    private static final long serialVersionUID = -1742566077364261222L;

    String mistake;

    public ElementCreationException() {
        super();
        mistake = "not available";
    }

    public ElementCreationException(String error) {
        super(error);
        mistake = error;
    }

    @Override
    public String getError() {
        return mistake;
    }
}
