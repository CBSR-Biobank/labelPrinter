package edu.ualberta.med.biobank.labelprinter.template.jasper.element;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.labelprinter.template.configuration.Rectangle;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.barcodes.Barcode1D;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.text.Text;

/**
 * Generates text element (2x) ,1D barcode elements in a single batch. Useful
 * for patient information fields.
 * 
 * @author Thomas Polasek 2011
 *
 */
@SuppressWarnings("nls")
public class FieldGenerator {

    public static List<Element> generateElements(Rectangle textRect,
        String label, String value, Font font, Rectangle barcodeRect,
        boolean printBarcode) {

        if ((font == null) && ((label != null) || (value != null)))
            throw new IllegalArgumentException(
                "must specify font to the draw the provided label,value texts");

        if ((textRect == null) || (barcodeRect == null))
            throw new IllegalArgumentException(
                "Null dimensions specified.");

        List<Element> elements = new ArrayList<Element>();

        String textLabel = "";

        if ((label != null) && (label.length() > 0)) {
            textLabel = label;
        }

        if ((value != null) && (value.length() > 0)) {
            textLabel += ": " + value;
            if (printBarcode) {
                elements.add(new Barcode1D(barcodeRect, value, font));
            }
        }

        if (textLabel.length() != 0)
            elements.add(new Text(textRect, textLabel, font));

        return elements;
    }
};