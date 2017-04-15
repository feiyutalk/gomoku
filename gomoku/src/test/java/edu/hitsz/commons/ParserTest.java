package edu.hitsz.commons;

import edu.hitsz.commons.utils.Parser;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ParserTest {

    @Test
    public void testParseClientConfig(){
        Map<String, String> clientConfig = Parser.parseClientConfig("clientconfig.xml");
        System.out.println(clientConfig);
    }
}
