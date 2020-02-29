package email.kleck.demo.jsainsburyplc.module.parser.api;

import java.util.ArrayList;
import java.util.List;

/**
 * The json response class which is the product of this module
 */
public class JsonResponse {

    private List<Result> results = new ArrayList<>();
    private Total total = new Total();

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "JsonResponse{" +
                "results=" + results +
                ", total=" + total +
                '}';
    }

}
