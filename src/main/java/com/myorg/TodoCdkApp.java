package com.myorg;

import software.amazon.awscdk.App;

public class TodoCdkApp {
    public static void main(final String[] args) {
        App app = new App();
        new TodoApiStack(app, "TodoApiStack");
        app.synth();
    }
}
