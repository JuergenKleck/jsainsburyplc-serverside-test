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
        configuration.put("global.vat","20.0");
        configuration.put("ident.products","div");
        configuration.put("ident.product.name","div=class=title");
        configuration.put("ident.product.price","div=class=price");

        Tree tree = new Tree();
        tree.getNodes().add(new Node("div", "class=\"someclass\"", "Node#1"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"title\"", "Title-Node"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"price\"", "1.75"));

        Transformer transformer = new Transformer();
        JsonResponse response = transformer.transformTree(tree,configuration);
        Assertions.assertNotNull(response.getResults());
        Assertions.assertFalse(response.getResults().isEmpty());
        Assertions.assertEquals("Title-Node", response.getResults().get(0).getTitle());
    }

}