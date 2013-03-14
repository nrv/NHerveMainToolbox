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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.Timer;

import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;

public class WaitingAnimation {
	private static final int NB_ANIM_STEP = 12;
	private static final int ANIM_SLEEP = 75;
	private static BufferedImage[] anim;

	private int currentStep;
	private Timer timer;

	// private final GridCell cell;
	// private final JComponent comp;

	public static void initAnimationImages(Color col) {
		if (anim == null) {
			anim = new BufferedImage[NB_ANIM_STEP];

			for (int cs = 0; cs < NB_ANIM_STEP; cs++) {
				anim[cs] = waitingImage(cs, NB_ANIM_STEP, 1.25, SomeStandardThumbnails.NICE_WIDTH, col);
			}
		}
	}

	public static BufferedImage waitingImage(int startingStep, int nbStep, double fade, int w, Color col) {
		BufferedImage img = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);

		final double s = w / 10.;
		final double r = w / 4.;
		final double c = (w - s) / 2.;
		final double ac = 2 * Math.PI / nbStep;
		final double gs = fade / nbStep;

		Graphics2D g2 = img.createGraphics();

		int finalStep = startingStep + nbStep;
		float gray = 1;
		for (int step = finalStep; step > startingStep; step--) {
			double a = step * ac;
			double x = c - r * Math.sin(a);
			double y = c + r * Math.cos(a);
			if (Math.abs(x) < 0.0000001) {
				x = 0;
			}
			if (Math.abs(y) < 0.0000001) {
				y = 0;
			}

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, gray));
			g2.setColor(col);
			g2.fillOval((int) x, (int) y, (int) s, (int) s);
			gray -= gs;
			if (gray < 0) {
				gray = 0;
			}
		}

		return img;
	}

	public WaitingAnimation(final GridCell cell) {
		super();

		currentStep = 0;

		timer = new Timer(ANIM_SLEEP, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentStep++;
				if (currentStep == NB_ANIM_STEP) {
					currentStep = 0;
				}
				if (cell != null && cell.isOnScreen()) {
					cell.repaint();
				}
			}
		});
	}

	public WaitingAnimation(final JComponent comp) {
		super();

		currentStep = 0;

		timer = new Timer(ANIM_SLEEP, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentStep++;
				if (currentStep == NB_ANIM_STEP) {
					currentStep = 0;
				}
				if (comp != null) {
					comp.repaint();
				}
			}
		});
	}

	public void paintAnimation(final Graphics2D g2, final GridCell c) {
		if (c.isOnScreen()) {
			paintAnimation(g2, c.getWidth(), c.getHeight(), 0, 0);
		}
	}

	public void paintAnimation(final Graphics2D g2, final int w, final int h, final int x, final int y) {
		SomeImageTools.resizeAndDraw(anim[currentStep], g2, w, h, x, y);
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public boolean isRunning() {
		return timer.isRunning();
	}
}
