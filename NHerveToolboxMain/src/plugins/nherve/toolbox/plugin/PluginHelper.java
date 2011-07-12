package plugins.nherve.toolbox.plugin;

import icy.preferences.XMLPreferences;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class PluginHelper {
	
	public static void fileChooser(int mode, final String fileExt, final String description, XMLPreferences preferences, String title, JTextField tf) {
		fileChooser(mode, new FileFilter() {
			
			@Override
			public String getDescription() {
				return description;
			}
			
			@Override
			public boolean accept(File f) {
				return f.getName().toUpperCase().endsWith(fileExt.toUpperCase());
			}
		}, preferences, title, tf);
	}

	public static void fileChooser(int mode, FileFilter ff, XMLPreferences preferences, String title, JTextField tf) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(mode);
		if (ff != null) {
			fileChooser.setFileFilter(ff);
		}
	
		File fp = new File(tf.getText());
		while ((fp != null) && (fp.getAbsolutePath().length() > 0) && (!fp.exists())) {
			fp = fp.getParentFile();
		}
		if ((fp == null) || (!fp.exists())) {
			fp = new File(preferences.get(PATH, ""));
		}
		fileChooser.setCurrentDirectory(fp);
	
		int x = preferences.getInt("output_x", 0);
		int y = preferences.getInt("output_y", 0);
		int width = preferences.getInt("output_width", 400);
		int height = preferences.getInt("output_height", 400);
	
		fileChooser.setLocation(x, y);
		fileChooser.setPreferredSize(new Dimension(width, height));
	
		fileChooser.setDialogTitle(title);
	
		int returnValue = fileChooser.showDialog(null, "OK");
	
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			preferences.put(PATH, fileChooser.getSelectedFile().getAbsolutePath());
			preferences.putInt("output_x", fileChooser.getX());
			preferences.putInt("output_y", fileChooser.getY());
			preferences.putInt("output_width", fileChooser.getWidth());
			preferences.putInt("output_height", fileChooser.getHeight());
	
			tf.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	public final static String PATH = "currentpath";

}
