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
import icy.gui.util.WindowPositionSaver;
import icy.main.Icy;
import icy.plugin.abstract_.Plugin;
import icy.plugin.interface_.PluginImageAnalysis;
import icy.preferences.PluginsPreferences;
import icy.preferences.XMLPreferences;
import icy.sequence.Sequence;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;

import plugins.nherve.toolbox.AbleToLogMessages;
import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.genericgrid.WaitingAnimation;

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

	protected static SingletonPlugin getInstance(Class<? extends SingletonPlugin> clazz) {
		return singletons.get(clazz);
	}
	
	private Sequence currentSequence;

	private boolean log;
	private JPanel mainPanel;
	private MyFrame myFrame;
	private boolean runningHeadless;
	
	private boolean uiDisplay;
//	private Cursor backupCursor;
	public SingletonPlugin() {
		super();
		
		setRunningHeadless(false);
	}

	protected void beforeDisplayInterface(JPanel mainPanel) {
	}

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
	public void compute() {
		if (!singletons.containsKey(getClass())) {
			singletons.put(getClass(), this);
			startPlugin();
		}
	}

	public void disableWaitingCursor() {
//		mainPanel.setCursor(backupCursor);
		mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void displayMessage(String message) {
		if (isUIDisplayEnabled()) {
			Algorithm.out(message);
		}
	}

	public void enableWaitingCursor() {
//		backupCursor = mainPanel.getCursor();
		mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	public abstract void fillInterface(JPanel mainPanel);

	/**
	 * Gets the current sequence.
	 * 
	 * @return the current sequence
	 */
	public Sequence getCurrentSequence() {
		return currentSequence;
	}

	public abstract Dimension getDefaultFrameDimension();

	public String getDefaultVersion() {
		return "unknown";
	}

	public MyFrame getFrame() {
		return myFrame;
	}

	public String getFullName() {
		return getName() + " " + getVersion();
	}

	public String getName() {
		return getDescriptor().getName();
	}
	
	public XMLPreferences getPreferences() {
		// TODO patch en attendant version suivante ICY
		PluginsPreferences.load();
		return PluginsPreferences.root(this);
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

	public String getVersion() {
		String v = getDescriptor().getVersion().toString();
		if (v.length() == 0) {
			return getDefaultVersion();
		}
		return v;
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

	public boolean isRunningHeadless() {
		return runningHeadless;
	}

	@Override
	public boolean isUIDisplayEnabled() {
		return uiDisplay;
	}

	/**
	 * Log.
	 */
	public void info() {
		info("");
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#log(java.lang.String)
	 */
	@Override
	public void info(String message) {
		if (isLogEnabled()) {
			Algorithm.out(message);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#logError(java.lang.String)
	 */
	@Override
	public void error(String message) {
		Algorithm.err("ERROR : " + message);
	}

	@Override
	public void error(Throwable e) {
		Algorithm.err("ERROR : " + e.getClass().getName() + " : " + e.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.AbleToLogMessages#logWarning(java.lang.String)
	 */
	@Override
	public void warn(String message) {
		Algorithm.err("WARNING : " + message);
	}
	
	protected void openHelpWindow(String text, int w, int h) {
		new HelpWindow(this, myFrame, text, w, h);
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

	public void setRunningHeadless(boolean runningHeadless) {
		this.runningHeadless = runningHeadless;
	}
	
	public void setTitle(String title) {
		myFrame.setTitle(title);
	}
	
	@Override
	public void setUIDisplayEnabled(boolean uiDisplay) {
		this.uiDisplay = uiDisplay;
	}
	
	protected void startInterface() {
		Dimension dd = getDefaultFrameDimension();
		
		if (dd == null) {
			myFrame = MyFrame.create(this, false, true, false, true);
		} else {
			myFrame = MyFrame.create(this, true, true, true, true);
		}
		
		SubstanceSkin skin = SubstanceLookAndFeel.getCurrentSkin();
		DecorationAreaType deco = SubstanceLookAndFeel.getDecorationType(getFrame().getInternalFrame());
		SubstanceColorScheme cs = skin.getActiveColorScheme(deco);
		Color darkColor = cs.getUltraDarkColor();
		WaitingAnimation.initAnimationImages(darkColor);
		
		mainPanel= myFrame.getMainPanel();
		addIcyFrame(myFrame);
		
	
		fillInterface(mainPanel);

		if (isRunningHeadless()) {
			myFrame.externalize();
		}
		
		new WindowPositionSaver(myFrame, getPreferences().absolutePath(), new Point(0, 0), dd);
		
		if (isRunningHeadless()) {
			myFrame.externalize();
		}
		
		myFrame.addFrameListener(this);
		myFrame.setVisible(true);
		myFrame.pack();
		
		beforeDisplayInterface(mainPanel);
		
		myFrame.requestFocus();
	}
	
	/**
	 * Start plugin.
	 */
	protected void startPlugin() {
		startInterface();
		Icy.getMainInterface().addListener(this);
		changeSequence();
	}

	public abstract void stopInterface();

	/**
	 * Stop plugin.
	 */
	protected void stopPlugin() {
		singletons.remove(getClass());

		myFrame.removeAll();
		myFrame = null;
		mainPanel = null;

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
}
