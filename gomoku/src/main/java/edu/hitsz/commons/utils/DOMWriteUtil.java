package edu.hitsz.commons.utils;

import org.w3c.dom.*;

import java.io.*;
import java.util.Properties;

public class DOMWriteUtil {
    private final FileOutputStream outputStream;
    private final Writer output;

    /**
     * current number of tabs to use for ident
     */
    private int tab;

    /**
     * Creates a new DOM writer on the given output writer.
     *
     * @param output the output writer
     * @throws IOException
     */
    public DOMWriteUtil(String filePath) throws IOException {
        tab = 0;

        String _encode = "";
        FileInputStream fs = null;
        try {
            fs = new FileInputStream("./encode");
            Properties properties = new Properties();
            properties.load(fs);
            _encode = properties.getProperty("OutputEncode");
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            _encode = "UTF-8";
        } catch (IOException e) {
//            e.printStackTrace();
            _encode = "UTF-8";
        } finally {
            try {
                if (fs != null) fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        outputStream = new FileOutputStream(filePath);
        output = new OutputStreamWriter(outputStream, _encode);
        output.write("<?xml version=\"1.0\" encoding=\"" + _encode + "\"?>\n");
    }

    public void close() throws IOException {
        if (output != null) output.close();
        if (outputStream != null) outputStream.close();
    }

    /**
     * Prints the given element.
     *
     * @param element the element to print
     * @throws IOException
     * @throws DOMException
     */
    public void write(Element element) throws DOMException, IOException {
        // Ensure extra whitespace is not emitted next to a Text node,
        // as that will result in a situation where the restored text data is not the
        // same as the saved text data.
        boolean hasChildren = element.hasChildNodes();
        startTag(element, hasChildren);
        if (hasChildren) {
            tab++;
            boolean prevWasText = false;
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    if (!prevWasText) {
                        output.write("\n");
                        writeTabulation();
                    }
                    write((Element) children.item(i));
                    prevWasText = false;
                } else if (node instanceof Text) {
                    output.write(getEscaped(node.getNodeValue()));
                    prevWasText = true;
                }
            }
            tab--;
            if (!prevWasText) {
                output.write("\n");
                writeTabulation();
            }
            endTag(element);
        }
    }

    private void writeTabulation() throws IOException {
        for (int i = 0; i < tab; i++)
            output.write("\t"); //$NON-NLS-1$
    }

    private void startTag(Element element, boolean hasChildren) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("<"); //$NON-NLS-1$
        sb.append(element.getTagName());
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            //sb.append(" "); //$NON-NLS-1$
            sb.append("\n"); //$NON-NLS-1$
            for (int n = 0; n < tab; n++) {
                sb.append("\t");
            }
            sb.append("\t\t");
            sb.append(attribute.getName());
            sb.append("=\""); //$NON-NLS-1$
            sb.append(getEscaped(String.valueOf(attribute.getValue())));
            sb.append("\""); //$NON-NLS-1$
        }
        if (hasChildren) {
            sb.append(">");
        } else {
            sb.append(">");
            sb.append("\n");
            for (int n = 0; n < tab; n++) {
                sb.append("\t");
            }
            sb.append("</" + element.getTagName() + ">");
        }

        output.write(sb.toString());
    }

    /**
     * write the intended end tag
     *
     * @param name the name of the tag to end
     * @throws IOException
     */
    private void endTag(Element element) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("</"); //$NON-NLS-1$
        sb.append(element.getNodeName());
        sb.append(">"); //$NON-NLS-1$
        output.write(sb.toString());
    }

    private static void appendEscapedChar(StringBuffer buffer, char c) {
        String replacement = getReplacement(c);
        if (replacement != null) {
            buffer.append('&');
            buffer.append(replacement);
            buffer.append(';');
        } else {
            buffer.append(c);
        }
    }

    private static String getEscaped(String s) {
        StringBuffer result = new StringBuffer(s.length() + 10);
        for (int i = 0; i < s.length(); ++i) {
            appendEscapedChar(result, s.charAt(i));
        }
        return result.toString();
    }

    private static String getReplacement(char c) {
        // Encode special XML characters into the equivalent character references.
        // The first five are defined by default for all XML documents.
        // The next three (#xD, #xA, #x9) are encoded to avoid them
        // being converted to spaces on deserialization
        switch (c) {
            case '<':
                return "lt"; //$NON-NLS-1$
            case '>':
                return "gt"; //$NON-NLS-1$
            case '"':
                return "quot"; //$NON-NLS-1$
            case '\'':
                return "apos"; //$NON-NLS-1$
            case '&':
                return "amp"; //$NON-NLS-1$
            case '\r':
                return "#x0D"; //$NON-NLS-1$
            case '\n':
                return "#x0A"; //$NON-NLS-1$
            case '\u0009':
                return "#x09"; //$NON-NLS-1$
        }
        return null;
    }
}