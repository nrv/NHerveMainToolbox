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
package plugins.nherve.toolbox.image;

import java.util.List;

import javax.vecmath.Point3i;

/**
 * The Interface PointsEnsemble.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface PointsEnsemble {

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public abstract int getHeight();

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public abstract int getId();

	/**
	 * Gets the min x.
	 * 
	 * @return the min x
	 */
	public abstract int getMinX();

	/**
	 * Gets the min y.
	 * 
	 * @return the min y
	 */
	public abstract int getMinY();

	/**
	 * Gets the points.
	 * 
	 * @return the points
	 */
	public abstract List<Point3i> getPoints();

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public abstract int getWidth();

	/**
	 * Gets the x center.
	 * 
	 * @return the x center
	 */
	public abstract int getXCenter();

	/**
	 * Gets the y center.
	 * 
	 * @return the y center
	 */
	public abstract int getYCenter();

}