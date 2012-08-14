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

import icy.gui.util.GuiUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
			return realFullHeight / 2;
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

	public static final int DEFAULT_CELL_LENGTH = 150;
	public static final int DEFAULT_CELL_SPACING = 10;
	public static final int DEFAULT_ZOOM_SMOOTH = 100;
	public static final int DEFAULT_MAX_ZOOM = 200;
	public static final double DEFAULT_MAX_ZOOM_FACTOR = DEFAULT_MAX_ZOOM / DEFAULT_ZOOM_SMOOTH;
	public static final int DEFAULT_MIN_ZOOM = 25;
	private static final String EMPTY_LABEL = "nothing to display";
	private static final long serialVersionUID = -3551019605947008673L;

	private JCheckBox cbZoomOnFocus;
	private JCheckBox cbDisplayName;
	private int cellWidth;
	private int cellHeight;
	
	private Font font;

	private GridCellCollection<T> cells;
	private boolean cellsJustSet;
	private int cellSpacing;
	private InternalGrid grid;
	private JLabel lbNbCells;

	private int pageLength;
	private int realFullWidth;
	private int realFullHeight;
	private JScrollPane scroll;
	private JSlider slZoom;

	private int smoothZoom;
	private double zoomFactor;
	
	private boolean zoomOnFocus;
	private boolean displayName;

	public GridPanel() {
		this(true);
	}
	
	public GridPanel(boolean zoomOnFocus) {
		this(zoomOnFocus, true);
	}
	
	public GridPanel(boolean zoomOnFocus, boolean showBottomLine) {
		this(DEFAULT_CELL_LENGTH, DEFAULT_CELL_SPACING, zoomOnFocus, showBottomLine, DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM, DEFAULT_ZOOM_SMOOTH);
	}

	public GridPanel(int cellLength, int cellSpacing, boolean zoomOnFocus, boolean showBottomLine) {
		this(cellLength, cellSpacing, zoomOnFocus, showBottomLine, DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM, DEFAULT_ZOOM_SMOOTH);
	}

	public GridPanel(int cellLength, int cellSpacing, boolean zoomOnFocus, boolean showBottomLine, int minZoom, int maxZoom, int smoothZoom) {
		this(cellLength, cellLength, cellSpacing, zoomOnFocus, showBottomLine, showBottomLine, minZoom, maxZoom, smoothZoom);
	}
	
	public GridPanel(int cellWidth, int cellHeight, int cellSpacing, boolean zoomOnFocus, boolean displayName, boolean showBottomLine, int minZoom, int maxZoom, int smoothZoom) {
		super();

		zoomFactor = 1.0;
		slZoom = null;
		lbNbCells = null;
		cellsJustSet = false;

		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.smoothZoom = smoothZoom;
		this.cellSpacing = cellSpacing;

		realFullWidth = cellWidth + cellSpacing;
		realFullHeight = cellHeight + cellSpacing;

		this.zoomOnFocus = zoomOnFocus;
		this.displayName = displayName;

		addComponentListener(this);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEtchedBorder());
		
		font = new Font("SansSerif", Font.PLAIN, 12);

		grid = new InternalGrid();
		grid.setLayout(null);

		scroll = new JScrollPane(grid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);
		scroll.getVerticalScrollBar().addAdjustmentListener(this);

		if (showBottomLine) {
			cbZoomOnFocus = new JCheckBox("Zoom");
			cbZoomOnFocus.setSelected(zoomOnFocus);
			cbZoomOnFocus.addItemListener(this);
			
			cbDisplayName = new JCheckBox("Show names");
			cbDisplayName.setSelected(displayName);
			cbDisplayName.addItemListener(this);

			slZoom = new JSlider(JSlider.HORIZONTAL, minZoom, maxZoom, smoothZoom);
			slZoom.addChangeListener(this);
			lbNbCells = new JLabel(EMPTY_LABEL);

			JPanel bottom = GuiUtil.createLineBoxPanel(cbDisplayName, Box.createHorizontalGlue(), cbZoomOnFocus, slZoom, Box.createHorizontalGlue(), lbNbCells);
			add(bottom, BorderLayout.SOUTH);
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		updateLbNbCells();
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

	public GridCellCollection<T> getCells() {
		return cells;
	}

	public int getZoomValue() {
		return slZoom.getValue();
	}

	public boolean isZoomOnFocus() {
		return zoomOnFocus;
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
			}
			
			if (c == cbDisplayName) {
				displayName = cbDisplayName.isSelected();
				if (cells != null) {
					cells.setDisplayName(displayName);
					cells.notifyDisplayParametersChanged();
				}
			}
			
			organizeCells();
			grid.revalidate();
			grid.repaint();
		}
	}

	private synchronized void organizeCells() {
		if (cells != null) {
			int realCellWidth = (int) (cellWidth * zoomFactor);
			int nbCol = (int) Math.floor((double) (getWidth() - cellSpacing) / (double) (realCellWidth + cellSpacing));
			realFullWidth = realCellWidth + cellSpacing;
			
			int realCellHeight = (int) (cellHeight * zoomFactor);
			realFullHeight = realCellHeight + cellSpacing;

			int row = 0;
			int col = 0;
			
			int spaceForName = 0;
			
			if (displayName) {
				FontMetrics fm = getGraphics().getFontMetrics(font);
				spaceForName = fm.getHeight() + fm.getMaxDescent();
			}

			for (GridCell cell : cells) {
				if (col == nbCol) {
					row++;
					col = 0;
				}
				Rectangle bounds = new Rectangle(cellSpacing + col * realFullWidth, cellSpacing + row * realFullHeight, realCellWidth, realCellHeight);
				cell.setBounds(bounds);
				cell.setHeightForName(spaceForName);
				col++;
			}

			if (zoomOnFocus) {
				row += 2;
			} else {
				row += 1;
			}

			pageLength = ((int) (getHeight() / realFullHeight)) * realFullHeight;

			grid.setPreferredSize(new Dimension(getWidth(), cellSpacing + row * realFullHeight));

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
			}

			cells.setZoomOnFocus(zoomOnFocus);
			cells.setDisplayName(displayName);
			cells.setNameFont(font);

			organizeCells();
		} else {
			grid.setPreferredSize(null);
		}

		grid.revalidate();
		grid.repaint();
	}

	public void setZoomValue(int n) {
		slZoom.setValue(n);
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

	private void updateLbNbCells() {
		if (lbNbCells != null) {
			if (cells == null) {
				lbNbCells.setText(EMPTY_LABEL);
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
}
