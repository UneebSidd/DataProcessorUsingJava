import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTransformer {


    public static void dataParser(List<List<Object>> dataList)
    {
        for(int i=0; i<dataList.size(); i++)
        {
            parseData(dataList.get(i));
        }

    }


    private static void parseData(List<Object> list) {
        long value;

        // to check if there is any occurrences of at least one digit.
        Pattern pattern = Pattern.compile("[0-9]");
        //to look for any occurrence of any character or characters in a string
        Pattern alphaPattern = Pattern.compile("[abcdefghijklmnopqrstuvwxyz]", Pattern.CASE_INSENSITIVE);

        if(list.size()<5)
        {
            list.add("");
        }

        for (int i = 0; i < list.size(); i++) {
            //if we have a match in a given string store it in a Matcher Object.
            Matcher matcher = pattern.matcher(list.get(i).toString());
            Matcher charMatcher = alphaPattern.matcher(list.get(i).toString());


            try {
                //column 3(phone number) or column 5(zipcode)
                if (i == 2 || i == 4) {
                    //if column 3 or 5 has no value or if they are not numeric
                    if (list.get(i).toString().isEmpty() || charMatcher.find()) {
                        //set value to zero
                        list.set(i, 0);
                    }

                    else {

                        value = Long.parseLong(list.get(i).toString());
                        list.set(i, value);
                    }

                } else if (i == 0 || i == 1 || i == 3) {

                    //if column 1,2,or 4 contains an empty string or numbers
                    if (list.get(i).toString().isEmpty() || matcher.find()) {
                        list.set(i, "null");
                    }

                }

            } catch (NumberFormatException e)
            {
                list.set(i,0);
            }


        }


    }

    public static String convertToJson(List<Object> data) throws IOException {

        Map<String,Object> map = new HashMap<>();
        map.put("firstName", data.get(0));
        map.put("lastName", data.get(1));
        map.put("phoneNumber", data.get(2));
        map.put("state", data.get(3));
        map.put("zipcode", data.get(4));



        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(map);


        return json;
    }


    public static void validateSchema(String jsonString) throws FileNotFoundException {

        File schemaFile = new File("/Users/uneebsiddiqui/Desktop/OAS/Java/src/main/resources/schemaFile.json");
        JSONTokener schemaData = new JSONTokener(new FileInputStream(schemaFile));
        JSONObject jsonSchema = new JSONObject(schemaData);

        try {

            FileWriter fileWriter = new FileWriter("/Users/uneebsiddiqui/Desktop/OAS/Java/src/main/resources/file1.json");
            fileWriter.write(jsonString);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("file not found");
        }

        File jsonData = new File("/Users/uneebsiddiqui/Desktop/OAS/Java/src/main/resources/file1.json");
        JSONTokener jsonDataFile = new JSONTokener(new FileInputStream(jsonData));
        JSONObject jsonObject = new JSONObject(jsonDataFile);
//
        Schema schemaValid = SchemaLoader.load(jsonSchema);
        schemaValid.validate(jsonObject);


    }

}
