package plugins.nherve.toolbox.image.feature.lbp;

import icy.image.IcyBufferedImage;
import icy.type.TypeUtil;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.IcySupportRegion;
import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;
import plugins.nherve.toolbox.image.feature.SupportRegion;
import plugins.nherve.toolbox.image.feature.clustering.ClusteringException;
import plugins.nherve.toolbox.image.feature.com.CooccurenceMatrixFactory;
import plugins.nherve.toolbox.image.feature.com.KernelFactory;
import plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringAlgorithm;
import plugins.nherve.toolbox.image.feature.fuzzy.FuzzyClusteringProcessor;
import plugins.nherve.toolbox.image.feature.region.IcyPixel;
import plugins.nherve.toolbox.image.feature.region.SupportRegionException;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;
import plugins.nherve.toolbox.image.mask.MaskStack;
import plugins.nherve.toolbox.image.toolboxes.SomeImageTools;


public class LBPToolbox extends Algorithm {
	public final static int FUZZY_FUNCTION_STANDARD = 1;
	public final static int FUZZY_FUNCTION_TANH = 2;
	public final static int FUZZY_FUNCTION_STEP = 3;

	public interface FuzzyFunction {
		double apply(double x);
	}

	public class StandardFuzzyFunction implements FuzzyFunction {
		public StandardFuzzyFunction(double fuzzifier) {
			super();
			this.fuzzifier = fuzzifier;
		}

		private double fuzzifier;

		@Override
		public double apply(double x) {
			if (x < -fuzzifier) {
				return 0d;
			} else if (x > fuzzifier) {
				return 1d;
			} else {
				return (1d + (x / fuzzifier)) / 2d;
			}
		}
	}

	public class TanhFuzzyFunction implements FuzzyFunction {
		public TanhFuzzyFunction(double alpha) {
			super();
			this.alpha = alpha;
		}

		private double alpha;

		@Override
		public double apply(double x) {
			return Math.tanh(alpha * x);
		}
	}

	public class StepFuzzyFunction implements FuzzyFunction {
		public StepFuzzyFunction(double alpha) {
			super();
		}

		@Override
		public double apply(double x) {
			return (x >= 0) ? 1 : 0;
		}
	}

	private int P;
	private long maxLBPIndex;
	private double R;
	private double[][] nb;
	private boolean ri;
	private boolean uniform;
	private int v;
	private int encoding;
	private double ternaryThreshold;
	// private double fuzzifier;
	private HashMap<Long, Long> rilut;
	private HashMap<Long, Long> rilutid;
	private boolean useIntensity;

	private FuzzyClusteringAlgorithm fca;
	private FuzzyClusteringProcessor proc;
	private FuzzyFunction ff;

	private List<IcyPixel> intKernel;

	public LBPToolbox(int p, double r, boolean ri, boolean uniform, int v, int encoding, boolean in, boolean display) {
		super(display);
		P = p;
		R = r;
		this.ri = ri;
		this.uniform = uniform;
		this.v = v;
		this.encoding = encoding;

		this.useIntensity = in;

		this.ternaryThreshold = 20d;

		maxLBPIndex = (long) Math.pow(2, P);
		if (ri) {
			initRILUT();
		}

		initNeighbours();

		log("LocalBinaryPattern signature size = " + getSignatureSize() + " - (" + maxLBPIndex + " * " + v + " * " + encoding + ")");
	}

	public long circularRightShift(long x) {
		return (x >> 1) | ((x & 1) << (P - 1));
	}

	public long uniformity(long x) {
		long u = 0;

		long xor = x ^ circularRightShift(x);
		for (long p = 0; p < P; p++) {
			u += xor & 1;
			xor = circularRightShift(xor);
		}

		return u;
	}

	public IcyBufferedImage computeLBP(final Mask m) {
		return computeLBP(m, m);
	}

	public IcyBufferedImage computeLBP(final Mask m1, final Mask m2) {
		final int w = m1.getWidth();
		final int h = m1.getHeight();
		IcyBufferedImage lbp = new IcyBufferedImage(w, h, 1, TypeUtil.TYPE_INT);
		int[] lbpdata = lbp.getDataXYAsInt(0);

		final byte[] bin1 = m1.getBinaryData().getRawData();
		final byte[] bin2 = m2.getBinaryData().getRawData();

		int idx = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (bin1[idx] == BinaryIcyBufferedImage.TRUE) {
					long lbpidx = 0;
					long p2 = 1;

					for (IcyPixel px : intKernel) {
						int nx = x + (int) px.x;
						int ny = y + (int) px.y;
						if ((nx >= 0) && (nx < w) && (ny >= 0) && (ny < h) && (bin2[nx + w * ny] == BinaryIcyBufferedImage.TRUE)) {
							lbpidx += p2;
						}
						p2 = p2 << 1;
					}

					if (ri) {
						lbpidx = getRI(lbpidx);
					}

					lbpdata[idx] = (int) lbpidx;
				} else {
					lbpdata[idx] = -1;
				}
				idx++;
			}
		}

		lbp.dataChanged();
		return lbp;
	}

	/*
	 * public IcyBufferedImage[] computeFuzzy(IcyBufferedImage gray) { int w =
	 * gray.getWidth(); int h = gray.getHeight();
	 * 
	 * IcyBufferedImage[] flbp = new IcyBufferedImage[(int) maxLBPIndex];
	 * double[][] id = new double[(int) maxLBPIndex][]; for (int i = 0; i <
	 * maxLBPIndex; i++) { flbp[i] = new IcyBufferedImage(w, h, 1,
	 * TypeUtil.TYPE_DOUBLE); id[i] = flbp[i].getDataXYAsDouble(0); }
	 * 
	 * for (int x = 0; x < w; x++) { for (int y = 0; y < h; y++) { double[] data
	 * = getFLBP(gray, x, y); int idx = x + y * w;
	 * 
	 * for (int i = 0; i < maxLBPIndex; i++) { id[i][idx] = data[i]; } } }
	 * 
	 * for (int i = 0; i < maxLBPIndex; i++) { flbp[i].dataChanged(); }
	 * 
	 * return flbp; }
	 */

	public double[] computeFuzzyFullImage(IcyBufferedImage gray) {
		return computeFuzzyFullImage(gray, gray);
	}

	public double[] computeFuzzyFullImage(IcyBufferedImage center, IcyBufferedImage neighbours) {
		int w = center.getWidth();
		int h = center.getHeight();

		double[] flbpsum = new double[(int) maxLBPIndex];
		Arrays.fill(flbpsum, 0d);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				double[] data = getFLBP(center, neighbours, x, y);

				for (int i = 0; i < maxLBPIndex; i++) {
					flbpsum[i] += data[i];
				}
			}
		}

		return flbpsum;
	}

	public double[] computeFuzzyRegion(IcyBufferedImage center, IcyBufferedImage neighbours, SupportRegion<IcyPixel> reg) {
		double[] flbpsum = new double[(int) maxLBPIndex];
		Arrays.fill(flbpsum, 0d);
		Rectangle2D bb = reg.getBoundingBox();
		for (int x = (int)Math.floor(bb.getMinX()); x < (int)Math.floor(bb.getMaxX()); x++) {
			for (int y = (int)Math.floor(bb.getMinY()); y < (int)Math.floor(bb.getMaxY()); y++) {
				if (reg.contains(x, y)) {
					double[] data = getFLBP(center, neighbours, x, y);
					for (int i = 0; i < maxLBPIndex; i++) {
						flbpsum[i] += data[i];
					}
				}
			}
		}

		return flbpsum;
	}
	
	private long varoff(IcyBufferedImage gray, int x, int y) {
		int ivar = 0;

		if (v > 1) {
			if (useIntensity) {
				double intens = SomeImageTools.getBilinearInterpolatedValue(gray, 0, x, y);
				ivar = (int) Math.floor(intens * v / 256.0);
			} else {
				double var = getVAR(gray, x, y);
				ivar = (int) Math.floor(var * v / 16384.0);
			}

			if (ivar == v) {
				ivar--;
			}
		}

		return ivar * maxLBPIndex;
	}

	public IcyBufferedImage[] compute(IcyBufferedImage gray) {
		int w = gray.getWidth();
		int h = gray.getHeight();

		IcyBufferedImage[] lbp = new IcyBufferedImage[encoding];
		int[][] id = new int[encoding][];
		for (int i = 0; i < encoding; i++) {
			lbp[i] = new IcyBufferedImage(w, h, 1, TypeUtil.TYPE_INT);
			id[i] = lbp[i].getDataXYAsInt(0);
		}

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				long varoff = varoff(gray, x, y);

				switch (encoding) {
				case LocalBinaryPattern.BINARY_ENCODING:
					id[0][x + y * w] = (int) (varoff + getLBP(gray, x, y));
					break;
				case LocalBinaryPattern.TERNARY_ENCODING:
					long[] ids = getLTP(gray, x, y);
					for (int i = 0; i < encoding; i++) {
						id[i][x + y * w] = (int) (varoff + ids[i]);
					}
					break;
				}
				// System.out.println("idx " + idx);

			}
		}

		for (int i = 0; i < encoding; i++) {
			lbp[i].dataChanged();
		}

		return lbp;
	}

	public long getLBP(IcyBufferedImage gray, int x, int y) {
		long lbp = 0;

		double gc = SomeImageTools.getBilinearInterpolatedValue(gray, 0, x, y);
		double gp, diff;
		long p2 = 1;

		for (int p = 0; p < P; p++) {
			gp = SomeImageTools.getBilinearInterpolatedValue(gray, 0, x + nb[p][0], y + nb[p][1]);
			diff = gp - gc;
			if (diff >= 0) {
				lbp += p2;
			}
			p2 = p2 << 1;
		}

		if (ri) {
			lbp = getRI(lbp);
		}

		return lbp;
	}

	public long[] getLTP(IcyBufferedImage gray, int x, int y) {
		long lbp[] = new long[2];

		lbp[0] = 0;
		lbp[1] = 0;

		double gc = SomeImageTools.getBilinearInterpolatedValue(gray, 0, x, y);
		double gp, diff;
		long p2 = 1;

		for (int p = 0; p < P; p++) {
			gp = SomeImageTools.getBilinearInterpolatedValue(gray, 0, x + nb[p][0], y + nb[p][1]);
			diff = gp - gc;
			// System.out.println(diff);
			if (diff >= ternaryThreshold) {
				lbp[0] += p2;
			} else if (diff <= (-ternaryThreshold)) {
				lbp[1] += p2;
			}
			p2 = p2 << 1;
		}

		if (ri) {
			lbp[0] = getRI(lbp[0]);
			lbp[1] = getRI(lbp[1]);
		}

		return lbp;
	}

	public double[] getFLBP(IcyBufferedImage gray, int x, int y) {
		return getFLBP(gray, gray, x, y);
	}

	public double[] getFLBP(IcyBufferedImage center, IcyBufferedImage neighbours, int x, int y) {
		// double check = 0;
		double[] res = new double[(int) maxLBPIndex];

		double gc = SomeImageTools.getBilinearInterpolatedValue(center, 0, x, y);
		double[] f = new double[P];

		for (int p = 0; p < P; p++) {
			double diff = SomeImageTools.getBilinearInterpolatedValue(neighbours, 0, x + nb[p][0], y + nb[p][1]) - gc;
			f[p] = ff.apply(diff);
		}

		for (long i = 0; i < maxLBPIndex; i++) {
			double v = 1;
			long p2 = 1;
			for (int p = 0; p < P; p++) {
				if ((i & p2) == p2) {
					v *= f[p];
				} else {
					v *= 1d - f[p];
				}
				if (v == 0) {
					break;
				}
				p2 = p2 << 1;
			}
			res[(int) i] = v;
			// check += v;
		}

		// System.out.println("("+x+", "+y+") : " + check);

		return res;
	}

	public int getP() {
		return P;
	}

	public double getR() {
		return R;
	}

	public int getSignatureSize() {
		return (int) (v * encoding * maxLBPIndex);
	}

	public int getTernarySingleSignatureSize() {
		return (int) (v * maxLBPIndex);
	}

	public double getVAR(IcyBufferedImage gray, int x, int y) {
		double sum = 0;
		double mean;
		double varsum = 0;
		double var;

		List<Double> gps = new ArrayList<Double>();

		for (int p = 0; p < P; p++) {
			double gp = SomeImageTools.getBilinearInterpolatedValue(gray, 0, x + nb[p][0], y + nb[p][1]);
			sum += gp;
			gps.add(gp);
		}

		mean = sum / (double) P;

		for (double gp : gps) {
			varsum += Math.pow((gp - mean), 2d);
		}
		var = varsum / (double) P;

		// System.out.println(var);

		return var;
	}

	public void initNeighbours() {
		nb = new double[P][2];
		double x, y, c;
		final double cst = 2 * Math.PI / P;

		for (int p = 0; p < P; p++) {
			c = p * cst;
			x = -R * Math.sin(c);
			y = R * Math.cos(c);
			if (Math.abs(x) < 0.0000001) {
				x = 0;
			}
			if (Math.abs(y) < 0.0000001) {
				y = 0;
			}

			nb[p][0] = x;
			nb[p][1] = y;
		}

		intKernel = KernelFactory.getKernel(P, R, false);
	}

	public long getRI(long lbp) {
		return rilut.get(lbp);
	}

	public void initRILUT() {
		rilut = new HashMap<Long, Long>();
		rilutid = new HashMap<Long, Long>();

		long id = 0;
		for (long i = 0; i < maxLBPIndex; i++) {
			long min = i;
			long nv = i;
			for (int j = 1; j < P; j++) {
				nv = circularRightShift(nv);
				if (nv < min) {
					min = nv;
				}
			}
			if (!rilutid.containsKey(min)) {
				if (uniform && (uniformity(min) > 2)) {
					rilutid.put(min, (long) P);
				} else {
					rilutid.put(min, id++);
				}
			}
			min = rilutid.get(min);
			rilut.put(i, min);
		}

		maxLBPIndex = id;

		// if (uniform) {
		// for (long i = 0; i < originalmaxLBPIndex; i++) {
		// System.out.println(i + " - " + toBin(i) + " -> [" + rilut.get(i) +
		// "]");
		// }
		// }

		// for (long idl : rilutid.keySet()) {
		// System.out.println(idl + " - id " + toBin(idl) + " - " +
		// rilutid.get(idl) + " - " + uniformity(idl));
		// }
	}

	public String toBin(long i) {
		String r = "";
		int p2 = 1;
		for (int p = 0; p < P; p++) {
			if ((i & p2) == 0) {
				r = "0" + r;
			} else {
				r = "1" + r;
			}
			p2 = p2 << 1;
		}
		return r;
	}

	@Override
	public String toString() {
		return getP() + ", " + getR() + (ri ? " (RI)" : "");
	}

	public FuzzyClusteringAlgorithm getFca() {
		return fca;
	}

	public void setFca(FuzzyClusteringAlgorithm sm) {
		this.fca = sm;
	}

	public IcyBufferedImage getIndexed(SegmentableIcyBufferedImage simg) throws SignatureException {
		try {
			MaskStack seg = getSegmented(simg);
			IcyBufferedImage indexed = CooccurenceMatrixFactory.getIndexedImage(seg);

			return indexed;
		} catch (SupportRegionException e) {
			throw new SignatureException(e);
		} catch (MaskException e) {
			throw new SignatureException(e);
		} catch (ClusteringException e) {
			throw new SignatureException(e);
		} catch (FeatureException e) {
			throw new SignatureException(e);
		}
	}

	public MaskStack getSegmented(SegmentableIcyBufferedImage simg) throws SignatureException {
		try {
			IcySupportRegion[] regions = proc.getRegions(simg);
			DefaultVectorSignature[] sigs = proc.getSignatures(simg, regions);
			MaskStack seg = new MaskStack(simg.getWidth(), simg.getHeight());
			proc.addToMaskStack(fca, simg.getImage(), seg, regions, sigs);
			return seg;
		} catch (SupportRegionException e) {
			throw new SignatureException(e);
		} catch (MaskException e) {
			throw new SignatureException(e);
		} catch (ClusteringException e) {
			throw new SignatureException(e);
		} catch (FeatureException e) {
			throw new SignatureException(e);
		}
	}

	public IcyBufferedImage[] getMembershipImages(SegmentableIcyBufferedImage simg) throws SignatureException {
		try {

			IcyBufferedImage[] res = new IcyBufferedImage[fca.getNbClasses()];

			IcySupportRegion[] regions = proc.getRegions(simg);
			DefaultVectorSignature[] sigs = proc.getSignatures(simg, regions);

			for (int i = 0; i < fca.getNbClasses(); i++) {
				double[] mb = fca.getMemberships(sigs, i);
				res[i] = proc.getAsImage(mb, regions, simg.getWidth(), simg.getHeight());
			}

			return res;
		} catch (SupportRegionException e) {
			throw new SignatureException(e);
		} catch (ClusteringException e) {
			throw new SignatureException(e);
		} catch (FeatureException e) {
			throw new SignatureException(e);
		}
	}

	public IcyBufferedImage[] getMembershipImages(SegmentableIcyBufferedImage simg, List<Integer> workOnCanals) throws SignatureException {
		try {

			IcyBufferedImage[] res = new IcyBufferedImage[fca.getNbClasses()];

			IcySupportRegion[] regions = proc.getRegions(simg);
			DefaultVectorSignature[] sigs = proc.getSignatures(simg, regions);

			for (int c : workOnCanals) {
				double[] mb = fca.getMemberships(sigs, c, workOnCanals);
				res[c] = proc.getAsImage(mb, regions, simg.getWidth(), simg.getHeight());
			}

			return res;
		} catch (SupportRegionException e) {
			throw new SignatureException(e);
		} catch (ClusteringException e) {
			throw new SignatureException(e);
		} catch (FeatureException e) {
			throw new SignatureException(e);
		}
	}

	public IcyBufferedImage getMembershipImage(SegmentableIcyBufferedImage simg, int canal) throws SignatureException {
		try {
			IcySupportRegion[] regions = proc.getRegions(simg);
			DefaultVectorSignature[] sigs = proc.getSignatures(simg, regions);

			double[] mb = fca.getMemberships(sigs, canal);
			return proc.getAsImage(mb, regions, simg.getWidth(), simg.getHeight());
		} catch (SupportRegionException e) {
			throw new SignatureException(e);
		} catch (ClusteringException e) {
			throw new SignatureException(e);
		} catch (FeatureException e) {
			throw new SignatureException(e);
		}
	}

	public int getEncoding() {
		return encoding;
	}

	public double getTernaryThreshold() {
		return ternaryThreshold;
	}

	public void setTernaryThreshold(double ternaryThreshold) {
		this.ternaryThreshold = ternaryThreshold;
	}

	public void setFuzzyFunction(int type, double param) {
		switch (type) {
		case FUZZY_FUNCTION_STANDARD:
			ff = new StandardFuzzyFunction(param);
			break;
		case FUZZY_FUNCTION_TANH:
			ff = new TanhFuzzyFunction(param);
			break;
		case FUZZY_FUNCTION_STEP:
			ff = new StepFuzzyFunction(param);
			break;
		}

	}

	public FuzzyClusteringProcessor getFuzzyClusteringProcessor() {
		return proc;
	}

	public void setFuzzyClusteringProcessor(FuzzyClusteringProcessor proc) {
		this.proc = proc;
	}

	public boolean isRotationInvariant() {
		return ri;
	}
}
