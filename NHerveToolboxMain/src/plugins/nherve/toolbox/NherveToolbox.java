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

import icy.image.ImageUtil;
import icy.plugin.abstract_.Plugin;
import icy.plugin.interface_.PluginLibrary;
import icy.resource.ResourceUtil;
import icy.util.ClassUtil;

import java.awt.Image;
import java.io.InputStream;

import javax.swing.Icon;

/**
 * The Class NherveToolbox.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class NherveToolbox extends Plugin implements PluginLibrary {
	private static final String DEV_NAME = "Nicolas HERVE";
	private static final String DEV_NAME_HTML = "Maintained by <a href=\"http://www.herve.name\">Nicolas HERVE</a>";
	private static final String COPYRIGHT_HTML = "Copyright 2010, 2011 Institut Pasteur.";
	private static final String LICENCE_HTML = "	is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.";
	private static final String LICENCE_HTMLLINK = "See <a href=\"http://www.gnu.org/licenses\">http://www.gnu.org/licenses</a>.";
	
	public static final Icon questionIcon = getIcon("question.png");
	public static final Icon plusIcon = getIcon("plus.png");
	public static final Icon minusIcon = getIcon("minus.png");
	public static final Icon saveIcon = getIcon("save.png");
	public static final Icon loadIcon = getIcon("load.png");
	public static final Icon toSwimingPoolIcon = getIcon("to-sp.png");
	public static final Icon fromSwimingPoolIcon = getIcon("from-sp.png");
	public static final Icon upIcon = getIcon("arrow-up.png");
	public static final Icon downIcon = getIcon("arrow-down.png");
	public static final Icon crossIcon = getIcon("cross.png");
	public static final Icon addIcon = getIcon("plus.png");
	public static final Icon intersectIcon = getIcon("minus.png");
	public static final Icon swimingPoolIcon = getIcon("sp.png");
	public static final Icon playIcon = getIcon("play.png");
	public static final Icon asroiIcon = getIcon("asroi.png");
	public static final Icon dotsIcon = getIcon("dots.png");
	public static final Icon colorsIcon = getIcon("3colors.png");
	public static final Icon roiPlusIcon = getIcon("roi_plus.png");
	public static final Icon roiMinusIcon = getIcon("roi_minus.png");
	public static final Icon toBlackIcon = getIcon("to_black.png");
	public static final Icon toWhiteIcon = getIcon("to_white.png");
	public static final Icon handIcon = getIcon("hand.png");
	public static final Image handImage = getImage("hand.png");
	public static final Icon switchIcon = getIcon("switch.png");
	
	private static Icon getIcon(String file) {
		return ResourceUtil.getImageIcon(getImage(file));
	}
	
	private static Image getImage(String file) {
		String pkg = ClassUtil.getPackageName(NherveToolbox.class.getName()) + ".";
		pkg = ClassUtil.getPathFromQualifiedName(pkg);
		pkg += file;
		InputStream url = NherveToolbox.class.getClassLoader().getResourceAsStream(pkg);
		return ImageUtil.loadImage(url);
	}

	public static String getDevName() {
		return DEV_NAME;
	}

	public static String getDevNameHtml() {
		return DEV_NAME_HTML;
	}

	public static String getCopyrightHtml() {
		return COPYRIGHT_HTML;
	}

	public static String getLicenceHtml() {
		return LICENCE_HTML;
	}

	public static String getLicenceHtmllink() {
		return LICENCE_HTMLLINK;
	}
}
