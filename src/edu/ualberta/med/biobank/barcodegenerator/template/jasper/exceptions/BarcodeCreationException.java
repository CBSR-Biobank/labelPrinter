package edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions;

public class BarcodeCreationException extends Exception {
	String mistake;

	public BarcodeCreationException() {
		super();
		mistake = "not available";
	}

	public BarcodeCreationException(String error) {
		super(error);
		mistake = error;
	}

	public String getError() {
		return mistake;
	}
}
