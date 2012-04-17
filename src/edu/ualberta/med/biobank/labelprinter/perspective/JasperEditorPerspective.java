package edu.ualberta.med.biobank.labelprinter.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JasperEditorPerspective implements IPerspectiveFactory {

    public static final String ID = "labelPrinter.perspective.jaspereditor"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
    }

}
