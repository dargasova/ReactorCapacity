package importers;

import reactors.ReactorType;
import reactors.ReactorsTypesOwner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class ReactorTypeImporterJSON extends ReactorTypeImporter{
    public void importReactorsFromStream(InputStream file, ReactorsTypesOwner reactorsOwner) {
        try {
            Path tempFile = Files.createTempFile("ReactorType", ".json");
            tempFile.toFile().deleteOnExit();

            // Копируем содержимое InputStream в временный файл
            Files.copy(file, tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Используем временный файл и передаем текущий экземпляр this
            importReactorsFromFile(tempFile.toFile(), reactorsOwner);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void importReactorsFromFile(File file, ReactorsTypesOwner reactorsOwner) {
        if (file.getName().endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode;
            try {
                rootNode = objectMapper.readTree(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            for (Iterator<String> fieldNameIterator = rootNode.fieldNames(); fieldNameIterator.hasNext(); ) {
                String fieldName = fieldNameIterator.next();
                JsonNode reactorNode = rootNode.get(fieldName);
                if (reactorNode != null && reactorNode.isObject()) {
                    ReactorType reactor = parseReactorFromJsonNode(reactorNode);
                    reactorsOwner.addReactor(fieldName, reactor);
                }
            }
        } else if (nextImporter != null) {
            nextImporter.importReactorsFromFile(file, reactorsOwner);
        } else {
            System.out.println("Неподдерживаемый формат");
        }
    }

    private ReactorType parseReactorFromJsonNode(JsonNode reactorNode) {
        return new ReactorType(
                reactorNode.has("type") ? reactorNode.get("type").asText() : null,
                reactorNode.has("class") ? reactorNode.get("class").asText() : null,
                reactorNode.has("burnup") ? reactorNode.get("burnup").asDouble() : 0.0,
                reactorNode.has("kpd") ? reactorNode.get("kpd").asDouble() : 0.0,
                reactorNode.has("enrichment") ? reactorNode.get("enrichment").asDouble() : 0.0,
                reactorNode.has("termal_capacity") ? reactorNode.get("termal_capacity").asDouble() : 0.0,
                reactorNode.has("electrical_capacity") ? reactorNode.get("electrical_capacity").asDouble() : 0.0,
                reactorNode.has("life_time") ? reactorNode.get("life_time").asInt() : 0,
                reactorNode.has("first_load") ? reactorNode.get("first_load").asDouble() : 0.0,
                "JSON"
        );
    }
}