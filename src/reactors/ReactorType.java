package reactors;


public class ReactorType {
    private String type;
    private String reactorClass;
    private Double burnup;
    private Double electricalCapacity;
    private Double enrichment;
    private Double firstLoad;
    private Double kpd;
    private Integer lifeTime;
    private Double termalCapacity;
    private String source;

    public ReactorType(
            String type, String reactorClass, Double burnup,
            Double kpd, Double enrichment, Double termalCapacity,
            Double electricalCapacity, Integer lifeTime, Double firstLoad,
            String source
    ) {
        this.type = type;
        this.reactorClass = reactorClass;
        this.burnup = burnup;
        this.electricalCapacity = electricalCapacity;
        this.enrichment = enrichment;
        this.firstLoad = firstLoad;
        this.kpd = kpd;
        this.lifeTime = lifeTime;
        this.termalCapacity = termalCapacity;
        this.source = source;
    }

    public Double getBurnup() {
        return burnup;
    }

    @Override
    public String toString() {
        return  reactorClass;
    }

    public String getFullDescription() {
        return  "Класс реактора " + reactorClass  + "\n"
                + "Выгорание " + burnup + "\n"
                + "КПД " + kpd + "\n"
                + "Обогащение " + enrichment + "\n"
                + "Теплоемкость " + termalCapacity + "\n"
                + "Электрическая мощность " + electricalCapacity + "\n"
                + "Продолжительность жизни " + lifeTime + "\n"
                + "Первая загрузка " + firstLoad + "\n\n"
                + "Ресурс: " + source;
    }
}