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

    // the sub tree can contain a deep-linked site
    private Tree subTree = null;

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

    public Tree getSubTree() {
        return subTree;
    }

    public void setSubTree(Tree subTree) {
        this.subTree = subTree;
    }

    public Map<String, String> getAttributeMap() {
        Map<String, String> map = new HashMap<>();
        if (this.attributes != null && !this.attributes.trim().isEmpty()) {
            String key = null;
            for (int i = 0, k = -1, v = -1; i < this.attributes.length(); i++) {
                // key start
                if (this.attributes.charAt(i) != '=' && this.attributes.charAt(i) != ' ' && k == -1 && key == null) {
                    k = i;
                }
                // key+value pair
                if (this.attributes.charAt(i) == '=' && k > -1) {
                    key = this.attributes.substring(k, i);
                    k = -1;
                }
                // standalone key
                if (this.attributes.charAt(i) == ' ' && k > -1) {
                    map.put(this.attributes.substring(k, i), null);
                    k = -1;
                }
                // value opening
                if (this.attributes.charAt(i) == '"' && v == -1 && key != null) {
                    v = i + 1;
                } else if (this.attributes.charAt(i) == '"' && v > -1 && key != null) {
                    // value closing
                    if (v < i) {
                        map.put(key, this.attributes.substring(v, i));
                    }
                    v = -1;
                    key = null;
                }
            }
        }
        return map;
    }

}
