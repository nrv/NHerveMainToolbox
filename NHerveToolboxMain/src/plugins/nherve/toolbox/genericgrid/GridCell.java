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

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;

public abstract class GridCell extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -3822419525699981741L;

	private BufferedImage thumbnail;
	private boolean needCacheRedraw;
	private BufferedImage thumbnailCache;
	private String name;
	private Color borderColor;
	private WaitingAnimation wa;
	private boolean zoomOnFocus;
	private boolean error;
	@SuppressWarnings("rawtypes")
	private ThumbnailProvider thumbnailProvider;

	private int bckZoomWidth;
	private int bckZoomHeight;
	private int bckZoomZO;
	private Point bckZoomLocation;
	private double zoomCenterX;
	private double zoomCenterY;
	
//	private Rectangle myBounds;
	
//	@SuppressWarnings("rawtypes")
//	private GridPanel father;

	public GridCell() {
		this(null);
	}

	public GridCell(String name) {
		super();
		setName(name);
		setError(false);
		wa = new WaitingAnimation(this);

		setBorderColor(null);
		addMouseListener(this);
		addMouseMotionListener(this);
		
//		father = null;
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
		return error;
	}

	public boolean isOnScreen() {
// WARNING : DEADLOCK ?		
		Rectangle r = getVisibleRect();
		if (r == null) {
			return false;
		}
		if ((r.height == 0) || (r.width == 0)) {
			return false;
		}
		return true;
// USE THIS INSTEAD ?
//		return father.isOnScreen(this);
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
//		father = null;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

//	@Override
//	public void setBounds(Rectangle b) {
//		myBounds = b;
//		super.setBounds(b);
//	}

	public void setError(boolean error) {
		this.error = error;
	}

//	void setFather(GridPanel<? extends GridCell> father) {
//		this.father = father;
//	}

	public void setName(String name) {
		this.name = name;
	}

	public void setThumbnail(BufferedImage thumb) {
		
		//System.out.println((int)(System.currentTimeMillis() / 1000) + " - " + getName() + " setThumbnail()");
		
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

//	Rectangle getMyBounds() {
//		return myBounds;
//	}
}
