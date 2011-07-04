package plugins.nherve.toolbox.genericgrid;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ThumbnailProvider<T extends GridCell> {

	public abstract void createCacheFor(T cell);

	public abstract BufferedImage getThumbnail(T cell) throws ThumbnailException;

	public abstract boolean isAbleToProvideThumbnailFor(T cell);

	public abstract boolean isAbleToProvideThumbnailFor(File f);

	public abstract void provideThumbnailFor(T cell);

	public abstract void stopCurrentWork();

	public abstract void close();

}