package plugins.nherve.toolbox.plugin;

import icy.gui.frame.IcyFrame;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;

public class MyFrame extends IcyFrame {
	private class MyLogo extends JPanel {
		private static final long serialVersionUID = -3599856522326379063L;

		final String pluginName;
		final String pluginVersion;
		final Font nameFont;
		final Font versionFont;
		final Image logo;

		public MyLogo(String pluginName, String pluginVersion, Dimension dim, Image logo) {
			super();
			this.pluginName = pluginName;
			this.pluginVersion = pluginVersion;
			this.logo = logo;
			setPreferredSize(dim);
			nameFont = new Font("Arial", Font.BOLD, 20);
			versionFont = new Font("Arial", Font.ITALIC, 10);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			final int w = getWidth();
			final int h = getHeight();

			final Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			SubstanceSkin skin = SubstanceLookAndFeel.getCurrentSkin();
			DecorationAreaType deco = SubstanceLookAndFeel.getDecorationType(getInternalFrame());
			SubstanceColorScheme cs = skin.getActiveColorScheme(deco);

			Color lightColor = cs.getLightColor();
			Color darkColor = cs.getUltraDarkColor();
			
			Point2D center = new Point2D.Float(w / 2, h / 2);
			float radius = (float) w / 1.7f;
			float[] dist = { 0.1f, 0.7f };
			Color[] colors = { lightColor, darkColor };
			RadialGradientPaint gradient = new RadialGradientPaint(center, radius, dist, colors);

			final float ray = Math.max(w, h) * 0.05f;
			final RoundRectangle2D roundRect = new RoundRectangle2D.Double(0, 0, w, h, Math.min(ray * 2, 20), Math.min(ray * 2, 20));
			g2.setPaint(gradient);
			g2.fill(roundRect);

			g2.setColor(darkColor);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.setStroke(new BasicStroke(Math.max(1f, 10f)));
			g2.draw(roundRect);

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			g2.setColor(darkColor.darker().darker());
			g2.setFont(nameFont);
			Rectangle2D ts = g2.getFontMetrics().getStringBounds(pluginName, g2);
			g2.drawString(pluginName, (int) ((w - ts.getWidth()) / 2), (int) ((h + ts.getHeight()) / 2));
			
			g2.setColor(lightColor.brighter().brighter());
			g2.setFont(versionFont);
			ts = g2.getFontMetrics().getStringBounds(pluginVersion, g2);
			g2.drawString(pluginVersion, (int) (w - ts.getWidth() - 5), h - 5);
			
			if (logo != null) {
				g2.drawImage (logo, 5, 5, null);
			}

			g2.dispose();
		}
	}

	public static MyFrame create(SingletonPlugin plugin) {
		return create(plugin, plugin.getFullName());
	}
	
	public static MyFrame create(SingletonPlugin plugin, String title) {
		return create(plugin, title, true, true, true, true, true);
	}

	public static MyFrame create(SingletonPlugin plugin, String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, boolean showLogo) {
		Image logo = showLogo ? plugin.getDescriptor().getIconAsImage() : null;
		final MyFrame result = new MyFrame(plugin.getName(), plugin.getVersion(), title, new Dimension(400, 74), resizable, closable, maximizable, iconifiable, logo);
		result.setVisible(true);
		return result;
	}
	
	public static MyFrame create(SingletonPlugin plugin, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
		return create(plugin, plugin.getName(), resizable, closable, maximizable, iconifiable, true);
	}

	protected final JPanel internalPanel;

	public MyFrame(String pluginName, String pluginVersion, String title, Dimension dim, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, Image logo) {
		super(title, resizable, closable, maximizable, iconifiable);

		setLayout(new BorderLayout());

		add(new MyLogo(pluginName, pluginVersion, dim, logo), BorderLayout.NORTH);

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
