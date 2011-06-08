package edu.ualberta.med.biobank.barcodegenerator.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JasperEditorPerspective implements IPerspectiveFactory {

    public static final String ID = "barcodeGenerator.perspective.jaspereditor";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
    }
    
}