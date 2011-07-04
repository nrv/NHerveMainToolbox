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

import icy.image.IcyBufferedImage;

/**
 * The Class BackupImageSingletonPlugin.
 * 
 * @author Nicolas HERVE, 2009
 * 
 *         This abstract Plugin is intended to manage backups of images. It is
 *         supposed to be run as a singleton and is aware of sequence focus
 *         changes. Therefore, this plugin is able to work simultaneously on
 *         several sequences.
 */
public abstract class BackupImageSingletonPlugin extends BackupSingletonPlugin<IcyBufferedImage> {
	
	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.BackupSingletonPlugin#backupCurrentSequence()
	 */
	public void backupCurrentSequence() {
		if (!hasBackupObject()) {
			IcyBufferedImage currentImage = getCurrentSequence().getFirstImage();
			IcyBufferedImage bi = new IcyBufferedImage(currentImage.getWidth(), currentImage.getHeight(), currentImage.getColorModel().getNumColorComponents(), currentImage.getColorModel().getTransferType());
			bi.getGraphics().drawImage(currentImage, 0, 0, null);
			addBackupObject(bi);
		}
	}

	/* (non-Javadoc)
	 * @see plugins.nherve.toolbox.plugin.BackupSingletonPlugin#restoreCurrentSequence(boolean)
	 */
	public void restoreCurrentSequence(boolean refresh) {
		IcyBufferedImage currentImage = getCurrentSequence().getFirstImage();
		IcyBufferedImage bck = getBackupObject();
		currentImage.getGraphics().drawImage(bck, 0, 0, null);
		if (refresh) {
			getCurrentSequence().dataChanged();
		}
	}
	
}
