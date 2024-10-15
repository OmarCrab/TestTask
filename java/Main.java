import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите дату (yyyy-MM-dd):");
        String inputDate = scanner.nextLine();

        System.out.println("Введите идентификаторы через запятую:");
        String[] inputIds = scanner.nextLine().split(",\\s*");

        String addressFilePath = "resourses/AS_ADDR_OBJ.XML";
        String hierarchyFilePath = "resourses/AS_ADM_HIERARCHY.XML";

        try {
            // Выполнение задачи FirstTask
            System.out.println("\nРезультаты FirstTask:");
            var addressData = FirstTask.loadAddressData(addressFilePath);
            List<String> addressDescriptions = FirstTask.getAddressesByIds(addressData, inputIds, inputDate);
            if (addressDescriptions.isEmpty()) {
                System.out.println("Нет данных для заданных идентификаторов на указанную дату.");
            } else {
                addressDescriptions.forEach(System.out::println);
            }

            // Выполнение задачи SecondTask
            System.out.println("\nРезультаты SecondTask:");
            var hierarchyData = SecondTask.loadXMLData(hierarchyFilePath);
            List<String> hierarchyDescriptions = SecondTask.getAddressesWithType(addressData, hierarchyData, "проезд");
            if (hierarchyDescriptions.isEmpty()) {
                System.out.println("Нет данных для адресов типа 'проезд'.");
            } else {
                hierarchyDescriptions.forEach(System.out::println);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}