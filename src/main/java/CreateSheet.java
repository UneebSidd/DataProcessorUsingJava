import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateSheet {


/**
 *
 *
 * Create a new spreadsheet.
 *
 * @param title - the name of the sheet to be created.
 * @return newly created spreadsheet id
 * @throws IOException - if credentials file not found.
 */



    public static String createSpreadsheet(String title) throws IOException, GeneralSecurityException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */

//sheet scope
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);




        // Create the sheets API client

        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Sheets samples")
                .build();

        // Create new spreadsheet with a title
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(title));
        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();

        // Prints the new spreadsheet id
        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        return spreadsheet.getSpreadsheetId();

    }







    /**
     * Batch permission modification.
     * realFileId file Id.
     * realUser User Id.
     * realDomain Domain of the user ID.
     * @return list of modified permissions if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public static List<String> shareFile(String realFileId, String realUser, String realDomain) throws IOException{
        /* Load pre-authorized user credentials from the environment.
         TODO(developer) - See https://developers.google.com/identity for
         guides on implementing OAuth2 for your application.application*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();

        final List<String> ids = new ArrayList<String>();


        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                // Handle error
                System.err.println(e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                System.out.println("Permission ID: " + permission.getId());

                ids.add(permission.getId());

            }
        };
        BatchRequest batch = service.batch();
        Permission userPermission = new Permission()
                .setType("user")
                .setRole("writer");

        userPermission.setEmailAddress(realUser);
        try {
            service.permissions().create(realFileId, userPermission)
                    .setFields("id")
                    .queue(batch, callback);

            Permission domainPermission = new Permission()
                    .setType("domain")
                    .setRole("reader");

            domainPermission.setDomain(realDomain);

            service.permissions().create(realFileId, domainPermission)
                    .setFields("id")
                    .queue(batch, callback);

            batch.execute();

            return ids;
        }catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to modify permission: " + e.getDetails());
            throw e;
        }
    }







}
