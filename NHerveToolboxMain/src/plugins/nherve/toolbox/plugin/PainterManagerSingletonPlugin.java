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

import icy.gui.main.MainEvent;
import icy.painter.Painter;
import icy.sequence.Sequence;

/**
 * The Class PainterManagerSingletonPlugin.
 * 
 * @param <P>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class PainterManagerSingletonPlugin<P extends Painter> extends SingletonPlugin implements PainterManager<P>, PainterFactory<P> {
	
	/** The pm. */
	protected PainterManager<P> pm;
	
	/**
	 * Sequence has changed before setting painter.
	 */
	public abstract void sequenceHasChangedBeforeSettingPainter();
	
	/**
	 * Sequence has changed after setting painter.
	 */
	public abstract void sequenceHasChangedAfterSettingPainter();
	

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterFactory#addPainterToCurrentSequence()
	 */
	public void addPainterToCurrentSequence() {
		P newPainter = createNewPainter();
		addPainterToCurrentSequence(newPainter);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterFactory#initPainterManager()
	 */
	@Override
	public void initPainterManager() {
		PainterManagerImpl<P> pmi = new PainterManagerImpl<P>(this);
		pmi.setPainterName(getPainterName());
		pm = pmi;
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#currentSequenceHasPainter()
	 */
	public boolean currentSequenceHasPainter() {
		return pm.currentSequenceHasPainter();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#getCurrentSequencePainter()
	 */
	public P getCurrentSequencePainter() {
		return pm.getCurrentSequencePainter();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#removePainterFromCurrentSequence()
	 */
	public void removePainterFromCurrentSequence() {
		pm.removePainterFromCurrentSequence();
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.SingletonPlugin#sequenceHasChanged()
	 */
	@Override
	public void sequenceHasChanged() {
		sequenceHasChangedBeforeSettingPainter();
		if (hasCurrentSequence()) {
			if (!currentSequenceHasPainter()) {
				addPainterToCurrentSequence();
			}
		}
		sequenceHasChangedAfterSettingPainter();
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.SingletonPlugin#startPlugin()
	 */
	@Override
	public void startPlugin() {
		initPainterManager();
		super.startPlugin();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#addPainterToCurrentSequence(icy.painter.Painter)
	 */
	@Override
	public void addPainterToCurrentSequence(P newPainter) {
		pm.addPainterToCurrentSequence(newPainter);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#removePainterFromAllSequences()
	 */
	public void removePainterFromAllSequences() {
		pm.removePainterFromAllSequences();
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#getPainterFrom(icy.sequence.Sequence)
	 */
	public P getPainterFrom(Sequence seq) {
		return pm.getPainterFrom(seq);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#hasPainter(icy.sequence.Sequence)
	 */
	public boolean hasPainter(Sequence seq) {
		return pm.hasPainter(seq);
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.PainterManager#removePainterFrom(icy.sequence.Sequence)
	 */
	public void removePainterFrom(Sequence seq) {
		pm.removePainterFrom(seq);
	}
	
	@Override
	public void stopPlugin() {
		removePainterFromAllSequences();
		super.stopPlugin();
	}

	@Override
	public void sequenceClosed(MainEvent event) {
		removePainterFrom((Sequence)event.getSource());
		super.sequenceClosed(event);
	}
}
