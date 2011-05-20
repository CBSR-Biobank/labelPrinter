package edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions;

public class CBSRPdfGenException extends Exception {
	String mistake;

	public CBSRPdfGenException() {
		super();
		mistake = "not available";
	}

	public CBSRPdfGenException(String error) {
		super(error);
		mistake = error;
	}

	public String getError() {
		return mistake;
	}
}
