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
package plugins.nherve.toolbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.DenseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.SparseVectorSignature;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;


/**
 * The Class PersistenceToolbox.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class PersistenceToolbox {
	
	/** The Constant INT_NB_BYTES. */
	private final static int INT_NB_BYTES = 4;
	
	/** The Constant DOUBLE_NB_BYTES. */
	private final static int DOUBLE_NB_BYTES = 8;
	
	/** The Constant FLOAT_NB_BYTES. */
	private final static int FLOAT_NB_BYTES = 4;
	
	/** The Constant DENSE_TYPE. */
	private final static int DENSE_TYPE = 0;
	
	/** The Constant SPARSE_TYPE. */
	private final static int SPARSE_TYPE = 1;
	
	/** The Constant BAG_TYPE. */
	private final static int BAG_TYPE = 2;
	
	/** The Constant BAG_TYPE. */
	private final static int NULL_TYPE = 3;

	/** The Constant cs. */
	private final static Charset cs = Charset.forName("UTF-8");
	
	/** The Constant cse. */
	private final static CharsetEncoder cse = cs.newEncoder();;
	
	/** The Constant csd. */
	private final static CharsetDecoder csd = cs.newDecoder();;

	/**
	 * Dump bag of signatures.
	 * 
	 * @param fc
	 *            the fc
	 * @param s
	 *            the s
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpBagOfSignatures(FileChannel fc, BagOfSignatures<VectorSignature> s) throws IOException {
		dumpInt(fc, s.size());
		for (VectorSignature vs : s) {
			dumpSignature(fc, vs);
		}
	}

	/**
	 * Dump dense vector signature.
	 * 
	 * @param fc
	 *            the fc
	 * @param s
	 *            the s
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpDenseVectorSignature(FileChannel fc, DenseVectorSignature s) throws IOException {
		dumpInt(fc, s.getSize());
		ByteBuffer bb = ByteBuffer.allocate(DOUBLE_NB_BYTES * s.getSize());
		DoubleBuffer db = bb.asDoubleBuffer();
		db.put(s.getData());
		db.flip();
		fc.write(bb);
	}

	/**
	 * Dump double.
	 * 
	 * @param fc
	 *            the fc
	 * @param d
	 *            the d
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpDouble(FileChannel fc, double d) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(DOUBLE_NB_BYTES);
		DoubleBuffer db = bb.asDoubleBuffer();
		db.put(d);
		db.flip();
		fc.write(bb);
	}
	
	/**
	 * Dump float.
	 * 
	 * @param fc
	 *            the fc
	 * @param f
	 *            the f
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpFloat(FileChannel fc, float f) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(FLOAT_NB_BYTES);
		FloatBuffer db = bb.asFloatBuffer();
		db.put(f);
		db.flip();
		fc.write(bb);
	}
	
	/**
	 * Dump boolean.
	 * 
	 * @param fc
	 *            the fc
	 * @param b
	 *            the b
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpBoolean(FileChannel fc, boolean b) throws IOException {
		if (b) {
			dumpInt(fc, 1);
		} else {
			dumpInt(fc, 0);
		}
	}

	/**
	 * Dump int.
	 * 
	 * @param fc
	 *            the fc
	 * @param i
	 *            the i
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpInt(FileChannel fc, int i) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(INT_NB_BYTES);
		IntBuffer ib = bb.asIntBuffer();
		ib.put(i);
		ib.flip();
		fc.write(bb);
	}
	
	/**
	 * Dump int array.
	 * 
	 * @param fc
	 *            the fc
	 * @param i
	 *            the i
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpIntArray(FileChannel fc, int[] i) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(i.length * INT_NB_BYTES);
		IntBuffer ib = bb.asIntBuffer();
		ib.put(i);
		ib.flip();
		fc.write(bb);
	}

	/**
	 * Dump signature.
	 * 
	 * @param fc
	 *            the fc
	 * @param s
	 *            the s
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public static void dumpSignature(FileChannel fc, Signature s) throws IOException {
		if (s == null) {
			dumpInt(fc, NULL_TYPE);
		} else if (s instanceof DenseVectorSignature) {
			dumpInt(fc, DENSE_TYPE);
			dumpDenseVectorSignature(fc, (DenseVectorSignature) s);
		} else if (s instanceof SparseVectorSignature) {
			dumpInt(fc, SPARSE_TYPE);
			dumpSparseVectorSignature(fc, (SparseVectorSignature) s);
		} else if (s instanceof BagOfSignatures<?>) {
			dumpInt(fc, BAG_TYPE);
			dumpBagOfSignatures(fc, (BagOfSignatures<VectorSignature>) s);
		} else {
			throw new IOException("dumpSignature(" + s.getClass().getName() + ") not yet implemented");
		}
	}

	/**
	 * Dump sparse vector signature.
	 * 
	 * @param fc
	 *            the fc
	 * @param s
	 *            the s
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpSparseVectorSignature(FileChannel fc, SparseVectorSignature s) throws IOException {
		try {
			int nzb = s.getNonZeroBins();

			int[] i = new int[nzb];
			double[] d = new double[nzb];
			int n = 0;
			for (int c : s) {
				i[n] = c;
				d[n] = s.get(c);
				n++;
			}

			dumpInt(fc, s.getSize());
			dumpInt(fc, nzb);

			ByteBuffer bb1 = ByteBuffer.allocate(INT_NB_BYTES * nzb);
			IntBuffer ib = bb1.asIntBuffer();
			ib.put(i);
			ib.flip();
			fc.write(bb1);

			ByteBuffer bb2 = ByteBuffer.allocate(DOUBLE_NB_BYTES * nzb);
			DoubleBuffer db = bb2.asDoubleBuffer();
			db.put(d);
			db.flip();
			fc.write(bb2);
		} catch (SignatureException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Dump string.
	 * 
	 * @param fc
	 *            the fc
	 * @param s
	 *            the s
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dumpString(FileChannel fc, String s) throws IOException {
		CharBuffer cb = CharBuffer.allocate(s.length());
		cb.put(s);
		cb.flip();
		ByteBuffer buff = cse.encode(cb);
		dumpInt(fc, buff.limit());
		fc.write(buff);
	}

	/**
	 * Gets the file.
	 * 
	 * @param f
	 *            the f
	 * @param write
	 *            the write
	 * @return the file
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public static RandomAccessFile getFile(File f, boolean write) throws FileNotFoundException {
		if (write) {
			if (f.exists()) {
				f.delete();
			}
			return new RandomAccessFile(f, "rw");
		} else {
			return new RandomAccessFile(f, "r");
		}
	}

	/**
	 * Load bag of signatures.
	 * 
	 * @param fc
	 *            the fc
	 * @return the bag of signatures
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static BagOfSignatures<VectorSignature> loadBagOfSignatures(FileChannel fc) throws IOException {
		int type = loadInt(fc);
		if (type == NULL_TYPE) {
			return null;
		}
		if (type != BAG_TYPE) {
			throw new IOException("Unknown BagOfSignatures type (" + type + ")");
		}
		int sz = loadInt(fc);
		BagOfSignatures<VectorSignature> bag = new BagOfSignatures<VectorSignature>();
		for (int i = 0; i < sz; i++) {
			bag.add(loadVectorSignature(fc));
		}
		return bag;
	}

	/**
	 * Load dense vector signature.
	 * 
	 * @param fc
	 *            the fc
	 * @return the dense vector signature
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static DenseVectorSignature loadDenseVectorSignature(FileChannel fc) throws IOException {
		int sz = loadInt(fc);
		ByteBuffer bb = ByteBuffer.allocate(DOUBLE_NB_BYTES * sz);
		fc.read(bb);
		bb.flip();
		DoubleBuffer db = bb.asDoubleBuffer();
		double[] data = new double[sz];
		db.rewind();
		while (db.hasRemaining()) {
			data[db.position()] = db.get();
		}
		DenseVectorSignature v = new DenseVectorSignature(data);
		return v;
	}

	/**
	 * Load double.
	 * 
	 * @param fc
	 *            the fc
	 * @return the double
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static double loadDouble(FileChannel fc) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(DOUBLE_NB_BYTES);
		fc.read(bb);
		bb.flip();
		DoubleBuffer db = bb.asDoubleBuffer();
		return db.get();
	}
	
	/**
	 * Load float.
	 * 
	 * @param fc
	 *            the fc
	 * @return the float
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static float loadFloat(FileChannel fc) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(FLOAT_NB_BYTES);
		fc.read(bb);
		bb.flip();
		FloatBuffer db = bb.asFloatBuffer();
		return db.get();
	}

	/**
	 * Load int.
	 * 
	 * @param fc
	 *            the fc
	 * @return the int
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static int loadInt(FileChannel fc) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(INT_NB_BYTES);
		fc.read(bb);
		bb.flip();
		IntBuffer ib = bb.asIntBuffer();
		return ib.get();
	}
	
	/**
	 * Load boolean.
	 * 
	 * @param fc
	 *            the fc
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static boolean loadBoolean(FileChannel fc) throws IOException {
		return loadInt(fc) == 1;
	}

	/**
	 * Load sparse vector signature.
	 * 
	 * @param fc
	 *            the fc
	 * @return the sparse vector signature
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static SparseVectorSignature loadSparseVectorSignature(FileChannel fc) throws IOException {
		try {
			int sz = loadInt(fc);
			int nzb = loadInt(fc);

			SparseVectorSignature v = new SparseVectorSignature(sz);

			ByteBuffer bb1 = ByteBuffer.allocate(INT_NB_BYTES * nzb);
			fc.read(bb1);
			bb1.flip();
			IntBuffer ib = bb1.asIntBuffer();
			ib.rewind();

			ByteBuffer bb2 = ByteBuffer.allocate(DOUBLE_NB_BYTES * nzb);
			fc.read(bb2);
			bb2.flip();
			DoubleBuffer db = bb2.asDoubleBuffer();
			db.rewind();

			while (db.hasRemaining() && ib.hasRemaining()) {
				v.set(ib.get(), db.get());
			}

			if (db.hasRemaining() || ib.hasRemaining()) {
				throw new IOException("IntBuffer and DoubleBuffer sizes mismatch");
			}

			return v;
		} catch (SignatureException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Load string.
	 * 
	 * @param fc
	 *            the fc
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String loadString(FileChannel fc) throws IOException {
		int sz = loadInt(fc);
		ByteBuffer bb = ByteBuffer.allocate(sz);
		fc.read(bb);
		bb.flip();
		CharBuffer cb = csd.decode(bb);
		return cb.toString();
	}

	/**
	 * Load vector signature.
	 * 
	 * @param fc
	 *            the fc
	 * @return the vector signature
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static VectorSignature loadVectorSignature(FileChannel fc) throws IOException {
		int type = loadInt(fc);
		switch (type) {
		case NULL_TYPE:
			return null;
		case DENSE_TYPE:
			return loadDenseVectorSignature(fc);
		case SPARSE_TYPE:
			return loadSparseVectorSignature(fc);
		}
		throw new IOException("Unknown VectorSignature type (" + type + ")");
	}
}
