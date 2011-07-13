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
package plugins.nherve.toolbox.image.mask;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.PersistenceException;
import plugins.nherve.toolbox.PersistenceToolbox;
import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;

/**
 * The Class MaskPersistenceImpl.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class MaskPersistenceImpl extends Algorithm implements MaskPersistence {
	
	/** The Constant MASK_FILE_EXTENSION. */
	private final static String MASK_FILE_EXTENSION = ".segz";
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistence#loadMaskStack(java.io.File)
	 */
	@Override
	public MaskStack loadMaskStack(File f) throws PersistenceException {
		RandomAccessFile raf = null;
		try {
			raf = PersistenceToolbox.getFile(f, false);
			return loadMaskStack(raf.getChannel());
		} catch (FileNotFoundException e) {
			throw new PersistenceException(e);
		} catch (IOException e) {
			throw new PersistenceException(e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistence#save(plugins.nherve.toolbox.image.mask.MaskStack, java.io.File)
	 */
	@Override
	public void save(MaskStack stack, File f) throws PersistenceException {
		RandomAccessFile raf = null;
		try {
			raf = PersistenceToolbox.getFile(f, true);
			FileChannel fc = raf.getChannel();
			dumpMaskStack(fc, stack);
		} catch (FileNotFoundException e) {
			throw new PersistenceException(e);
		} catch (IOException e) {
			throw new PersistenceException(e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistence#getMaskFileExtension()
	 */
	@Override
	public String getMaskFileExtension() {
		return MASK_FILE_EXTENSION;
	}

	/**
	 * Dump mask.
	 * 
	 * @param fc
	 *            the fc
	 * @param mask
	 *            the mask
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void dumpMask(FileChannel fc, Mask mask) throws IOException {
		PersistenceToolbox.dumpInt(fc, mask.getHeight());
		PersistenceToolbox.dumpInt(fc, mask.getWidth());
		PersistenceToolbox.dumpInt(fc, mask.getId());
		PersistenceToolbox.dumpFloat(fc, mask.getOpacity());
		PersistenceToolbox.dumpBoolean(fc, mask.isNeedAutomaticLabel());
		PersistenceToolbox.dumpBoolean(fc, mask.isVisibleLayer());
		PersistenceToolbox.dumpString(fc, mask.getLabel());
		PersistenceToolbox.dumpInt(fc, mask.getColor().getRed());
		PersistenceToolbox.dumpInt(fc, mask.getColor().getGreen());
		PersistenceToolbox.dumpInt(fc, mask.getColor().getBlue());
		dumpBinaryIcyBufferedImage(fc, mask.getBinaryData());
	}

	/**
	 * Load mask.
	 * 
	 * @param fc
	 *            the fc
	 * @return the mask
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected Mask loadMask(FileChannel fc, int v) throws IOException {
		int h = PersistenceToolbox.loadInt(fc);
		int w = PersistenceToolbox.loadInt(fc);
		Mask m = new Mask(w, h);
		m.setId(PersistenceToolbox.loadInt(fc));
		m.setOpacity(PersistenceToolbox.loadFloat(fc));
		m.setNeedAutomaticLabel(PersistenceToolbox.loadBoolean(fc));
		m.setVisibleLayer(PersistenceToolbox.loadBoolean(fc));
		m.setLabel(PersistenceToolbox.loadString(fc));
		Color c = new Color(PersistenceToolbox.loadInt(fc), PersistenceToolbox.loadInt(fc), PersistenceToolbox.loadInt(fc));
		m.setColor(c);
		m.setBinaryData(loadBinaryIcyBufferedImage(fc, v));
		return m;
	}

	/**
	 * Dump mask stack.
	 * 
	 * @param fc
	 *            the fc
	 * @param stack
	 *            the stack
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void dumpMaskStack(FileChannel fc, MaskStack stack) throws IOException {
		PersistenceToolbox.dumpInt(fc, stack.getHeight());
		PersistenceToolbox.dumpInt(fc, stack.getWidth());
		PersistenceToolbox.dumpInt(fc, stack.getActiveIndex());
		PersistenceToolbox.dumpInt(fc, stack.size());
		for (Mask m : stack) {
			dumpMask(fc, m);
		}
	}

	/**
	 * Load mask stack.
	 * 
	 * @param fc
	 *            the fc
	 * @return the mask stack
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected MaskStack loadMaskStack(FileChannel fc) throws IOException {
		return loadMaskStack(fc, -1);
	}
	
	protected MaskStack loadMaskStack(FileChannel fc, int v) throws IOException {
		int h = PersistenceToolbox.loadInt(fc);
		int w = PersistenceToolbox.loadInt(fc);
		MaskStack res = new MaskStack(w, h);
		res.setActiveIndex(PersistenceToolbox.loadInt(fc));
		
		int sz = PersistenceToolbox.loadInt(fc);
		for (int i = 0; i < sz; i++) {
			res.add(loadMask(fc, v));
		}
		
		return res;
	}

	/**
	 * Load binary icy buffered image.
	 * 
	 * @param fc
	 *            the fc
	 * @return the binary icy buffered image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected BinaryIcyBufferedImage loadBinaryIcyBufferedImage(FileChannel fc, int v) throws IOException {
		int h = PersistenceToolbox.loadInt(fc);
		int w = PersistenceToolbox.loadInt(fc);
		int sz = PersistenceToolbox.loadInt(fc);
		ByteBuffer bb = ByteBuffer.allocate(sz);
		fc.read(bb);
		bb.flip();
		byte[] data = new byte[sz];
		bb.rewind();
		while (bb.hasRemaining()) {
			data[bb.position()] = bb.get();
		}
		BinaryIcyBufferedImage bin = new BinaryIcyBufferedImage(w, h);
		bin.setDataXYAsByte(0, data);
		return bin;
	}

	/**
	 * Dump binary icy buffered image.
	 * 
	 * @param fc
	 *            the fc
	 * @param bin
	 *            the bin
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void dumpBinaryIcyBufferedImage(FileChannel fc, BinaryIcyBufferedImage bin) throws IOException {
		PersistenceToolbox.dumpInt(fc, bin.getHeight());
		PersistenceToolbox.dumpInt(fc, bin.getWidth());
		byte[] raw = bin.getRawData();
		PersistenceToolbox.dumpInt(fc, raw.length);
		ByteBuffer bb = ByteBuffer.allocate(raw.length);
		bb.put(raw);
		bb.flip();
		fc.write(bb);
	}

	@Override
	public File getMaskFileFor(File image) {
		String name = image.getAbsolutePath();
		File result = null;
		if ((name != null) && (name.length() > 0)) {
			int idx = name.lastIndexOf(".");
			if (idx > 0) {
				name = name.substring(0, idx);
			}
			name += getMaskFileExtension();
			result = new File(name);
		}
		return result;
	}

}
