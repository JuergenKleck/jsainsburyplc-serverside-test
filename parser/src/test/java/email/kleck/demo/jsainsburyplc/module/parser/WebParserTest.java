package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.parser.api.JsonResponse;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Test class for the web parser
 */
public class WebParserTest {

    private static final String HTML_STRING_1 = "<div id=\"random\" class=\"randomclass moreclasses\"><span>random content</span><!-- comment --><div>more random content <!-- some comment --></div></div>";
    private static final String HTML_STRING_2 = "<div id=\"random\" class=\"randomclass moreclasses\"><span>random content\t</span><!-- comment --> <div>more random\tcontent <!-- some comment --></div></div>";

    @Test
    public void givenHtml_whenProviding_thenSearchProducts() {
        StringBuilder html = new StringBuilder();
        Properties configuration = new Properties();
        configuration.put("global.vat","20.0");
        configuration.put("ident.products",".*=class=.*productLister.* .*=class=.*product.*");
        configuration.put("ident.product.name",".*=class=.*productInfo.* h3 a img");
        configuration.put("ident.product.price","p=class=.*pricePerUnit.*");

        Path path = Paths.get("demo.html");
        Assertions.assertTrue(Files.exists(path));
        Assertions.assertDoesNotThrow(() -> html.append(new String(Files.readAllBytes(path))));

        WebParser parser = new WebParser();
        Tree result = parser.extractProducts(html, configuration);
        Assertions.assertTrue(result.getNodes().size() > 0);
        result.getNodes().forEach(r -> Assertions.assertNotNull(r.getType()));
    }

    @Test
    public void givenHtml_whenProviding_thenFilterComments() {
        String html = HTML_STRING_1;
        WebParser parser = new WebParser();
        String filtered = parser.clearComments(html);
        Assertions.assertFalse(filtered.contains("<!--"));
        Assertions.assertFalse(filtered.contains("-->"));
    }

    @Test
    public void givenHtml_whenProviding_thenConvertTabs() {
        String html = HTML_STRING_2;
        WebParser parser = new WebParser();
        String filtered = parser.replaceTabWithSpace(html);
        Assertions.assertFalse(filtered.contains("\t"));
    }

    @Test
    public void givenHtml_whenProviding_thenCreateTree() {
        String html = HTML_STRING_1;
        WebParser parser = new WebParser();
        Tree tree = parser.generateTree(html);

        Assertions.assertFalse(tree.getNodes().isEmpty());
        Assertions.assertNotNull(tree.getNodes().get(0).getType());
    }

    @Test
    public void givenNodes_whenProviding_thenSearchNodes() {
        String html = HTML_STRING_1;
        WebParser parser = new WebParser();
        Transformer transformer = new Transformer();
        Tree tree = parser.generateTree(html);
        List<Node> found = new ArrayList<>();
        String nodeSelector = ".*=class=.*moreclasses.*";

        Stack<String> stack = new Stack<>();
        List<String> nodeSelectors = Arrays.asList(nodeSelector.split(" "));
        Collections.reverse(nodeSelectors);
        nodeSelectors.forEach(e -> stack.push(e));
        transformer.searchNodesByAttribute(found, tree.getNodes(), stack);
        Assertions.assertFalse(found.isEmpty());
        Assertions.assertNotNull(found.get(0).getType());
    }


}
