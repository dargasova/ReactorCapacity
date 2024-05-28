package importers;

import reactors.ReactorType;
import reactors.ReactorsTypesOwner;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReactorTypeImporterXML extends ReactorTypeImporter{

    @Override
    public void importReactorsFromFile(File file, ReactorsTypesOwner reactorsOwner) {
        if (file.getName().endsWith(".xml")) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                try (var fileInputStream = new FileInputStream(file)) {
                    Document doc = builder.parse(fileInputStream);
                    doc.getDocumentElement().normalize();

                    NodeList nodeList = doc.getDocumentElement().getChildNodes();

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) nodeList.item(i);
                            ReactorType reactor = parseReactorFromXmlElement(element);
                            reactorsOwner.addReactor(element.getNodeName(), reactor);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (nextImporter != null) {
            nextImporter.importReactorsFromFile(file, reactorsOwner);
        } else {
            System.out.println("Неподдерживаемый формат");
        }
    }


    private ReactorType parseReactorFromXmlElement(Element element) {
        String type = element.getNodeName();
        Map<String, String> attributes = getAttributes(element);
        return new ReactorType(
                type,
                attributes.get("class"),
                getDouble(attributes, "burn_up"),
                getDouble(attributes, "efficiency_factor"),
                getDouble(attributes, "enrichment"),
                getDouble(attributes, "thermal_capacity"),
                getDouble(attributes, "electrical_capacity"),
                getInt(attributes),
                getDouble(attributes, "first_load"),
                "XML"
        );
    }

    private Map<String, String> getAttributes(Element element) {
        Map<String, String> attributes = new HashMap<>();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                attributes.put(childElement.getNodeName(), childElement.getTextContent());
            }
        }
        return attributes;
    }


    private Double getDouble(Map<String, String> attributes, String key) {
        return !attributes.containsKey(key) || attributes.get(key).isEmpty() ? null : Double.parseDouble(attributes.get(key));
    }

    private Integer getInt(Map<String, String> attributes) {
        return !attributes.containsKey("life_time") || attributes.get("life_time").isEmpty() ? null : Integer.parseInt(attributes.get("life_time"));
    }
}