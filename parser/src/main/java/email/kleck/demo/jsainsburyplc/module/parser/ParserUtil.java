package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class
 */
public final class ParserUtil {

    /**
     * Generate a stack from the provided pattern to identify a node
     *
     * @param pattern the pattern to transform into a stack
     * @return the transformed stack
     */
    public static Stack<String> generateStackFromPattern(String pattern) {
        Stack<String> stack = new Stack<>();
        if (pattern != null) {
            List<String> nodeSelectors = Arrays.asList(pattern.split(" "));
            Collections.reverse(nodeSelectors);
            nodeSelectors.forEach(stack::push);
        }
        return stack;
    }

    /**
     * Search a single node by attribute definition
     *
     * @param nodesToSearch the nodes to iterate through
     * @param nodeSelector  the expression to select the node
     * @return the single node if matched
     */
    public static Node searchSingleNodeByAttribute(List<Node> nodesToSearch, Stack<String> nodeSelector) {
        Node retVal = null;
        if(nodeSelector.isEmpty()) {
            return null;
        }
        String[] selectors = nodeSelector.peek().split("=");
        Pattern pattern = selectors.length == 3 ? Pattern.compile(selectors[2]) : null;
        String nodeType = selectors[0];
        int nodeIndex = -1;
        if (nodeType.contains("[")) {
            nodeIndex = Integer.parseInt(nodeType.substring(nodeType.indexOf('[') + 1, nodeType.indexOf(']')));
            nodeType = nodeType.substring(0, nodeType.indexOf('['));
        }

        // simple counter of identified nodes
        int foundNodes = -1;
        for (Node node : nodesToSearch) {
            Map<String, String> attributeMap = node.getAttributeMap();
            if (node.getType().matches(nodeType) && (selectors.length == 1 || attributeMap.containsKey(selectors[1]))) {
                // pre-check the index
                if (nodeIndex > -1) {
                    foundNodes++;
                    if (foundNodes != nodeIndex) {
                        continue;
                    }
                }

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
    public static void searchNodesByAttribute(List<Node> found, List<Node> nodesToSearch, Stack<String> nodeSelector) {
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
