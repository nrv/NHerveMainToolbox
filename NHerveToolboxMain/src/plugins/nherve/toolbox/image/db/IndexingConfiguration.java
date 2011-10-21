package plugins.nherve.toolbox.image.db;


public interface IndexingConfiguration {
	void populate(ImageDatabaseIndexer idxr);
	String getName();
}
