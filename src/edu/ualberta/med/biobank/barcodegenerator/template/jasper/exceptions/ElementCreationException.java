package edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions;

public class ElementCreationException extends BarcodeCreationException {

    private static final long serialVersionUID = -1742566077364261222L;

    String mistake;

    public ElementCreationException() {
        super();
        mistake = Messages.ElementCreationException_mistake_notavailable_text;
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
