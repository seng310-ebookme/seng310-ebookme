package cz.cacek.ebook.packager;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


/**
 * Transfer handler for book files. It adds (Drag&)Drop functionality to bookList
 * @author Josef Cacek [josef.cacek (at) gmail.com]
 * @author $Author: kwart $
 * @version $Revision: 1.3 $
 * @created $Date: 2010/01/08 13:58:51 $
 */
public class BookTransferHandler
	extends TransferHandler
{
	private static final long serialVersionUID = 1L;
	private NewEbGui gui;

	/**
	 * Creates TransferHandler for NewEbGui
	 * @param newEbGui
	 */
	public BookTransferHandler(NewEbGui newEbGui) {
		gui = newEbGui;
	}

	/**
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];

			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				return true;
			}
		}

		// Didn't find any that match, so:
		return false;
	}

	/**
	 * Do the actual import.
	 *
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();

		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];

			try {
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List l =
						(List) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator iter = l.iterator();

					while (iter.hasNext()) {
						File file = (File) iter.next();
						gui.addBook();
						gui.openBookFile(file);
						gui.saveBook();
						gui.clearBookSelection();
					}

					return true;
				}
			} catch (IOException ex) {
				gui.alert("IOError getting data: " + ex);
			} catch (UnsupportedFlavorException e) {
				gui.alert("Unsupported Flavor: " + e);
			}
		}

		// If you get here, I didn't like the flavor.
		Toolkit.getDefaultToolkit().beep();

		return false;
	}
}
