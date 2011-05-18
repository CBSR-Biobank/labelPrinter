package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes.Barcode1D;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text.Text;

public class FieldGenerator {

	public static ArrayList<Element> generateElements(Rectangle textRect, String label, String value, Font font,
			Rectangle barcodeRect, boolean printBarcode) {

		ArrayList<Element> elements = new ArrayList<Element>();

		String textLabel;

		textLabel = "";
		if (label != null  && label.length() > 0) {
			textLabel = label;
		}
		
		if (value != null && value.length() > 0) {
			textLabel += " : " + value;
			if(printBarcode){
				elements.add(new Barcode1D(barcodeRect,value, font));
			}
		}
	
		if(font == null){
			font = new Font("Times New Roman", Font.PLAIN, 23);
		}

		elements.add(new Text(textRect, textLabel, font));
		
		return elements;
	}
};