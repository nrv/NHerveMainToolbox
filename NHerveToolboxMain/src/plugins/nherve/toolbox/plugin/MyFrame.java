package plugins.nherve.toolbox.plugin;

import icy.gui.frame.IcyFrame;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class MyFrame extends IcyFrame {
	private class MyLogo extends JPanel {
		private static final long serialVersionUID = -3599856522326379063L;

		final String title;
		final Font titleFont;

		public MyLogo(String title, Dimension dim) {
			super();
			this.title = title;
			setPreferredSize(dim);
			titleFont = new Font("Arial", Font.BOLD, 20);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			final int w = getWidth();
			final int h = getHeight();

			final Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


			final float ray = Math.max(w, h) * 0.05f;
			final RoundRectangle2D roundRect = new RoundRectangle2D.Double(0, 0, w, h, Math.min(ray * 2, 20), Math.min(ray * 2, 20));
			g2.setPaint(new GradientPaint(0, 0, Color.white.darker(), 0, h / 1.5f, Color.black));
			g2.fill(roundRect);

			g2.setPaint(Color.black);
			g2.setColor(Color.black);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			g2.fillOval(-w + (w / 2), h / 2, w * 2, h);

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			g2.setStroke(new BasicStroke(Math.max(1f, Math.min(5f, ray))));
			g2.draw(roundRect);

			g2.setColor(Color.white);
			g2.setFont(titleFont);
			Rectangle2D ts = g2.getFontMetrics().getStringBounds(title, g2);
	        g2.drawString(title, (int)((w - ts.getWidth()) / 2), (int)(h - ts.getHeight() / 2));

			g2.dispose();
		}
	}
	
	public static MyFrame create(SingletonPlugin plugin) {
		return create(plugin, true, true, true, true);
	}

	public static MyFrame create(SingletonPlugin plugin, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
		final MyFrame result = new MyFrame(plugin.getName(), plugin.getFullName(), new Dimension(400, 50), resizable, closable, maximizable, iconifiable);
		result.setVisible(true);

		return result;
	}

	protected final JPanel internalPanel;

	public MyFrame(String title, String fullTitle, Dimension dim, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
		super(fullTitle, resizable, closable, maximizable, iconifiable);

		setLayout(new BorderLayout());
		
		add(new MyLogo(title, dim), BorderLayout.NORTH);
		
		internalPanel = new JPanel();
		internalPanel.setOpaque(false);
		internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.PAGE_AXIS));
		internalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(internalPanel, BorderLayout.CENTER);
	}

	public JPanel getMainPanel() {
		return internalPanel;
	}
}
