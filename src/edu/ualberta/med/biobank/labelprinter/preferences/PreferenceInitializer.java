package edu.ualberta.med.biobank.labelprinter.preferences;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.labelprinter.BarcodeGenPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BarcodeGenPlugin.getDefault()
            .getPreferenceStore();

        setDefaults(store);
    }

    public static void setDefaults(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.PROJECT_TITLE, "BBPSP"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.LOGO_FILE_LOCATION, ""); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.TEMPLATE_NAME, ""); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.PRINTER_NAME, ""); //$NON-NLS-1$

        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_1, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_2, true);
        store.setDefault(PreferenceConstants.LABEL_CHECKBOX_3, true);

        store.setDefault(PreferenceConstants.LABEL_TEXT_1, Messages.PreferenceInitializer_patient_name);
        store.setDefault(PreferenceConstants.LABEL_TEXT_2, "PHN"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.LABEL_TEXT_3, Messages.PreferenceInitializer_patient_type);

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
            "Sp. Type__________"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.PDF_DIRECTORY_PATH, ""); //$NON-NLS-1$

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

                if (cFontLower.contains("times new")) { //$NON-NLS-1$
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("courier")) { //$NON-NLS-1$
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("arial")) { //$NON-NLS-1$
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("serif")) { //$NON-NLS-1$
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("sans")) { //$NON-NLS-1$
                    defaultFontName = cFontName;
                    break;
                } else if (cFontLower.contains("mono")) { //$NON-NLS-1$
                    defaultFontName = cFontName;
                    break;
                }
            }
            store.setDefault(PreferenceConstants.TEXT_FONT_NAME,
                defaultFontName);
        } else
            store.setDefault(PreferenceConstants.TEXT_FONT_NAME, ""); //$NON-NLS-1$

    }

}
