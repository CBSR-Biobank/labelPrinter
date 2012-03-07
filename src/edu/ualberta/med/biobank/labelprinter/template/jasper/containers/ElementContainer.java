package edu.ualberta.med.biobank.labelprinter.template.jasper.containers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.labelprinter.template.jasper.element.Element;



public abstract class ElementContainer {
	protected List<Element> elements  = new ArrayList<Element>();
	
	public List<Element> getElements(){
		return this.elements;
	}
}
