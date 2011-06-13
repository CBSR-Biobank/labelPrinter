package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Rectangle;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrinterLabelTemplateWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Contains a name, intented printer name, jasper file reference and a
 * configuration reference. This class is used to store specific configuration
 * data for a chosen jasper file.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class Template implements Serializable {

    private static final long serialVersionUID = -4213741888020425604L;

    private String intendedPrinterName = "default";

    private String name = "default";

    private PrinterLabelTemplateWrapper plt;

    private byte[] jasperTemplateFileData = null;

    private Configuration config = null;

    public Template() {
        plt = new PrinterLabelTemplateWrapper(SessionManager.getAppService());
    }

    public Template clone() {
        Template clone = new Template();

        // clone template name
        clone.name = this.name;

        // clone intended printer name
        clone.intendedPrinterName = this.intendedPrinterName;

        // clone jasper template name
        clone.plt = this.plt;

        // clone configuration
        if (this.config != null) {
            clone.config = new Configuration();
            Map<String, Rectangle> settings = config.getSettings();
            if (settings != null) {
                for (Entry<String, Rectangle> entry : settings.entrySet()) {
                    Rectangle newRect = new Rectangle(entry.getValue().getX(),
                        entry.getValue().getY(), entry.getValue().getWidth(),
                        entry.getValue().getHeight());
                    clone.config.setSetting(entry.getKey(), newRect);
                }
            }
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

    public boolean jasperTemplateExists() {
        JasperTemplateWrapper jasp = plt.getJasperTemplate();
        if (jasp == null)
            return false;
        return !jasp.getXml().isEmpty();
    }

    public void setJasperTemplate(JasperTemplateWrapper jt) {
        plt.setJasperTemplate(jt);
    }

    public String getJasperTemplate() throws Exception {
        JasperTemplateWrapper jasp = plt.getJasperTemplate();
        if (jasp == null) {
            throw new Exception("jasper template has not been set");
        }
        return jasp.getXml();
    }

    public String getJasperTemplateName() throws Exception {
        JasperTemplateWrapper jasp = plt.getJasperTemplate();
        if (jasp == null) {
            throw new Exception("jasper template has not been set");
        }
        return jasp.getName();
    }

    /**
     * Configuration objects are stored in XML in the database. This method
     * unmarshals the object.
     * 
     * @return
     * @throws JAXBException
     */
    public Configuration getConfiguration() throws JAXBException {

        if (config == null) {
            String configData = plt.getConfigData();
            if (configData == null)
                return null;

            config = new Configuration();
            JAXBContext context = JAXBContext.newInstance(Configuration.class,
                Rectangle.class);
            Unmarshaller u = context.createUnmarshaller();
            ByteArrayInputStream in = new ByteArrayInputStream(plt
                .getConfigData().getBytes());
            config = (Configuration) u.unmarshal(in);
        }
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
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(configuration, sw);
        plt.setConfigData(sw.toString());

        config = configuration;
    }

    public Rectangle getKey(String key) throws JAXBException {
        return getConfiguration().getSetting(key);
    }

    public void persist() throws Exception {
        plt.persist();
    }

    public void reload() throws Exception {
        plt.reload();
    }

    public boolean isNew() {
        return (plt.getId() == null);
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

    public String toString() {
        return new StringBuilder(name).toString();
    }
}
