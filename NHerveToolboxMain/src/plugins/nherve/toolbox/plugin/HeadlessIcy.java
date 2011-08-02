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

import icy.gui.frame.IcyExternalFrame;
import icy.gui.frame.IcyFrameAdapter;
import icy.gui.frame.IcyFrameEvent;
import icy.gui.util.LookAndFeelUtil;
import icy.preferences.IcyPreferences;
import icy.system.thread.ThreadUtil;

import javax.swing.JFrame;


public class HeadlessIcy extends IcyFrameAdapter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage : HeadlessIcy singleton.plugin.class");
			return;
		}
		
		String className = args[0];
		try {
			Class<?> clazz = Class.forName(className);
			SingletonPlugin myPlugin = (SingletonPlugin) clazz.newInstance();

			HeadlessIcy icy = new HeadlessIcy();
			icy.start(myPlugin);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void start(final SingletonPlugin myPlugin) {
		IcyPreferences.init();
		LookAndFeelUtil.init();
		
		ThreadUtil.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
            	myPlugin.setRunningHeadless(true);
            	myPlugin.compute();
        		
        		MyFrame myFrame = myPlugin.getFrame();
        		myFrame.addFrameListener(HeadlessIcy.this);
        		
        		JFrame frame = (IcyExternalFrame) myFrame.getFrame();
        		
        		frame.pack();
        		frame.setVisible(true);
            }
        });
		
	}

	@Override
	public void icyFrameClosed(IcyFrameEvent e) {
		IcyPreferences.save();
	}

}
