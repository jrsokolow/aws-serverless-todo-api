package com.myorg;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class TodoHandler implements RequestHandler<Map<String, Object>, ApiResponse> {

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
                return handleGet(input);
            case "POST":
                // Create a new TODO.
                return handlePost(input);
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
    private ApiResponse handleGet(Map<String, Object> input) {
        // TODO: Implement GET logic with DynamoDB client
        return new ApiResponse(200, "GET method called");
    }

    private ApiResponse handlePost(Map<String, Object> input) {
        // TODO: Implement POST logic to create a new TODO item
        return new ApiResponse(201, "POST method called");
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
