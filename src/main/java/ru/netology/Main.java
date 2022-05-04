package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main (String[]args){

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        System.out.println(json);
        writeString(json, "data.json");
        List<Employee> list1 = parseXML("data.xml");
        String json1 = listToJson(list1);
        writeString(json, "data1.json");


    }

    private static List<Employee> parseXML(String s) {
        List<Employee> elementList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(s));
            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()){
                    System.out.println(node.getNodeName());
                    Element element = (Element) node;
                    NamedNodeMap map = element.getAttributes();
                    Employee employee = new Employee();
                    for (int j = 0; j < map.getLength(); j++) {
                        String atrName = map.item(j).getNodeName();
                        String atrValue = map.item(j).getNodeValue();
                        if (atrName.equals("id")){
                            employee.id = Long.parseLong(atrValue);
                        }
                        if (atrName.equals("firstName")){
                            employee.firstName = atrValue;
                        }
                        if (atrName.equals("lastName")){
                            employee.lastName = atrValue;
                        }
                        if (atrName.equals("country")){
                            employee.country = atrValue;
                        }
                        if (atrName.equals("age")){
                            employee.age = Integer.parseInt(atrValue);
                        }
                    }
                    elementList.add(employee);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return elementList;

    }

    private static void writeString(String json, String name) {
        try (FileWriter file = new FileWriter(name)){
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().create();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        String json = gson.toJson(list, listType);


        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> allRows = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))){

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                            .build();
            allRows = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allRows;
    }
}
