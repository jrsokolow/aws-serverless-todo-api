package com.myorg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TodoHandler implements RequestHandler<Map<String, Object>, ApiResponse> {

    private static final String TABLE_NAME = "TodoTable";

    // Create a DynamoDB client and instantiate a DynamoDB object.
    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private static final DynamoDB dynamoDB = new DynamoDB(client);
    private static final Table table = dynamoDB.getTable(TABLE_NAME);

    @Override
    public ApiResponse handleRequest(Map<String, Object> input, Context context) {
        // Log the received event (you can see it in CloudWatch logs).
        context.getLogger().log("Received event: " + input);

        // Determine the HTTP method and route from the input.
        String httpMethod = (String) input.get("httpMethod");

        // Based on the HTTP method, call different functions.
        switch (httpMethod) {
            case "GET":
                // Return list of TODOs or a specific TODO.
                return handleGet(context);
            case "POST":
                // Create a new TODO.
                return handlePost(context, input);
            case "PUT":
                // Update an existing TODO.
                return handlePut(input);
            case "DELETE":
                // Delete a TODO.
                return handleDelete(input);
            default:
                return new ApiResponse(400, "Unsupported HTTP method");
        }
    }

    // Sample implementations (to be fleshed out with actual DynamoDB logic)
    private ApiResponse handleGet(Context context) {
        // Create a DynamoDB client and instantiate a DynamoDB object.
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(TABLE_NAME);

        List<Map<String, Object>> itemsAsMap = new ArrayList<>();
        try {
            // Scan the table for all items.
            ScanSpec scanSpec = new ScanSpec();
            ItemCollection<ScanOutcome> itemsCollection = table.scan(scanSpec);

            for (Item item : itemsCollection) {
                itemsAsMap.add(item.asMap());
            }
        } catch (Exception e) {
            context.getLogger().log("Failed to scan table: " + e.getMessage());
            return new ApiResponse(500, "Failed to fetch items: " + e.getMessage());
        }

        // Convert the list of item maps to JSON.
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse;
        try {
            jsonResponse = mapper.writeValueAsString(itemsAsMap);
        } catch (Exception e) {
            context.getLogger().log("Failed to convert items to JSON: " + e.getMessage());
            return new ApiResponse(500, "Error converting items to JSON: " + e.getMessage());
        }
        return new ApiResponse(200, jsonResponse);
    }

    private ApiResponse handlePost(Context context, Map<String, Object> input) {
        // Extract the body from the input event
        String body = (String) input.get("body");
        if (body == null || body.isEmpty()) {
            return new ApiResponse(400, "Missing request body");
        }

        // Use Jackson ObjectMapper to parse the JSON payload
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> payload;
        try {
            payload = mapper.readValue(body, Map.class);
        } catch (Exception e) {
            return new ApiResponse(400, "Invalid JSON in request body: " + e.getMessage());
        }

        // Validate required fields (e.g., title)
        String title = (String) payload.get("title");
        if (title == null || title.isEmpty()) {
            return new ApiResponse(400, "Missing or empty 'title' field");
        }
        String description = (String) payload.get("description");

        // Generate a unique ID for the new TODO item
        String id = java.util.UUID.randomUUID().toString();

        try {
            // Build the new item and insert it into the table
            Item newItem = new Item()
                    .withPrimaryKey("id", id)
                    .withString("title", title)
                    .withString("description", description != null ? description : "")
                    .withBoolean("status", false);
            table.putItem(newItem);
        } catch (Exception e) {
            return new ApiResponse(500, "Error inserting item: " + e.getMessage());
        }

        // Build the response with the created item details
        Map<String, Object> responseMap = new java.util.HashMap<>();
        responseMap.put("id", id);
        responseMap.put("title", title);
        responseMap.put("description", description);

        String jsonResponse;
        try {
            jsonResponse = mapper.writeValueAsString(responseMap);
        } catch (Exception e) {
            return new ApiResponse(500, "Error generating JSON response: " + e.getMessage());
        }

        return new ApiResponse(201, jsonResponse);
    }

    private ApiResponse handlePut(Map<String, Object> input) {
        // TODO: Implement PUT logic to update a TODO item
        return new ApiResponse(200, "PUT method called");
    }

    private ApiResponse handleDelete(Map<String, Object> input) {
        // TODO: Implement DELETE logic to remove a TODO item
        return new ApiResponse(200, "DELETE method called");
    }
}
