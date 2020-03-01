package email.kleck.demo.jsainsburyplc.module.parser.api;

public class Total {

    private Double gross = 0.0;
    private Double vat = 0.0;

    public Double getGross() {
        return gross;
    }

    public void setGross(Double gross) {
        this.gross = gross;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    @Override
    public String toString() {
        return "{" +
                "\"gross\":" + gross +
                ", \"vat\":" + vat +
                '}';
    }
}
