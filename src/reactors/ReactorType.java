package reactors;


public class ReactorType {
    private String type;
    private String reactorClass;
    private Double burn_up;
    private Double electricalCapacity;
    private Double enrichment;
    private Double firstLoad;
    private Double efficiency_factor;
    private Integer lifeTime;
    private Double heatCapacity;
    private String source;

    public ReactorType(
            String type, String reactorClass, Double burn_up,
            Double efficiency_factor, Double enrichment, Double heatCapacity,
            Double electricalCapacity, Integer lifeTime, Double firstLoad,
            String source
    ) {
        this.type = type;
        this.reactorClass = reactorClass;
        this.burn_up = burn_up;
        this.electricalCapacity = electricalCapacity;
        this.enrichment = enrichment;
        this.firstLoad = firstLoad;
        this.efficiency_factor = efficiency_factor;
        this.lifeTime = lifeTime;
        this.heatCapacity = heatCapacity;
        this.source = source;
    }

    public Double getburn_up() {
        return burn_up;
    }

    @Override
    public String toString() {
        return  reactorClass;
    }

    public String getFullDescription() {
        return  "Класс реактора " + reactorClass  + "\n"
                + "Выгорание " + burn_up + "\n"
                + "КПД " + efficiency_factor + "\n"
                + "Обогащение " + enrichment + "\n"
                + "Теплоемкость " + heatCapacity + "\n"
                + "Электрическая мощность " + electricalCapacity + "\n"
                + "Продолжительность жизни " + lifeTime + "\n"
                + "Первая загрузка " + firstLoad + "\n\n"
                + "Ресурс: " + source;
    }
}