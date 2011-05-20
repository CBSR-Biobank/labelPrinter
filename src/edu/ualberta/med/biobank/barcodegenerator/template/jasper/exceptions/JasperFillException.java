package edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions;

public class JasperFillException extends Exception {
	String mistake;

	public JasperFillException() {
		super();
		mistake = "not available";
	}

	public JasperFillException(String error) {
		super(error);
		mistake = error;
	}

	public String getError() {
		return mistake;
	}
}
