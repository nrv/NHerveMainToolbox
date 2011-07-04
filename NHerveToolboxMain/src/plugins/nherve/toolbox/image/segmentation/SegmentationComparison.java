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
package plugins.nherve.toolbox.image.segmentation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.My2DConnectedComponent;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;
import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;

/**
 * The Class SegmentationComparison.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SegmentationComparison {

	/**
	 * Intersection.
	 * 
	 * @param m1
	 *            the m1
	 * @param m2
	 *            the m2
	 * @return the double
	 */
	public static double intersection(Mask m1, Mask m2) {
		return BinaryIcyBufferedImage.intersection(m1.getBinaryData(), m2.getBinaryData());
	}

	/**
	 * Clean segmentation.
	 * 
	 * @param seg
	 *            the seg
	 * @param maskPrefix
	 *            the mask prefix
	 * @throws MaskException
	 *             the mask exception
	 */
	private static void cleanSegmentation(Segmentation seg, String maskPrefix) throws MaskException {
		List<Mask> trm = new ArrayList<Mask>();
		for (Mask m : seg) {
			if (!m.getLabel().toUpperCase().startsWith(maskPrefix.toUpperCase())) {
				trm.add(m);
			}
		}
		for (Mask m : trm) {
			seg.remove(m);
		}
	}

	/**
	 * Prepare for lre score.
	 * 
	 * @param seg1
	 *            the seg1
	 * @param seg2
	 *            the seg2
	 * @param maskPrefix
	 *            the mask prefix
	 * @return the binary icy buffered image
	 * @throws MaskException
	 *             the mask exception
	 */
	private static BinaryIcyBufferedImage prepareForLREScore(Segmentation seg1, Segmentation seg2, String maskPrefix) throws MaskException {
		cleanSegmentation(seg1, maskPrefix);
		cleanSegmentation(seg2, maskPrefix);

		BinaryIcyBufferedImage rt = new BinaryIcyBufferedImage(seg1.getWidth(), seg1.getHeight());
		for (Mask m : seg1) {
			rt.add(m.getBinaryData());
		}
		for (Mask m : seg2) {
			rt.add(m.getBinaryData());
		}

		BinaryIcyBufferedImage rt1 = rt.getCopy();
		for (Mask m : seg1) {
			rt1.remove(m.getBinaryData());
		}
		Mask rt1m = seg1.createNewMask("RestrictTo background", false, Color.WHITE, 0);
		rt1m.setBinaryData(rt1);

		BinaryIcyBufferedImage rt2 = rt.getCopy();
		for (Mask m : seg2) {
			rt2.remove(m.getBinaryData());
		}
		Mask rt2m = seg2.createNewMask("RestrictTo background", false, Color.WHITE, 0);
		rt2m.setBinaryData(rt2);

		seg1.createBackgroundMask("Unused", Color.WHITE);
		seg1.createIndex();
		seg2.createBackgroundMask("Unused", Color.WHITE);
		seg2.createIndex();

		return rt;
	}

	/**
	 * Restricted gce.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @param restrict
	 *            the restrict
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	public static double restrictedGce(Segmentation gt, Segmentation at, String restrict) throws MaskException {
		try {
			Segmentation gtb = gt.clone();
			Segmentation atb = at.clone();

			BinaryIcyBufferedImage rt = prepareForLREScore(gtb, atb, restrict);

			// XXX debug
			// try {
			// OptimizedMaskPersistenceImpl p = new
			// OptimizedMaskPersistenceImpl();
			// p.save(gtb, new
			// File("/Users/nherve/Travail/glomdetect/glomdb/debug_pool/gtb" +
			// p.getMaskFileExtension()));
			// p.save(atb, new
			// File("/Users/nherve/Travail/glomdetect/glomdb/debug_pool/atb" +
			// p.getMaskFileExtension()));
			// try {
			// Saver.saveImage(rt, new
			// File("/Users/nherve/Travail/glomdetect/glomdb/debug_pool/rt2.tif"),
			// true);
			// } catch (FormatException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// } catch (ServiceException e) {
			// e.printStackTrace();
			// }
			// } catch (PersistenceException e) {
			// e.printStackTrace();
			// }
			// ---

			return gce(gtb, atb, rt);
		} catch (CloneNotSupportedException e) {
			throw new MaskException(e);
		}
	}

	/**
	 * Restricted lce.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @param restrict
	 *            the restrict
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	public static double restrictedLce(Segmentation gt, Segmentation at, String restrict) throws MaskException {
		try {
			Segmentation gtb = gt.clone();
			Segmentation atb = at.clone();

			BinaryIcyBufferedImage rt = prepareForLREScore(gtb, atb, restrict);

			return lce(gtb, atb, rt);
		} catch (CloneNotSupportedException e) {
			throw new MaskException(e);
		}
	}
	

	/**
	 * Mccc.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	private static double mccc2(Segmentation gt, Segmentation at, String restrict) throws MaskException {
		int w = gt.getWidth();
		int h = gt.getHeight();

		int gts = gt.getMaxId() + 1;
		int ats = at.getMaxId() + 1;

		double[] lreCache = new double[gts * ats];
		Arrays.fill(lreCache, -1d);

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Mask gtm = gt.getMask(x, y);
				int gtmid = gtm.getId();
				Mask atm = at.getMask(x, y);
				int atmid = atm.getId();
				if (lreCache[gtmid + atmid * gts] == -1) {
					lreCache[gtmid + atmid * gts] = lre(gtm, atm) + lre(atm, gtm);
				}
			}
		}

		double[] gtc = new double[gts];
		Arrays.fill(gtc, 0);
		double[] atc = new double[ats];
		Arrays.fill(atc, 0);

		for (Mask gtm : gt) {
			gtc[gtm.getId()] = 1;

		}

		for (Mask atm : at) {
			atc[atm.getId()] = 1;
		}

		for (Mask gtm : gt) {
			if (gtm.getLabel().toUpperCase().startsWith(restrict.toUpperCase())) {
				int gtmid = gtm.getId();
				double min = Double.MAX_VALUE;
				int minId = -1;
				for (Mask atm : at) {
					if (atm.getLabel().toUpperCase().startsWith(restrict.toUpperCase())) {
						int atmid = atm.getId();
						if (atc[atmid] > 0) {
							double v = lreCache[gtmid + atmid * gts];
							if ((v >= 0) && (v < min)) {
								min = v;
								minId = atmid;
							}
						}
					}
				}
				if (minId >= 0) {
					gtc[gtmid] = 0;
					atc[minId] = 0;
					// System.out.println("MCCC association : " +
					// gt.getById(gtmid) + " - " + at.getById(minId));
				}
			}
		}

		double sum = 0;
		for (int i = 0; i < gts; i++) {
			if (gtc[i] != 0) {
				// System.out.println("MCCC (1) unassociated : " +
				// gt.getById(i));
				sum += gtc[i];
			}
		}

		for (int i = 0; i < ats; i++) {
			if (atc[i] != 0) {
				// System.out.println("MCCC (2) unassociated : " +
				// at.getById(i));
				sum += atc[i];
			}
		}

		return sum / (double) gt.size();
	}

	/**
	 * Mccc.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @param restrict
	 *            the restrict
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	public static double mccc(Segmentation gt, Segmentation at, String restrict) throws MaskException {
		try {
			Segmentation gtb = gt.clone();
			Segmentation atb = at.clone();

			cleanSegmentation(gtb, restrict);
			cleanSegmentation(atb, restrict);

			gtb.createBackgroundMask("Unused", Color.WHITE);
			gtb.createIndex();
			atb.createBackgroundMask("Unused", Color.WHITE);
			atb.createIndex();

			// prepareForLREScore(gtb, atb, restrict);

			return mccc2(gtb, atb, restrict);
		} catch (CloneNotSupportedException e) {
			throw new MaskException(e);
		}
	}

	/**
	 * Gce.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	public static double gce(Segmentation gt, Segmentation at) throws MaskException {
		return gce(gt, at, null);
	}

	/**
	 * Gce.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @param restrictTo
	 *            the restrict to
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	public static double gce(Segmentation gt, Segmentation at, BinaryIcyBufferedImage restrictTo) throws MaskException {
		int w = gt.getWidth();
		int h = gt.getHeight();
		int s = 0;

		int gts = gt.getMaxId() + 1;
		int ats = at.getMaxId() + 1;

		double[] lre12Cache = new double[gts * ats];
		Arrays.fill(lre12Cache, -1d);
		double[] lre21Cache = new double[gts * ats];
		Arrays.fill(lre21Cache, -1d);

		double lre12 = 0;
		double lre21 = 0;

		byte[] rt = null;
		if (restrictTo != null) {
			rt = restrictTo.getRawData();
		}

		int idx = 0;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if ((rt == null) || (rt[idx] == BinaryIcyBufferedImage.TRUE)) {
					Mask gtm = gt.getMask(x, y);
					int gtmid = gtm.getId();
					Mask atm = at.getMask(x, y);
					int atmid = atm.getId();
					if (lre12Cache[gtmid + atmid * gts] == -1) {
						lre12Cache[gtmid + atmid * gts] = lre(gtm, atm);
						lre21Cache[gtmid + atmid * gts] = lre(atm, gtm);
					}
					lre12 += lre12Cache[gtmid + atmid * gts];
					lre21 += lre21Cache[gtmid + atmid * gts];
					s++;
				}
				idx++;
			}
		}

		if (s > 0) {
			lre12 /= s;
			lre21 /= s;
		}

		return Math.min(lre12, lre21);
	}

	/**
	 * Lce.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	public static double lce(Segmentation gt, Segmentation at) throws MaskException {
		return lce(gt, at, null);
	}

	/**
	 * Lce.
	 * 
	 * @param gt
	 *            the gt
	 * @param at
	 *            the at
	 * @param restrictTo
	 *            the restrict to
	 * @return the double
	 * @throws MaskException
	 *             the mask exception
	 */
	public static double lce(Segmentation gt, Segmentation at, BinaryIcyBufferedImage restrictTo) throws MaskException {
		int w = gt.getWidth();
		int h = gt.getHeight();
		int s = 0;

		int gts = gt.getMaxId() + 1;
		int ats = at.getMaxId() + 1;

		double[] lre12Cache = new double[gts * ats];
		Arrays.fill(lre12Cache, -1d);
		double[] lre21Cache = new double[gts * ats];
		Arrays.fill(lre21Cache, -1d);

		double lce = 0;

		byte[] rt = null;
		if (restrictTo != null) {
			rt = restrictTo.getRawData();
		}

		int idx = 0;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if ((rt == null) || (rt[idx] == BinaryIcyBufferedImage.TRUE)) {
					Mask gtm = gt.getMask(x, y);
					int gtmid = gtm.getId();
					Mask atm = at.getMask(x, y);
					int atmid = atm.getId();
					if (lre12Cache[gtmid + atmid * gts] == -1) {
						lre12Cache[gtmid + atmid * gts] = lre(gtm, atm);
						lre21Cache[gtmid + atmid * gts] = lre(atm, gtm);
					}

					lce += Math.min(lre12Cache[gtmid + atmid * gts], lre21Cache[gtmid + atmid * gts]);
					s++;
				}
				idx++;
			}
		}

		if (s > 0) {
			lce /= s;
		}

		return lce;
	}

	/**
	 * Lre.
	 * 
	 * @param cc1
	 *            the cc1
	 * @param cc2
	 *            the cc2
	 * @return the double
	 */
	public static double lre(Mask cc1, Mask cc2) {
		BinaryIcyBufferedImage b1 = cc1.getBinaryData().getCopy();
		double b1Card = b1.getSurface();
		BinaryIcyBufferedImage b2 = cc2.getBinaryData();
		b1.remove(b2);
		double b1minb2Card = b1.getSurface();
		double lre = b1minb2Card / b1Card;
		// System.out.println(cc1.getId() + " - " + cc2.getId() + " : " + lre);
		return lre;
	}

	/**
	 * Nhd.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double nhd(BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		return score1(gt, attempt) / Mask.getSurface(gt);
	}

	/**
	 * Nhd.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double nhd(Mask gt, Mask attempt) {
		return nhd(gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Ncc.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double ncc(Mask gt, Mask attempt) {
		return ncc(gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Ncc.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double ncc(BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		List<My2DConnectedComponent> gtCc = SomeImageTools.findConnectedComponents(gt);
		List<My2DConnectedComponent> attemptCc = SomeImageTools.findConnectedComponents(attempt);
		return ((double) (gtCc.size() - attemptCc.size())) / (double) (gtCc.size());
	}

	/**
	 * Score.
	 * 
	 * @param type
	 *            the type
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score(int type, BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		switch (type) {
		case 1:
			return score1(gt, attempt);
		case 2:
			return score2(gt, attempt);
		case 3:
			return score3(gt, attempt);
		case 4:
			return score4(gt, attempt);
		case 5:
			return score5(gt, attempt);
		default:
			return Double.MAX_VALUE;
		}
	}

	/**
	 * Score.
	 * 
	 * @param type
	 *            the type
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score(int type, Mask gt, Mask attempt) {
		return score(type, gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Score1.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score1(BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		return BinaryIcyBufferedImage.union(gt, attempt) - BinaryIcyBufferedImage.intersection(gt, attempt);
	}

	/**
	 * Score1.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score1(Mask gt, Mask attempt) {
		return score1(gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Score2.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score2(BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		double u = BinaryIcyBufferedImage.union(gt, attempt);
		double i = BinaryIcyBufferedImage.intersection(gt, attempt);
		return (u - i) / u;
	}

	/**
	 * Score2.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score2(Mask gt, Mask attempt) {
		return score2(gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Score3.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score3(BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		return (1 + BinaryIcyBufferedImage.union(gt, attempt)) / (1 + BinaryIcyBufferedImage.intersection(gt, attempt));
	}

	/**
	 * Score3.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score3(Mask gt, Mask attempt) {
		return score3(gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Score4.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score4(BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		return Math.abs(Mask.getSurface(gt) - Mask.getSurface(attempt));
	}

	/**
	 * Score4.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score4(Mask gt, Mask attempt) {
		return score4(gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Score5.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score5(BinaryIcyBufferedImage gt, BinaryIcyBufferedImage attempt) {
		return Math.abs(Mask.getSurface(gt) - Mask.getSurface(attempt));
	}

	/**
	 * Score5.
	 * 
	 * @param gt
	 *            the gt
	 * @param attempt
	 *            the attempt
	 * @return the double
	 */
	public static double score5(Mask gt, Mask attempt) {
		return score5(gt.getBinaryData(), attempt.getBinaryData());
	}

	/**
	 * Union.
	 * 
	 * @param m1
	 *            the m1
	 * @param m2
	 *            the m2
	 * @return the double
	 */
	public static double union(Mask m1, Mask m2) {
		return BinaryIcyBufferedImage.union(m1.getBinaryData(), m2.getBinaryData());
	}

}
