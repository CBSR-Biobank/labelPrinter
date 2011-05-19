package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class TemplateStore implements Serializable {


	ArrayList<Template> templates;

	public TemplateStore() {
		templates = new ArrayList<Template>();
	}

	public String[] getTemplateNames() {
		String[] templateNames = new String[templates.size()];

		int i = 0;
		for (Template t : templates) {
			templateNames[i] = t.getName();
			i++;
		}
		return templateNames;
	}

	public Template getTemplate(String name) {
		for (Template t : templates) {
			if (name.equals(t.getName()))
				return t;
		}
		return null;
	}

	public boolean removeTemplate(String name) {
		Template targetTemplate = null;
		for (Template t : templates) {
			if (name.equals(t.getName())) {
				targetTemplate = t;
				break;
			}
		}

		if (targetTemplate == null)
			return false;

		templates.remove(targetTemplate);
		return true;
	}	
	public boolean removeTemplate(Template temp) {
		Template targetTemplate = null;
		for (Template t : templates) {
			if (temp.getName().equals(t.getName())) {
				targetTemplate = t;
				break;
			}
		}

		if (targetTemplate == null)
			return false;

		templates.remove(targetTemplate);
		return true;
	}


	// false if a template with the same name was found
	public boolean addTemplate(Template template) {
		for (String name : getTemplateNames()) {
			if (name.equals(template.getName())) {
				return false;
			}
		}
		templates.add(template);
		return true;
	}



	public  void loadStore(File file) throws IOException, ClassNotFoundException{
		TemplateStore templateStore = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		fis = new FileInputStream(file);
		in = new ObjectInputStream(fis);
		templateStore = (TemplateStore) in.readObject();
		in.close();
		this.templates = templateStore.templates;
	}

	public void saveStore(File file) throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(file);
		out = new ObjectOutputStream(fos);
		out.writeObject(this);
		out.close();
	}

}
