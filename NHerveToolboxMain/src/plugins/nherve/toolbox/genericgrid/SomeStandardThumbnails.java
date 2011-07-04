package plugins.nherve.toolbox.genericgrid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;

public class SomeStandardThumbnails {
	static final int NICE_WIDTH = 1024;
	private static BufferedImage error;

	static {
		createErrorImage();
	}

	public static void paintError(final Graphics2D g2, final GridCell c) {
		int w = c.getWidth();
		int h = c.getHeight();
		
		SomeImageTools.resizeAndDraw(error, g2, w, h);
	}

	private static void createErrorImage() {
		error = new BufferedImage(NICE_WIDTH, NICE_WIDTH, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2 = error.createGraphics();
		
		g2.setColor(Color.RED);
		
		int bordure = NICE_WIDTH / 5;
		int epaisseur = NICE_WIDTH / 10;
		int longueur = NICE_WIDTH - 2 * bordure;
		
		AffineTransform t = new AffineTransform();
		t.translate(NICE_WIDTH / 2, NICE_WIDTH / 2);
		t.rotate(Math.PI / 4);
		g2.setTransform(t);
		
		g2.fillRect(- longueur / 2, - epaisseur / 2, longueur, epaisseur);
		g2.fillRect(- epaisseur / 2, - longueur / 2, epaisseur, longueur);
	}
}
