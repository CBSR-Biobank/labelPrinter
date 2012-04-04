package edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.exceptions;

public class CBSRPdfGenException extends Exception {
	
	private static final long serialVersionUID = -6225763254642894265L;
	
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
