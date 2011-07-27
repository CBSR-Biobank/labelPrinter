package edu.ualberta.med.biobank.barcodegenerator.trees;

import java.util.Map.Entry;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Rectangle;
import edu.ualberta.med.biobank.gui.common.BgcLogger;

/**
 * 
 * Creates a tree from a specified configuration file. Child nodes have editable
 * fields, any change to a field will be reflected in the configuration file.
 * The tree structure is created as mentioned below.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class ConfigurationTree {

    private static final BgcLogger logger = BgcLogger
        .getLogger(ConfigurationTree.class.getName());

    // tree cannot be subclassed
    private Tree tree;
    private TreeEditor editor;
    private Text textEdit;
    private Configuration configuration;
    private ListenerList modifyListeners = new ListenerList();

    public ConfigurationTree(Composite parent, int style) {

        tree = new Tree(parent, style | SWT.BORDER | SWT.H_SCROLL
            | SWT.V_SCROLL | SWT.FULL_SELECTION);
        tree.setHeaderVisible(true);

        // remove this to make the standalone main function work.
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
        gd.horizontalSpan = 2;
        gd.heightHint = 350;
        tree.setLayoutData(gd);

        editor = new TreeEditor(tree);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        editor.minimumWidth = 50;

        // columns
        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText(Messages.ConfigurationTree_settings_tree_column);
        column1.setWidth(200);
        TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
        column2.setText(Messages.ConfigurationTree_horizontal_column);
        column2.setWidth(85);
        TreeColumn column3 = new TreeColumn(tree, SWT.CENTER);
        column3.setText(Messages.ConfigurationTree_vertical_column);
        column3.setWidth(70);
        TreeColumn column4 = new TreeColumn(tree, SWT.CENTER);
        column4.setText(Messages.ConfigurationTree_width_column);
        column4.setWidth(70);
        TreeColumn column5 = new TreeColumn(tree, SWT.CENTER);
        column5.setText(Messages.ConfigurationTree_height_column);
        column5.setWidth(70);
        TreeColumn column6 = new TreeColumn(tree, SWT.CENTER);
        column6.setWidth(1);
        column6.setText(""); //$NON-NLS-1$

        tree.addListener(SWT.MouseDown, treeListner);
    }

    /**
     * Used for modifying configuration data. Creates a tree structure from the
     * key name.
     * 
     * createTreeItem("animal.dog.nose.hair", new Rectangle(1,2,3,4)) will
     * create the root node "animal", then the child node "dog", then the
     * child-child node "nose", the last (leaf) node "hair" will have its column
     * values set to 1,2,3,4.
     * 
     * @param location eg. "animal.dog.nose.hair"
     * @param value eg. new Rectangle(1,2,3,4)
     * @throws TreeException
     */
    public void createTreeItem(String location, Rectangle value)
        throws TreeException {

        if (tree == null)
            throw new TreeException("Cannot create an item in a null tree."); //$NON-NLS-1$

        if (location == null)
            throw new TreeException("Cannot create an item in a null location."); //$NON-NLS-1$

        if (value == null)
            throw new TreeException(
                "Cannot create an item with a null rectangle value."); //$NON-NLS-1$

        int locationIndex = 0;
        String[] locationSegments = location.split("\\."); //$NON-NLS-1$
        if (locationSegments.length <= 0)
            throw new TreeException(
                "Location must contain at least one segment."); //$NON-NLS-1$

        // to traverse through the tree
        Object currentItem = tree;

        MAIN_LOOP: while (true) {

            TreeItem[] currentItemChildren = null;

            if (currentItem instanceof Tree) {
                currentItemChildren = ((Tree) currentItem).getItems();

            } else if (currentItem instanceof TreeItem) {
                currentItemChildren = ((TreeItem) currentItem).getItems();
            }

            if ((currentItemChildren != null)
                && (currentItemChildren.length != 0)) {
                for (TreeItem childItem : currentItemChildren) {
                    if (childItem.getText(0).equals(
                        locationSegments[locationIndex])) {
                        currentItem = childItem;
                        locationIndex++;
                        continue MAIN_LOOP;
                    }
                }
            }
            // if foundItem: found a child item that matches the next
            // locationSegment name.

            // if our location does not exist, add nodes until the entire
            // set of locationSegments are complete.

            // the leaf node is special: it requires a tab in the first
            // index of its string values -- and the it sets its other fields
            // depending on the value parameter.
            if (currentItem != null) {

                for (int i = locationIndex; i < locationSegments.length; i++) {
                    TreeItem newItem;
                    if (currentItem instanceof Tree) {
                        newItem = new TreeItem((Tree) currentItem, SWT.NONE);
                    } else {
                        newItem = new TreeItem((TreeItem) currentItem, SWT.NONE);
                    }
                    newItem.setText(locationSegments[i]);
                    currentItem = newItem;
                }

                String[] values = rectangleToString(value);
                values[0] = locationSegments[locationSegments.length - 1];
                ((TreeItem) currentItem).setText(values);

                break;
            } else {
                throw new TreeException(
                    "TreeItem searching failed: currentItem is null."); //$NON-NLS-1$
            }

        }

    }

    /**
     * Removes all the TreeItem children of the tree. It then adds all of the
     * configuration data to tree. NOTE: the configuration data keys must
     * contain periods to segregate different types of configuration settings.
     * 
     * @param config Any changes made to the tree will be reflected in the
     *            specified configuration file.
     * @throws TreeException
     */
    public void populateTree(Configuration config) throws TreeException {

        resetEditor();

        if (tree == null)
            throw new TreeException("Cannot populate tree: Tree is null."); //$NON-NLS-1$

        tree.removeAll();

        if (config == null)
            return;

        if (config.getSettings() == null)
            throw new TreeException(
                Messages.ConfigurationTree_valid_config_settings_error);

        /*
         * List<String> mapKeys = new ArrayList<String>(config.getSettings()
         * .keySet()); Collections.sort(mapKeys);
         * 
         * for (String key : mapKeys) createTreeItem(key,
         * config.getSetting(key));
         */

        for (Entry<String, Rectangle> e : config.getSettings().entrySet()) {
            createTreeItem(e.getKey(), e.getValue());
        }
        configuration = config;

        tree.redraw();
    }

    /**
     * Returns the configuration. Main purpose is to obtain and save the current
     * configuration. unDirty() should be called shortly after saving this tree
     * configuration.
     * 
     * @return
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setEnabled(boolean enable) {
        tree.setEnabled(enable);
    }

    public void resetEditor() throws TreeException {
        if (textEdit != null)
            textEdit.dispose();
        textEdit = null;

        if (editor == null)
            throw new TreeException("Editor is null."); //$NON-NLS-1$

        editor.setEditor(null, null, 0);
    }

    /**
     * Tree config has a very basic modify listener. When the user changes any
     * column field the ModifyEvent is passed to all the attached listeners.
     * Absolutely no information is passed to the modify event. The purpose of
     * this modify listener is to help set dirty flags in an application
     * implementing this tree.
     * 
     * @param listener
     */
    public void addModifyListener(ModifyListener listener) {
        modifyListeners.add(listener);
    }

    private void notifyModifyListeners() {
        Object[] listeners = modifyListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final ModifyListener l = (ModifyListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    Event e = new Event();
                    e.widget = tree;

                    l.modifyText(new ModifyEvent(e));
                }
            });
        }
    }

    private Listener treeListner = new Listener() {
        @Override
        public void handleEvent(Event event) {

            Control oldEditor = editor.getEditor();
            if (oldEditor != null)
                oldEditor.dispose();

            Point pt = new Point(event.x, event.y);
            final TreeItem currentTableItem = tree.getItem(pt);

            // stop if not a leaf item.
            if ((currentTableItem == null)
                || (currentTableItem.getItemCount() != 0))
                return;

            int editableColumn = -1;

            for (int colIndex = 1; colIndex < 5; colIndex++) {
                final int fColIndex = colIndex;

                org.eclipse.swt.graphics.Rectangle rect = currentTableItem
                    .getBounds(colIndex);
                if (rect.contains(pt)) {
                    createNewTextEditor(currentTableItem, fColIndex);
                    editableColumn = fColIndex;
                    break;
                }

            }
            if ((textEdit != null) && (editableColumn >= 0)) {
                textEdit.selectAll();
                textEdit.setFocus();
                editor.setEditor(textEdit, currentTableItem, editableColumn);
            }
        }
    };

    private void createNewTextEditor(final TreeItem currentTableItem,
        final int fColIndex) {
        textEdit = new Text(tree, SWT.CENTER);
        textEdit.setText(currentTableItem.getText(fColIndex));

        textEdit.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    try {
                        resetEditor();
                    } catch (TreeException e1) {
                        // do nothing
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // do nothing
            }
        });

        textEdit.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent me) {
                Text text = (Text) editor.getEditor();

                boolean validNumber = true;
                try {
                    int parsedInt = Integer.parseInt(text.getText());
                    if ((parsedInt <= -1000) || (parsedInt >= 1000))
                        validNumber = false;

                } catch (NumberFormatException e) {
                    validNumber = false;
                }

                if (validNumber) {
                    editor.getItem().setText(fColIndex, text.getText());

                    TreeItem c = currentTableItem;
                    if ((c != null) && (c.getText() != null)) {
                        String location = c.getText();
                        while (c != null) {
                            c = c.getParentItem();
                            if (c != null)
                                location = c.getText() + "." + location; //$NON-NLS-1$
                        }
                        if (configuration.getSettings().containsKey(location)) {

                            configuration.setSetting(location,
                                TreeItemToRectangle(currentTableItem));
                            notifyModifyListeners();
                        } else {
                            logger
                                .error("Could not find key in configuration: " //$NON-NLS-1$
                                    + location);
                        }
                    }

                }

            }
        });
        textEdit.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                if (!e.text.matches("[{0-9-}]*")) { //$NON-NLS-1$
                    e.doit = false;
                    return;
                }

            }

        });
    }

    private String[] rectangleToString(Rectangle r) {
        if (r == null)
            return null;

        String[] buf = new String[5];

        buf[0] = "null"; //$NON-NLS-1$
        buf[1] = String.valueOf(r.getX());
        buf[2] = String.valueOf(r.getY());
        buf[3] = String.valueOf(r.getWidth());
        buf[4] = String.valueOf(r.getHeight());

        return buf;
    }

    private Rectangle StringToRectangle(String[] str) {
        if (str == null)
            return null;

        if (str.length == 5)
            return new Rectangle(Integer.valueOf(str[1]),
                Integer.valueOf(str[2]), Integer.valueOf(str[3]),
                Integer.valueOf(str[4]));

        if (str.length == 4)
            return new Rectangle(Integer.valueOf(str[0]),
                Integer.valueOf(str[1]), Integer.valueOf(str[2]),
                Integer.valueOf(str[3]));

        return null;
    }

    private Rectangle TreeItemToRectangle(TreeItem item) {
        return StringToRectangle(new String[] { item.getText(1),
            item.getText(2), item.getText(3), item.getText(4) });
    }

}
