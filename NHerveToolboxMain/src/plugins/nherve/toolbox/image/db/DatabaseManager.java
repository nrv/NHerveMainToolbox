package plugins.nherve.toolbox.image.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import plugins.nherve.toolbox.Algorithm;
import plugins.nherve.toolbox.image.feature.FeatureException;
import plugins.nherve.toolbox.image.feature.signature.BagOfSignatures;
import plugins.nherve.toolbox.image.feature.signature.VectorSignature;

public class DatabaseManager extends Algorithm {

	public DatabaseManager() {
		super();
	}

	public DatabaseManager(boolean log) {
		super(log);
	}

	public long getUniqueId(int imageId, int localDescriptorId) {
		return imageId * 100000000l + localDescriptorId;
	}

	public ImageDatabase create(final DatabaseConfiguration conf) throws IOException {
		log("Creating a new database : " + conf);
		ImageDatabase db = new ImageDatabase(conf.getName(), conf.getRoot(), conf.getPictures(), conf.getSignatures());

		File imagesDirectory = new File(db.getRootImageDirectory());
		if (!imagesDirectory.exists()) {
			throw new IOException("Unknown images directory + " + imagesDirectory.getAbsolutePath());
		}

		File[] images = imagesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(conf.getExtension());
			}
		});

		for (File image : images) {
			ImageEntry e = new ImageEntry(image.getName());
			db.add(e);
		}

		log(" - found " + db.size() + " pictures");

		return db;
	}

	public void save(final ImageDatabase db) throws IOException {
		ImageDatabasePersistence ptv = new ImageDatabasePersistence(db);
		ptv.dump();
	}

	public ImageDatabase load(final DatabaseConfiguration conf) throws IOException {
		return load(conf, false);
	}

	public ImageDatabase load(final DatabaseConfiguration conf, boolean headersOnly) throws IOException {
		ImageDatabasePersistence ptv = new ImageDatabasePersistence(conf.getRoot() + "/" + conf.getSignatures());
		if (headersOnly) {
			ptv.loadHeaders();
		} else {
			ptv.load();
		}
		ImageDatabase db = ptv.getDb();
		db.setRootDirectory(conf.getRoot());
		db.setImageDirectory(conf.getPictures());
		db.setSignatureDirectory(conf.getSignatures());
		return db;
	}

	public void index(final ImageDatabase db, final IndexingConfiguration conf) {
		db.clearDescriptors();

		ImageDatabaseIndexer idxr = new ImageDatabaseIndexer(db);
		idxr.setLogEnabled(isLogEnabled());
		conf.populate(idxr);

		log("Launching signatures extraction");

		idxr.launch();

		db.updateAvailableDescriptors();
	}

	public void textDump(final ImageDatabase db, String desc) throws IOException, FeatureException {
		File f = new File(db.getRootDirectory(), db.getName() + "_" + desc + ".export");
		BufferedWriter w = new BufferedWriter(new FileWriter(f));

		int nbNonNullSignatures = 0;
		int sigSize = -1;

		if (db.containsGlobalDescriptor(desc)) {
			for (ImageEntry e : db) {
				VectorSignature s = db.getGlobalSignature(e, desc);
				if (s != null) {
					if (sigSize < 0) {
						sigSize = s.getSize();
					}
					nbNonNullSignatures++;
				}
			}
		} else if (db.containsLocalDescriptor(desc)) {
			for (ImageEntry e : db) {
				BagOfSignatures<VectorSignature> bag = db.getLocalSignature(e, desc);
				if (bag != null) {
					for (VectorSignature s : bag) {
						if (sigSize < 0) {
							sigSize = s.getSize();
						}
						nbNonNullSignatures++;
					}
				}
			}
		}

		w.write(db.getName());
		w.newLine();
		w.write(desc);
		w.newLine();
		w.write(Integer.toString(nbNonNullSignatures));
		w.newLine();
		w.write(Integer.toString(sigSize));
		w.newLine();

		if (db.containsGlobalDescriptor(desc)) {
			for (ImageEntry e : db) {
				VectorSignature s = db.getGlobalSignature(e, desc);
				if (s != null) {
					w.write(Long.toString(e.getId()));
					for (int d = 0; d < s.getSize(); d++) {
						w.write(" " + s.get(d));
					}
					w.newLine();
				}
			}
		} else if (db.containsLocalDescriptor(desc)) {
			for (ImageEntry e : db) {
				BagOfSignatures<VectorSignature> bag = db.getLocalSignature(e, desc);
				if (bag != null) {
					int lid = 0;
					for (VectorSignature s : bag) {
						w.write(Long.toString(getUniqueId(e.getId(), lid)));
						for (int d = 0; d < s.getSize(); d++) {
							w.write(" " + s.get(d));
						}
						w.newLine();
						lid++;
					}
				}
			}
		}
		
		w.close();
	}
}
