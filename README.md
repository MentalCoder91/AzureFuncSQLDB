---
page_type: sample
languages:
- java
products:
- azure
description: "This is a sample application to showcase the use of Spring Cloud Function on top of Azure Functions."
urlFragment: AzureFuncSQLDB

---

There are 4 endpoints the DB functionality 


getAllProducts: [GET,POST] http://localhost:7071/api/getAllProducts

getProducts: [GET,POST] http://localhost:7071/api/getProducts

hello: [GET,POST] http://localhost:7071/api/hello

httpEx: [GET,POST] http://localhost:7071/api/httpEx

It is integrated with Azure MySQL Database 


------------------------------------------------------
Create Azure MySQL Database



AZ_RESOURCE_GROUP=azure-spring-workshop
AZ_DATABASE_NAME=spring-db
AZ_LOCATION=eastus
AZ_MYSQL_USERNAME=spring


create rg:
az group create --name $AZ_RESOURCE_GROUP --location $AZ_LOCATION 


create MYSQL server:
az mysql server create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name $AZ_DATABASE_NAME \
    --location $AZ_LOCATION \
    --sku-name B_Gen5_1 \
    --storage-size 5120 \
    --admin-user $AZ_MYSQL_USERNAME 
	
COPY password from above generated output	

create firewall rules:
az mysql server firewall-rule create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name allAzureIPs \
    --server-name $AZ_DATABASE_NAME \
    --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0 

create db:
az mysql db create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name demo \
    --server-name $AZ_DATABASE_NAME 

-----------------------------------------------------------------------------------------

application.properties:


spring.datasource.url=jdbc:mysql://spring-db.mysql.database.azure.com:3306/demo?serverTimezone=UTC
spring.datasource.username=spring@spring-db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.password={password}
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update

-----------------------------------------------------------------------------------------

# Example "Hello, world" Spring Boot application that runs on Azure Functions

This is a sample application to showcase the use of Spring Cloud Function on top of Azure Functions.

## Features

This is just a "Hello, world", but it uses domain objects so it's easy to extend to do something more complex.

## Getting Started

### Prerequisites

This project uses the Maven Wrapper, so all you need is Java installed.

### Installation

- Clone the project: `git clone https://github.com/MentalCoder91/AzureFuncSQLDB.git`
- Configure the project to use your own resource group and your own application name (it should be unique across Azure)
  - Open the `pom.xml` file
  - Customize the `functionResourceGroup` and `functionAppName` properties
- Build the project: `./mvnw clean package`

### Quickstart

Once the application is built, you can run it locally using the Azure Function Maven plug-in:

`./mvnw azure-functions:run`

And you can test it using a cURL command:

`curl http://localhost:7071/api/hello -d '{"name": "Azure"}'`

## Deploying to Azure Functions

Deploy the application on Azure Functions with the Azure Function Maven plug-in:

`./mvnw azure-functions:deploy`

You can then test the running application, by running a POST request:

```
curl https://<YOUR_SPRING_FUNCTION_NAME>.azurewebsites.net/api/hello -d '{"name": "Azure"}'
```

Or a GET request:

```
curl https://<YOUR_SPRING_FUNCTION_NAME>.azurewebsites.net/api/hello?name=Azure
```



[INFO]   getAllProducts : https://myfunc1991.azurewebsites.net/api/getallproducts
[INFO]   getProducts : https://myfunc1991.azurewebsites.net/api/getproducts
[INFO]   hello : https://myfunc1991.azurewebsites.net/api/hello
[INFO]   httpEx : https://myfunc1991.azurewebsites.net/api/httpex
