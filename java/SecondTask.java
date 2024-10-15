import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SecondTask {

    public static void main(String[] args) {
        String addressFilePath = "resourses/AS_ADDR_OBJ.XML"; // путь к файлу
        String hierarchyFilePath = "resourses/AS_ADM_HIERARCHY.XML"; // путь к файлу иерархии

        try {
            Document addressData = loadXMLData(addressFilePath);
            Document hierarchyData = loadXMLData(hierarchyFilePath);
            List<String> fullAddresses = getAddressesWithType(addressData, hierarchyData, "проезд");

            for (String address : fullAddresses) {
                System.out.println(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Document loadXMLData(String filePath) throws Exception {
        File file = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(file);
    }

    public static List<String> getAddressesWithType(Document addressData, Document hierarchyData, String targetType) throws Exception {
        Map<String, String> addressMap = new HashMap<>();
        NodeList addressNodes = addressData.getElementsByTagName("OBJECT");

        // Создание карты адресов: OBJECTID -> Полное название (тип + имя)
        for (int i = 0; i < addressNodes.getLength(); i++) {
            Node node = addressNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String objectId = element.getAttribute("OBJECTID");
                String typeName = element.getAttribute("TYPENAME");
                String name = element.getAttribute("NAME");
                addressMap.put(objectId, typeName + " " + name);
            }
        }

        List<String> results = new ArrayList<>();
        NodeList hierarchyNodes = hierarchyData.getElementsByTagName("ITEM");

        // Поиск актуальных связей для адресов типа "проезд"
        for (int i = 0; i < hierarchyNodes.getLength(); i++) {
            Node node = hierarchyNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String objectId = element.getAttribute("OBJECTID");
                String parentObjectId = element.getAttribute("PARENTOBJID");
                String isActive = element.getAttribute("ISACTIVE");

                if ("1".equals(isActive) && addressMap.containsKey(objectId) && addressMap.get(objectId).startsWith(targetType)) {
                    StringBuilder fullAddress = new StringBuilder(addressMap.get(objectId));
                    String currentParentId = parentObjectId;

                    // Построение полного адреса по цепочке иерархии
                    while (addressMap.containsKey(currentParentId) && !currentParentId.equals("0")) {
                        fullAddress.insert(0, addressMap.get(currentParentId) + ", ");
                        currentParentId = getParentObjectId(hierarchyData, currentParentId);
                    }

                    results.add(fullAddress.toString());
                }
            }
        }

        return results;
    }

    public static String getParentObjectId(Document hierarchyData, String objectId) {
        NodeList hierarchyNodes = hierarchyData.getElementsByTagName("ITEM");
        for (int i = 0; i < hierarchyNodes.getLength(); i++) {
            Node node = hierarchyNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("OBJECTID").equals(objectId)) {
                    return element.getAttribute("PARENTOBJID");
                }
            }
        }
        return "0";
    }
}