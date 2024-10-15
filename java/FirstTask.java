import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FirstTask {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите дату (yyyy-MM-dd): ");
        String inputDate = scanner.nextLine();

        System.out.print("Введите идентификаторы через запятую: ");
        String[] inputIds = scanner.nextLine().split(",\\s*");

        String addressFilePath = "resourses/AS_ADDR_OBJ.XML"; // путь к файлу

        try {
            Document addressData = loadAddressData(addressFilePath);
            List<String> descriptions = getAddressesByIds(addressData, inputIds, inputDate);

            if (descriptions.isEmpty()) {
                System.out.println("Нет данных для заданных идентификаторов на указанную дату.");
            } else {
                for (String desc : descriptions) {
                    System.out.println(desc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Document loadAddressData(String filePath) throws Exception {
        File file = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(file);
    }

    public static List<String> getAddressesByIds(Document addressData, String[] ids, String targetDateStr) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date targetDate = dateFormat.parse(targetDateStr);
        Set<String> idSet = new HashSet<>();
        for (String id : ids) {
            idSet.add(id);
        }

        List<String> results = new ArrayList<>();
        NodeList nodeList = addressData.getElementsByTagName("OBJECT");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String objectId = element.getAttribute("OBJECTID");

                if (idSet.contains(objectId)) {
                    Date startDate = dateFormat.parse(element.getAttribute("STARTDATE"));
                    Date endDate = dateFormat.parse(element.getAttribute("ENDDATE"));

                    //Не знаю, нужно ли делать проверку актуальности адресов, на всякий случай оставил
                    /**
                     * boolean isActual = "1".equals(element.getAttribute("ISACTUAL"));
                    boolean isActive = "1".equals(element.getAttribute("ISACTIVE"));
                    */
                    
                    if (!targetDate.before(startDate) && !targetDate.after(endDate) /*&& isActual && isActive*/) {
                        String typeName = element.getAttribute("TYPENAME");
                        String name = element.getAttribute("NAME");
                        results.add(objectId + ": " + typeName + " " + name);
                    }
                }
            }
        }

        return results;
    }
}