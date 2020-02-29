package email.kleck.demo.jsainsburyplc.module.parser.api;

import java.util.ArrayList;
import java.util.List;

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

    /**
     {
     "results": [
     {
     "title": "Sainsbury's Strawberries 400g",
     "kcal_per_100g": 33,
     "unit_price": 1.75,
     "description": "by Sainsbury's strawberries"
     },
     {
     "title": "Sainsbury's Blueberries 200g",
     "kcal_per_100g": 45,
     "unit_price": 1.75,
     "description": "by Sainsbury's blueberries"
     },
     {
     "title": "Sainsbury's Cherry Punnet 200g",
     "kcal_per_100g": 52,
     "unit_price": 1.5,
     "description": "Cherries"
     }
     ],
     "total": {
     "gross": 5.00,
     "vat": 0.83
     }
     }

     */


}
