package edu.ualberta.med.biobank.labelprinter.template;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.labelPrinter.PrinterLabelTemplateDeleteAction;
import edu.ualberta.med.biobank.common.action.labelPrinter.PrinterLabelTemplateSaveAction;
import edu.ualberta.med.biobank.common.wrappers.JasperTemplateWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrinterLabelTemplateWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.labelprinter.template.configuration.Configuration;
import edu.ualberta.med.biobank.labelprinter.template.configuration.Rectangle;

/**
 * Contains a name, intented printer name, jasper file reference and a
 * configuration reference. This class is used to store specific configuration
 * data for a chosen jasper file.
 *
 * @author Thomas Polasek 2011
 *
 */
public class Template implements Serializable {

    private static final I18n i18n = I18nFactory.getI18n(Template.class);
    private static final long serialVersionUID = -4213741888020425604L;

    public static final BgcLogger logger = BgcLogger.getLogger(Template.class
        .getName());

    private PrinterLabelTemplateWrapper plt;

    private Configuration config = null;

    public Template() {
        plt = new PrinterLabelTemplateWrapper(SessionManager.getAppService());
    }

    public Template(PrinterLabelTemplateWrapper plTemplate) {
        plt = plTemplate;
    }


    @SuppressWarnings("nls")
    @Override
    public Template clone() {
        Template clone = new Template();

        clone.plt = new PrinterLabelTemplateWrapper(
            SessionManager.getAppService());

        try {
            clone.setJasperTemplate(this.getJasperTemplate());
        } catch (Exception e1) {
            logger
                .error(i18n.tr("Error: Failed to clone jasper template."), e1);
            return null;
        }

        // clone template name
        clone.setName(this.getName());

        // clone intended printer name
        clone.setPrinterName(this.getPrinterName());

        // clone configuration
        if (config != null) {
            Configuration newConfig = new Configuration();
            if (config.exists()) {
                for (Entry<String, Rectangle> entry : config.entrySet()) {
                    Rectangle newRect = new Rectangle(entry.getValue().getX(),
                        entry.getValue().getY(), entry.getValue().getWidth(),
                        entry.getValue().getHeight());
                    newConfig.setSetting(entry.getKey(), newRect);
                }
            }
            try {
                clone.setConfiguration(newConfig);
            } catch (JAXBException e) {
                logger.error(i18n.tr("Error: Failed to clone configuration."),
                    e);
                return null;
            }
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

    public String getJasperTemplateXML() throws Exception {
        return getJasperTemplate().getXml();
    }

    @SuppressWarnings("nls")
    public JasperTemplateWrapper getJasperTemplate() throws Exception {
        JasperTemplateWrapper jasp = plt.getJasperTemplate();
        if (jasp == null) {
            throw new Exception(i18n.tr("jasper template has not been set"));
        }
        return jasp;
    }

    @SuppressWarnings("nls")
    public String getJasperTemplateName() throws Exception {
        JasperTemplateWrapper jasp = plt.getJasperTemplate();
        if (jasp == null) {
            throw new Exception(i18n.tr("jasper template has not been set"));
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
    @SuppressWarnings("nls")
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

        if (config.upgradeKeys()) {

            setConfiguration(config);
            try {
                Integer id =
                    SessionManager
                        .getAppService()
                        .doAction(
                            new PrinterLabelTemplateSaveAction(plt
                                .getWrappedObject())).getId();

                plt.setId(id);

            } catch (Exception e) {
                logger
                    .error(
                        i18n.tr("Could not save changes to the database"),
                        e);
                return null;
            }
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
        Integer id = SessionManager.getAppService().doAction(
            new PrinterLabelTemplateSaveAction(plt.getWrappedObject())).getId();
        plt.setId(id);
    }

    public void reload() throws Exception {
        plt.reload();
    }

    public boolean isNew() {
        return (plt.getId() == null);
    }

    public void delete() throws Exception {
        SessionManager.getAppService().doAction(
            new PrinterLabelTemplateDeleteAction(plt.getWrappedObject()));
        plt = null;
    }

    public boolean hasWrapper() {
        return (plt != null);
    }

    @Override
    public String toString() {
        return new StringBuilder(getName()).toString();
    }
}
