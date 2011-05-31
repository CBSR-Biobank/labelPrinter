package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline;

public abstract class Template extends JasperOutline implements Serializable {

	private static final long serialVersionUID = -4213741888020425604L;

	// TODO implement clonable
	public static void Clone(Template original, Template clone) {
		
		// clone template name
		clone.name = original.name;

		//clone intended printer name
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

	public void setName(String name) {
		if (name == null)
			name = "default";

		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setIntendedPrinter(String intendedPrinterName) {
		if (intendedPrinterName == null)
			intendedPrinterName = "default";

		this.intendedPrinterName = intendedPrinterName;
	}

	public String getIntendedPrinter() {
		if (this.intendedPrinterName == null)
			this.intendedPrinterName = "default";

		return this.intendedPrinterName;
	}

	private String intendedPrinterName = "default";

	private String name = "default";

	protected byte[] jasperTemplateFileData = null;

	protected abstract void setJasperFileData(byte[] jasperData);

	protected abstract boolean jasperFileDataExists();

	protected Configuration config = null;

	protected abstract Configuration getConfiguration();

	public abstract void setConfiguration(Configuration configuration)
			throws Exception;

	public abstract ArrayList<String> getConfigurationKeyList();

}
