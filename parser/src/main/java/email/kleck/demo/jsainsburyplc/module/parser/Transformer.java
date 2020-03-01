package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.config.ConfigConstants;
import email.kleck.demo.jsainsburyplc.module.parser.api.JsonResponse;
import email.kleck.demo.jsainsburyplc.module.parser.api.Result;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Properties;

/**
 * Transformer class which converts the tree into the json response
 */
public class Transformer {

    /**
     * Convert the tree to json
     *
     * @param tree          the tree input
     * @param configuration the configuration properties
     * @return json response
     */
    public JsonResponse transformTree(Tree tree, Properties configuration) {

        double vat = Double.parseDouble(configuration.getProperty(ConfigConstants.GLOBAL_VAT));
        String productPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCTS);
        String titlePattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_NAME);
        String pricePattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_PRICE);
        String descriptionPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_DESCRIPTION);
        String descriptionAltPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_DESCRIPTION_ALT);
        String kcalPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_KCAL);
        String kcalAltPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_KCAL_ALT);
        String deepLinkPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_DEEPLINK);

        JsonResponse json = new JsonResponse();

        // skim through the nodes and identify the products
        List<Node> products = new ArrayList<>();

        ParserUtil.searchNodesByAttribute(products, tree.getNodes(), ParserUtil.generateStackFromPattern(productPattern));

        for (Node node : products) {
            Result result = new Result();

            Node title = ParserUtil.searchSingleNodeByAttribute(node.getChildren(), ParserUtil.generateStackFromPattern(titlePattern));
            if (title != null) {
                result.setTitle(title.getContent().trim());
            }
            Node price = ParserUtil.searchSingleNodeByAttribute(node.getChildren(), ParserUtil.generateStackFromPattern(pricePattern));
            if (price != null) {
                result.setUnitPrice(convertToDouble(price.getContent()));
            }

            Node deepLinkNode = ParserUtil.searchSingleNodeByAttribute(node.getChildren(), ParserUtil.generateStackFromPattern(deepLinkPattern));
            if (deepLinkNode != null && deepLinkNode.getSubTree() != null) {
                Node kcalNode = ParserUtil.searchSingleNodeByAttribute(deepLinkNode.getSubTree().getNodes(), ParserUtil.generateStackFromPattern(kcalPattern));
                if (kcalNode != null && kcalNode.getContent() != null && !kcalNode.getContent().isEmpty()) {
                    result.setKcal(kcalNode.getContent().replaceAll("[^\\d]*", ""));
                } else {
                    // try the alternative pattern
                    kcalNode = ParserUtil.searchSingleNodeByAttribute(deepLinkNode.getSubTree().getNodes(), ParserUtil.generateStackFromPattern(kcalAltPattern));
                    if (kcalNode != null) {
                        result.setKcal(kcalNode.getContent().replaceAll("[^\\d]*", ""));
                    }
                }

                Node longDescNode = ParserUtil.searchSingleNodeByAttribute(deepLinkNode.getSubTree().getNodes(), ParserUtil.generateStackFromPattern(descriptionPattern));
                if (longDescNode != null && longDescNode.getContent() != null && !longDescNode.getContent().isEmpty()) {
                    result.setDescription(longDescNode.getContent());
                } else {
                    // try the alternative pattern
                    longDescNode = ParserUtil.searchSingleNodeByAttribute(deepLinkNode.getSubTree().getNodes(), ParserUtil.generateStackFromPattern(descriptionAltPattern));
                    if (longDescNode != null) {
                        result.setDescription(longDescNode.getContent().trim());
                    }
                }
            }

            if (result.getTitle() != null) {
                json.getResults().add(result);
            }
        }

        // calculate totals
        json.getTotal().setGross(json.getResults().stream().mapToDouble(Result::getUnitPrice).sum());
        json.getTotal().setVat(roundByCurrency(json.getTotal().getGross() / (100 + vat) * vat));

        return json;
    }


    /**
     * Round a double value by the currency fraction
     *
     * @param value the double to round
     * @return rounded value
     */
    public Double roundByCurrency(Double value) {
        Currency currency = Currency.getInstance("GBP");
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Convert a string to double value with correct currency fraction rounding
     *
     * @param content the string to parse
     * @return rounded value
     */
    public Double convertToDouble(String content) {
        double result = 0.0;
        if (content != null && !content.isEmpty()) {
            String value = content.trim();
            value = value.replaceAll("[^\\d.]+", "");
            Currency currency = Currency.getInstance("GBP");
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
            result = bd.doubleValue();
        }
        return result;
    }


}
