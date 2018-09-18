package com.example.ah.push;

/**
 * Created by ah on 11/07/2018.
 */

// Include the following imports to use table APIs
import android.app.Activity;
import android.os.AsyncTask;
import android.util.ArrayMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.table.*;


public class FetchTable extends AsyncTask<String, Void, HashMap<String,HashMap<String, String>>> {

    MyParser parser = new MyParser();

    static HashMap<String, HashMap<String, String>> fetchedData = new HashMap<String,HashMap<String, String>>();
    // Define the connection-string with your values.
    public static String storageConnectionString;
    public static String tableName;

     /*       "DefaultEndpointsProtocol=http;" +
                    "AccountName=arduappba0e;" +
                    "AccountKey=p+en8MZCfLJ4ATCn25CGp8IbNUZy/UfsHBWlT5YKU9dyCHZhq02WUWcNxIidlYjZWrU1UmtJUn5g2+ya3n3Ewg==;"; //TODO conn str from QR
*/

    @Override
    protected HashMap<String,HashMap<String, String>> doInBackground(String... params) {

        Map<String, String> deviceInfo = parser.parseQr(params[0]);
        storageConnectionString = deviceInfo.get("StorageConnectionString");
        tableName = deviceInfo.get("TableName");

            try
            {
                // Retrieve storage account from connection-string.
                CloudStorageAccount storageAccount =
                        CloudStorageAccount.parse(storageConnectionString);

                // Create the table client.
                CloudTableClient tableClient = storageAccount.createCloudTableClient();

                // Loop through the collection of table names.
                for (String table : tableClient.listTables())
                {
                    // Output each table name.
                    System.out.println(table);
                }
            }
            catch (Exception e)
            {
                // Output the stack trace.
                e.printStackTrace();
            }


            try
            {
                final String PARTITION_KEY = "PartitionKey";
                final String ROW_KEY = "RowKey";
                final String TIMESTAMP = "Timestamp";

                // Retrieve storage account from connection-string.
                CloudStorageAccount storageAccount =
                        CloudStorageAccount.parse(storageConnectionString);

                // Create the table client.
                CloudTableClient tableClient = storageAccount.createCloudTableClient();

                // Create a cloud table object for the table.
                CloudTable cloudTable = tableClient.getTableReference(tableName);

                // Create a filter condition where the partition key is "Smith".
                String partitionFilter = TableQuery.generateFilterCondition(
                        PARTITION_KEY,
                        TableQuery.QueryComparisons.EQUAL,
                        "0");

                // Create a filter condition where the partition key is "Smith".
                String timeLower = TableQuery.generateFilterCondition(
                        ROW_KEY,
                        TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL,
                        params[1]);
                        //"11-07-2018 09:56:05,569"); TODO works perfect too  ???
                        //testDate2); TODO good

                // Create a filter condition where the row key is less than the letter "E".
                String timeUpper = TableQuery.generateFilterCondition(
                        ROW_KEY,
                        TableQuery.QueryComparisons.LESS_THAN_OR_EQUAL,
                        params[2]);
                        //"12-07-2018 11:37:30,330"); TODO works perfect too  ???
                        //testDate3); TODO good

                // Combine the two conditions into a filter expression.
                String timeFilter = TableQuery.combineFilters(timeLower,
                        TableQuery.Operators.AND, timeUpper);

                String boardAndTimeFilter = TableQuery.combineFilters(timeFilter,
                        TableQuery.Operators.AND, partitionFilter);



                // Define a projection query that retrieves only the Email property
                TableQuery<TableEntity> projectionQuery =
                        TableQuery.from(TableEntity.class)
                                .select(new String[] {"sensorid" ,"value", "time"}).where(timeFilter);

                // Define a Entity resolver to project the entity to the Email value.
                EntityResolver<HashMap<String,HashMap<String, String>>> emailResolver = new EntityResolver<HashMap<String,HashMap<String, String>>>() {
                    @Override
                    public HashMap<String,HashMap<String, String>> resolve(String PartitionKey, String RowKey, Date timeStamp, HashMap<String, EntityProperty> properties, String etag) {

                        HashMap<String,HashMap<String, String>> resolvedEntity = new HashMap<String, HashMap<String, String>>();
                        HashMap<String, String> valAndTime = new HashMap<String, String>();
                        valAndTime.put(properties.get("time").getValueAsString(), (properties.get("value").getValueAsString()));
                        resolvedEntity.put(properties.get("sensorid").getValueAsString(), valAndTime);
                        return resolvedEntity;
                    }
                };

                // Loop through the results, displaying the Email values.
                for (HashMap<String,HashMap<String, String>> el :
                        cloudTable.execute(projectionQuery, emailResolver)) {
                    String[] keys = el.keySet().toArray(new String[el.size()]);
                    if(!fetchedData.containsKey(keys[0])){
                        fetchedData.put(keys[0].toString(), new HashMap<String, String>());
                    }
                    fetchedData.get(keys[0].toString()).putAll(el.get(keys[0].toString()));
                }

            }
            catch (Exception e)
            {
                // Output the stack trace.
                e.printStackTrace();
            }
            return fetchedData;
    }
}

/*
    // Define a Entity resolver to project the entity to the Email value.
    EntityResolver<HashMap<String,String>> emailResolver = new EntityResolver<HashMap<String,String>>() {
        @Override
        public HashMap<String,String> resolve(String PartitionKey, String RowKey, Date timeStamp, HashMap<String, EntityProperty> properties, String etag) {
            HashMap<String,String> resolvedEntity = new HashMap<String, String>();
            resolvedEntity.put(properties.get("time").getValueAsString(), properties.get("value").getValueAsString());
            // resolvedEntity.put(properties.get("time").getValueAsString(), properties.get("value").getValueAsString());
            return resolvedEntity;
                   }
    };
    */