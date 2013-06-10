package plugins.nherve.toolbox.image.feature.lbp;

import icy.image.IcyBufferedImage;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugins.nherve.toolbox.Pair;
import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.descriptor.GlobalAndLocalDescriptor;
import plugins.nherve.toolbox.image.feature.region.FullImageSupportRegion;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;
import plugins.nherve.toolbox.image.feature.signature.VectorSignatureConcatenator;
import plugins.nherve.toolbox.image.toolboxes.ColorSpaceTools;
import plugins.nherve.toolbox.image.toolboxes.ImageTools;
import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;


public class LocalBinaryPattern extends GlobalAndLocalDescriptor<SegmentableIcyBufferedImage, VectorSignature> {
	public final static int BINARY_ENCODING = 1;
	public final static int TERNARY_ENCODING = 2;

	private LBPToolbox tbx;
	private int w;
	private boolean fuzzy;
	private boolean fuzzyAllChannels;
	private boolean fuzzyCross;
	private boolean fuzzyOnlyCross;
	private boolean ri;
	private int fuzzyChannel;
	private int fuzzyColorSpace;
	private LBPToolbox pptbx;

	private Map<SegmentableIcyBufferedImage, IcyBufferedImage[]> cachePrecomputedLBP;
	
	private Map<SegmentableIcyBufferedImage, List<Pair<Integer, Integer>>> cachePairs;
	private Map<SegmentableIcyBufferedImage, Map<Integer, IcyBufferedImage>> cacheGrays;

	public LocalBinaryPattern(int p, double r, int w, boolean ri, boolean uniform, int v, int encoding, boolean in, boolean display) {
		super(display);
		fuzzy = false;
		fuzzyAllChannels = false;
		fuzzyCross = false;
		fuzzyOnlyCross = false;
		fuzzyChannel = 0;
		fuzzyColorSpace = ColorSpaceTools.RGB_TO_I1H2H3;
		this.w = w;
		this.ri = ri;

		tbx = new LBPToolbox(p, r, ri, uniform, v, encoding, in, display);
		cachePrecomputedLBP = new HashMap<SegmentableIcyBufferedImage, IcyBufferedImage[]>();
		cacheGrays = new HashMap<SegmentableIcyBufferedImage, Map<Integer,IcyBufferedImage>>();
		cachePairs = new HashMap<SegmentableIcyBufferedImage, List<Pair<Integer,Integer>>>();
	}

	public LocalBinaryPattern(int p, double r, int w, boolean ri, boolean uniform, int v, boolean in, boolean display) {
		this(p, r, w, ri, uniform, v, BINARY_ENCODING, in, display);
	}

	public LocalBinaryPattern(int p, double r, boolean ri, boolean uniform, int v, int encoding, boolean in, boolean display) {
		this(p, r, 0, ri, uniform, v, encoding, in, display);
	}

	public LocalBinaryPattern(int p, double r, boolean ri, boolean uniform, int v, boolean in, boolean display) {
		this(p, r, 0, ri, uniform, v, BINARY_ENCODING, in, display);
	}

	public LocalBinaryPattern(int p, double r, int ft, double param, boolean display) {
		this(p, r, ft, param, display, true);
	}

	public LocalBinaryPattern(int p, double r, int ft, double t, boolean display, boolean riu) {
		this(p, r, 0, false, false, 1, true, display);

		this.ri = riu;

		tbx.setFuzzyFunction(ft, t);

		fuzzy = true;
		pptbx = new LBPToolbox(p, r, true, true, 1, BINARY_ENCODING, true, display);
	}
	
	public LocalBinaryPattern(int p, double r, int ft, double t, boolean display, boolean riu, int v, boolean in) {
		this(p, r, 0, false, false, v, in, display);

		this.ri = riu;

		tbx.setFuzzyFunction(ft, t);

		fuzzy = true;
		pptbx = new LBPToolbox(p, r, true, true, v, BINARY_ENCODING, in, display);
		
		
	}

	public VectorSignature extractLocalSignature(IcyBufferedImage precomputedLBP, SupportRegion<IcyPixel> reg) throws SignatureException {
		VectorSignature sig = getEmptySignature(tbx.getTernarySingleSignatureSize());
		int[] loc = precomputedLBP.getDataXYAsInt(0);

		if (reg instanceof FullImageSupportRegion) {
			for (int x = 0; x < loc.length; x++) {
				sig.addTo(loc[x], 1);
			}
		} else {
			int imgW = precomputedLBP.getWidth();
			int imgH = precomputedLBP.getHeight();

			IcyPixel px = reg.getCenter();
			int cx = (int) px.x;
			int cy = (int) px.y;
			int x1 = Math.max(cx - w, 0);
			int x2 = Math.min(cx + w, imgW - 1);
			int y1 = Math.max(cy - w, 0);
			int y2 = Math.min(cy + w, imgH - 1);

			int off = 0;

			for (int y = y1; y <= y2; y++) {
				off = y * imgW + x1;
				for (int x = x1; x <= x2; x++) {
					sig.addTo(loc[off], 1);
					off++;
				}
			}
		}

		sig.normalizeSumToOne(true);

		return sig;
	}

	@Override
	public VectorSignature extractLocalSignature(SegmentableIcyBufferedImage img, SupportRegion<IcyPixel> reg) throws SignatureException {
		if (fuzzy) {
			int off = 0;

			List<Pair<Integer, Integer>> pairs = null;
			Map<Integer, IcyBufferedImage> grays = null;
			
			synchronized (cacheGrays) {
				grays = cacheGrays.get(img);
			}

			if (grays == null) {
				throw new SignatureException("PreProcess not launched for current image (" + img.getName() + ")");
			}
			
			synchronized (cachePairs) {
				pairs = cachePairs.get(img);
			}

			if (pairs == null) {
				throw new SignatureException("PreProcess not launched for current image (" + img.getName() + ")");
			}
			
			// TODO EN COURS DE TEST 2
			int finalSigSize = 0;
			if (ri) {
				finalSigSize = pptbx.getSignatureSize() * pairs.size();
			} else {
				finalSigSize = tbx.getSignatureSize() * pairs.size();
			}
			VectorSignature sigf = getEmptySignature(finalSigSize);

			for (Pair<Integer, Integer> p : pairs) {
				IcyBufferedImage center = grays.get(p.first);
				IcyBufferedImage neighbours = grays.get(p.second);
				double[] precomputedLBP = null;
				if (reg instanceof FullImageSupportRegion) {
					precomputedLBP = tbx.computeFuzzyFullImage(center, neighbours);
				} else {
					precomputedLBP = tbx.computeFuzzyRegion(center, neighbours, reg);
				}
				VectorSignature sig1 = getEmptySignature(precomputedLBP.length);
				for (int i = 0; i < precomputedLBP.length; i++) {
					sig1.set(i, precomputedLBP[i]);
				}

				// TODO EN COURS DE TEST 2
				// TODO Revoir notamment la dimension finale du vecteur
				if (ri) {
					VectorSignature sig2 = getEmptySignature(pptbx.getSignatureSize());
					for (int d = 0; d < sig1.getSize(); d++) {
						sig2.addTo((int) pptbx.getRI(d), sig1.get(d));
					}
					sig2.normalizeSumToOne(true);
					sig1 = sig2;
				}
				// ---

				for (int d = 0; d < sig1.getSize(); d++) {
					sigf.set(off + d, sig1.get(d));
				}

				off += sig1.getSize();
			}
			sigf.normalizeSumToOne(true);
			// System.out.println(sigf.toString());
			return sigf;
		} else {
			IcyBufferedImage[] precomputedLBP = null;

			synchronized (cachePrecomputedLBP) {
				precomputedLBP = cachePrecomputedLBP.get(img);
			}

			if (precomputedLBP == null) {
				throw new SignatureException("PreProcess not launched for current image (" + img.getName() + ")");
			}

			if (precomputedLBP.length > 1) {
				VectorSignatureConcatenator concat = new VectorSignatureConcatenator(VectorSignature.DENSE_VECTOR_SIGNATURE, false);
				for (IcyBufferedImage pre : precomputedLBP) {
					concat.add(extractLocalSignature(pre, reg));
				}
				return concat.concatenate()[0];
			} else {
				return extractLocalSignature(precomputedLBP[0], reg);
			}
		}
	}

	@Override
	public VectorSignature extractLocalSignature(SegmentableIcyBufferedImage img, Shape shape) throws SignatureException {
		throw new SignatureException("LocalBinaryPattern::extractLocalSignature not implemented for shape");
	}

	@Override
	public int getSignatureSize() {
		if (fuzzyAllChannels) {
			return tbx.getSignatureSize() * 3;
		}
		return tbx.getSignatureSize();
	}

	@Override
	public void postProcess(SegmentableIcyBufferedImage img) throws SignatureException {
		synchronized (cachePrecomputedLBP) {
			cachePrecomputedLBP.remove(img);
		}
		synchronized (cacheGrays) {
			cacheGrays.remove(img);
		}
		synchronized (cachePairs) {
			cachePairs.remove(img);
		}
	}

	@Override
	public void preProcess(SegmentableIcyBufferedImage img) throws SignatureException {
		if (fuzzy) {
			int maxCanal = 0;
			int minCanal = 0;

			if (fuzzyAllChannels) {
				minCanal = 0;
				maxCanal = 2;
			} else {
				minCanal = fuzzyChannel;
				maxCanal = fuzzyChannel;
			}

			List<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>();
			Map<Integer, IcyBufferedImage> grays = new HashMap<Integer, IcyBufferedImage>();

			if (fuzzyCross) {
				for (int c1 = minCanal; c1 <= maxCanal; c1++) {
					for (int c2 = minCanal; c2 <= maxCanal; c2++) {
						if (!fuzzyOnlyCross || (c1 != c2)) {
							pairs.add(Pair.of(c1, c2));
						}
					}
				}
			} else {
				for (int canal = minCanal; canal <= maxCanal; canal++) {
					pairs.add(Pair.of(canal, canal));
				}
			}

			for (int canal = minCanal; canal <= maxCanal; canal++) {
				grays.put(canal, SomeImageTools.computeGrayScale(img.getImage(), fuzzyColorSpace, canal, 1));
			}
			
			synchronized (cacheGrays) {
				cacheGrays.put(img, grays);
			}
			
			synchronized (cachePairs) {
				cachePairs.put(img, pairs);
			}
			
		} else {
			IcyBufferedImage gray = SomeImageTools.computeGrayScale(img.getImage(), 0, 1);

			// TODO en cours de test1
			/*
			 * if (tbx.getR() > 1) { ConvolutionKernel2D g =
			 * ConvolutionKernel2D.getGaussianFilter(Math.PI * tbx.getR() / (2 *
			 * tbx.getP())); double[] d = g.convolveIntensity(gray);
			 * gray.setDataXYAsDouble(0, d); gray.dataChanged(); }
			 */
			// ---

			IcyBufferedImage[] precomputedLBP = null;
			// if (fuzzy) {
			// precomputedLBP = tbx.computeFuzzy(gray);
			// } else {
			precomputedLBP = tbx.compute(gray);
			// }
			synchronized (cachePrecomputedLBP) {
				cachePrecomputedLBP.put(img, precomputedLBP);
			}
		}
	}

	@Override
	public String toString() {
		return "LBP " + tbx.toString();
	}

	@Override
	public boolean needToLoadSegmentable() {
		return true;
	}

	public LBPToolbox getTbx() {
		return tbx;
	}

	public void setTernaryThreshold(double ternaryThreshold) {
		tbx.setTernaryThreshold(ternaryThreshold);
	}

	public void setFuzzyAllChannels(boolean fuzzyAllChannels) {
		this.fuzzyAllChannels = fuzzyAllChannels;
	}

	public void setFuzzyChannel(int fuzzyChannel) {
		this.fuzzyChannel = fuzzyChannel;
	}

	public void setFuzzyColorSpace(int fuzzyColorSpace) {
		this.fuzzyColorSpace = fuzzyColorSpace;
	}

	public void setFuzzyCross(boolean fuzzyCross) {
		this.fuzzyCross = fuzzyCross;
	}

	public void setFuzzyOnlyCross(boolean fuzzyOnlyCross) {
		this.fuzzyOnlyCross = fuzzyOnlyCross;
	}

	@Override
	public void setLogEnabled(boolean display) {
		super.setLogEnabled(display);
		
		if (tbx != null) {
			tbx.setLogEnabled(display);
		}
		
		if (pptbx != null) {
			pptbx.setLogEnabled(display);
		}
	}
}
