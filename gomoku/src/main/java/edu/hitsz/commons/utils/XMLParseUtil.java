package edu.hitsz.commons.utils;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

/**
 * This class represents the default implementation of the
 * <code>IMemento</code> interface.
 * <p>
 * This class is not intended to be extended by clients.
 * </p>
 *
 * @see
 */
public class XMLParseUtil {
    /**
     * Special reserved key used to store the memento id
     * (value <code>"IMemento.internal.id"</code>).
     *
     * @see #getID()
     */
    public static final String TAG_ID = "IMemento.internal.id"; //$NON-NLS-1$
    private Document factory;
    private Element element;

    /**
     * Creates a <code>Document</code> from the <code>Reader</code>
     * and returns a memento on the first <code>Element</code> for reading
     * the document.
     * <p>
     * Same as calling createReadRoot(reader, null)
     * </p>
     *
     * @param reader the <code>Reader</code> used to create the memento's document
     * @return a memento on the first <code>Element</code> for reading the document
     * @throws IOException
     * @throws  if IO problems, invalid format, or no element.
     */
    public static XMLParseUtil createReadRoot(String configPath) throws IOException {
        // 读取xml配置文件第一行，解析出encoding
        FileReader fr = new FileReader(configPath);
        BufferedReader br = new BufferedReader(fr);
        String encode = br.readLine().split("\"")[3];
        br.close();
        fr.close();
        // 根据encoding创建出相应编码的输入流
        FileInputStream fis = new FileInputStream(configPath);
        Reader reader = new InputStreamReader(fis, encode);
        XMLParseUtil xmlParseUtil = createReadRoot(reader, null);
        reader.close();
        fis.close();
        return xmlParseUtil;
    }

    /**
     * Creates a <code>Document</code> from the <code>Reader</code>
     * and returns a memento on the first <code>Element</code> for reading
     * the document.
     *
     * @param reader  the <code>Reader</code> used to create the memento's document
     * @param baseDir the directory used to resolve relative file names
     *                in the XML document. This directory must exist and include the
     *                trailing separator. The directory format, including the separators,
     *                must be valid for the platform. Can be <code>null</code> if not
     *                needed.
     * @return a memento on the first <code>Element</code> for reading the document
     * @throws WorkbenchException if IO problems, invalid format, or no element.
     */
    public static XMLParseUtil createReadRoot(Reader reader, String baseDir) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            InputSource source = new InputSource(reader);
            if (baseDir != null) {
                source.setSystemId(baseDir);
            }
            Document document = parser.parse(source);
            NodeList list = document.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node instanceof Element) {
                    return new XMLParseUtil(document, (Element) node);
                }
            }
        } catch (ParserConfigurationException e) {
            System.err.println("解析XML文件发生异常！");
            e.printStackTrace(System.err);
            return null;
        } catch (IOException e) {
            System.err.println("解析XML文件发生异常！");
            e.printStackTrace(System.err);
            return null;
        } catch (SAXException e) {
            System.err.println("解析XML文件发生异常！");
            e.printStackTrace(System.err);
            return null;
        }
        return null;
    }

    /**
     * Returns a root memento for writing a document.
     *
     * @param type the element node type to create on the document
     * @return the root memento for writing a document
     */
    public static XMLParseUtil createWriteRoot(String type) {
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element element = document.createElement(type);
            document.appendChild(element);
            return new XMLParseUtil(document, element);
        } catch (ParserConfigurationException e) {
            System.err.println("解析XML文件发生异常！");
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Creates a memento for the specified document and element.
     * <p>
     * Clients should use <code>createReadRoot</code> and
     * <code>createWriteRoot</code> to create the initial
     * memento on a document.
     * </p>
     *
     * @param document the document for the memento
     * @param element  the element node for the memento
     */
    public XMLParseUtil(Document document, Element element) {
        super();
        this.factory = document;
        this.element = element;
    }

    public XMLParseUtil createChild(String type) {
        Element child = factory.createElement(type);
        element.appendChild(child);
        return new XMLParseUtil(factory, child);
    }

    public XMLParseUtil createChild(String type, String id) {
        Element child = factory.createElement(type);
        child.setAttribute(TAG_ID, id == null ? "" : id); //$NON-NLS-1$
        element.appendChild(child);
        return new XMLParseUtil(factory, child);
    }

    public XMLParseUtil copyChild(XMLParseUtil child) {
        Element childElement = child.element;
        Element newElement = (Element) factory.importNode(childElement, true);
        element.appendChild(newElement);
        return new XMLParseUtil(factory, newElement);
    }

    public XMLParseUtil getChild(String type) {

        // Get the nodes.
        NodeList nodes = element.getChildNodes();
        int size = nodes.getLength();
        if (size == 0) {
            return null;
        }

        // Find the first node which is a child of this node.
        for (int nX = 0; nX < size; nX++) {
            Node node = nodes.item(nX);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getNodeName().equals(type)) {
                    return new XMLParseUtil(factory, element);
                }
            }
        }

        // A child was not found.
        return null;
    }

    public XMLParseUtil[] getChildren(String type) {

        // Get the nodes.
        NodeList nodes = element.getChildNodes();
        int size = nodes.getLength();
        if (size == 0) {
            return new XMLParseUtil[0];
        }

        // Extract each node with given type.
        ArrayList<Element> list = new ArrayList<Element>(size);
        for (int nX = 0; nX < size; nX++) {
            Node node = nodes.item(nX);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getNodeName().equals(type)) {
                    list.add(element);
                }
            }
        }

        // Create a memento for each node.
        size = list.size();
        XMLParseUtil[] results = new XMLParseUtil[size];
        for (int x = 0; x < size; x++) {
            results[x] = new XMLParseUtil(factory, list.get(x));
        }
        return results;
    }

    public Float getFloat(String key) {
        Attr attr = element.getAttributeNode(key);
        if (attr == null) {
            return null;
        }
        String strValue = attr.getValue();
        try {
            return new Float(strValue);
        } catch (NumberFormatException e) {
            System.err.println("解析XML文件发生异常！");
            e.printStackTrace(System.err);
            return null;
        }
    }

    public String getType() {
        return element.getNodeName();
    }

    public String getID() {
        return element.getAttribute(TAG_ID);
    }

    public Integer getInteger(String key) {
        Attr attr = element.getAttributeNode(key);
        if (attr == null) {
            return null;
        }
        String strValue = attr.getValue();
        try {
            return new Integer(strValue);
        } catch (NumberFormatException e) {
            System.err.println("解析XML文件发生异常！");
            e.printStackTrace(System.err);
            return null;
        }
    }

    public String getString(String key) {
        Attr attr = element.getAttributeNode(key);
        if (attr == null) {
            return null;
        }
        return attr.getValue();
    }

    public Boolean getBoolean(String key) {
        Attr attr = element.getAttributeNode(key);
        if (attr == null) {
            return null;
        }
        return Boolean.valueOf(attr.getValue());
    }

    public String getTextData() {
        Text textNode = getTextNode();
        if (textNode != null) {
            return textNode.getData();
        }
        return null;
    }

    public String[] getAttributeKeys() {
        NamedNodeMap map = element.getAttributes();
        int size = map.getLength();
        String[] attributes = new String[size];
        for (int i = 0; i < size; i++) {
            Node node = map.item(i);
            attributes[i] = node.getNodeName();
        }
        return attributes;
    }

    /**
     * Returns the Text node of the memento. Each memento is allowed only
     * one Text node.
     *
     * @return the Text node of the memento, or <code>null</code> if
     * the memento has no Text node.
     */
    private Text getTextNode() {
        // Get the nodes.
        NodeList nodes = element.getChildNodes();
        int size = nodes.getLength();
        if (size == 0) {
            return null;
        }
        for (int nX = 0; nX < size; nX++) {
            Node node = nodes.item(nX);
            if (node instanceof Text) {
                return (Text) node;
            }
        }
        // a Text node was not found
        return null;
    }

    /**
     * Places the element's attributes into the document.
     *
     * @param copyText true if the first text node should be copied
     */
    private void putElement(Element element, boolean copyText) {
        NamedNodeMap nodeMap = element.getAttributes();
        int size = nodeMap.getLength();
        for (int i = 0; i < size; i++) {
            Attr attr = (Attr) nodeMap.item(i);
            putString(attr.getName(), attr.getValue());
        }

        NodeList nodes = element.getChildNodes();
        size = nodes.getLength();
        // Copy first text node (fixes bug 113659).
        // Note that text data will be added as the first child (see putTextData)
        boolean needToCopyText = copyText;
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                XMLParseUtil child = createChild(node.getNodeName());
                child.putElement((Element) node, true);
            } else if (node instanceof Text && needToCopyText) {
                putTextData(((Text) node).getData());
                needToCopyText = false;
            }
        }
    }

    public void putFloat(String key, float f) {
        element.setAttribute(key, String.valueOf(f));
    }

    public void putInteger(String key, int n) {
        element.setAttribute(key, String.valueOf(n));
    }

    public void putMemento(XMLParseUtil memento) {
        // Do not copy the element's top level text node (this would overwrite the existing text).
        // Text nodes of children are copied.
        putElement(memento.element, false);
    }

    public void putString(String key, String value) {
        if (value == null) {
            return;
        }
        element.setAttribute(key, value);
    }

    public void putBoolean(String key, boolean value) {
        element.setAttribute(key, value ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void putTextData(String data) {
        Text textNode = getTextNode();
        if (textNode == null) {
            textNode = factory.createTextNode(data);
            // Always add the text node as the first child (fixes bug 93718) 
            element.insertBefore(textNode, element.getFirstChild());
        } else {
            textNode.setData(data);
        }
    }

    /**
     * Saves this memento's document current values to thespecified writer.
     */
    public void save(String filePath) throws IOException {
        DOMWriteUtil out = new DOMWriteUtil(filePath);
        out.write(element);
        out.close();
    }
}