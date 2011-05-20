package edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions;

public class ElementCreationException extends BarcodeCreationException {
	String mistake;

	public ElementCreationException() {
		super();
		mistake = "not available";
	}

	public ElementCreationException(String error) {
		super(error);
		mistake = error;
	}

	public String getError() {
		return mistake;
	}
}
