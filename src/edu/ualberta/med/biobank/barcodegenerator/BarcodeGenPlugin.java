package edu.ualberta.med.biobank.barcodegenerator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 */
public class BarcodeGenPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "barcodeGenerator"; //$NON-NLS-1$

    // The shared instance
    private static BarcodeGenPlugin plugin;

    /**
     * The constructor
     */
    public BarcodeGenPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static BarcodeGenPlugin getDefault() {
        return plugin;
    }

    /**
     * Display an error message asynchronously
     */
    public static void openAsyncError(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), title, message);
            }
        });
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
