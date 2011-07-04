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

import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.region.FullImageSupportRegion;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;

/**
 * The Class GlobalAndLocalDescriptor.
 * 
 * @param <T>
 *            the generic type
 * @param <S>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class GlobalAndLocalDescriptor<T extends Segmentable, S extends Signature> extends DefaultDescriptorImpl<T, S> implements GlobalDescriptor<T, S>, LocalDescriptor<T, S> {
	
	/**
	 * Instantiates a new global and local descriptor.
	 * 
	 * @param display
	 *            the display
	 */
	public GlobalAndLocalDescriptor(boolean display) {
		super(display);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.image.feature.descriptor.GlobalDescriptor#extractGlobalSignature(plugins.nherve.toolbox.image.feature.Segmentable)
	 */
	@Override
	public S extractGlobalSignature(T img) throws SignatureException {
		return extractLocalSignature(img, new FullImageSupportRegion(img));
	}
}
