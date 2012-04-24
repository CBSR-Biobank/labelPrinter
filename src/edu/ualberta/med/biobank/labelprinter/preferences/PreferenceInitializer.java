package edu.ualberta.med.biobank.labelprinter.preferences;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.labelprinter.BarcodeGenPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    private static final I18n i18n = I18nFactory
        .getI18n(PreferenceInitializer.class);

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BarcodeGenPlugin.getDefault()
            .getPreferenceStore();

        setDefaults(store);
    }

    @SuppressWarnings("nls")
    public static void setDefaults(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.PROJECT_TITLE, "BBPSP");
        store.setDefault(PreferenceConstants.LOGO_FILE_LOCATION, "");
        store.setDefault(PreferenceConstants.TEMPLATE_NAME, "");
        store.setDefault(PreferenceConstants.PRINTER_NAME, "");

        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.LABEL_TEXT_1,
            i18n.tr("Patient Name"));
        store.setDefault(PreferenceConstants.LABEL_TEXT_2, i18n.tr("PHN"));
        store.setDefault(PreferenceConstants.LABEL_TEXT_3,
            i18n.tr("Patient Type"));

        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.VALUE_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.BARCODE_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.BARCODE_2D_TEXT_TYPE_CHECKBOX,
            false);
        store.setDefault(PreferenceConstants.SPECIMEN_TYPE_CHECKBOX, true);
        store.setDefault(PreferenceConstants.SPECIMEN_TYPE_TEXT,
            i18n.trc("Specimen Type", "Sp. Type") + "__________");
        store.setDefault(PreferenceConstants.PDF_DIRECTORY_PATH, "");

        // sets the default font.
        GraphicsEnvironment e = GraphicsEnvironment
            .getLocalGraphicsEnvironment();

        Font[] fonts = null;
        if (e != null)
            fonts = e.getAllFonts();

        if ((fonts != null) && (fonts.length >= 1)) {
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
