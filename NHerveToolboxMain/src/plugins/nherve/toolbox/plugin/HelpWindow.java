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

public class HelpWindow extends IcyFrame implements HyperlinkListener, IcyFrameListener {
	public static final String TAG_PLUGIN_NAME = "{{PLUGIN_NAME}}";
	public static final String TAG_FULL_PLUGIN_NAME = "{{FULL_PLUGIN_NAME}}";
	
	HelpWindow(SingletonPlugin plugin, MyFrame frame, String htmlText, int w, int h) {
		super(plugin.getName() + " Help", false, true, false, false);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		JEditorPane helpEditorPane = new JEditorPane("text/html", htmlText);
		helpEditorPane.setEditable(false);
		helpEditorPane.setCaretPosition(0);

		helpEditorPane.addHyperlinkListener(this);

		setSize(w, h);

		add(new JScrollPane(helpEditorPane));

		frame.addFrameListener(this);
		
		setVisible(true);
		center();
		addToMainDesktopPane();
		requestFocus();
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
						System.err.println("Unable to open external browser for " + urlString);
					}
				}

				catch (Throwable excep) {
					System.err.println(excep.getClass().getName() + " : " + excep.getMessage());
				}
			} else {
				System.err.println("No URL on this link");
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

}
