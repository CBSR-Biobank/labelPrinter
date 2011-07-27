package edu.ualberta.med.biobank.barcodegenerator.trees;

public class TreeException extends Exception {

	private static final long serialVersionUID = -7571443033631791683L;
	
	String mistake;

	public TreeException() {
		super();
		mistake = Messages.TreeException_mistake_notavailable_text;
	}

	public TreeException(String error) {
		super(error);
		mistake = error;
	}

	public String getError() {
		return mistake;
	}
}