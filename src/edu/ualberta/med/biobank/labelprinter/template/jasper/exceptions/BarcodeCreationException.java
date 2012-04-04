package edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions;

public class BarcodeCreationException extends Exception {
	
	private static final long serialVersionUID = -8668642570419427542L;
	
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
