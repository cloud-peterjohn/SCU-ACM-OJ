package com.scuoj.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;

public class XMLResultUtil {
    public static String getXmlResult(String file)
    {
        try {
            // 创建DocumentBuilderFactory对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 创建DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 从文件中读取XML
            File xmlFile = new File(file); // 替换为你的XML文件路径
            FileInputStream fis = new FileInputStream(xmlFile);
            Document document = builder.parse(fis);
            document.getDocumentElement().normalize();

            // 获取<result>元素
            NodeList resultNodes = document.getElementsByTagName("result");
            for (int i = 0; i < resultNodes.getLength(); i++) {
                Element resultElement = (Element) resultNodes.item(i);
                // 获取outcome属性的值
                String outcome = resultElement.getAttribute("outcome");
                return outcome;
                //System.out.println("Outcome: " + outcome);
            }
            fis.close(); // 关闭文件输入流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
