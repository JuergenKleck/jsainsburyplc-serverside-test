package email.kleck.demo.jsainsburyplc.module.parser.api;

/**
 * Single result json extracted from a product
 */
public class Result {

    private String title = "";
    private String kcal;
    private Double unitPrice = 0.0;
    private String description = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + '\"' +
                (kcal != null ? ", \"kcal_per_100g\":" + kcal : "") +
                ", \"unit_price\":" + unitPrice +
                ", \"description\":\"" + description + '\"' +
                '}';
    }
}
