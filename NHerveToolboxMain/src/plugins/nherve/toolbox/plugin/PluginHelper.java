package plugins.nherve.toolbox.plugin;

import icy.main.Icy;
import icy.preferences.XMLPreferences;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import plugins.nherve.toolbox.image.mask.MaskPersistence;

public class PluginHelper {
	public final static String H = "output_height";
	public final static String PATH = "currentpath";
	public final static String W = "output_width";

	// From org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities
	public static String clipString(FontMetrics metrics, int availableWidth, String fullText) {
		if (metrics.stringWidth(fullText) <= availableWidth)
			return fullText;

		String ellipses = "...";
		int ellipsesWidth = metrics.stringWidth(ellipses);
		if (ellipsesWidth > availableWidth)
			return "";

		String starter = "";
		String ender = "";

		int w = fullText.length();
		int w2 = (w / 2) + (w % 2);
		String prevTitle = "";
		for (int i = 0; i < w2; i++) {
			String newStarter = starter + fullText.charAt(i);
			String newEnder = ender;
			if ((w - i) > w2)
				newEnder = fullText.charAt(w - i - 1) + newEnder;
			String newTitle = newStarter + ellipses + newEnder;
			if (metrics.stringWidth(newTitle) <= availableWidth) {
				starter = newStarter;
				ender = newEnder;
				prevTitle = newTitle;
				continue;
			}
			return prevTitle;
		}
		return fullText;
	}

	public static File fileChooser(FileFilter ff, XMLPreferences preferences, String title, File defaultFile, File defaultDirectory) {
		return fileChooserInternal(JFileChooser.FILES_AND_DIRECTORIES, ff, preferences, title, defaultFile, defaultDirectory);
	}

	public static File fileChooser(final String description, XMLPreferences preferences, String title, File defaultFile, final String... fileExts) {
		return fileChooser(getFilter(description, fileExts), preferences, title, defaultFile, null);
	}

	public static File fileChooser(final String description, XMLPreferences preferences, String title, final String... fileExts) {
		return fileChooser(description, preferences, title, null, fileExts);
	}

	public static File fileChooser(XMLPreferences preferences, MaskPersistence repository, File defaultFile) {
		return fileChooser("Segmentation mask (*" + repository.getMaskFileExtension() + ")", preferences, "Choose segmentation file", defaultFile, repository.getMaskFileExtension());
	}

	private static File fileChooserInternal(int mode, FileFilter ff, XMLPreferences preferences, String title, File defaultFile, File defaultDirectory) {
		int width = preferences.getInt(W, 400);
		int height = preferences.getInt(H, 400);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setPreferredSize(new Dimension(width, height));
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(mode);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setDialogTitle(title);

		if (ff != null) {
			fileChooser.setFileFilter(ff);
		}

		if (defaultFile != null) {
			fileChooser.setSelectedFile(defaultFile);
		} else if (defaultDirectory != null) {
			File fp = new File(defaultDirectory.getAbsolutePath());
			while ((fp != null) && (fp.getAbsolutePath().length() > 0) && (!fp.exists())) {
				fp = fp.getParentFile();
			}
			if ((fp == null) || (!fp.exists())) {
				fp = new File(preferences.get(PATH, ""));
			}
			fileChooser.setCurrentDirectory(fp);
		} else {
			String path = preferences.get(PATH, "");
			fileChooser.setCurrentDirectory(new File(path));
		}

		int returnValue = fileChooser.showDialog(Icy.getMainInterface().getFrame(), "OK");

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			preferences.put(PATH, fileChooser.getSelectedFile().getAbsolutePath());
			preferences.putInt(W, fileChooser.getWidth());
			preferences.putInt(H, fileChooser.getHeight());

			File file = fileChooser.getSelectedFile();
			return file;
		} else {
			return null;
		}
	}

	public static void fileChooserTF(int mode, FileFilter ff, XMLPreferences preferences, String title, JTextField tf, File defaultFile) {
		File defaultDirectory = null;
		String dd = tf.getText();
		if ((dd != null) && (dd.length() > 0)) {
			defaultDirectory = new File(dd);
		}
		File f = fileChooserInternal(mode, ff, preferences, title, defaultFile, defaultDirectory);
		if (f != null) {
			tf.setText(f.getAbsolutePath());
		}
	}

	public static void fileChooserTF(int mode, final String fileExt, final String description, XMLPreferences preferences, String title, JTextField tf, File defaultFile) {
		fileChooserTF(mode, getFilter(description, fileExt), preferences, title, tf, defaultFile);
	}

	private static FileFilter getFilter(final String desc, final String... exts) {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				for (String ext : exts) {
					if (f.getName().toUpperCase().endsWith(ext.toUpperCase())) {
						return true;
					}
				}

				return false;
			}

			@Override
			public String getDescription() {
				return desc;
			}
		};
	}

}
