package plugins.nherve.toolbox.plugin;

import icy.gui.frame.IcyFrame;
import icy.gui.frame.IcyFrameEvent;
import icy.gui.frame.IcyFrameListener;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import plugins.nherve.toolbox.Algorithm;

public class HelpWindow extends IcyFrame implements HyperlinkListener, IcyFrameListener {
	private static final String TAG_PLUGIN_NAME = "__PLUGIN_NAME__";
	private static final String TAG_FULL_PLUGIN_NAME = "__FULL_PLUGIN_NAME__";

	HelpWindow(SingletonPlugin plugin, MyFrame frame, String htmlText, int w, int h) {
		super(plugin.getName() + " Help", false, true, false, false);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		JEditorPane helpEditorPane = new JEditorPane("text/html", setVariables(plugin, htmlText));
		helpEditorPane.setEditable(false);
		helpEditorPane.setCaretPosition(0);

		helpEditorPane.addHyperlinkListener(this);

		setSize(w, h);

		add(new JScrollPane(helpEditorPane));

		frame.addFrameListener(this);

		if (plugin.isRunningHeadless()) {
			externalize();
		}

		setVisible(true);
		center();
		addToMainDesktopPane();
		requestFocus();
	}

	private String setVariables(SingletonPlugin plugin, String t) {
		String result = t.replaceAll(TAG_PLUGIN_NAME, plugin.getName());
		result = result.replaceAll(TAG_FULL_PLUGIN_NAME, plugin.getFullName());
		return result;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		HyperlinkEvent.EventType type = e.getEventType();
		if (type == HyperlinkEvent.EventType.ACTIVATED) {
			String urlString = e.getURL().toExternalForm();
			if (urlString != null) {
				try {
					if (Desktop.isDesktopSupported()) {
						URI uri = new URI(urlString);
						Desktop.getDesktop().browse(uri);
					} else {
						Algorithm.err("Unable to open external browser for " + urlString);
					}
				}

				catch (Throwable excep) {
					Algorithm.err(excep);
				}
			} else {
				Algorithm.err("No URL on this link");
			}
		}
	}

	@Override
	public void icyFrameOpened(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameClosing(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameClosed(IcyFrameEvent e) {
		this.close();
	}

	@Override
	public void icyFrameIconified(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameDeiconified(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameActivated(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameDeactivated(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameInternalized(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameExternalized(IcyFrameEvent e) {
	}

	public static String getTagPluginName() {
		return TAG_PLUGIN_NAME;
	}

	public static String getTagFullPluginName() {
		return TAG_FULL_PLUGIN_NAME;
	}

}
