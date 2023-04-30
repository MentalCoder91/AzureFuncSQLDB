package com.example;

import com.example.model.Greeting;
import com.example.model.Product;
import com.example.model.User;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class HelloHandler {

    @Autowired
    Function<Mono<User>, Mono<Greeting>> hello;


    @FunctionName("hello")  // This acts as an endpoint /api/hello
    public HttpResponseMessage execute(
            @HttpTrigger(name = "request", methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<User>> request,
            ExecutionContext context) {
        User user = request.getBody()
                .filter((u -> u.getName() != null))
                .orElseGet(() -> new User(
                        request.getQueryParameters()
                                .getOrDefault("name", "world")));
        context.getLogger().info("Greeting user name: " + user.getName());
        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(hello.apply(Mono.just(user)).block())
                .header("Content-Type", "application/json")
                .build();
    }


    @FunctionName("httpEx")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("getAllProducts")
    public HttpResponseMessage execJDBC(
            @HttpTrigger(
                    name = "getAllProducts",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Get products function triggered");

        HttpResponseMessage response = null;
        String connectionUrl = "jdbc:sqlserver://sql-server-1991.database.windows.net:1433;" +
                "database=sql-database-1991;user=sqladmin@sql-server-1991;password=Anish@1991;" +
                "encrypt=true;trustServerCertificate=false;" +
                "hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

        Connection connection = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionUrl);

            // Execute SQL query to retrieve data
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Products");
            ResultSet resultSet = statement.executeQuery();

            // Create list to store product data
            List<Product> productList = new ArrayList<>();

            // Iterate over result set and create Product objects
            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getLong("product_id"));
                product.setProductName(resultSet.getString("product_name"));
                product.setQuantity(resultSet.getLong("quantity"));
                productList.add(product);
            }

            if (CollectionUtils.isEmpty(productList)) {
                response = request.createResponseBuilder(HttpStatus.OK).body("The product list is empty").build();
            } else {
                response = request.createResponseBuilder(HttpStatus.OK).body(productList).build();
            }

        } catch (Exception e) {
            context.getLogger().severe("Exception caught: " + e.getMessage());
            response = request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred " +
                    "while retrieving products").build();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                context.getLogger().severe("Exception caught while closing connection: " + e.getMessage());
            }
        }

        return response;
    }



//    @FunctionName("getProducts")
//    public HttpResponseMessage exec(
//            @HttpTrigger(
//                    name = "getProducts",
//                    methods = {HttpMethod.GET, HttpMethod.POST},
//                    authLevel = AuthorizationLevel.ANONYMOUS)
//                    HttpRequestMessage<Optional<String>> request,
//            final ExecutionContext context) {
//
//        context.getLogger().info("Get products function triggered");
//
//        HttpResponseMessage response=null;
//        List<Product> productList = repository.findAll();
//
//        if (CollectionUtils.isEmpty(productList)) {
//            response = request.createResponseBuilder(HttpStatus.OK).body("The product list is empty").build();
//        } else {
//            response = request.createResponseBuilder(HttpStatus.OK).body(productList).build();
//        }
//
//        return response;
//
//    }

}



