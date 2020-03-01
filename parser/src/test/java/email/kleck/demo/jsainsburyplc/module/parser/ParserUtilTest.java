package email.kleck.demo.jsainsburyplc.module.parser;

import email.kleck.demo.jsainsburyplc.module.parser.internal.Node;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

class ParserUtilTest {


    @Test
    void givenPattern_whenProvided_getStack() {
        Stack<String> stack = ParserUtil.generateStackFromPattern("h3 h5 div=class=abcd");
        Assertions.assertFalse(stack.isEmpty());
        Assertions.assertEquals("h3", stack.pop());
        Assertions.assertEquals("h5", stack.pop());
        Assertions.assertEquals("div=class=abcd", stack.pop());
        Assertions.assertTrue(stack.isEmpty());
    }

    @Test
    void givenSinglePattern_whenProvided_getStack() {
        Stack<String> stack = ParserUtil.generateStackFromPattern("h3");
        Assertions.assertFalse(stack.isEmpty());
        Assertions.assertEquals("h3", stack.pop());
        Assertions.assertTrue(stack.isEmpty());
    }

    @Test
    void givenInvalidPattern_whenProvided_getStack() {
        Stack<String> stack = ParserUtil.generateStackFromPattern("");
        Assertions.assertFalse(stack.isEmpty());
        Assertions.assertEquals("", stack.pop());
        Assertions.assertTrue(stack.isEmpty());
    }

    @Test
    void givenNull_whenProvided_getEmptyStack() {
        AtomicReference<Stack<String>> stack = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> stack.set(ParserUtil.generateStackFromPattern(null)));
        Assertions.assertNotNull(stack.get());
        Assertions.assertTrue(stack.get().isEmpty());
    }

    @Test
    void givenNodes_whenSearchingByAttribute_returnNode() {
        Tree tree = new Tree();
        tree.getNodes().add(new Node("div", "class=\"someclass\"", "Node#1"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"title\"", "Title-Node"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"price\"", "1.75"));
        Stack<String> stack = ParserUtil.generateStackFromPattern("div div=class=title");

        Node titleNode = ParserUtil.searchSingleNodeByAttribute(tree.getNodes(), stack);
        Assertions.assertNotNull(titleNode);
        Assertions.assertEquals("Title-Node", titleNode.getContent());
    }

    @Test
    void givenNodes_whenSearchingByTag_returnNodes() {
        Tree tree = new Tree();
        tree.getNodes().add(new Node("div", "class=\"someclass\"", "Node#1"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"title\"", "Title-Node"));
        tree.getNodes().get(0).addChild(new Node("div", "class=\"price\"", "1.75"));
        Stack<String> stack = ParserUtil.generateStackFromPattern("div div");

        List<Node> found = new ArrayList<>();
        ParserUtil.searchNodesByAttribute(found, tree.getNodes(), stack);
        Assertions.assertEquals(2, found.size());
    }
}