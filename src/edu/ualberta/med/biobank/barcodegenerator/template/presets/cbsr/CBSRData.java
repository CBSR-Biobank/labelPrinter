package edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr;

import java.io.ByteArrayInputStream;

import edu.ualberta.med.biobank.barcodegenerator.template.Template;

/**
 * Class passed to a CBSR jasper label maker class. Contains the user input from
 * the GUI.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class CBSRData {
    public String projectTileStr;
    public ByteArrayInputStream logoStream;

    public String fontName;
    
    public String patientNumberStr;

    public String label1Str;
    public String value1Str;
    public boolean barcode1Print;

    public String label2Str;
    public String value2Str;
    public boolean barcode2Print;

    public String label3Str;
    public String value3Str;
    public boolean barcode3Print;

    public String specimenTypeStr;

    public String printerNameStr;

    public Template template;
}
