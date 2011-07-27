package edu.ualberta.med.biobank.barcodegenerator.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class TemplateEditorPerspective implements IPerspectiveFactory {

    public static final String ID = "barcodeGenerator.perspective.templateeditor"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
    }
}