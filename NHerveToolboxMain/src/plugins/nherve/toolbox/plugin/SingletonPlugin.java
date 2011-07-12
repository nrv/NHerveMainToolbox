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
package plugins.nherve.toolbox.plugin;

import icy.gui.frame.IcyFrameEvent;
import icy.gui.frame.IcyFrameListener;
import icy.gui.main.MainEvent;
import icy.gui.main.MainListener;
import icy.main.Icy;
import icy.plugin.abstract_.Plugin;
import icy.plugin.interface_.PluginImageAnalysis;
import icy.preferences.PluginsPreferences;
import icy.preferences.XMLPreferences;
import icy.sequence.Sequence;

import java.util.HashMap;
import java.util.Map;

import plugins.nherve.toolbox.AbleToLogMessages;

/**
 * The Class SingletonPlugin.
 * 
 * @author Nicolas HERVE, 2009 This abstract Plugin is intended to manage data
 *         attached to sequences. It is supposed to be run as a singleton and is
 *         aware of sequence focus changes. Therefore, this plugin is able to
 *         work simultaneously on several sequences.
 */
public abstract class SingletonPlugin extends Plugin implements PluginImageAnalysis, MainListener, IcyFrameListener, AbleToLogMessages {
	private final static Map<Class<? extends SingletonPlugin>, SingletonPlugin> singletons = new HashMap<Class<? extends SingletonPlugin>, SingletonPlugin>();
	
	private boolean log;
	private boolean uiDisplay;
	
	/** The current sequence. */
	private Sequence currentSequence;

	/**
	 * Change sequence.
	 */
	protected void changeSequence() {
		sequenceWillChange();

		if (getSequences().size() > 0 || getFocusedSequence() != null) {
			setCurrentSequence(getFocusedSequence());
		} else {
			setCurrentSequence(null);
		}

		sequenceHasChanged();
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

	/**
	 * Gets the current sequence.
	 * 
	 * @return the current sequence
	 */
	public Sequence getCurrentSequence() {
		return currentSequence;
	}

	/**
	 * Gets the sequence by name.
	 * 
	 * @param name
	 *            the name
	 * @return the sequence by name
	 */
	public Sequence getSequenceByName(String name) {
		for (Sequence s : getSequences()) {
			if (s.getName() == name) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Checks for current sequence.
	 * 
	 * @return true, if successful
	 */
	public boolean hasCurrentSequence() {
		return currentSequence != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * icy.gui.frame.IcyFrameListener#icyFrameActivated(icy.gui.frame.IcyFrameEvent
	 * )
	 */
	@Override
	public void icyFrameActivated(IcyFrameEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * icy.gui.frame.IcyFrameListener#icyFrameClosed(icy.gui.frame.IcyFrameEvent
	 * )
	 */
	@Override
	public void icyFrameClosed(IcyFrameEvent e) {
		Icy.getMainInterface().removeListener(this);
		stopPlugin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * icy.gui.frame.IcyFrameListener#icyFrameClosing(icy.gui.frame.IcyFrameEvent
	 * )
	 */
	@Override
	public void icyFrameClosing(IcyFrameEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeicy.gui.frame.IcyFrameListener#icyFrameDeactivated(icy.gui.frame.
	 * IcyFrameEvent)
	 */
	@Override
	public void icyFrameDeactivated(IcyFrameEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeicy.gui.frame.IcyFrameListener#icyFrameDeiconified(icy.gui.frame.
	 * IcyFrameEvent)
	 */
	@Override
	public void icyFrameDeiconified(IcyFrameEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeicy.gui.frame.IcyFrameListener#icyFrameExternalized(icy.gui.frame.
	 * IcyFrameEvent)
	 */
	@Override
	public void icyFrameExternalized(IcyFrameEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * icy.gui.frame.IcyFrameListener#icyFrameIconified(icy.gui.frame.IcyFrameEvent
	 * )
	 */
	@Override
	public void icyFrameIconified(IcyFrameEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeicy.gui.frame.IcyFrameListener#icyFrameInternalized(icy.gui.frame.
	 * IcyFrameEvent)
	 */
	@Override
	public void icyFrameInternalized(IcyFrameEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * icy.gui.frame.IcyFrameListener#icyFrameOpened(icy.gui.frame.IcyFrameEvent
	 * )
	 */
	@Override
	public void icyFrameOpened(IcyFrameEvent e) {
	}

	@Override
	public boolean isLogEnabled() {
		return log;
	}

	@Override
	public boolean isUIDisplayEnabled() {
		return uiDisplay;
	}

	/**
	 * Log.
	 */
	public void log() {
		log("");
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

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#logError(java.lang.String)
	 */
	@Override
	public void logError(String message) {
		System.err.println("ERROR : " + message);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#logWarning(java.lang.String)
	 */
	@Override
	public void logWarning(String message) {
		System.err.println("WARNING : " + message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#painterAdded(icy.gui.main.MainEvent)
	 */
	@Override
	public void painterAdded(MainEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#painterRemoved(icy.gui.main.MainEvent)
	 */
	@Override
	public void painterRemoved(MainEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#pluginClosed(icy.gui.main.MainEvent)
	 */
	@Override
	public void pluginClosed(MainEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#pluginOpened(icy.gui.main.MainEvent)
	 */
	@Override
	public void pluginOpened(MainEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#roiAdded(icy.gui.main.MainEvent)
	 */
	@Override
	public void roiAdded(MainEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#roiRemoved(icy.gui.main.MainEvent)
	 */
	@Override
	public void roiRemoved(MainEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#sequenceClosed(icy.gui.main.MainEvent)
	 */
	@Override
	public void sequenceClosed(MainEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#sequenceFocused(icy.gui.main.MainEvent)
	 */
	@Override
	public void sequenceFocused(MainEvent event) {
		changeSequence();
	}

	/**
	 * Sequence has changed.
	 */
	public abstract void sequenceHasChanged();

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#sequenceOpened(icy.gui.main.MainEvent)
	 */
	@Override
	public void sequenceOpened(MainEvent event) {

	}

	/**
	 * Sequence will change.
	 */
	public abstract void sequenceWillChange();

	/**
	 * Sets the current sequence.
	 * 
	 * @param currentSequence
	 *            the new current sequence
	 */
	protected void setCurrentSequence(Sequence currentSequence) {
		this.currentSequence = currentSequence;
	}
	
	@Override
	public void setLogEnabled(boolean log) {
		this.log = log;
	}
	
	@Override
	public void setUIDisplayEnabled(boolean uiDisplay) {
		this.uiDisplay = uiDisplay;
	}
	
	/**
	 * Start interface.
	 */
	public abstract void startInterface();
	
	/**
	 * Start plugin.
	 */
	protected void startPlugin() {
		startInterface();
		Icy.getMainInterface().addListener(this);
		changeSequence();
	}
	


	/**
	 * Stop plugin.
	 */
	protected void stopPlugin() {
		singletons.remove(getClass());
		stopInterface();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#viewerClosed(icy.gui.main.MainEvent)
	 */
	@Override
	public void viewerClosed(MainEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#viewerFocused(icy.gui.main.MainEvent)
	 */
	@Override
	public void viewerFocused(MainEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see icy.gui.main.MainListener#viewerOpened(icy.gui.main.MainEvent)
	 */
	@Override
	public void viewerOpened(MainEvent event) {

	}

	@Override
	public void compute() {
		if (!singletons.containsKey(getClass())) {
			singletons.put(getClass(), this);
			startPlugin();
		}
	}
	
	protected static SingletonPlugin getInstance(Class<? extends SingletonPlugin> clazz) {
		return singletons.get(clazz);
	}
	
	public abstract void stopInterface();
	
	public XMLPreferences getPreferences() {
		// TODO patch en attendant version suivante ICY
		PluginsPreferences.load();
		return PluginsPreferences.root(this);
	}
}
