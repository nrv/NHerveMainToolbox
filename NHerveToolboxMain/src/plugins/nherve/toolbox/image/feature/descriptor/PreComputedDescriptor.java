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
package plugins.nherve.toolbox.image.feature.descriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import plugins.nherve.toolbox.image.db.ImageDatabase;
import plugins.nherve.toolbox.image.db.ImageEntry;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;
import plugins.nherve.toolbox.image.feature.signature.DefaultVectorSignature;


/**
 * The Class PreComputedDescriptor.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class PreComputedDescriptor extends DefaultDescriptorImpl<ImageEntry, DefaultVectorSignature> implements GlobalDescriptor<ImageEntry, DefaultVectorSignature> {
	
	/** The file. */
	private String file;
	
	/** The sig size. */
	private int sigSize;
	
	/** The sigs. */
	private Map<String, DefaultVectorSignature> sigs;

	/**
	 * Instantiates a new pre computed descriptor.
	 * 
	 * @param file
	 *            the file
	 * @param display
	 *            the display
	 */
	public PreComputedDescriptor(String file, boolean display) {
		super(display);
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#getSignatureSize()
	 */
	@Override
	public int getSignatureSize() {
		return sigSize;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#toString()
	 */
	@Override
	public String toString() {
		return "PreComputedDescriptor(" + file + ")";
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.DefaultDescriptorImpl#initForDatabase(plugins.nherve.toolbox.image.db.ImageDatabase)
	 */
	@Override
	public void initForDatabase(ImageDatabase db) throws SignatureException {
		RandomAccessFile raf = null;
		try {
			super.initForDatabase(db);
			File f = new File(db.getRootDirectory(), file);
			raf = new RandomAccessFile(f, "r");
			String firstLine = raf.readLine();
			StringTokenizer tkz = new StringTokenizer(firstLine, " ");
			int nbImages = Integer.parseInt(tkz.nextToken());
			sigSize = Integer.parseInt(tkz.nextToken());
			
			sigs = new HashMap<String, DefaultVectorSignature>();
			
			for (int i = 0; i < nbImages; i++) {
				String line = raf.readLine();
				tkz = new StringTokenizer(line, " ");
				String imageFile = tkz.nextToken();
				if (imageFile.startsWith(db.getRootImageDirectory())) {
					imageFile = imageFile.substring(db.getRootImageDirectory().length() + 1);
				} else {
					throw new SignatureException(this.toString() + " - directory mismatch : " + imageFile + " / " + db.getRootImageDirectory());
				}
				DefaultVectorSignature sig = getEmptySignature();
				for (int d = 0; d < sigSize; d++) {
					sig.set(d, Double.parseDouble(tkz.nextToken()));
				}
				sigs.put(imageFile, sig);
			}
			
		} catch (FileNotFoundException e) {
			throw new SignatureException(e);
		} catch (IOException e) {
			throw new SignatureException(e);
		} catch (NullPointerException e) {
			throw new SignatureException(e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.GlobalDescriptor#extractGlobalSignature(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public DefaultVectorSignature extractGlobalSignature(ImageEntry img) throws SignatureException {
		return sigs.get(img.getFile());
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#needToLoadSegmentable()
	 */
	@Override
	public boolean needToLoadSegmentable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#postProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void postProcess(ImageEntry img) throws SignatureException {

	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.Descriptor#preProcess(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public void preProcess(ImageEntry img) throws SignatureException {

	}

}
