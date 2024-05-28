package reactors;

import regions.Regions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public class ConsumptionCalculator {
    Map<String, List<Reactor>> reactors;
    Regions regions;

    public ConsumptionCalculator(Map<String, List<Reactor>> reactors, Regions regions) {
        this.reactors = reactors;
        this.regions = regions;
    }

    public ConsumptionCalculator(Map<String, List<Reactor>> reactors) {
        this.reactors = reactors;
    }

    public Map<Integer, Double> calculateReactorConsumption(Reactor reactor) {
        Map<Integer, Double> consumptionPerYear = new HashMap<>();

        for (Integer year: reactor.getLoadFactors().keySet()) {
            Double consumption = reactor.getThermalCapacity() /
                    reactor.getReactorType().getburn_up() * reactor.getLoadFactors().get(year) / 100000 * 365;
            consumptionPerYear.put(year, consumption);
        }

        return consumptionPerYear;
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByCountries() {
        return calculateConsumption(Reactor::getCountry);
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByRegions() {
        return calculateConsumption(reactor -> regions.getRegion(reactor.getCountry()));
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByOperator() {
        return calculateConsumption(Reactor::getOperator);
    }

    private Map<String, Map<Integer, Double>> calculateConsumption(Function<Reactor, String> keyExtractor) {
        Map<String, Map<Integer, Double>> consumption = new HashMap<>();

        for (String entity : reactors.keySet()) {
            for (Reactor reactor : reactors.get(entity)) {
                String key = keyExtractor.apply(reactor);
                Map<Integer, Double> entityConsumption = consumption.computeIfAbsent(key, k -> new HashMap<>());
                Map<Integer, Double> consumptionPerYear = calculateReactorConsumption(reactor);

                for (Integer year : consumptionPerYear.keySet()) {
                    entityConsumption.merge(year, consumptionPerYear.get(year), Double::sum);
                }
            }
        }

        Map<String, Map<Integer, Double>> sortedConsumption = new TreeMap<>(consumption);
        sortedConsumption.replaceAll((e, v) -> new TreeMap<>(sortedConsumption.get(e)));

        return sortedConsumption;
    }

}