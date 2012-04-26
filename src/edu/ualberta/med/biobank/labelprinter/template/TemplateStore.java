package edu.ualberta.med.biobank.labelprinter.template;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.labelPrinter.PrinterLabelTemplateGetAllAction;
import edu.ualberta.med.biobank.common.action.labelPrinter.PrinterLabelTemplateGetInfoAction;
import edu.ualberta.med.biobank.common.action.labelPrinter.PrinterLabelTemplateGetInfoAction.PrinterLabelTemplateInfo;
import edu.ualberta.med.biobank.common.wrappers.PrinterLabelTemplateWrapper;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * For add/editing/listing/removing templates. Contains several helper functions
 * to access template information.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class TemplateStore {

    private Map<String, Template> templates;
    private static final I18n i18n = I18nFactory.getI18n(TemplateStore.class);

    public TemplateStore() throws ApplicationException {
        List<PrinterLabelTemplate> plTemplates =
            SessionManager.getAppService().doAction(
                new PrinterLabelTemplateGetAllAction()).getList();

        templates = new HashMap<String, Template>();
        for (PrinterLabelTemplate plTemplate : plTemplates) {
            Template t =
                new Template(new PrinterLabelTemplateWrapper(
                    SessionManager.getAppService(),
                    plTemplate));
            templates.put(plTemplate.getName(), t);
        }
    }

    public Set<String> getTemplateNames() {
        return templates.keySet();
    }

    @SuppressWarnings("nls")
    public Template getTemplate(String name) throws Exception {
        Template t = templates.get(name);
        if (t == null) {
            throw new Exception(i18n.tr("Template with name {0} not found",
                name));
        }
        return t;
    }

    public void addTemplate(Template updatedTemplate) {
        String name = updatedTemplate.getName();
        Template oldTemplate = templates.get(name);
        if (oldTemplate != null) {
            templates.remove(oldTemplate);
        }
        templates.put(name, updatedTemplate);
    }

    @SuppressWarnings("nls")
    public void deleteTemplate(Template template) throws Exception {
        String name = template.getName();
        if (!templates.containsKey(name)) {
            throw new Exception(i18n.tr("Template with name {0} not found",
                name));
        }
        templates.remove(name);
    }

    @SuppressWarnings("nls")
    public void deleteTemplate(String name) throws Exception {
        Template t = templates.get(name);
        if (t == null) {
            throw new Exception(i18n.tr("Template with name {0} not found",
                name));
        }
        deleteTemplate(t);
    }

    public void reloadTemplate(String name) throws Exception {
        deleteTemplate(name);

        PrinterLabelTemplateInfo info =
            SessionManager.getAppService().doAction(
                new PrinterLabelTemplateGetInfoAction(name));

        Template t =
            new Template(new PrinterLabelTemplateWrapper(
                SessionManager.getAppService(),
                info.printerLabelTemplate));

        templates.put(name, t);

    }

    public void purge() {
        ArrayList<Template> removeTemplateList = new ArrayList<Template>();

        for (Template t : templates.values())
            if (!t.hasWrapper())
                removeTemplateList.add(t);

        for (Template t : removeTemplateList)
            templates.remove(t);

    }
}
