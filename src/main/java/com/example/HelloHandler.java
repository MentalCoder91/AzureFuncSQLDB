package com.example;

import com.example.model.Greeting;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.AzureRepository;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class HelloHandler {

    @Autowired
    Function<Mono<User>, Mono<Greeting>> hello;

    @Autowired
    AzureRepository repository;

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

    @FunctionName("getProducts")
    public HttpResponseMessage exec(
            @HttpTrigger(
                    name = "getProducts",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Get products function triggered");

        HttpResponseMessage response=null;
        List<Product> productList = repository.findAll();

        if (CollectionUtils.isEmpty(productList)) {
            response = request.createResponseBuilder(HttpStatus.OK).body("The product lst is empty").build();
        } else {
            response = request.createResponseBuilder(HttpStatus.OK).body(repository.findAll()).build();
        }

        return response;

    }


}
