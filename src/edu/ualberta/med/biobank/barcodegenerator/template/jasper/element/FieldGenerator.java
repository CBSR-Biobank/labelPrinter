package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;


import java.awt.Font;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes.Barcode1D;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text.Text;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.ElementCreationException;

public class FieldGenerator {

	public static ArrayList<Element> generateElements(Rectangle textRect, String label, String value, Font font,
			Rectangle barcodeRect, boolean printBarcode) throws ElementCreationException {

		if (font == null && (label != null || value != null))
			throw new ElementCreationException(
					"must specify font to the draw the provided label,value texts");
		
		if(textRect == null || barcodeRect == null)
			throw new ElementCreationException("Null dimensions specified.");
		

		ArrayList<Element> elements = new ArrayList<Element>();
		
		String textLabel = "";
		
		if (label != null  && label.length() > 0) {
			textLabel = label;
		}
		
		if (value != null && value.length() > 0) {
			textLabel += " : " + value;
			if(printBarcode){
				elements.add(new Barcode1D(barcodeRect,value, font));
			}
		}

		if(textLabel.length() != 0)
			elements.add(new Text(textRect, textLabel, font));
		
		return elements;
	}
};