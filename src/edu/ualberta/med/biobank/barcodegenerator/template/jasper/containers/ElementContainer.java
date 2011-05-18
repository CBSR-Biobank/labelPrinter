package edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers;

import java.util.ArrayList;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;



public abstract class ElementContainer {
	protected ArrayList<Element> elements  = new ArrayList<Element>();
	
	public ArrayList<Element> getElements(){
		return this.elements;
	}
}
