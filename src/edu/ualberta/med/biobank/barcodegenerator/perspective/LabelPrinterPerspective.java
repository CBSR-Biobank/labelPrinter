package edu.ualberta.med.biobank.barcodegenerator.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class LabelPrinterPerspective implements IPerspectiveFactory {

    public static final String ID = "barcodeGenerator.perspective.labeprinter";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
    }
}