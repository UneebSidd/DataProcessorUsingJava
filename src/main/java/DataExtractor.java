import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.util.Collections;
import java.util.List;

public class DataExtractor {


    public static List<List<Object>> getData(String spreadSheetId, String sheetName, String rangeDataToRead) throws Exception {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        Sheets sheet = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName("Data set").build();


        List<List<Object>> data = sheet.spreadsheets().values()
                .get(spreadSheetId, sheetName + "!" + rangeDataToRead)
                .execute().getValues();

        return data;

    }



}
