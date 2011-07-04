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

/**
 * The Class Algorithm.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class Algorithm implements AbleToLogMessages {
	
	private boolean log;
	private boolean uiDisplay;
	
	/**
	 * Instantiates a new algorithm.
	 * 
	 * @param display
	 *            the display
	 */
	public Algorithm(boolean display) {
		super();
		setLogEnabled(display);
	}
	
	/**
	 * Instantiates a new algorithm.
	 */
	public Algorithm() {
		this(false);
	}
		
	@Override
	public boolean isLogEnabled() {
		return log;
	}
	
	@Override
	public void setLogEnabled(boolean log) {
		this.log = log;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#log(java.lang.String)
	 */
	@Override
	public void log(String message) {
		if (isLogEnabled()) {
			System.out.println(message);
		}
	}
	
	/**
	 * Log.
	 */
	public void log() {
		log("");
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#logWarning(java.lang.String)
	 */
	@Override
	public void logWarning(String message) {
		System.err.println("WARNING : " + message);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#logError(java.lang.String)
	 */
	@Override
	public void logError(String message) {
		System.err.println("ERROR : " + message);
	}

	@Override
	public boolean isUIDisplayEnabled() {
		return uiDisplay;
	}

	@Override
	public void setUIDisplayEnabled(boolean uiDisplay) {
		this.uiDisplay = uiDisplay;
	}

	@Override
	public void clearDisplay() {
	}

	@Override
	public void displayMessage(String message) {
		if (isUIDisplayEnabled()) {
			System.out.println(message);
		}
	}
	
}
