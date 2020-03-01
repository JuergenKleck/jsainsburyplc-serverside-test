package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test class for the web parser
 */
public class WebParserTest {

    private static final String HTML_STRING_1 = "<html><div id=\"random\" class=\"randomclass moreclasses\"><span>random content</span><!-- comment --><div>more random content <!-- some comment --></div></div></html>";
    private static final String HTML_STRING_2 = "<html><div id=\"random\" class=\"randomclass moreclasses\"><span>random content\t</span><!-- comment --> <div>more random\tcontent <!-- some comment --></div></div></html>";

    @Test
    public void givenHtml_whenProviding_thenSearchProducts() {
        StringBuilder html = new StringBuilder();
        Properties configuration = new Properties();
        configuration.put("global.vat", "20.0");
        configuration.put("ident.products", ".*=class=.*productLister.* .*=class=.*product.*");
        configuration.put("ident.product.name", ".*=class=.*productInfo.* h3 a img");
        configuration.put("ident.product.price", "p=class=.*pricePerUnit.*");

        AtomicReference<Path> path = new AtomicReference<>(Paths.get("demo.html"));
        if (!Files.exists(path.get())) {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(path.get().getFileName().toString());
            if (resource != null) {
                Assertions.assertDoesNotThrow(() -> {
                    File file = new File(resource.getFile());
                    path.set(Paths.get(file.getAbsolutePath()));
                });
            }
        }

        Assertions.assertTrue(Files.exists(path.get()));
        Assertions.assertDoesNotThrow(() -> html.append(new String(Files.readAllBytes(path.get()))));

        WebParser parser = new WebParser();
        Tree result = parser.createTree(html, configuration, false);
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
        Tree tree = parser.generateTree(html);
        List<Node> found = new ArrayList<>();

        Stack<String> stack = ParserUtil.generateStackFromPattern(".*=class=.*(moreclasses).*");
        ParserUtil.searchNodesByAttribute(found, tree.getNodes(), stack);
        Assertions.assertFalse(found.isEmpty());
        Assertions.assertNotNull(found.get(0).getType());
    }


}
