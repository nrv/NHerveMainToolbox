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

import icy.sequence.Sequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import plugins.adufour.connectedcomponents.ConnectedComponent;
import plugins.adufour.connectedcomponents.ConnectedComponents;

/**
 * The Class My2DConnectedComponentFinder.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class My2DConnectedComponentFinder implements Iterable<My2DConnectedComponent> {
	
	/** The found objects. */
	private ArrayList<My2DConnectedComponent> foundObjects;
	
	/**
	 * Instantiates a new my2 d connected component finder.
	 * 
	 * @param binaryData
	 *            the binary data
	 * @param minVolume
	 *            the min volume
	 * @param maxVolume
	 *            the max volume
	 */
	public My2DConnectedComponentFinder(BinaryIcyBufferedImage binaryData, int minVolume, int maxVolume) {
		super();
		
		foundObjects = new ArrayList<My2DConnectedComponent>();
		List<ConnectedComponent> ccs = ConnectedComponents.extractConnectedComponents(new Sequence(binaryData), minVolume, maxVolume, null).get(0);
		
		int id = 0;
		for (ConnectedComponent cc : ccs) {
			foundObjects.add(new My2DConnectedComponent(id, cc));
			id++;
		}
	}

	/**
	 * Gets the component.
	 * 
	 * @param index
	 *            the index
	 * @return the component
	 * @throws ArrayIndexOutOfBoundsException
	 *             the array index out of bounds exception
	 */
	public My2DConnectedComponent getComponent(int index) throws ArrayIndexOutOfBoundsException {
		return foundObjects.get(index);
	}

	/**
	 * Gets the nb objects.
	 * 
	 * @return the nb objects
	 */
	public int getNbObjects() {
		return foundObjects.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<My2DConnectedComponent> iterator() {
		return foundObjects.iterator();
	}
}
