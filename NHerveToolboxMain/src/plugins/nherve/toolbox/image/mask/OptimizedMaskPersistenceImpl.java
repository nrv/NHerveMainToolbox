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

import java.io.IOException;
import java.nio.channels.FileChannel;

import plugins.nherve.toolbox.PersistenceToolbox;
import plugins.nherve.toolbox.image.BinaryIcyBufferedImage;

/**
 * The Class OptimizedMaskPersistenceImpl.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class OptimizedMaskPersistenceImpl extends MaskPersistenceImpl {
	
	/** The Constant MASK_FILE_EXTENSION. */
	private final static String MASK_FILE_EXTENSION = ".segz2";
	
	private final static int CURRENT_FILE_VERSION = 1;

	/**
	 * Instantiates a new optimized mask persistence impl.
	 */
	public OptimizedMaskPersistenceImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistenceImpl#getMaskFileExtension()
	 */
	@Override
	public String getMaskFileExtension() {
		return MASK_FILE_EXTENSION;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistenceImpl#dumpBinaryIcyBufferedImage(java.nio.channels.FileChannel, plugins.nherve.toolbox.image.BinaryIcyBufferedImage)
	 */
	@Override
	protected void dumpBinaryIcyBufferedImage(FileChannel fc, BinaryIcyBufferedImage bin) throws IOException {
		PersistenceToolbox.dumpInt(fc, bin.getHeight());
		PersistenceToolbox.dumpInt(fc, bin.getWidth());
		byte[] raw = bin.getRawData();
		final int size = raw.length;
		PersistenceToolbox.dumpInt(fc, size);
		
		int idx = 0;
		byte previous = BinaryIcyBufferedImage.FALSE;
		int count = 0;
		
		while (idx < size) {
			if (raw[idx] == previous) {
				count++;
			} else {
				PersistenceToolbox.dumpInt(fc, count);
				count = 1;
				previous = raw[idx];
			}
			idx++;
		}
		
		if (count > 0) {
			PersistenceToolbox.dumpInt(fc, count);
		}
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistenceImpl#loadBinaryIcyBufferedImage(java.nio.channels.FileChannel)
	 */
	@Override
	protected BinaryIcyBufferedImage loadBinaryIcyBufferedImage(FileChannel fc, int v) throws IOException {
		int h = PersistenceToolbox.loadInt(fc);
		int w = PersistenceToolbox.loadInt(fc);
		int sz = PersistenceToolbox.loadInt(fc);
		byte[] data = new byte[sz];
		
		int idx = 0;
		byte current = BinaryIcyBufferedImage.FALSE;
		while (idx < sz) {
			int count = PersistenceToolbox.loadInt(fc);
			int limit = idx + count;
			while (idx < limit) {
				data[idx] = current;
				idx++;
			}
			current = (current == BinaryIcyBufferedImage.FALSE) ? BinaryIcyBufferedImage.TRUE : BinaryIcyBufferedImage.FALSE;
		}
		
		BinaryIcyBufferedImage bin = new BinaryIcyBufferedImage(w, h);
		bin.setDataXYAsByte(0, data);
		return bin;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistenceImpl#dumpMask(java.nio.channels.FileChannel, plugins.nherve.toolbox.image.mask.Mask)
	 */
	@Override
	protected void dumpMask(FileChannel fc, Mask mask) throws IOException {
		super.dumpMask(fc, mask);
		
		PersistenceToolbox.dumpInt(fc, mask.getNbTags());
		for (String tag : mask) {
			PersistenceToolbox.dumpString(fc, tag);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.mask.MaskPersistenceImpl#loadMask(java.nio.channels.FileChannel)
	 */
	@Override
	protected Mask loadMask(FileChannel fc, int v) throws IOException {
		Mask m = super.loadMask(fc, v);
		
		int n = PersistenceToolbox.loadInt(fc);
		
		for (int i = 0; i < n; i++) {
			m.addTag(PersistenceToolbox.loadString(fc));
		}
		
		return m;
	}

	@Override
	protected void dumpMaskStack(FileChannel fc, MaskStack stack) throws IOException {
		PersistenceToolbox.dumpInt(fc, CURRENT_FILE_VERSION);
		super.dumpMaskStack(fc, stack);
	}

	protected MaskStack loadMaskStack(FileChannel fc) throws IOException {
		int v = PersistenceToolbox.loadInt(fc);
		return super.loadMaskStack(fc, v);
	}

}
