package reactors;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Reactor {
    private String name;
    private String country;
    private ReactorType reactorType;
    private String owner;
    private String operator;
    private String status;
    private Integer thermalCapacity;
    private Map<Integer, Double> loadFactors;
    private Integer firstGridConnection;
    private Integer suspendedDate;
    private Integer permanentShutdownDate;

    public Reactor(String name, String country, ReactorType reactorType, String owner, String operator, String status,
                   Integer thermalCapacity, Integer firstGridConnection, Integer suspendedDate,
                   Integer permanentShutdownDate) {
        this.name = name;
        this.country = country;
        this.reactorType = reactorType;
        this.owner = owner;
        this.operator = operator;
        this.status = status;
        this.thermalCapacity = thermalCapacity;
        this.loadFactors = new HashMap<>();
        this.firstGridConnection = firstGridConnection;
        this.suspendedDate = suspendedDate;
        this.permanentShutdownDate = permanentShutdownDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ReactorType getReactorType() {
        return reactorType;
    }

    public void setReactorType(ReactorType reactorType) {
        this.reactorType = reactorType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getThermalCapacity() {
        return thermalCapacity;
    }

    public void setThermalCapacity(Integer thermalCapacity) {
        this.thermalCapacity = thermalCapacity;
    }

    public void addLoadFactor(Integer year, Double loadFactor) {
        if (Objects.equals(year, this.getFirstGridConnection())) {
            loadFactor *= 3;
        }
        loadFactors.put(year, loadFactor);
    }

    public void fixLoadFactors() {
        for (Integer year: loadFactors.keySet()) {
            if ((year >= this.getSuspendedDate() && this.getSuspendedDate() != 0) ||
                    (year >= this.getPermanentShutdownDate() && this.getPermanentShutdownDate() != 0)) {
                loadFactors.put(year, 0.0);
            } else if (loadFactors.get(year) == 0 && year > this.getFirstGridConnection()) {
                loadFactors.put(year, 85.0);
            }
        }
    }

    public Map<Integer, Double> getLoadFactors() {
        return new HashMap<>(loadFactors);
    }

    public Integer getFirstGridConnection() {
        return firstGridConnection;
    }

    public void setFirstGridConnection(Integer firstGridConnection) {
        this.firstGridConnection = firstGridConnection;
    }

    public Integer getSuspendedDate() {
        return suspendedDate;
    }

    public void setSuspendedDate(Integer suspendedDate) {
        this.suspendedDate = suspendedDate;
    }

    public Integer getPermanentShutdownDate() {
        return permanentShutdownDate;
    }

    public void setPermanentShutdownDate(Integer permanentShutdownDate) {
        this.permanentShutdownDate = permanentShutdownDate;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getFullDescription() {
        StringBuilder description = new StringBuilder();
        description.append("Страна: ").append(this.getCountry()).append("\n");
        description.append("Тип: ").append(this.getReactorType().toString()).append("\n");
        description.append("Владелец: ").append(this.getOwner()).append("\n");
        description.append("Оператор: ").append(this.getOperator()).append("\n");
        description.append("Статус: ").append(this.getStatus()).append("\n");
        description.append("Тепловая мощность: ").append(this.getThermalCapacity()).append("\n");
        description.append("Первое подключение к сети: ").append(this.getFirstGridConnection()).append("\n");
        if (this.getSuspendedDate() != 0) {
            description.append("Дата приостановки: ").append(this.getSuspendedDate()).append("\n");
        }
        if (this.getPermanentShutdownDate() != 0) {
            description.append("Дата окончательного закрытия: ").append(this.getPermanentShutdownDate()).append("\n");
        }
        return description.toString();
    }
}