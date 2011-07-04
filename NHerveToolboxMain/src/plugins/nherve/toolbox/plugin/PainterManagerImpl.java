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
 * The Class PainterManagerImpl.
 * 
 * @param <P>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class PainterManagerImpl<P extends Painter> implements PainterManager<P> {
	
	/** The callback. */
	private SingletonPlugin callback;
	
	/** The painter name. */
	private String painterName;

	/**
	 * Instantiates a new painter manager impl.
	 * 
	 * @param callback
	 *            the callback
	 */
	public PainterManagerImpl(SingletonPlugin callback) {
		super();
		this.callback = callback;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#getCurrentSequencePainter()
	 */
	public P getCurrentSequencePainter() {
		return getPainterFrom(callback.getCurrentSequence()); 
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#currentSequenceHasPainter()
	 */
	public boolean currentSequenceHasPainter() {
		return hasPainter(callback.getCurrentSequence());
	}
	

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#removePainterFromCurrentSequence()
	 */
	public void removePainterFromCurrentSequence() {
		removePainterFrom(callback.getCurrentSequence());
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#addPainterToCurrentSequence(icy.painter.Painter)
	 */
	public void addPainterToCurrentSequence(P newPainter) {
		if (callback.hasCurrentSequence()) {
			Sequence currentSequence = callback.getCurrentSequence();
			currentSequence.addPainter(newPainter);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#removePainterFromAllSequences()
	 */
	@Override
	public void removePainterFromAllSequences() {
		for (Sequence seq : callback.getSequences()) {
			removePainterFrom(seq);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#removePainterFrom(icy.sequence.Sequence)
	 */
	@Override
	public void removePainterFrom(Sequence seq) {
		Painter rm = getPainterFrom(seq);
		
		if (rm != null) {
			seq.removePainter(rm);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#getPainterFrom(icy.sequence.Sequence)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public P getPainterFrom(Sequence seq) {
		if (seq != null) {
			for (Painter cp : seq.getPainters()) {
				if (cp.getClass().getName() == getPainterName()) {
					return (P)cp;
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#hasPainter(icy.sequence.Sequence)
	 */
	@Override
	public boolean hasPainter(Sequence seq) {
		return (getPainterFrom(seq) != null);
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#getPainterName()
	 */
	public String getPainterName() {
		return painterName;
	}

	/**
	 * Sets the painter name.
	 * 
	 * @param painterName
	 *            the new painter name
	 */
	public void setPainterName(String painterName) {
		this.painterName = painterName;
	}
}
