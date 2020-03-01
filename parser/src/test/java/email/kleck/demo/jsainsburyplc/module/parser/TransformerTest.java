package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.parser.api.JsonResponse;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {

    @Test
    void givenTree_whenProviding_returnJson() {

        Properties configuration = new Properties();
        configuration.put("global.vat", "20.0");
        configuration.put("ident.products", "div");
        configuration.put("ident.product.name", "div=class=title");
        configuration.put("ident.product.deeplink", "a");
        configuration.put("ident.product.price", "div=class=price");

        Tree tree = new Tree();
        tree.getNodes().add(new Node("div", "class=\"someclass\"", "Node#1"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"title\"", "Title-Node"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"price\"", "1.75"));

        Transformer transformer = new Transformer();
        JsonResponse response = transformer.transformTree(tree, configuration);
        Assertions.assertNotNull(response.getResults());
        Assertions.assertFalse(response.getResults().isEmpty());
        Assertions.assertEquals("Title-Node", response.getResults().get(0).getTitle());
    }

    @Test
    void givenTreeWithSubTree_whenProviding_returnJson() {

        Properties configuration = new Properties();
        configuration.put("global.vat", "20.0");
        configuration.put("ident.products", "div");
        configuration.put("ident.product.name", "div=class=title");
        configuration.put("ident.product.deeplink", "a");
        configuration.put("ident.product.price", "div=class=price");
        configuration.put("ident.product.description", "h3");

        Tree tree = new Tree();
        tree.getNodes().add(new Node("div", "class=\"someclass\"", "Node#1"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"title\"", "Title-Node"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"price\"", "1.75"));
        Node deepLinkNode = new Node("a", "class=\"deeplink\"", "");
        tree.getNodes().get(0).addChild(deepLinkNode);
        Tree subTree = new Tree();
        subTree.getNodes().add(new Node("div", "", "Sub Node 1"));
        subTree.getNodes().get(0).addChild(new Node("span", "", ""));
        subTree.getNodes().get(0).getChildren().get(0).addChild(new Node("h3", "", "H3 Title"));
        deepLinkNode.setSubTree(subTree);

        Transformer transformer = new Transformer();
        JsonResponse response = transformer.transformTree(tree, configuration);
        Assertions.assertNotNull(response.getResults());
        Assertions.assertFalse(response.getResults().isEmpty());
        Assertions.assertEquals("Title-Node", response.getResults().get(0).getTitle());
        Assertions.assertEquals("H3 Title", response.getResults().get(0).getDescription());
    }

    @Test
    void givenDouble_whenProviding_returnRounded_1() {
        double input = 1.44343;
        Transformer transformer = new Transformer();
        double result = transformer.roundByCurrency(input);
        Assertions.assertEquals(1.44, result);
    }

    @Test
    void givenDouble_whenProviding_returnRounded_2() {
        double input = 1.44843;
        Transformer transformer = new Transformer();
        double result = transformer.roundByCurrency(input);
        Assertions.assertEquals(1.45, result);
    }

    @Test
    void givenDouble_whenProviding_returnConverted_1() {
        String input = "1.5542";
        Transformer transformer = new Transformer();
        double result = transformer.convertToDouble(input);
        Assertions.assertEquals(1.55, result);
    }

    @Test
    void givenDouble_whenProviding_returnConverted_2() {
        String input = "1.5582";
        Transformer transformer = new Transformer();
        double result = transformer.convertToDouble(input);
        Assertions.assertEquals(1.56, result);
    }

    @Test
    void givenDouble_whenProviding_returnConverted_3() {
        String input = "A1.55F82";
        Transformer transformer = new Transformer();
        double result = transformer.convertToDouble(input);
        Assertions.assertEquals(1.56, result);
    }

    @Test
    void givenDouble_whenProviding_returnConverted_4() {
        String input = "5";
        Transformer transformer = new Transformer();
        double result = transformer.convertToDouble(input);
        Assertions.assertEquals(5, result);
    }

    @Test
    void givenEmpty_whenProviding_returnZero() {
        String input = "";
        Transformer transformer = new Transformer();
        double result = transformer.convertToDouble(input);
        Assertions.assertEquals(0.0, result);
    }

    @Test
    void givenNull_whenProviding_returnZero() {
        Transformer transformer = new Transformer();
        double result = transformer.convertToDouble(null);
        Assertions.assertEquals(0.0, result);
    }

}