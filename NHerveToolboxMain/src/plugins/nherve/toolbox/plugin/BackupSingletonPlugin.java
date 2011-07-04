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

import icy.sequence.Sequence;

import java.util.HashMap;

// FIXME En attente de SequenceClosed event pour retirer le backup correspondant
/**
 * The Class BackupSingletonPlugin.
 * 
 * @param <O>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public abstract class BackupSingletonPlugin<O> extends SingletonPlugin /*implements SequenceListener */{
	
	/** The backup stuff. */
	private HashMap<Sequence, O> backupStuff;

	/**
	 * Backup current sequence.
	 */
	public abstract void backupCurrentSequence();
	
	/**
	 * Restore current sequence.
	 * 
	 * @param refresh
	 *            the refresh
	 */
	public abstract void restoreCurrentSequence(boolean refresh);

	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.SingletonPlugin#startPlugin()
	 */
	@Override
	public void startPlugin() {
		backupStuff = new HashMap<Sequence, O>();
		
		super.startPlugin();
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.SingletonPlugin#stopPlugin()
	 */
	@Override
	public void stopPlugin() {
		super.stopPlugin();
		backupStuff.clear();
	}
	
	/**
	 * Adds the backup object.
	 * 
	 * @param o
	 *            the o
	 */
	public void addBackupObject(O o) {
		addBackupObject(getCurrentSequence(), o);
	}
	
	/**
	 * Adds the backup object.
	 * 
	 * @param aSequence
	 *            the a sequence
	 * @param o
	 *            the o
	 */
	public void addBackupObject(Sequence aSequence, O o) {
		backupStuff.put(aSequence, o);
	}
	
	/**
	 * Removes the backup object.
	 * 
	 * @param aSequence
	 *            the a sequence
	 */
	public void removeBackupObject(Sequence aSequence) {
		backupStuff.remove(aSequence);
	}

	/**
	 * Gets the backup object.
	 * 
	 * @return the backup object
	 */
	public O getBackupObject() {
		return getBackupObject(getCurrentSequence());
	}
	
	/**
	 * Gets the backup object.
	 * 
	 * @param s
	 *            the s
	 * @return the backup object
	 */
	public O getBackupObject(Sequence s) {
		return backupStuff.get(s);
	}
	
	/**
	 * Checks for backup object.
	 * 
	 * @return true, if successful
	 */
	public boolean hasBackupObject() {
		return backupStuff.containsKey(getCurrentSequence());
	}
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.SingletonPlugin#changeSequence()
	 */
	protected void changeSequence() {
		sequenceWillChange();

		if (getSequences().size() > 0 || getFocusedSequence() != null) {
			setCurrentSequence(getFocusedSequence());
			/*
			getCurrentSequence().removeListener(this);
			getCurrentSequence().addListener(this);
			*/
			backupCurrentSequence();
		} else {
			setCurrentSequence(null);
		}

		sequenceHasChanged();
	}
	
//	@Override
//	public void sequenceChanged(SequenceEvent sequenceEvent) {
//		System.out.println("SequenceListener.sequenceChanged");
//	}
//	@Override
//	public void sequenceClosed(SequenceEvent sequenceEvent) {
//		System.out.println("SequenceListener.sequenceClosed");
//	}
}
