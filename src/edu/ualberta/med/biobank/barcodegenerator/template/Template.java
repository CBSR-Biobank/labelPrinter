package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.Serializable;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;

public class Template implements Serializable {

    private static final long serialVersionUID = -4213741888020425604L;

    private String intendedPrinterName = "default";

    private String name = "default";

    private byte[] jasperTemplateFileData = null;

    private Configuration config = null;

    // TODO implement clonable
    public static void Clone(Template original, Template clone) {

        // clone template name
        clone.name = original.name;

        // clone intended printer name
        clone.intendedPrinterName = original.intendedPrinterName;

        // clone configuration
        clone.config = new Configuration();
        for (Entry<String, Rectangle> entry : original.config.getSettings()
            .entrySet()) {
            Rectangle newRect = new Rectangle(entry.getValue().x,
                entry.getValue().y, entry.getValue().width,
                entry.getValue().height);
            clone.config.setSettingsEntry(entry.getKey(), newRect);
        }

        // clone jasper file
        if (original.jasperTemplateFileData != null) {
            clone.jasperTemplateFileData = new byte[original.jasperTemplateFileData.length];
            System.arraycopy(original.jasperTemplateFileData, 0,
                clone.jasperTemplateFileData, 0,
                original.jasperTemplateFileData.length);
        } else {
            clone.jasperTemplateFileData = null;
        }

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null)
            name = "default";

        this.name = name;
    }

    public String getIntendedPrinter() {
        if (this.intendedPrinterName == null)
            this.intendedPrinterName = "default";

        return this.intendedPrinterName;
    }

    public void setIntendedPrinter(String intendedPrinterName) {
        if (intendedPrinterName == null)
            intendedPrinterName = "default";

        this.intendedPrinterName = intendedPrinterName;
    }

    public boolean jasperFileDataExists() {
        return (this.jasperTemplateFileData != null);
    }

    public byte[] getJasperFileData() {
        return this.jasperTemplateFileData;
    }

    public void setJasperFileData(byte[] jasperData) {
        this.jasperTemplateFileData = jasperData;
    }

    public Configuration getConfiguration() {
        return this.config;
    }

    public void setConfiguration(Configuration configuration) {
        this.config = configuration;
    }

    public Rectangle getKey(String key) {
        return config.getSettingsKey(key);
    }
}
