package importers;

import reactors.ReactorsTypesOwner;

import java.io.File;

public abstract class ReactorTypeImporter {
    protected ReactorTypeImporter nextImporter;

    public void setNextImporter(ReactorTypeImporter reactorImporter) {
        this.nextImporter = reactorImporter;
    }

    public abstract void importReactorsFromFile(File file, ReactorsTypesOwner reactorsOwner);
}