package edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions;


public class CBSRGuiVerificationException extends Exception {

	public CBSRGuiVerificationException(String title, String message) {
		this(title + " : " + message);
	}

	public CBSRGuiVerificationException(String message) {
		super(message);
	}
};