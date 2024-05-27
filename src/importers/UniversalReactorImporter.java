package importers;

public class UniversalReactorImporter {

    public static ReactorTypeImporter getUniversalReactorImporter() {
        ReactorTypeImporterJSON reactorImporterJSON = new ReactorTypeImporterJSON();
        ReactorTypeImporterXML reactorImporterXML = new ReactorTypeImporterXML();
        ReactorTypeImporterYAML reactorImporterYAML = new ReactorTypeImporterYAML();

        reactorImporterXML.setNextImporter(reactorImporterYAML);
        reactorImporterJSON.setNextImporter(reactorImporterXML);

        return reactorImporterJSON;
    }
}