package email.kleck.demo.jsainsburyplc.module.parser.api;

public class Total {

    private Double gross;
    private Double vat;

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
        return "Total{" +
                "gross=" + gross +
                ", vat=" + vat +
                '}';
    }
}
