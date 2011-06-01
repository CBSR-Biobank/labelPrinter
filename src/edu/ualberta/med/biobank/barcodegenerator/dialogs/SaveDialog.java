package edu.ualberta.med.biobank.barcodegenerator.dialogs;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * This class provides a facade for the "save" FileDialog class. If the selected
 * file already exists, the user is asked to confirm before overwriting.
 */
public class SaveDialog {

	private FileDialog fileDialog;

	public SaveDialog(Shell shell) {
		fileDialog = new FileDialog(shell, SWT.SAVE);
	}

	public String open() {
		String fileName = null;

		while (true) {
			fileName = fileDialog.open();

			if (fileName == null)
				return null;

			if (!new File(fileName).exists()) {
				MessageBox mb = new MessageBox(fileDialog.getParent(),
						SWT.ICON_WARNING | SWT.YES | SWT.NO);

				mb.setMessage(fileName
						+ " already exists. Do you want to replace it?");

				if (mb.open() == SWT.YES)
					return fileName;

			} else {
				return fileName;
			}

		}

	}
}