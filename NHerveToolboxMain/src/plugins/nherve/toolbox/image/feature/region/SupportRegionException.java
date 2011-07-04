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
package plugins.nherve.toolbox.image.feature.region;

import plugins.nherve.toolbox.image.feature.FeatureException;

/**
 * The Class SupportRegionException.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class SupportRegionException extends FeatureException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6983020810797334805L;

	/**
	 * Instantiates a new support region exception.
	 */
	public SupportRegionException() {
	}

	/**
	 * Instantiates a new support region exception.
	 * 
	 * @param message
	 *            the message
	 */
	public SupportRegionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new support region exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public SupportRegionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new support region exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public SupportRegionException(String message, Throwable cause) {
		super(message, cause);
	}

}
