package plugins.nherve.toolbox.image.db;

import icy.common.exception.UnsupportedFormatException;
import icy.file.Loader;
import icy.image.IcyBufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import plugins.nherve.toolbox.image.ImageLoader;
import plugins.nherve.toolbox.image.feature.SegmentableIcyBufferedImage;

public class IcyImageLoader extends ImageLoader<SegmentableIcyBufferedImage> {
	public IcyImageLoader(boolean useLoci) {
		super();
		this.useLoci = useLoci;
	}

	private boolean useLoci;
	
	@Override
	public SegmentableIcyBufferedImage load(File f) throws IOException {
		IcyBufferedImage ibi = null;
		
		if (isUseLoci()) {
			 try {
				ibi = Loader.loadImage(f);
			} catch (UnsupportedFormatException e) {
				throw new IOException(e);
			}
		} else {
			ibi = IcyBufferedImage.createFrom(ImageIO.read(f));
		}
		
		return new SegmentableIcyBufferedImage(ibi);
	}

	public boolean isUseLoci() {
		return useLoci;
	}

	public void setUseLoci(boolean useLoci) {
		this.useLoci = useLoci;
	}

}
