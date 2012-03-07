package edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions;

public class JasperFillException extends Exception {
	
	private static final long serialVersionUID = -1203245512937171890L;
	
	String mistake;

	public JasperFillException() {
		super();
		mistake = Messages.JasperFillException_mistake_notavailable_text;
	}

	public JasperFillException(String error) {
		super(error);
		mistake = error;
	}

	public String getError() {
		return mistake;
	}
}
