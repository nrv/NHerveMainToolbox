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

import plugins.nherve.toolbox.image.feature.Descriptor;
import plugins.nherve.toolbox.image.feature.Segmentable;
import plugins.nherve.toolbox.image.feature.Signature;
import plugins.nherve.toolbox.image.feature.signature.SignatureException;

/**
 * The Interface GlobalDescriptor.
 * 
 * @param <T>
 *            the generic type
 * @param <S>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface GlobalDescriptor<T extends Segmentable, S extends Signature> extends Descriptor<T> {
	
	/**
	 * Extract global signature.
	 * 
	 * @param img
	 *            the img
	 * @return the s
	 * @throws SignatureException
	 *             the signature exception
	 */
	S extractGlobalSignature(T img) throws SignatureException;
}
