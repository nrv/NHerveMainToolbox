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

import icy.painter.Painter;
import icy.sequence.Sequence;

/**
 * The Interface PainterManager.
 * 
 * @param <P>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public interface PainterManager<P extends Painter> {
	
	/**
	 * Gets the painter name.
	 * 
	 * @return the painter name
	 */
	String getPainterName();
	
	/**
	 * Gets the painter from.
	 * 
	 * @param seq
	 *            the seq
	 * @return the painter from
	 */
	P getPainterFrom(Sequence seq);
	
	/**
	 * Checks for painter.
	 * 
	 * @param seq
	 *            the seq
	 * @return true, if successful
	 */
	boolean hasPainter(Sequence seq);
	
	/**
	 * Removes the painter from.
	 * 
	 * @param seq
	 *            the seq
	 */
	void removePainterFrom(Sequence seq);
	
	/**
	 * Gets the current sequence painter.
	 * 
	 * @return the current sequence painter
	 */
	P getCurrentSequencePainter();
	
	/**
	 * Current sequence has painter.
	 * 
	 * @return true, if successful
	 */
	boolean currentSequenceHasPainter();
	
	/**
	 * Removes the painter from current sequence.
	 */
	void removePainterFromCurrentSequence();
	
	/**
	 * Removes the painter from all sequences.
	 */
	void removePainterFromAllSequences();
	
	/**
	 * Adds the painter to current sequence.
	 * 
	 * @param newPainter
	 *            the new painter
	 */
	void addPainterToCurrentSequence(P newPainter);
}
