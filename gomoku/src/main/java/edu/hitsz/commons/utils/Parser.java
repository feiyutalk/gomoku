package edu.hitsz.commons.utils;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 用于解析.xml的配置文件
 * @author
 * 批注：函数参数的名称是以_File结尾，
 * 		则输出的则是文件名，形如"/home/user/frobot/config.xml"的字符串
 *      函数参数的名称是以_Path结尾，
 *      则输出的则是文件所在的文件夹路径，形如"/home/user/frobot/"的字符串
 */
public class Parser {
	private final static Logger LOGGER = Logger.getLogger(Parser.class);

	/**
	 * paser worker config
	 * @param clientConfigFile
	 * @return
	 */
	public static Map<String ,String> parseClientConfig(String clientConfigFile){
		try {
			Map<String ,String> info = new HashMap<>();
			XMLParseUtil clientConfig = XMLParseUtil.createReadRoot(clientConfigFile);
			XMLParseUtil nameItem = clientConfig.getChild("name");
			info.put("name",nameItem.getTextData());
			XMLParseUtil genderItem = clientConfig.getChild("gender");
			info.put("gender",genderItem.getTextData());
			XMLParseUtil ageItem = clientConfig.getChild("age");
			info.put("age",ageItem.getTextData());
			XMLParseUtil fromItem = clientConfig.getChild("from");
			info.put("from",fromItem.getTextData());
			XMLParseUtil imageItem = clientConfig.getChild("image");
			info.put("image",imageItem.getTextData());
			XMLParseUtil ipItem = clientConfig.getChild("ip");
			info.put("ip", ipItem.getTextData());
			XMLParseUtil portItem = clientConfig.getChild("port");
			info.put("port", portItem.getTextData());
			XMLParseUtil serverIpItem = clientConfig.getChild("serverIp");
			info.put("serverIp", serverIpItem.getTextData());
			XMLParseUtil serverPortItem = clientConfig.getChild("serverPort");
			info.put("serverPort", serverPortItem.getTextData());
			return info;
		} catch (IOException e) {
			LOGGER.error("读取clientconfig.xml失败",e);
		}
		return null;
	}

	public static Map<String ,String> parserServerConfig(String serverConfigFile){
		try {
			Map<String ,String> info = new HashMap<>();
			XMLParseUtil serverConfig = XMLParseUtil.createReadRoot(serverConfigFile);
			XMLParseUtil ipItem = serverConfig.getChild("ip");
			info.put("ip", ipItem.getTextData());
			XMLParseUtil portItem = serverConfig.getChild("port");
			info.put("port", portItem.getTextData());
			XMLParseUtil matchesItem = serverConfig.getChild("matches");
			info.put("matches", matchesItem.getTextData());
			return info;
		} catch (IOException e) {
			LOGGER.error("读取serverconfig.xml失败",e);
		}
		return null;
	}

}
