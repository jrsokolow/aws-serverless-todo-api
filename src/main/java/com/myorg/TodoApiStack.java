package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;

public class TodoApiStack extends Stack {
        public TodoApiStack(final App scope, final String id) {
                this(scope, id, null);
        }

        public TodoApiStack(final App scope, final String id, final StackProps props) {
                super(scope, id, props);

                // Create a DynamoDB table to store TODO items.
                Table todoTable = Table.Builder.create(this, "TodoTable")
                                .tableName("TodoTable")
                                .billingMode(BillingMode.PAY_PER_REQUEST)
                                .partitionKey(Attribute.builder()
                                                .name("id")
                                                .type(AttributeType.STRING)
                                                .build())
                                .build();

                // Create a Lambda function to handle CRUD operations.
                Function todoLambda = Function.Builder.create(this, "TodoLambda")
                                .runtime(Runtime.JAVA_17)
                                .handler("com.myorg.TodoHandler::handleRequest")
                                .code(Code.fromAsset("target/todo-api-lambda-0.1.jar")) // Path to your built Lambda jar
                                .build();

                // Grant the Lambda function read/write permissions on the DynamoDB table.
                todoTable.grantReadWriteData(todoLambda);

                // Create an API Gateway REST API backed by the Lambda function.
                LambdaRestApi api = LambdaRestApi.Builder.create(this, "TodoApi")
                                .handler(todoLambda)
                                .restApiName("Todo Service")
                                .build();
        }
}
