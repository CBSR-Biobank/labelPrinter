package edu.ualberta.med.biobank.labelprinter.template.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigurationUpdater {

    /* @formatter:off */
        @SuppressWarnings("nls")
        private static String[][] august15PairsNormal = new String[][] {
            
            { "Patient Info.Custom Field 1.Field Text", "Sheet Info.Custom Field 1.Text" },
            { "Patient Info.Custom Field 1.1D Barcode", "Sheet Info.Custom Field 1.1D Barcode" },

            { "Patient Info.Custom Field 2.Field Text", "Sheet Info.Custom Field 2.Text" },
            { "Patient Info.Custom Field 2.1D Barcode", "Sheet Info.Custom Field 2.1D Barcode" },

            { "Patient Info.Custom Field 3.Field Text", "Sheet Info.Custom Field 3.Text"},
            { "Patient Info.Custom Field 3.1D Barcode", "Sheet Info.Custom Field 3.1D Barcode" },

            { "Patient Info.Patient ID.1D Barcode", "Sheet Info.Patient Number.1D Barcode" },
            
            { "Barcodes.General.Specimen Text", "Labels.General.Text" },
            { "Barcodes.General.Barcode 1D", "Labels.General.Barcode 1D" },
            { "Barcodes.General.Barcode 2D", "Labels.General.Barcode 2D"}
            
        };
        
        @SuppressWarnings("nls")
        private static String[][] august15PairsFormatted = new String[][] {
              
            { "Barcodes.Individual.Barcode %03d.Barcode 1D", "Labels.Individual.Barcode %03d.Barcode 1D" },
            { "Barcodes.Individual.Barcode %03d.Barcode 2D", "Labels.Individual.Barcode %03d.Barcode 2D" },
            { "Barcodes.Individual.Barcode %03d.Specimen Text", "Labels.Individual.Barcode %03d.Text" }
        
        };
        
        @SuppressWarnings("nls")
        private static String[][] update2Formatted = new String[][] {
            
          { "Labels.Individual.Barcode %03d.Barcode 1D", "Labels.Individual.Label %03d.Barcode 1D" },
          { "Labels.Individual.Barcode %03d.Barcode 2D", "Labels.Individual.Label %03d.Barcode 2D" },
          { "Labels.Individual.Barcode %03d.Text" , "Labels.Individual.Label %03d.Text" }
      
      };
        /* @formatter:on */

    public static Integer updateKeys(Map<String, Rectangle> settings,
        Integer version) {

        final int NEWEST_VERSION_NUMBER = 3;
        // CHANGE THIS AND UPDATE ConfigurationTranslator each time you change
        // key names.

        if ((version == null) || (version < NEWEST_VERSION_NUMBER)) {

            Map<String, Rectangle> settingsUpgraded = new LinkedHashMap<String, Rectangle>();

            for (String key : settings.keySet()) {
                settingsUpgraded.put(
                    ConfigurationUpdater.translate(key, version),
                    settings.get(key));
            }
            addNewKeys(settingsUpgraded, version);

            settings.clear();
            for (String key : settingsUpgraded.keySet())
                settings.put(key, settingsUpgraded.get(key));

            return NEWEST_VERSION_NUMBER;
        }

        return NEWEST_VERSION_NUMBER;
    }

    public static void addNewKeys(Map<String, Rectangle> settings,
        Integer configurationVersion) {

        if ((configurationVersion == null) || (configurationVersion <= 2)) {
            settings.put("Labels.General.Barcode 2D Text", new Rectangle(32, //$NON-NLS-1$
                15, 0, 0));

            for (int i = 1; i <= 32; i++) {
                settings.put(String.format(
                    "Labels.Individual.Label %03d.Barcode 2D Text", i), //$NON-NLS-1$
                    new Rectangle(0, 0, 0, 0));
            }
            configurationVersion = 3;
        }

    }

    // NOTICE: update NEWEST_VERSION_NUMBER in the Configuration object to
    // reflect the most latest version number that is being translated to.

    public static String translate(String key, Integer configurationVersion) {

        // old config file, does not have a version number.
        if (configurationVersion == null) {
            key = genericTranslator(key, august15PairsNormal,
                august15PairsFormatted);
            configurationVersion = 1;
        }

        if (configurationVersion == 1) {
            key = genericTranslator(key, null, update2Formatted);
            configurationVersion = 2;
        }

        if (configurationVersion == 2) {
            configurationVersion = 3;
        }

        return key;
    }

    private static String genericTranslator(String key, String[][] pairsNormal,
        String[][] pairsFormatted) {

        // translate regualar keys
        if (pairsNormal != null) {
            for (int i = 0, n = pairsNormal.length; i < n; i++)
                if (key.equals(pairsNormal[i][0]))
                    return pairsNormal[i][1];
        }

        // translate "formatted" keys
        if (pairsFormatted != null) {
            for (int i = 0, n = pairsFormatted.length; i < n; i++) {

                for (int barcodeCount = 1; barcodeCount <= 32; barcodeCount++) {
                    if (key.equals(String.format(pairsFormatted[i][0],
                        barcodeCount)))
                        return String
                            .format(pairsFormatted[i][1], barcodeCount);
                }
            }
        }

        // key remains unchanged.
        return key;
    }
}
