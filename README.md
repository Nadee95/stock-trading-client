# Stock Trading Client

A Spring Boot gRPC client application for interacting with a stock trading service.

## Overview

This project implements a client application that communicates with a stock trading service using the gRPC protocol. It allows operations such as placing orders, streaming updates, and processing bulk orders.

## Technologies Used

- Java 24
- Spring Boot 3.4.5
- gRPC 1.72.0
- Protobuf 4.30.2
- Maven

## Prerequisites

- JDK 24+
- Maven 3.6+
- A running instance of the stock trading gRPC server

## Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/Nadee95/stock-trading-client.git
   cd stock-trading-client
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Configuration

Configure the gRPC client in your `application.yml` or `application.properties` file:

```yaml
grpc:
  client:
    stockService:
      address: static://localhost:9090
      negotiationType: plaintext
```

> Replace `localhost:9090` with the actual address of your gRPC server.

## Features

- Place individual stock orders
- Stream real-time order updates
- Process bulk orders from JSON files

## Running the Application

1. Select function call in the command line runner

2. Start the application with the following command:

    ```bash
    mvn spring-boot:run
    ```



## Usage Examples

The client provides various service methods for interacting with the stock trading server:

- Place orders
- Get order status
- Process bulk orders from JSON files
- Stream real-time updates  
