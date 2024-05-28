package reactors;

import regions.Regions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConsumptionCalculator {
    private final Map<String, List<Reactor>> reactors;

    public ConsumptionCalculator(Map<String, List<Reactor>> reactors) {
        this.reactors = reactors;
    }

    public Map<Integer, Double> calculateReactorConsumption(Reactor reactor) {
        Map<Integer, Double> consumptionPerYear = new HashMap<>();

        reactor.fixLoadFactors();

        for (Integer year : reactor.getLoadFactors().keySet()) {
            Double loadFactor = reactor.getLoadFactors().get(year);
            Double consumption = reactor.getThermalCapacity() * loadFactor / 100.0 * 365;
            consumptionPerYear.put(year, consumption);
        }

        return consumptionPerYear;
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByCountries() {
        return calculateConsumption(Reactor::getCountry);
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByRegions(Regions regions) {
        return calculateConsumption(reactor -> regions.getRegion(reactor.getCountry()));
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByOperator() {
        return calculateConsumption(Reactor::getOperator);
    }

    private Map<String, Map<Integer, Double>> calculateConsumption(Function<Reactor, String> keyExtractor) {
        Map<String, Map<Integer, Double>> consumption = new HashMap<>();

        for (List<Reactor> reactorList : reactors.values()) {
            for (Reactor reactor : reactorList) {
                String key = keyExtractor.apply(reactor);
                Map<Integer, Double> entityConsumption = consumption.computeIfAbsent(key, k -> new HashMap<>());
                Map<Integer, Double> consumptionPerYear = calculateReactorConsumption(reactor);

                for (Integer year : consumptionPerYear.keySet()) {
                    entityConsumption.merge(year, consumptionPerYear.get(year), Double::sum);
                }
            }
        }

        return consumption;
    }
}
