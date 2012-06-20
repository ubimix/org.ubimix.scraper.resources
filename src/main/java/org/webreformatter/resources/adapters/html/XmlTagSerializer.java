package org.webreformatter.resources.adapters.html;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CommentNode;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.Utils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.webreformatter.commons.xml.XmlException;
import org.webreformatter.commons.xml.XmlWrapper;
import org.webreformatter.commons.xml.XmlWrapper.CompositeNamespaceContext;
import org.webreformatter.commons.xml.XmlWrapper.SimpleNamespaceContext;
import org.webreformatter.commons.xml.XmlWrapper.XmlContext;

public class XmlTagSerializer {

    public static final String _NS_XHTML = "http://www.w3.org/1999/xhtml";

    private SimpleNamespaceContext fLocalNsContext;

    protected CleanerProperties fProps;

    private XmlContext fXmlContext;

    public XmlTagSerializer(CleanerProperties props) {
        this(XmlContext.build("", XmlTagSerializer._NS_XHTML), props);
    }

    public XmlTagSerializer(XmlContext xmlContext, CleanerProperties props) {
        fXmlContext = xmlContext;
        fProps = props;
    }

    protected void addToLocalNamespace(String nsPrefix, String namespaceUri) {
        if (fLocalNsContext == null) {
            fLocalNsContext = new SimpleNamespaceContext();
            CompositeNamespaceContext namespaceContext = fXmlContext
                .getNamespaceContext();
            namespaceContext.addContext(fLocalNsContext);
        }
        fLocalNsContext.addNamespacePrefix(nsPrefix, namespaceUri);
    }

    public XmlWrapper createDOM(TagNode rootNode) throws XmlException {
        try {
            String name = getNodeName(rootNode, rootNode.getName());
            XmlWrapper root = fXmlContext.newXML(name);
            setAttributes(root, rootNode);
            createSubnodes(root, rootNode.getChildren());
            return root;
        } finally {
            fLocalNsContext = null;
        }
    }

    private void createSubnodes(XmlWrapper root, List<?> tagChildren)
        throws XmlException {
        Element element = root.getRootElement();
        Document document = element.getOwnerDocument();
        if (tagChildren != null) {
            Iterator<?> it = tagChildren.iterator();
            while (it.hasNext()) {
                Object item = it.next();
                if (item instanceof CommentNode) {
                    CommentNode commentNode = (CommentNode) item;
                    Comment comment = document.createComment(commentNode
                        .getContent()
                        .toString());
                    element.appendChild(comment);
                } else if (item instanceof ContentNode) {
                    String nodeName = element.getNodeName();
                    String content = item.toString();
                    boolean specialCase = fProps.isUseCdataForScriptAndStyle()
                        && ("script".equalsIgnoreCase(nodeName) || "style"
                            .equalsIgnoreCase(nodeName));
                    if (!specialCase) {
                        content = Utils.escapeXml(content, fProps, true);
                    }
                    element.appendChild(specialCase ? document
                        .createCDATASection(content) : document
                        .createTextNode(content));
                } else if (item instanceof TagNode) {
                    TagNode subTagNode = (TagNode) item;
                    String name = subTagNode.getName();
                    name = getNodeName(subTagNode, name);
                    XmlWrapper child = root.appendElement(name);
                    setAttributes(child, subTagNode);
                    createSubnodes(child, subTagNode.getChildren());
                } else if (item instanceof List) {
                    List<?> sublist = (List<?>) item;
                    createSubnodes(root, sublist);
                }
            }
        }
    }

    private String getNamespacePrefix(String nsPrefix, String namespaceUri) {
        CompositeNamespaceContext namespaceContext = fXmlContext
            .getNamespaceContext();
        String prefix = namespaceContext.getPrefix(namespaceUri);
        if (prefix == null) {
            addToLocalNamespace(nsPrefix, namespaceUri);
        }
        return prefix;
    }

    private String getNamespaceURIOnPath(TagNode node, String nsPrefix) {
        if (nsPrefix == null) {
            nsPrefix = "";
        }
        String result = null;
        Map<String, String> nsDeclarations = node.getNamespaceDeclarations();
        if (nsDeclarations != null) {
            for (Map.Entry<String, String> nsEntry : nsDeclarations.entrySet()) {
                String currName = nsEntry.getKey();
                if (currName.equals(nsPrefix)
                    || ("".equals(currName) && nsPrefix == null)) {
                    result = nsEntry.getValue();
                    break;
                }
            }
        }
        if (result == null) {
            TagNode parent = node.getParent();
            if (parent != null) {
                result = getNamespaceURIOnPath(parent, nsPrefix);
            }
        }
        return result;
    }

    private String getNodeName(TagNode node, String name) {
        String localName = Utils.getXmlName(name);
        String namespacePrefix = Utils.getXmlNSPrefix(name);
        String namespaceUri = getNamespaceURIOnPath(node, namespacePrefix);
        namespacePrefix = getNamespacePrefix(namespacePrefix, namespaceUri);
        if (namespacePrefix == null) {
            namespacePrefix = "";
        }
        String result = localName;
        if (!"".equals(namespacePrefix)) {
            result = namespacePrefix + ":" + localName;
        }
        return result;
    }

    private void setAttributes(XmlWrapper root, TagNode node) {
        for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            String attrName = entry.getKey();
            String attrValue = entry.getValue();
            attrValue = Utils.escapeXml(attrValue, fProps, true);
            attrName = getNodeName(node, attrName);
            root.setAttribute(attrName, attrValue);
        }
    }
}
