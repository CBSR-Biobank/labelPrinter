package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Rectangle;
import edu.ualberta.med.biobank.common.wrappers.PrinterLabelTemplateWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class Template implements Serializable {

    private static final long serialVersionUID = -4213741888020425604L;

    private String intendedPrinterName = "default";

    private String name = "default";

    PrinterLabelTemplateWrapper plt;

    private byte[] jasperTemplateFileData = null;

    private Configuration config = null;

    public Template clone() {
        Template clone = new Template();

        // clone template name
        clone.name = this.name;

        // clone intended printer name
        clone.intendedPrinterName = this.intendedPrinterName;

        // clone configuration
        clone.config = new Configuration();
        for (Entry<String, Rectangle> entry : this.config.getSettings()
            .entrySet()) {
            Rectangle newRect = new Rectangle(entry.getValue().getX(), entry
                .getValue().getY(), entry.getValue().getWidth(), entry
                .getValue().getHeight());
            clone.config.setSettingsEntry(entry.getKey(), newRect);
        }

        // clone jasper file
        if (this.jasperTemplateFileData != null) {
            clone.jasperTemplateFileData = new byte[this.jasperTemplateFileData.length];
            System.arraycopy(this.jasperTemplateFileData, 0,
                clone.jasperTemplateFileData, 0,
                this.jasperTemplateFileData.length);
        } else {
            clone.jasperTemplateFileData = null;
        }
        return clone;
    }

    public String getName() {
        return plt.getName();
    }

    public void setName(String name) {
        plt.setName(name);
    }

    public String getPrinterName() {
        return plt.getPrinterName();
    }

    public void setPrinterName(String printerName) {
        plt.setPrinterName(printerName);
    }

    public boolean jasperFileDataExists() {
        return !plt.getJasperTemplate().getXml().isEmpty();
    }

    public byte[] getJasperFileData() {
        // TODO; unserialize jasper file
        return null;
    }

    public void setJasperFileData(byte[] jasperData) {
        // TODO: serialize jasper file
    }

    /**
     * Configuration objects are stored in XML in the database. This method
     * unmarshals the object.
     * 
     * @return
     * @throws JAXBException
     */
    public Configuration getConfiguration() throws JAXBException {
        Configuration config = new Configuration();
        JAXBContext context = JAXBContext.newInstance(Configuration.class,
            Rectangle.class);
        Unmarshaller u = context.createUnmarshaller();
        ByteArrayInputStream in = new ByteArrayInputStream(plt.getConfigData()
            .getBytes());
        config = (Configuration) u.unmarshal(in);
        return config;
    }

    /**
     * Configuration objects are stored in XML in the database. This method
     * marshals the object.
     * 
     * @param configuration
     * @throws JAXBException
     */
    public void setConfiguration(Configuration configuration)
        throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Configuration.class,
            Rectangle.class);
        Marshaller marshaller;
        marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(config, sw);
        plt.setConfigData(sw.toString());
    }

    public Rectangle getKey(String key) {
        return config.getSettingsKey(key);
    }

    public void persist() throws Exception {
        plt.persist();
    }

    public void delete() throws Exception {
        plt.delete();
        plt = null;
    }

    public static Template getTemplateByName(String name)
        throws ApplicationException {
        Template tplt = new Template();
        tplt.plt = PrinterLabelTemplateWrapper.getTemplateByName(
            SessionManager.getAppService(), name);
        return tplt;
    }

    public static List<String> getTemplateNames() throws ApplicationException {
        return PrinterLabelTemplateWrapper.getTemplateNames(SessionManager
            .getAppService());
    }
}
