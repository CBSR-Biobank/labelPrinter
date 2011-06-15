package edu.ualberta.med.biobank.barcodegenerator.trees;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
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

/**
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class ConfigurationTree {

    // tree cannot be subclassed
    private Tree tree;
    private TreeEditor editor;
    private Text textEdit;

    private boolean isDirty;

    private Configuration configuration;

    public ConfigurationTree(Composite parent, int style) {

        tree = new Tree(parent, style | SWT.BORDER | SWT.H_SCROLL
            | SWT.V_SCROLL);
        tree.setHeaderVisible(true);

        // remove this to make the standalone main function work.
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        gd.heightHint = 150;
        tree.setLayoutData(gd);

        editor = new TreeEditor(tree);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        editor.minimumWidth = 50;

        // columns
        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText("Settings Editor Tree");
        column1.setWidth(200);
        TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
        column2.setText("Horizontal (mm)");
        column2.setWidth(85);
        TreeColumn column3 = new TreeColumn(tree, SWT.CENTER);
        column3.setText("Vertical (mm)");
        column3.setWidth(70);
        TreeColumn column4 = new TreeColumn(tree, SWT.CENTER);
        column4.setText("Width (mm)");
        column4.setWidth(70);
        TreeColumn column5 = new TreeColumn(tree, SWT.CENTER);
        column5.setText("Height (mm)");
        column5.setWidth(70);
        TreeColumn column6 = new TreeColumn(tree, SWT.CENTER);
        column6.setWidth(1);
        column6.setText("");

        tree.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {

                Control oldEditor = editor.getEditor();
                if (oldEditor != null)
                    oldEditor.dispose();

                Point pt = new Point(event.x, event.y);
                final TreeItem currentTableItem = tree.getItem(pt);

                // stop if not a leaf item.
                if (currentTableItem == null
                    || currentTableItem.getItemCount() != 0)
                    return;

                int editableColumn = -1;

                for (int colIndex = 1; colIndex < 5; colIndex++) {
                    final int fColIndex = colIndex;

                    org.eclipse.swt.graphics.Rectangle rect = currentTableItem
                        .getBounds(colIndex);
                    if (rect.contains(pt)) {
                        textEdit = new Text(tree, SWT.CENTER);
                        textEdit.setText(currentTableItem.getText(fColIndex));
                        textEdit.addModifyListener(new ModifyListener() {
                            public void modifyText(ModifyEvent me) {
                                Text text = (Text) editor.getEditor();

                                boolean validNumber = true;
                                try {
                                    int parsedInt = Integer.parseInt(text
                                        .getText());
                                    if (parsedInt <= -1000 || parsedInt >= 1000)
                                        validNumber = false;

                                } catch (NumberFormatException e) {
                                    validNumber = false;
                                }

                                if (validNumber) {
                                    editor.getItem().setText(fColIndex,
                                        text.getText());

                                    TreeItem c = currentTableItem;
                                    if (c != null && c.getText() != null) {
                                        String location = c.getText()
                                            .replaceAll("\t", "");
                                        while (c != null) {
                                            c = c.getParentItem();
                                            if (c != null)
                                                location = c.getText()
                                                    .replaceAll("\t", "")
                                                    + "."
                                                    + location;
                                        }
                                        if (configuration.getSettings()
                                            .containsKey(location)) {

                                            configuration
                                                .setSetting(
                                                    location,
                                                    TreeItemToRectangle(currentTableItem));
                                            isDirty = true;
                                        } else {
                                            System.err
                                                .println("Could not find key in configuration :"
                                                    + location);
                                        }
                                    }

                                }

                            }
                        });
                        textEdit.addVerifyListener(new VerifyListener() {

                            @Override
                            public void verifyText(VerifyEvent e) {
                                if (!e.text.matches("[{0-9-}]*")) {
                                    e.doit = false;
                                    return;
                                }

                            }

                        });

                        editableColumn = fColIndex;
                        break;
                    }

                }
                if (textEdit != null && editableColumn >= 0) {
                    textEdit.selectAll();
                    textEdit.setFocus();
                    editor
                        .setEditor(textEdit, currentTableItem, editableColumn);
                }
            }
        });
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
            throw new TreeException("Cannot create an item in a null tree.");

        if (location == null)
            throw new TreeException("Cannot create an item in a null location.");

        if (value == null)
            throw new TreeException(
                "Cannot create an item with a null rectangle value.");

        int locationIndex = 0;
        String[] locationSegments = location.split("\\.");
        if (locationSegments.length <= 0)
            throw new TreeException(
                "Location must contain at least one segment.");

        // to traverse through the tree
        Object currentItem = tree;

        MAIN_LOOP: while (true) {

            TreeItem[] currentItemChildren = null;

            if (currentItem instanceof Tree) {
                currentItemChildren = ((Tree) currentItem).getItems();

            } else if (currentItem instanceof TreeItem) {
                currentItemChildren = ((TreeItem) currentItem).getItems();
            }

            if (currentItemChildren != null && currentItemChildren.length != 0) {
                for (TreeItem childItem : currentItemChildren) {
                    if (childItem.getText(0).replaceAll("\t", "")
                        .equals(locationSegments[locationIndex])) {
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
                values[0] = "\t"
                    + locationSegments[locationSegments.length - 1];
                ((TreeItem) currentItem).setText(values);

                break;
            } else {
                throw new TreeException(
                    "TreeItem searching failed: currentItem is null.");
            }

        }

    }

    public void setEnabled(boolean enable) {
        tree.setEnabled(enable);

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void resetEditor() throws TreeException {
        if (textEdit != null)
            textEdit.dispose();
        textEdit = null;

        if (editor == null)
            throw new TreeException("Cannot populate tree: Editor is null.");

        editor.setEditor(null, null, 0);

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
        isDirty = false;

        if (tree == null)
            throw new TreeException("Cannot populate tree: Tree is null.");

        tree.removeAll();

        if (config == null)
            return;

        if (config.getSettings() == null)
            throw new TreeException(
                "A valid configuration setting is required.");

        ArrayList<String> mapKeys = new ArrayList<String>(config.getSettings()
            .keySet());
        Collections.sort(mapKeys);

        for (String key : mapKeys)
            createTreeItem(key, config.getSetting(key));

        configuration = config;

        tree.redraw();
    }

    public void unDirty() {
        isDirty = false;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public Rectangle getTreeValue(String location) {
        TreeItem ti = getTreeItem(location);
        return StringToRectangle(new String[] { ti.getText(1), ti.getText(2),
            ti.getText(3), ti.getText(4) });
    }

    public boolean setTreeValue(String location, Rectangle r) {

        TreeItem currentItem = getTreeItem(location);

        if (currentItem == null)
            return false;

        String[] values = rectangleToString(r);
        values[0] = ((TreeItem) currentItem).getText(0);

        ((TreeItem) currentItem).setText(values);
        return true;
    }

    private String[] rectangleToString(Rectangle r) {
        if (r == null)
            return null;

        String[] buf = new String[5];

        buf[0] = "null";
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

    /**
     * Changes a particular row in the tree. The location specified must be in
     * the form: ROOT.CHILD.SUBCHILD.SUBSUB[CHILD].LEAF eg.
     * "Patient Info.Top Field.1D Barcode"
     * 
     **/
    private TreeItem getTreeItem(String location) {
        if (tree == null || location == null)
            return null;

        String[] locationSegments = location.split("\\.");

        if (locationSegments.length == 0)
            return null;

        int locationIndex = 0;
        Object currentItem = tree;

        while (true) {

            TreeItem[] currentItemChildren = null;

            if (currentItem instanceof Tree) {
                currentItemChildren = ((Tree) currentItem).getItems();

            } else if (currentItem instanceof TreeItem) {
                currentItemChildren = ((TreeItem) currentItem).getItems();
            }

            if (currentItemChildren == null || currentItemChildren.length == 0)
                break;

            boolean foundItem = false;
            for (TreeItem childItem : currentItemChildren) {
                if (childItem.getText(0).replaceAll("\t", "")
                    .equals(locationSegments[locationIndex])) {
                    currentItem = childItem;
                    locationIndex++;
                    foundItem = true;
                    break;
                }
            }

            if (!foundItem) {
                currentItem = null;
                break;
            }

        }
        if (currentItem instanceof TreeItem) {
            return (TreeItem) currentItem;
        } else {
            return null;
        }
    }

}
