package edu.ualberta.med.biobank.barcodegenerator.template.configuration;

public class ConfigurationTranslator {

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

        /* @formatter:on */

    public static String translate(String key, Integer configurationVersion) {

        // old config file, does not have a version number.
        if (configurationVersion == null) {
            key = genericTranslator(key, august15PairsNormal,
                august15PairsFormatted);
            configurationVersion = 1;
        }

        if (configurationVersion == 1) {
            int i = 0;
            i = i + 1;
            // do nothing
        }

        return key;
    }

    private static String genericTranslator(String key, String[][] pairsNormal,
        String[][] pairsFormatted) {

        // translate regualar keys
        for (int i = 0, n = pairsNormal.length; i < n; i++)
            if (key.equals(pairsNormal[i][0]))
                return pairsNormal[i][1];

        // translate "formatted" keys
        for (int i = 0, n = pairsFormatted.length; i < n; i++) {

            for (int barcodeCount = 1; barcodeCount <= 32; barcodeCount++) {
                if (key.equals(String
                    .format(pairsFormatted[i][0], barcodeCount)))
                    return String.format(pairsFormatted[i][1], barcodeCount);
            }
        }

        // key remains unchanged.
        return key;
    }
}
