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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;

public abstract class GridCell extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -3822419525699981741L;

	protected BufferedImage thumbnail;
	private boolean needCacheRedraw;
	private BufferedImage thumbnailCache;
	private String name;
	private Color borderColor;
	private WaitingAnimation wa;
	private boolean zoomOnFocus;
	private String errorMessage;
	@SuppressWarnings("rawtypes")
	private ThumbnailProvider thumbnailProvider;

	private int bckZoomWidth;
	private int bckZoomHeight;
	private int bckZoomZO;
	private Point bckZoomLocation;
	private double zoomCenterX;
	private double zoomCenterY;

	public GridCell() {
		this(null);
	}

	public GridCell(String name) {
		super();
		setName(name);
		setError(null);
		wa = new WaitingAnimation(this);

		setBorderColor(null);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	void createThumbnailCache() {
		if (thumbnail == null) {
			this.thumbnailCache = null;
			return;
		}

		thumbnailCache = SomeImageTools.resize(thumbnail, getWidth(), getHeight());
		needCacheRedraw = false;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public String getName() {
		return name;
	}

	public boolean isError() {
		return errorMessage != null;
	}

	public boolean isOnScreen() {
		Rectangle r = getVisibleRect();
		if (r == null) {
			return false;
		}
		if ((r.height == 0) || (r.width == 0)) {
			return false;
		}
		return true;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (zoomOnFocus) {
			bckZoomWidth = getWidth();
			bckZoomHeight = getHeight();
			bckZoomLocation = getLocation();
			bckZoomZO = getParent().getComponentZOrder(this);
			getParent().setComponentZOrder(this, 0);
			Rectangle r = getBounds();
			zoomCenterX = r.getCenterX();
			zoomCenterY = r.getCenterY();
		} else {
			setBorderColor(Color.GREEN);
			repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (zoomOnFocus) {
			setLocation(bckZoomLocation);
			setSize(bckZoomWidth, bckZoomHeight);
			getParent().setComponentZOrder(this, bckZoomZO);
			createThumbnailCache();
		} else {
			setBorderColor(null);
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (zoomOnFocus) {
			e = SwingUtilities.convertMouseEvent(this, e, getParent());
			int mX = e.getX();
			int mY = e.getY();

			double dX = zoomCenterX - mX;
			double dY = zoomCenterY - mY;

			double distToCenter = Math.sqrt(dX * dX + dY * dY);
			distToCenter = Math.min(1d, distToCenter / (getWidth() / 2));

			double zoomFactor = 1d + Math.exp(-distToCenter * distToCenter * distToCenter * distToCenter * 5d);

			int nW = (int) (bckZoomWidth * zoomFactor);
			int nH = (int) (bckZoomHeight * zoomFactor);

			int nX = bckZoomLocation.x - (nW - bckZoomWidth) / 2;
			int nY = bckZoomLocation.y - (nH - bckZoomHeight) / 2;

			setSize(nW, nH);
			setLocation(nX, nY);

			createThumbnailCache();
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	void notifyDisplayParametersChanged() {
		needCacheRedraw = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int w = getWidth();
		int h = getHeight();

		if (isError()) {
			SomeStandardThumbnails.paintError(g2, this);
		} else if (thumbnail == null) {
			if (!wa.isRunning()) {
				wa.start();
			}
			thumbnailProvider.provideThumbnailFor(this);
		} else if (needCacheRedraw) {
			thumbnailProvider.createCacheFor(this);
		}

		if (wa.isRunning()) {
			wa.paintAnimation(g2, this);
		}

		if (thumbnailCache != null) {
			g2.drawImage(thumbnailCache, null, this);
		}

		if (getBorderColor() != null) {
			g2.setColor(getBorderColor());
			g2.drawRect(0, 0, w - 1, h - 1);
		}
	}

	public void removedFromGrid() {
		wa.stop();
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public void setErrorMessage(String error) {
		this.errorMessage = error;

		if (error != null) {
			if (zoomOnFocus) {
				setToolTipText(null);
			} else {
				setToolTipText(error);
			}
		}
	}

	public void setError(Throwable tw) {
		if (tw != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			tw.printStackTrace(ps);
			setErrorMessage(baos.toString());
			ps.close();
			
			wa.stop();
			createThumbnailCache();
			repaint();
		} else {
			setErrorMessage(null);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setThumbnail(BufferedImage thumb) {
		wa.stop();

		if (thumb != null) {
			this.thumbnail = thumb;
		} else {
			this.thumbnail = null;
		}

		createThumbnailCache();
		repaint();
	}

	void setThumbnailProvider(@SuppressWarnings("rawtypes") ThumbnailProvider thumbnailProvider) {
		this.thumbnailProvider = thumbnailProvider;
	}

	void setZoomOnFocus(boolean zoomOnFocus) {
		this.zoomOnFocus = zoomOnFocus;

		if (zoomOnFocus) {
			setToolTipText(null);
		} else {
			setToolTipText(getName());
		}
	}
}
