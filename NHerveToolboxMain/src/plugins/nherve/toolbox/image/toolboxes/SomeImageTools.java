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
package plugins.nherve.toolbox.image.toolboxes;

import icy.file.Saver;
import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.type.TypeUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.formats.FormatException;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.My2DConnectedComponent;
import plugins.nherve.toolbox.image.My2DConnectedComponentFinder;
import plugins.nherve.toolbox.image.feature.fuzzy.HysteresisThresholder;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.segmentation.Segmentation;



/**
 * The Class SomeImageTools.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SomeImageTools {
	
	// TODO revoir ce code pourri !
	/**
	 * Binarize.
	 * 
	 * @param img
	 *            the img
	 * @param bin
	 *            the bin
	 * @param rowOffset
	 *            the row offset
	 * @param colOffset
	 *            the col offset
	 * @param scale
	 *            the scale
	 * @param thresh
	 *            the thresh
	 */
	public static void binarize(IcyBufferedImage img, BinaryIcyBufferedImage bin, int rowOffset, int colOffset, int scale, double thresh) {
		Raster raster = img.getRaster();
		int x = 0;
		int y = 0;

		double t = 0;
		int w = bin.getWidth();
		byte[] raw = bin.getRawData();

		if (img.getSampleModel().getNumBands() == 1) {
			t = thresh;
			for (int i = 0; i < raster.getWidth(); i += scale) {
				for (int j = 0; j < raster.getHeight(); j += scale) {
					int v = raster.getSample(i, j, 0);
					if (v < t) {
						x = (colOffset + i) / scale;
						y = (rowOffset + j) / scale;
						raw[x + w * y] = BinaryIcyBufferedImage.TRUE;
					}
				}
			}
		} else {
			t = 3 * thresh;
			for (int i = 0; i < raster.getWidth(); i += scale) {
				for (int j = 0; j < raster.getHeight(); j += scale) {
					int r = raster.getSample(i, j, 0);
					int g = raster.getSample(i, j, 1);
					int b = raster.getSample(i, j, 2);
					int crit = r + g + b;
					if (crit < t) {
						x = (colOffset + i) / scale;
						y = (rowOffset + j) / scale;
						raw[x + w * y] = BinaryIcyBufferedImage.TRUE;
					}
				}
			}
		}
	}

	/**
	 * Binarize.
	 * 
	 * @param img
	 *            the img
	 * @param thresh
	 *            the thresh
	 * @return the binary icy buffered image
	 */
	public static BinaryIcyBufferedImage binarize(IcyBufferedImage img, double thresh) {
		int w = img.getWidth();
		int h = img.getHeight();
		int s = w * h;

		BinaryIcyBufferedImage ibin = new BinaryIcyBufferedImage(w, h);
		double[] data = img.getDataXYAsDouble(0);
		byte[] bool = ibin.getDataXYAsByte(0);

		for (int idx = 0; idx < s; idx++) {
			if (data[idx] > thresh) {
				bool[idx] = BinaryIcyBufferedImage.TRUE;
			} else {
				bool[idx] = BinaryIcyBufferedImage.FALSE;
			}
		}

		return ibin;
	}
	
	
	public static BinaryIcyBufferedImage toMask(IcyBufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		int s = w * h;

		BinaryIcyBufferedImage ibin = new BinaryIcyBufferedImage(w, h);
		double[] data = img.getDataXYAsDouble(0);
		byte[] bool = ibin.getDataXYAsByte(0);

		for (int idx = 0; idx < s; idx++) {
			if (data[idx] == 0) {
				bool[idx] = BinaryIcyBufferedImage.TRUE;
			} else {
				bool[idx] = BinaryIcyBufferedImage.FALSE;
			}
		}

		return ibin;
	}

	/**
	 * Binarize.
	 * 
	 * @param img
	 *            the img
	 * @param toSave
	 *            the to save
	 * @param thresh
	 *            the thresh
	 */
	public static void binarize(IcyBufferedImage img, IcyBufferedImage toSave, double thresh) {
		int w = img.getWidth();
		int h = img.getHeight();
		int s = w * h;

		double[] data = img.getDataXYAsDouble(0);
		byte[] byt0 = toSave.getDataXYAsByte(0);
		byte[] byt1 = toSave.getDataXYAsByte(1);
		byte[] byt2 = toSave.getDataXYAsByte(2);

		for (int idx = 0; idx < s; idx++) {
			if (data[idx] > thresh) {
				byt0[idx] = (byte) 0;
				byt1[idx] = (byte) 0;
				byt2[idx] = (byte) 0;
			}
		}

		toSave.dataChanged();
	}

	/**
	 * Binarize for save.
	 * 
	 * @param img
	 *            the img
	 * @param thresh
	 *            the thresh
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage binarizeForSave(IcyBufferedImage img, double thresh) {
		int w = img.getWidth();
		int h = img.getHeight();
		int s = w * h;

		IcyBufferedImage toSave = new IcyBufferedImage(w, h, 3, TypeUtil.TYPE_BYTE);
		double[] data = img.getDataXYAsDouble(0);
		byte[] byt = toSave.getDataXYAsByte(0);

		for (int idx = 0; idx < s; idx++) {
			byt[idx] = (data[idx] > thresh) ? (byte) 0 : (byte) -1;
		}

		toSave.setDataXYAsByte(1, byt.clone());
		toSave.setDataXYAsByte(2, byt.clone());

		toSave.dataChanged();
		return toSave;
	}

	/**
	 * Change color space.
	 * 
	 * @param img
	 *            the img
	 * @param cs
	 *            the cs
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage changeColorSpace(IcyBufferedImage img, int cs, double min, double max) {
		int w = img.getWidth();
		int h = img.getHeight();

		IcyBufferedImage gs = new IcyBufferedImage(w, h, 3, TypeUtil.TYPE_DOUBLE);
		double[][] id = new double[3][];

		for (int i = 0; i < 3; i++) {
			id[i] = gs.getDataXYAsDouble(i);
		}

		try {
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					double[] ihh = ColorSpaceTools.getColorComponentsD_0_255(img, cs, x, y);
					for (int i = 0; i < 3; i++) {
						if (ihh[i] > max) {
							id[i][x + y * w] = max;
						} else if (ihh[i] < min) {
							id[i][x + y * w] = min;
						} else {
							id[i][x + y * w] = ihh[i];
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		gs.dataChanged();
		return gs;
	}
	
	/**
	 * Compute gray scale.
	 * 
	 * @param img
	 *            the img
	 * @param c
	 *            the c
	 * @param n
	 *            the n
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage computeGrayScale(IcyBufferedImage img, int c, int n) {
		return computeGrayScale(img, ColorSpaceTools.RGB_TO_I1H2H3, c, n);
	}

	/**
	 * Compute gray scale.
	 * 
	 * @param img
	 *            the img
	 * @param cs
	 *            the cs
	 * @param c
	 *            the c
	 * @param n
	 *            the n
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage computeGrayScale(IcyBufferedImage img, int cs, int c, int n) {
		int w = img.getWidth();
		int h = img.getHeight();

		IcyBufferedImage gs = new IcyBufferedImage(w, h, n, TypeUtil.TYPE_DOUBLE);
		ArrayList<double[]> id = new ArrayList<double[]>();

		for (int i = 0; i < n; i++) {
			id.add(i, gs.getDataXYAsDouble(i));
		}

		try {
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					double[] ihh = ColorSpaceTools.getColorComponentsD_0_255(img, cs, x, y);
					for (double[] lid : id) {
						lid[x + y * w] = ihh[c];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		gs.dataChanged();
		return gs;
	}

	// TODO � utiliser partout au lieu d'un appel direct �
	// SectionConnectedComponentFinder
	/**
	 * Find connected components.
	 * 
	 * @param bin
	 *            the bin
	 * @return the array list
	 */
	public static List<My2DConnectedComponent> findConnectedComponents(BinaryIcyBufferedImage bin) {
		return findConnectedComponents(bin, 0, bin.getWidth() * bin.getHeight());
	}

	/**
	 * Find connected components.
	 * 
	 * @param bin
	 *            the bin
	 * @param maxCCSurface
	 *            the max cc surface
	 * @param minCCSurface
	 *            the min cc surface
	 * @return the array list
	 */
	public static List<My2DConnectedComponent> findConnectedComponents(BinaryIcyBufferedImage bin, int minCCSurface, int maxCCSurface) {
		My2DConnectedComponentFinder finder = new My2DConnectedComponentFinder(bin, minCCSurface, maxCCSurface);
		ArrayList<My2DConnectedComponent> ccs = new ArrayList<My2DConnectedComponent>();

		for (My2DConnectedComponent cc : finder) {
			ccs.add(cc);
		}

		return ccs;
	}

	/**
	 * Gets the bilinear interpolated rgb values.
	 * 
	 * @param img
	 *            the img
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the bilinear interpolated rgb values
	 */
	public static double[] getBilinearInterpolatedRGBValues(IcyBufferedImage img, double x, double y) {
		int w = img.getWidth();
		int h = img.getHeight();

		double[] rgb = new double[3];

		for (int i = 0; i < 3; i++) {
			rgb[i] = getBilinearInterpolatedValue(img.getDataXYAsDouble(i), w, h, x, y);
		}

		return rgb;
	}

	/**
	 * Gets the bilinear interpolated value.
	 * 
	 * @param data
	 *            the data
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the bilinear interpolated value
	 */
	public static double getBilinearInterpolatedValue(final double[] data, int w, int h, double x, double y) {
		if ((x < 0) || (x > w - 1) || (y < 0) || (y > h - 1)) {
			return 0;
		}

		int x1 = (int) Math.floor(x);
		int y1 = (int) Math.floor(y);

		if ((x == x1) && (y == y1)) {
			return data[x1 + y1 * w];
		}

		int x2 = x1 + 1;
		int y2 = y1 + 1;

		if (x == x1) {
			x2 = x1;
		}

		if (y == y1) {
			y2 = y1;
		}

		double dx = x2 - x;
		double dy = y2 - y;
		double dxdy = dx * dy;

		double a1 = dx - dxdy;
		double a2 = 1 + dxdy - dx - dy;
		double a3 = dy - dxdy;
		double a4 = dxdy;

		try {
			return a1 * data[x1 + y2 * w] + a2 * data[x2 + y2 * w] + a3 * data[x2 + y1 * w] + a4 * data[x1 + y1 * w];
		} catch (ArrayIndexOutOfBoundsException e) {
			Algorithm.err("Error for (" + x + ", " + y + ") : " + x1 + "-" + x2 + ", " + y1 + "-" + y2 + " / (" + w + ", " + h + ")");
			return 0;
		}
	}

	/**
	 * Gets the bilinear interpolated value.
	 * 
	 * @param img
	 *            the img
	 * @param canal
	 *            the canal
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the bilinear interpolated value
	 */
	public static double getBilinearInterpolatedValue(IcyBufferedImage img, int canal, double x, double y) {
		int w = img.getWidth();
		int h = img.getHeight();

		final double[] data = img.getDataXYAsDouble(canal);

		return getBilinearInterpolatedValue(data, w, h, x, y);
	}

	/**
	 * Make area.
	 * 
	 * @param bin
	 *            the bin
	 * @return the area
	 */
	public static Area makeArea(BinaryIcyBufferedImage bin) {
		Area area = new Area();
		ArrayList<Line2D> lines = scanLine(bin);
		float total = lines.size();
		float step = 10f;
		float nextStep = 0;
		float done = 0;
		float pct = 0;
		for (Line2D l : lines) {
			pct = done * 100 / total;
			while (pct >= nextStep) {
				Algorithm.out("Transforming : " + (int) pct + " % done");
				nextStep += step;
			}
			Shape shape = new Rectangle2D.Float((float) l.getX1(), (float) l.getY1(), (float) l.getX2() - (float) l.getX1(), 1f);
			area.add(new Area(shape));
			done += 1;
		}
		Algorithm.out("Transforming : done");
		return area;
	}

	/**
	 * Make area.
	 * 
	 * @param img
	 *            the img
	 * @param thresh
	 *            the thresh
	 * @return the area
	 */
	public static Area makeArea(IcyBufferedImage img, int thresh) {
		BinaryIcyBufferedImage bin = new BinaryIcyBufferedImage(img.getWidth(), img.getHeight());
		binarize(img, bin, 0, 0, 1, thresh);
		return makeArea(bin);
	}

	/**
	 * Make binary.
	 * 
	 * @param area
	 *            the area
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the binary icy buffered image
	 */
	public static BinaryIcyBufferedImage makeBinary(Area area, int w, int h) {
		BinaryIcyBufferedImage bin = new BinaryIcyBufferedImage(w, h);
		float step = 10f;
		float nextStep = 0;
		float pct = 0;
		int idx = 0;
		byte[] raw = bin.getRawData();
		for (int y = 0; y < h; y++) {
			pct = y * 100 / h;
			while (pct >= nextStep) {
				Algorithm.out("Transforming : " + (int) pct + " % done");
				nextStep += step;
			}
			for (int x = 0; x < w; x++) {
				if (area.contains(x, y)) {
					raw[idx] = BinaryIcyBufferedImage.TRUE;
				}
				idx++;
			}
		}
		Algorithm.out("Transforming : done");
		return bin;
	}

	/**
	 * Make binary and dilate.
	 * 
	 * @param img
	 *            the img
	 * @param scale
	 *            the scale
	 * @param thresh
	 *            the thresh
	 * @return the binary icy buffered image
	 */
	public static BinaryIcyBufferedImage makeBinaryAndDilate(IcyBufferedImage img, int scale, int thresh) {
		int binW = (int) Math.ceil(img.getWidth() / (double) scale);
		int binH = (int) Math.ceil(img.getHeight() / (double) scale);
		BinaryIcyBufferedImage bin = new BinaryIcyBufferedImage(binW, binH);
		binarize(img, bin, 0, 0, scale, thresh);
		MorphologyToolbox.fillHolesInPlace(bin);
		MorphologyToolbox.dilateInPlace(bin);
		return bin;
	}

	/**
	 * Rotate.
	 * 
	 * @param aDrawing
	 *            the a drawing
	 * @param angle
	 *            the angle
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage rotate(IcyBufferedImage aDrawing, double angle) {
		AffineTransform at = new AffineTransform();
		at.rotate(angle);

		Rectangle2D rectangle2D = new Rectangle2D.Float(0, 0, aDrawing.getWidth(), aDrawing.getHeight());
		Rectangle2D transformedRectangle = at.createTransformedShape(rectangle2D).getBounds2D();

		IcyBufferedImage rotatedDrawing = new IcyBufferedImage((int) transformedRectangle.getWidth(), (int) transformedRectangle.getHeight(), aDrawing.getColorModel().getNumColorComponents(), aDrawing.getColorModel().getTransferType());
		Graphics2D g = rotatedDrawing.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, rotatedDrawing.getWidth(), rotatedDrawing.getHeight());

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.translate(-transformedRectangle.getMinX(), -transformedRectangle.getMinY());
		g.rotate(angle);
		g.drawImage(aDrawing, null, 0, 0);

		return rotatedDrawing;
	}

	/**
	 * Saliency.
	 * 
	 * @param dtct
	 *            the dtct
	 * @param max
	 *            the max
	 * @param sigma
	 *            the sigma
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage saliency(IcyBufferedImage dtct, double max, double sigma) {
		int w = dtct.getWidth();
		int h = dtct.getHeight();
	
		int sz = (int) Math.ceil(3 * sigma);
		int dim = 2 * sz + 1;
		double[] g = new double[dim * dim];
		double cst = max / (sigma * Math.sqrt(2 * Math.PI));
	
		int idx = 0;
		// double norm = 0;
		for (int y = -sz; y <= sz; y++) {
			for (int x = -sz; x <= sz; x++) {
				double dst = Math.sqrt(x * x + y * y) / sigma;
				g[idx] = cst * Math.exp(-0.5 * dst * dst);
				// norm += g[idx];
				idx++;
			}
		}
	
		IcyBufferedImage res = new IcyBufferedImage(w, h, 1, TypeUtil.TYPE_DOUBLE);
		double[] resData = res.getDataXYAsDouble(0);
		double[] dtctData = dtct.getDataXYAsDouble(0);
		Arrays.fill(resData, 0.0);
	
		int off = 0;
		for (int y = 0; y < h; y++) {
			off = y * w;
			for (int x = 0; x < w; x++) {
				if (dtctData[off] > 0) {
					idx = 0;
					for (int dy = -sz; dy <= sz; dy++) {
						for (int dx = -sz; dx <= sz; dx++) {
							int nx = x + dx;
							int ny = y + dy;
							int idx2 = nx + w * ny;
							if ((nx >= 0) && (nx < w) && (ny >= 0) && (ny < h)) {
								resData[idx2] += g[idx] * dtctData[off];
							}
							idx++;
						}
					}
				}
				off++;
			}
		}
	
		res.dataChanged();
	
		return res;
	}

	/**
	 * Process.
	 * 
	 * @param img
	 *            the img
	 * @param sm
	 *            the sm
	 * @param ss
	 *            the ss
	 * @param hl
	 *            the hl
	 * @param hh
	 *            the hh
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage saliencyAndHysteresis(IcyBufferedImage img, double sm, double ss, double hl, double hh) {
		return saliencyAndHysteresis(img, sm, ss, hl, hh, null);
	}

	/**
	 * Process.
	 * 
	 * @param img
	 *            the img
	 * @param sm
	 *            the sm
	 * @param ss
	 *            the ss
	 * @param hl
	 *            the hl
	 * @param hh
	 *            the hh
	 * @param output
	 *            the output
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage saliencyAndHysteresis(IcyBufferedImage img, double sm, double ss, double hl, double hh, Sequence output) {
		IcyBufferedImage sal = saliency(img, sm, ss);
	
		if (output != null) {
			output.setImage(0, 0, sal);
		}
	
		HysteresisThresholder hta = new HysteresisThresholder(hh, hl);
		IcyBufferedImage hyst = hta.work(sal);
	
		return hyst;
	}

	/**
	 * Save.
	 * 
	 * @param bin
	 *            the bin
	 * @param binaryFile
	 *            the binary file
	 * @param nbc
	 *            the nbc
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void save(BinaryIcyBufferedImage bin, File binaryFile, int nbc) throws IOException {
		IcyBufferedImage toSave = bin.asIcyBufferedImage(nbc, false);
		
		try {
			Saver.saveImage(toSave, binaryFile, true);
		} catch (FormatException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Save.
	 * 
	 * @param seg
	 *            the seg
	 * @param tiffFile
	 *            the tiff file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void save(Segmentation seg, File tiffFile) throws IOException {
		int w = seg.getWidth();
		int h = seg.getHeight();

		IcyBufferedImage img = new IcyBufferedImage(w, h, ColorSpaceTools.NB_COLOR_CHANNELS, TypeUtil.TYPE_BYTE);

		Graphics2D g = img.createGraphics();
		g.setBackground(Color.WHITE);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		for (Mask m : seg) {
			g.setColor(m.getColor());

			byte[] raw = m.getBinaryData().getRawData();
			int idx = 0;
			for (int j = 0; j < h; j++) {
				for (int i = 0; i < w; i++) {
					if (raw[idx] == BinaryIcyBufferedImage.TRUE) {
						g.fillRect(i, j, 1, 1);
					}
					idx++;
				}
			}
		}

		try {
			Saver.saveImage(img, tiffFile, true);
		} catch (FormatException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Scan line.
	 * 
	 * @param bin
	 *            the bin
	 * @return the array list
	 */
	public static ArrayList<Line2D> scanLine(BinaryIcyBufferedImage bin) {
		ArrayList<Line2D> lines = new ArrayList<Line2D>();

		int w = bin.getWidth();
		int h = bin.getHeight();
		int sx = -1;

		byte[] binraw = bin.getRawData();

		int idx = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (binraw[idx] == BinaryIcyBufferedImage.TRUE) {
					if (sx == -1) {
						sx = x;
					}
				} else {
					if (sx >= 0) {
						lines.add(new Line2D.Float(sx, y, x, y));
						sx = -1;
					}
				}
				idx++;
			}
			if (sx >= 0) {
				lines.add(new Line2D.Float(sx, y, w, y));
				sx = -1;
			}
		}

		return lines;
	}

	/**
	 * To image.
	 * 
	 * @param bin
	 *            the bin
	 * @return the icy buffered image
	 */
	public static IcyBufferedImage toImage(BinaryIcyBufferedImage bin) {
		IcyBufferedImage toSave = new IcyBufferedImage(bin.getWidth(), bin.getHeight(), 1, TypeUtil.TYPE_DOUBLE);
		double[] ddt = toSave.getDataXYAsDouble(0);
		byte[] bdt = bin.getRawData();

		for (int i = 0; i < bdt.length; i++) {
			if (bdt[i] == BinaryIcyBufferedImage.TRUE) {
				ddt[i] = 1d;
			} else {
				ddt[i] = 0d;
			}
		}

		return toSave;
	}
	
	
	public static BufferedImage resize(BufferedImage original, int w, int h) {
		BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) resized.getGraphics();

		resizeAndDraw(original, g2, w, h);
		
		return resized;
	}
	
	public static void resizeAndDraw(BufferedImage original, Graphics2D g2, int w, int h) {
		int ow = original.getWidth();
		int oh = original.getHeight();

		double wr = (double) w / (double) ow;
		double hr = (double) h / (double) oh;

		double fr = Math.min(wr, hr);
		int nw = (int) (fr * ow);
		int nh = (int) (fr * oh);

		int wo = (w - nw) / 2;
		int ho = (h - nh) / 2;

		AffineTransform t = new AffineTransform();
		t.translate(wo, ho);
		t.scale(fr, fr);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED ); 
		g2.drawImage(original, t, null);
		
//		Graphics2D g3 = (Graphics2D) g2.create();
//		g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
//		g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
//		g3.translate(wo, ho);
//		g3.scale(fr, fr);
//		g3.drawImage(original, 0, 0, null);
//		g3.dispose();
	}

}
