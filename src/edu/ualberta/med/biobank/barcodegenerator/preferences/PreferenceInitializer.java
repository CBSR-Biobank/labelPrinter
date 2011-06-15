package edu.ualberta.med.biobank.barcodegenerator.preferences;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.barcodegenerator.BarcodeGenPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BarcodeGenPlugin.getDefault()
            .getPreferenceStore();

        setDefaults(store);
    }

    public static void setDefaults(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.PROJECT_TITLE, "BBPSP");
        store.setDefault(PreferenceConstants.LOGO_FILE_LOCATION, "");
        store.setDefault(PreferenceConstants.TEMPLATE_NAME, "");
        store.setDefault(PreferenceConstants.PRINTER_NAME, "");

        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.LABEL_TEXT_1, "Patient Name");
        store.setDefault(PreferenceConstants.LABEL_TEXT_2, "PHN");
        store.setDefault(PreferenceConstants.LABEL_TEXT_3, "Patient Type");

        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_1, false);
        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_2, false);
        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_3, false);

        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.SAMPLETYPE_CHECKBOX, true);
        store.setDefault(PreferenceConstants.SAMPLETYPE_TEXT,
            "Pt Type___________");
        store.setDefault(PreferenceConstants.PDF_DIRECTORY_PATH, "");

        // sets the default font.
        GraphicsEnvironment e = GraphicsEnvironment
            .getLocalGraphicsEnvironment();

        Font[] fonts = null;
        if (e != null)
            fonts = e.getAllFonts();

        if (fonts != null && fonts.length >= 1) {
            String defaultFontName = fonts[0].getFontName();

            for (Font f : fonts) {

                String cFontName = f.getFontName();
                String cFontLower = f.getFontName().toLowerCase();

                if (cFontLower.contains("times new")) {
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("courier")) {
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("arial")) {
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("serif")) {
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("sans")) {
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("mono")) {
                    defaultFontName = cFontName;
                    break;
                }
            }
            store.setDefault(PreferenceConstants.TEXT_FONT_NAME,
                defaultFontName);
        } else
            store.setDefault(PreferenceConstants.TEXT_FONT_NAME, "");

    }

}
