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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class Algorithm.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class Algorithm implements AbleToLogMessages {
	
	private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
	
	private boolean log;
	private boolean uiDisplay;
	private boolean logTime;
	
	/**
	 * Instantiates a new algorithm.
	 * 
	 * @param display
	 *            the display
	 */
	public Algorithm(boolean display) {
		super();
		setLogEnabled(display);
		setLogTime(true);
	}
	
	public static void out(String msg) {
		System.out.println(msg);
	}
	
	public static void err(String msg) {
		System.err.println(msg);
	}
	
	public static void err(Throwable e) {
		err(e.getClass().getName() + " : " + e.getMessage());
	}
	
	public static void outWithTime(String msg) {
		out("[" + df.format(new Date()) + "] " + msg);
	}
	
	public static void errWithTime(String msg) {
		err("[" + df.format(new Date()) + "] " + msg);
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
			if (isLogTime()) {
				outWithTime(message);
			} else {
				out(message);
			}
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
		if (isLogTime()) {
			errWithTime("WARNING : " + message);
		} else {
			err("WARNING : " + message);
		}
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#logError(java.lang.String)
	 */
	@Override
	public void logError(String message) {
		if (isLogTime()) {
			errWithTime("ERROR : " + message);
		} else {
			err("ERROR : " + message);
		}
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
			if (isLogTime()) {
				outWithTime(message);
			} else {
				out(message);
			}
		}
	}

	public boolean isLogTime() {
		return logTime;
	}

	public void setLogTime(boolean logTime) {
		this.logTime = logTime;
	}
	
}
