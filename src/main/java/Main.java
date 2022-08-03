import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {




    public static void main(String[] args) throws Exception {

        //instantiate CreateSheet object
        CreateSheet sheet2 = new CreateSheet();

        //instantiate DataGenerator class object
        DataGenerator dataGenerator = new DataGenerator();



        //stores the newly created sheet id in variable named 'id'.
        String id = sheet2.createSpreadsheet("Sheet");

        //writing newly created sheet's id to a file to keep track of the sheets.

        try {
            FileWriter fileWriter = new FileWriter("/Users/uneebsiddiqui/Desktop/OAS/Java/src/main/resources/log.txt");
            fileWriter.write("Sheet ID " + id);
            fileWriter.close();
        }
        catch (IOException e)
        {
            System.out.println("Can not write to log file.");
        }

        //loading data to List of list

       List<List<Object>> aList = dataGenerator.addDataToList();

        //uploading data to google sheet
        dataGenerator.updateValues(id,"Sheet1!A2:Z","USER_ENTERED",aList);

        CreateSheet.shareFile(id,"uu1997@gmail.com", "@gmail.com");




       List<List<Object>>  data = DataExtractor.getData("id","Sheet1", "A2:Z");


             DataTransformer.dataParser(data);

             String jsonString = DataTransformer.convertToJson(data.get(0));

             DataTransformer.validateSchema(jsonString);

    }
}

