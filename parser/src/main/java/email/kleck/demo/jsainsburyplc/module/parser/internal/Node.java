package email.kleck.demo.jsainsburyplc.module.parser.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal data structure to process the html
 */
public class Node {

    private String type;
    private String attributes;
    private String content;

    private List<Node> children = new ArrayList<>();
    private Node parent = null;

    public Node() {
    }

    public Node(String type, String attributes, String content) {
        this.type = type;
        this.attributes = attributes;
        this.content = content;
    }

    public Node addChild(Node child) {
        child.parent = this;
        this.children.add(child);
        return child;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public String getType() {
        return type;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getAttributeMap() {
        Map<String, String> map = new HashMap<>();
        if (this.attributes != null && !this.attributes.trim().isEmpty()) {
            Pattern pattern = Pattern.compile("\\s*([a-zA-Z]+)\\=\"(.+)\".*");
            Matcher matcher = pattern.matcher(this.attributes.trim());
            if (matcher.find()) {
                for (int i = 0; i < matcher.groupCount(); i += 2) {
                    map.put(matcher.group(i + 1).trim(), matcher.group(i + 2).trim());
                }
            }
        }
        return map;
    }

}
