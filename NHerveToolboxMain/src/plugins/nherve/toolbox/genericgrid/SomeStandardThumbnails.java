/*
 * Copyright 2010, 2011 Institut Pasteur.
 * 
 * This file is part of NHerve Main Toolbox, which is an ICY plugin.
 * 
 * NHerve Main Toolbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NHerve Main Toolbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NHerve Main Toolbox. If not, see <http://www.gnu.org/licenses/>.
 */

package plugins.nherve.toolbox.genericgrid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;

public class SomeStandardThumbnails {
	static final int NICE_WIDTH = 1024;
	private static BufferedImage error;

	static {
		createErrorImage();
	}

	public static void paintError(final Graphics2D g2, final JComponent c) {
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
