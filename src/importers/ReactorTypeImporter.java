package importers;

import reactors.ReactorsTypesOwner;

import java.io.File;

public abstract class ReactorTypeImporter {
    protected ReactorTypeImporter nextImporter;

    public abstract void importReactorsFromFile(File file, ReactorsTypesOwner reactorsOwner);
}