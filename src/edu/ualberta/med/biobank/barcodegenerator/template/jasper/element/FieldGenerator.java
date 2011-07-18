package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Rectangle;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes.Barcode1D;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text.Text;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.ElementCreationException;

/**
 * Generates text element (2x) ,1D barcode elements in a single batch. Useful
 * for patient information fields.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class FieldGenerator {

    public static List<Element> generateElements(Rectangle textRect,
        String label, String value, Font font, Rectangle barcodeRect,
        boolean printBarcode) throws ElementCreationException {

        if (font == null && (label != null || value != null))
            throw new ElementCreationException(
                Messages.FieldGenerator_null_font_msg);

        if (textRect == null || barcodeRect == null)
            throw new ElementCreationException(Messages.FieldGenerator_null_dim_error);

        List<Element> elements = new ArrayList<Element>();

        String textLabel = ""; //$NON-NLS-1$

        if (label != null && label.length() > 0) {
            textLabel = label;
        }

        if (value != null && value.length() > 0) {
            textLabel += " : " + value; //$NON-NLS-1$
            if (printBarcode) {
                elements.add(new Barcode1D(barcodeRect, value, font));
            }
        }

        if (textLabel.length() != 0)
            elements.add(new Text(textRect, textLabel, font));

        return elements;
    }
};