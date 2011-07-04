package plugins.nherve.toolbox.genericgrid;

import icy.gui.util.GuiUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GridPanel<T extends GridCell> extends JPanel implements ComponentListener, ChangeListener, ItemListener, AdjustmentListener {
	private class InternalGrid extends JPanel implements Scrollable {
		private static final long serialVersionUID = -4811144385819002930L;

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return pageLength;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return realFullLength / 2;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (cellsJustSet) {
				updateLbNbCells();
				cellsJustSet = false;
			}
		}
	}

	private static final long serialVersionUID = -3551019605947008673L;

	public static final int DEFAULT_CELL_LENGTH = 150;
	public static final int DEFAULT_CELL_SPACING = 10;
	public static final int DEFAULT_MIN_ZOOM = 25;
	public static final int DEFAULT_MAX_ZOOM = 200;
	public static final int DEFAULT_ZOOM_SMOOTH = 100;
	public static final double DEFAULT_MAX_ZOOM_FACTOR = DEFAULT_MAX_ZOOM / DEFAULT_ZOOM_SMOOTH;

	private GridCellCollection<T> cells;
	private double zoomFactor;

	private InternalGrid grid;
	private JScrollPane scroll;
	private JSlider slZoom;
	private JCheckBox cbZoomOnFocus;
	private JLabel lbNbCells;

	private int cellLength;
	private int smoothZoom;
	private int cellSpacing;
	private boolean zoomOnFocus;

	private int realFullLength;
	private int pageLength;
	
//	private Rectangle viewportRectangle;

	private boolean cellsJustSet;

	public GridPanel() {
		this(DEFAULT_CELL_LENGTH, DEFAULT_CELL_SPACING, true, true, DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM, DEFAULT_ZOOM_SMOOTH);
	}

	public GridPanel(int cellLength, int cellSpacing, boolean zoomOnFocus, boolean showBottomLine) {
		this(cellLength, cellSpacing, zoomOnFocus, showBottomLine, DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM, DEFAULT_ZOOM_SMOOTH);
	}

	public GridPanel(int cellLength, int cellSpacing, boolean zoomOnFocus, boolean showBottomLine, int minZoom, int maxZoom, int smoothZoom) {
		super();

		zoomFactor = 1.0;
		slZoom = null;
		lbNbCells = null;
		cellsJustSet = false;

		this.cellLength = cellLength;
		this.smoothZoom = smoothZoom;
		this.cellSpacing = cellSpacing;

		realFullLength = cellLength + cellSpacing;

		this.zoomOnFocus = zoomOnFocus;

		addComponentListener(this);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEtchedBorder());

		grid = new InternalGrid();
		grid.setLayout(null);

		scroll = new JScrollPane(grid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);
		scroll.getVerticalScrollBar().addAdjustmentListener(this);

		if (showBottomLine) {
			cbZoomOnFocus = new JCheckBox("Zoom");
			cbZoomOnFocus.setSelected(zoomOnFocus);
			cbZoomOnFocus.addItemListener(this);

			slZoom = new JSlider(JSlider.HORIZONTAL, minZoom, maxZoom, smoothZoom);
			slZoom.addChangeListener(this);
			lbNbCells = new JLabel("empty");

			JPanel bottom = GuiUtil.createLineBoxPanel(cbZoomOnFocus, Box.createHorizontalGlue(), slZoom, Box.createHorizontalGlue(), lbNbCells);
			add(bottom, BorderLayout.SOUTH);
		}
	}

	public GridCellCollection<T> getCells() {
		return cells;
	}

//	private void updateViewport() {
//		viewportRectangle = scroll.getViewport().getViewRect();
//	}
	
	private void updateLbNbCells() {
		if (lbNbCells != null) {
			if (cells == null) {
				lbNbCells.setText("empty");
			} else {
				int countVisible = 0;
				for (GridCell cell : cells) {
					if (cell.isOnScreen()) {
						countVisible++;
					}
				}
				lbNbCells.setText(countVisible + " / " + cells.size() + " files");
			}
		}
	}

	private synchronized void organizeCells() {
		if (cells != null) {
			int realCellLength = (int) (cellLength * zoomFactor);
			int nbCol = (int) Math.floor((double) (getWidth() - cellSpacing) / (double) (realCellLength + cellSpacing));
			realFullLength = realCellLength + cellSpacing;

			int row = 0;
			int col = 0;

			for (GridCell cell : cells) {
				if (col == nbCol) {
					row++;
					col = 0;
				}
				Rectangle bounds = new Rectangle(cellSpacing + col * realFullLength, cellSpacing + row * realFullLength, realCellLength, realCellLength);
				cell.setBounds(bounds);
				col++;
			}

			if (zoomOnFocus) {
				row += 2;
			} else {
				row += 1;
			}

			pageLength = ((int) (getHeight() / realFullLength)) * realFullLength;

			grid.setPreferredSize(new Dimension(getWidth(), cellSpacing + row * realFullLength));

			updateLbNbCells();
		} else {
			grid.setPreferredSize(null);
		}
	}

	public void setCells(GridCellCollection<T> cells) {
		if (this.cells != null) {
			this.cells.clear();
		}

		this.cells = cells;

		cellsJustSet = true;

		grid.removeAll();

		if (cells != null) {
			for (GridCell cell : cells) {
				grid.add(cell);
//				cell.setFather(this);
			}

			cells.setZoomOnFocus(zoomOnFocus);

			organizeCells();
		} else {
			grid.setPreferredSize(null);
		}

		grid.revalidate();
//		updateViewport();
		grid.repaint();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JCheckBox) {
			JCheckBox c = (JCheckBox) e.getSource();

			if (c == cbZoomOnFocus) {
				zoomOnFocus = cbZoomOnFocus.isSelected();
				if (cells != null) {
					cells.setZoomOnFocus(zoomOnFocus);
				}
				organizeCells();
				grid.revalidate();
//				updateViewport();
				grid.repaint();
			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		organizeCells();
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JSlider) {
			JSlider s = (JSlider) e.getSource();

			if (s == slZoom) {
				zoomFactor = slZoom.getValue() / (double) smoothZoom;
				organizeCells();
				if (cells != null) {
					cells.notifyDisplayParametersChanged();
				}
				grid.revalidate();
//				updateViewport();
				grid.repaint();
			}
		}

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
//		updateViewport();
		updateLbNbCells();
	}
	
//	public boolean isOnScreen(T cell) {
//		return viewportRectangle.intersects(cell.getMyBounds());
//	}

}
