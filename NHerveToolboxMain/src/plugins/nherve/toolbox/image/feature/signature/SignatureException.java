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
package plugins.nherve.toolbox.image.feature.signature;

import plugins.nherve.toolbox.image.feature.FeatureException;

/**
 * The Class SignatureException.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SignatureException extends FeatureException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8090763616946786075L;

	/**
	 * Instantiates a new signature exception.
	 */
	public SignatureException() {
	}

	/**
	 * Instantiates a new signature exception.
	 * 
	 * @param message
	 *            the message
	 */
	public SignatureException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new signature exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public SignatureException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new signature exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public SignatureException(String message, Throwable cause) {
		super(message, cause);
	}

}
