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
