package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.config.ConfigConstants;
import email.kleck.demo.jsainsburyplc.module.parser.api.JsonResponse;
import email.kleck.demo.jsainsburyplc.module.parser.api.Result;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Double vat = Double.valueOf(configuration.getProperty(ConfigConstants.GLOBAL_VAT));
        String productPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCTS);
        String titlePattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_NAME);
        String pricePattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_PRICE);
        String descriptionPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_DESCRIPTION);
        String kcalPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCT_KCAL);

        JsonResponse json = new JsonResponse();

        // skim through the nodes and identify the products
        List<Node> products = new ArrayList<>();

        searchNodesByAttribute(products, tree.getNodes(), generateStackFromPattern(productPattern));

        for (Node node : products) {
            Result result = new Result();

            Node title = searchSingleNodeByAttribute(node.getChildren(), generateStackFromPattern(titlePattern));
            if (title != null) {
                result.setTitle(title.getContent());
            }
            Node price = searchSingleNodeByAttribute(node.getChildren(), generateStackFromPattern(pricePattern));
            if (price != null) {
                result.setUnitPrice(convertToDouble(price.getContent()));
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
        Double result = 0.0;
        if (content != null) {
            String value = content.trim();
            value = value.replaceAll("[^\\d.]+", "");
            Currency currency = Currency.getInstance("GBP");
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
            result = bd.doubleValue();
        }
        return result;
    }

    /**
     * Generate a stack from the provided pattern to identify a node
     *
     * @param pattern the pattern to transform into a stack
     * @return the transformed stack
     */
    public Stack<String> generateStackFromPattern(String pattern) {
        Stack<String> stack = new Stack<>();
        List<String> nodeSelectors = Arrays.asList(pattern.split(" "));
        Collections.reverse(nodeSelectors);
        nodeSelectors.forEach(stack::push);
        return stack;
    }


    public Node searchSingleNodeByAttribute(List<Node> nodesToSearch, Stack<String> nodeSelector) {
        Node retVal = null;
        String[] selectors = nodeSelector.peek().split("=");
        Pattern pattern = selectors.length == 3 ? Pattern.compile(selectors[2]) : null;
        for (Node node : nodesToSearch) {
            Map<String, String> attributeMap = node.getAttributeMap();
            if (node.getType().matches(selectors[0]) && (selectors.length == 1 || attributeMap.containsKey(selectors[1]))) {
                Matcher matcher = pattern != null ? pattern.matcher(attributeMap.get((selectors[1]))) : null;
                if (matcher == null || matcher.find()) {
                    if (nodeSelector.size() == 1) {
                        retVal = node;
                        break;
                    } else {
                        // get into next sub-node
                        nodeSelector.pop();
                    }
                }
            }
            if (node.getChildren() != null) {
                // might be within the children
                retVal = searchSingleNodeByAttribute(node.getChildren(), nodeSelector);
            }
            if (retVal != null) {
                break;
            }
        }
        return retVal;
    }

    /**
     * Search nodes for a specific identifier
     * This method uses a 3-way identification string which consists of the following pattern:
     * &lt;tag-regex&gt;=&lt;attribute&gt;=&lt;attribute-value-regex&gt;
     *
     * @param found         the found nodes
     * @param nodesToSearch the nodes to iterate through
     * @param nodeSelector  the expression to select the nodes
     */
    public void searchNodesByAttribute(List<Node> found, List<Node> nodesToSearch, Stack<String> nodeSelector) {
        String[] selectors = nodeSelector.peek().split("=");
        Pattern pattern = selectors.length == 3 ? Pattern.compile(selectors[2]) : null;
        boolean foundNode = false;
        for (Node node : nodesToSearch) {
            Map<String, String> attributeMap = node.getAttributeMap();
            if (node.getType().matches(selectors[0]) && (selectors.length == 1 || attributeMap.containsKey(selectors[1]))) {
                Matcher matcher = pattern != null ? pattern.matcher(attributeMap.get((selectors[1]))) : null;
                if (matcher == null || matcher.find()) {
                    if (nodeSelector.size() == 1) {
                        found.add(node);
                        foundNode = true;
                    } else {
                        // get into next sub-node
                        nodeSelector.pop();
                    }
                }
            }
            if (!foundNode && node.getChildren() != null) {
                // might be within the children
                searchNodesByAttribute(found, node.getChildren(), nodeSelector);
            }
        }
    }
}
