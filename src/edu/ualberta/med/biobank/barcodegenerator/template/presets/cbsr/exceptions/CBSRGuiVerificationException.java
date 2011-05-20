package edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions;


public class CBSRGuiVerificationException extends Exception {

	private static final long serialVersionUID = -591112207091663347L;

	public CBSRGuiVerificationException(String title, String message) {
		this(title + " : " + message);
	}

	public CBSRGuiVerificationException(String message) {
		super(message);
	}
};