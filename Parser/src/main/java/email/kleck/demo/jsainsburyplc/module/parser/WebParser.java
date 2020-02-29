package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.config.ConfigConstants;
import email.kleck.demo.jsainsburyplc.module.parser.api.JsonResponse;
import email.kleck.demo.jsainsburyplc.module.parser.api.Result;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The entry class to parse the website
 *
 * The identification of the relevant parts is based on the javascript from the original site as there is a similar
 * mechanism to scan the products which can be trusted as reliable solution
 */
public class WebParser {

    public JsonResponse extractProducts(StringBuilder html, Properties configuration) {
        JsonResponse json = new JsonResponse();

        String productPattern = configuration.getProperty(ConfigConstants.PARAM_IDENT_PRODUCTS_SELECTOR);

        // Followed are transformations to skip unrelevant parts of the website
        String productBox = html.toString();
        productBox = replaceTabWithSpace(productBox);
        productBox = removeLineFeed(productBox);
        productBox = clearComments(productBox);
        productBox = removeBlankBetweenTags(productBox);
        // this will finally generate the tree structure
        Tree tree = generateTree(productBox);

        // skim through the nodes and identify the products
        List<Node> products = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        List<String> nodeSelectors = Arrays.asList(productPattern.split(" "));
        Collections.reverse(nodeSelectors);
        nodeSelectors.forEach(e -> stack.push(e));

        searchNodeByAttribute(products, tree.getNodes(), stack);

        for (Node node : products) {
            Result result = new Result();
            result.setTitle(node.getContent().trim());
            json.getResults().add(result);
        }

        return json;
    }

    /**
     * Generate a tree out of the html contents
     *
     * @param html the string to process
     * @return tree with nodes and sub-nodes
     */
    public Tree generateTree(String html) {
        Tree tree = new Tree();

        Node currentNode = null;//new Node();
        //tree.getNodes().add(currentNode);

        boolean processed = false;
        int tagOpen = 0;
        int tagClosed = 0;
        boolean tagClosing = false;

        // i for character iterator, a for attributes, c for content
        for (int i = 0, a = 0, c = 0; i < html.length() - 1; i++) {
            char chr = html.charAt(i);
            char chr2 = html.charAt(i + 1);

            if (currentNode == null) {
                Node tmp = new Node();
                tree.getNodes().add(tmp);
                currentNode = tmp;
            }

            // open tag
            if (chr == '<') {
                if (chr2 != '/' && currentNode.getType() != null) {
                    // if the current node has already been filled we have a child node
                    Node tmp = new Node();

                    // BUG: The main html contains a bug in the generation of the html tags. the div.product within the li.gridItem is missing the closing </div> element
                    // as the browser auto-corrects this issue, it needs to be adjusted here
                    // START bug work-around
                    if (i + 21 < html.length() && html.substring(i, i + 21).equals("<li class=\"gridItem\">")
                            && "li".equals(currentNode.getType())) {
                        currentNode = currentNode.getParent();
                    }
                    // END bug work-around

                    currentNode.addChild(tmp);
                    currentNode = tmp;
                }

                tagOpen = i;
                if (tagClosed > 0 && tagClosed < i) {
                    currentNode.setContent(html.substring(tagClosed, i));
                    tagClosed = 0;
                }
            }

            if (chr == '>') {
                if (currentNode.getType() == null) {
                    currentNode.setType(html.substring(tagOpen + 1, i));
                }
                if (currentNode.getType() != null && currentNode.getAttributes() == null) {
                    currentNode.setAttributes(html.substring(tagOpen + 1 + currentNode.getType().length(), i));
                }
                tagOpen = 0;
                if (tagClosing || html.charAt(i - 1) == '/' || "input".equals(currentNode.getType())) {
                    // this is an immediate tag close or a misformed input tag - switch back to parent node
                    currentNode = currentNode.getParent();

                    // BUG: The main html contains a bug in the generation of the html tags. the div.product within the li.gridItem is missing the closing </div> element
                    // as the browser auto-corrects this issue, it needs to be adjusted here
                    // START bug work-around
                    if(currentNode != null && "div".equals(currentNode.getType()) && "li".equals(currentNode.getParent().getType()) && "gridItem".equals(currentNode.getParent().getAttributeMap().get("class"))
                        && currentNode.getAttributeMap().containsKey("class") && currentNode.getAttributeMap().get("class").contains("product")) {
                        // switch up another node to the <ul> tag
                        currentNode = currentNode.getParent();
                    }
                    // END bug work-around

                    tagClosing = false;
                    tagClosed = 0;
                    continue;
                }
                tagClosed = i + 1;
            }

            // set the tag type
            if (chr == ' ' && tagOpen > 0 && currentNode.getType() == null) {
                currentNode.setType(html.substring(tagOpen + 1, i));
            }

            if (chr == '/' && html.charAt(i - 1) == '<' && tagOpen > 0) {
                tagClosing = true;
            }
        }

        return tree;
    }

    /**
     * Method to clear the comments
     *
     * @param html the html string to process
     * @return comment-free html
     */
    public String clearComments(String html) {
        int commentStart = 0;
        boolean isComment = false;
        StringBuilder tmpBuilder = new StringBuilder(html);
        // blank out comments first
        for (int i = 0; i < html.length() - 4; i++) {
            char chr = html.charAt(i);
            char chr2 = html.charAt(i + 1);
            char chr3 = html.charAt(i + 2);
            char chr4 = html.charAt(i + 3);
            char chr5 = html.charAt(i + 4);

            if (!isComment && chr == '<' && chr2 == '!' && chr3 == '-' && chr4 == '-') {
                isComment = true;
                commentStart = i;
            }
            // we skip comments completely as they may corrupt the result
            if (isComment && chr == '-' && chr2 == '-' && chr3 == '>') {
                isComment = false;
                for (int j = commentStart; j < i + 3; j++) {
                    tmpBuilder.setCharAt(j, ' ');
                }
            }
        }
        html = tmpBuilder.toString();
        return html;
    }

    /**
     * Remove carriage-return line feed and transform into one line html.
     * Web-optimized html pages should not have any carriage-return at all.
     *
     * @param html the string to process
     * @return transformed html
     */
    public String removeLineFeed(String html) {
        StringBuilder tmpBuilder = new StringBuilder();
        int lastPos = 0;
        for (int i = 0; i < html.length() - 1; i++) {
            char chr = html.charAt(i);
            char chr2 = html.charAt(i + 1);
            if (chr == '\r' && chr2 == '\n') {
                lastPos = i + 1;
                i += 1;
            } else if (lastPos == 0) {
                tmpBuilder.append(chr);
            } else if (lastPos > 0 && chr != ' ') {
                lastPos = 0;
                tmpBuilder.append(chr);
            }
        }
        html = tmpBuilder.toString();
        return html;
    }

    /**
     * Remove blank spaces between the tags.
     * Web-optimized html pages should not need spaces between the html tags to save traffic.
     *
     * @param html the string to process
     * @return transformed html
     */
    public String removeBlankBetweenTags(String html) {
        StringBuilder tmpBuilder = new StringBuilder();
        int lastPos = 0;
        for (int i = 0; i < html.length() - 1; i++) {
            char chr = html.charAt(i);
            if (chr == '>') {
                lastPos = i;
                tmpBuilder.append(chr);
            } else if (lastPos == 0) {
                tmpBuilder.append(chr);
            } else if (lastPos > 0 && chr == ' ') {
                // skip this
            } else if (chr == '<' || chr != ' ') {
                // resume appending
                lastPos = 0;
                tmpBuilder.append(chr);
            }
        }
        html = tmpBuilder.toString();
        return html;
    }

    /**
     * Replace the tabular character with a space.
     * This should not happen at all.
     *
     * @param html the html string to process
     * @return tab-free html
     */
    public String replaceTabWithSpace(String html) {
        StringBuilder tmpBuilder = new StringBuilder(html);
        // blank out comments first
        for (int i = 0; i < html.length(); i++) {
            char chr = html.charAt(i);
            if (chr == '\t') {
                tmpBuilder.setCharAt(i, ' ');
            }
        }
        html = tmpBuilder.toString();
        return html;
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
    public void searchNodeByAttribute(List<Node> found, List<Node> nodesToSearch, Stack<String> nodeSelector) {
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
                searchNodeByAttribute(found, node.getChildren(), nodeSelector);
            }
        }
    }

}
