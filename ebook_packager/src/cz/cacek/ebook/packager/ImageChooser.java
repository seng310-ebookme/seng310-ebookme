package cz.cacek.ebook.packager;

import java.awt.Component;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class ImageChooser {
	public final static String jpeg = "jpeg";
	public final static String jpg = "jpg";
	public final static String gif = "gif";
	public final static String tiff = "tiff";
	public final static String tif = "tif";
	public final static String png = "png";
	JFileChooser fc;

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if ((i > 0) && (i < (s.length() - 1))) {
			ext = s.substring(i + 1).toLowerCase();
		}

		return ext;
	}

	public File openImageFile(Component aParent) {
		File tmpResult = null;

		// Set up the file chooser.
		if (fc == null) {
			fc = new JFileChooser(".");

			// Add a custom file filter and disable the default
			// (Accept All) file filter.
			fc.addChoosableFileFilter(new ImageFilter());
			fc.setAcceptAllFileFilterUsed(false);

			// Add the preview pane.
			fc.setAccessory(new ImagePreview(fc));
		}

		// Show it.
		int returnVal = fc.showOpenDialog(aParent);

		// Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			tmpResult = fc.getSelectedFile();
		}

		// Reset the file chooser for the next time it's shown.
		fc.setSelectedFile(null);

		return tmpResult;
	}

	public static boolean isFileImage(File f) {
		return ImageFilter.isImage(f);
	}
}


class ImageFilter
	extends FileFilter
{
	//Accept all directories and all gif, jpg, tiff, or png files.
	public boolean accept(File f) {
		return f.isDirectory() || isImage(f);
	}

	public static boolean isImage(File f) {
		if (f.isDirectory()) {
			return false;
		}

		String extension = ImageChooser.getExtension(f);

		if (extension != null) {
			if (extension.equals(ImageChooser.tiff)
					|| extension.equals(ImageChooser.tif)
					|| extension.equals(ImageChooser.gif)
					|| extension.equals(ImageChooser.jpeg)
					|| extension.equals(ImageChooser.jpg)
					|| extension.equals(ImageChooser.png)) {
				return true;
			}
		}

		return false;
	}

	//The description of this filter
	public String getDescription() {
		return "Images (jpg, png, tif, gif)";
	}
}
