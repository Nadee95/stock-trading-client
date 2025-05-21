package com.nadeex.stock_trading_client.service;

import com.nadeex.grpc.StockOrder;
import com.nadeex.grpc.TradeStatus;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import com.nadeex.grpc.StockTradingServiceGrpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class StockClientService {

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub serviceStub;

    /*
    public com.nadeex.grpc.StockResponse getStockPrice(String
                                                       stockSymbol) {
        com.nadeex.grpc.StockRequest request = com.nadeex.grpc.StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        return this.serviceBlockingStub.getStockPrice(request);
    }
     */

    public void subscribeStockPrice(String stockSymbol) {
        com.nadeex.grpc.StockRequest request = com.nadeex.grpc.StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        this.serviceStub.subscribeStockPrice(request, new StreamObserver<com.nadeex.grpc.StockResponse>() {
            @Override
            public void onNext(com.nadeex.grpc.StockResponse stockResponse) {
                System.out.println("Received stock price update: " + stockResponse.getPrice());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed.");
            }
        });
    }

    public void placeBulkOrder() throws InterruptedException, IOException {

        StreamObserver<com.nadeex.grpc.OrderSummary> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(com.nadeex.grpc.OrderSummary orderSummary) {
                System.out.println("Received order summary: " + orderSummary.getTotalAmount());
                System.out.println("Successful orders: " + orderSummary.getSuccessCount());
                System.out.println("Total orders: " + orderSummary.getTotalOrders());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Bulk order completed.");
            }
        };

        StreamObserver<com.nadeex.grpc.StockOrder> requestObserver = this.serviceStub.bulkStockOrder(responseObserver);

        try {

            Path path = Paths.get("src/main/resources/bulk_orders.json");
            List<String> lines = Files.readAllLines(path);

            ObjectMapper objectMapper = new ObjectMapper();

            for (String line : lines) {
                // Parse each line as a JSON object
                JsonNode node = objectMapper.readTree(line);

                // Create StockOrder object
                com.nadeex.grpc.StockOrder order = com.nadeex.grpc.StockOrder.newBuilder()
                        .setOrderId(node.get("order_id").asText())
                        .setStockSymbol(node.get("stock_symbol").asText())
                        .setQuantity(node.get("quantity").asInt())
                        .setPrice(node.get("price").asDouble())
                        .setOrderType(node.get("order_type").asText())
                        .build();

                // Send order to the server
                requestObserver.onNext(order);

                // Add a small delay to avoid overwhelming the server
                Thread.sleep(100);
            }

            System.out.println("All orders sent successfully");

        } catch (Exception e) {
            requestObserver.onError(e);
            throw e;
        } finally {
            requestObserver.onCompleted();
        }
    }

    public void startLiveTrading() throws IOException {

        StreamObserver<TradeStatus> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(TradeStatus tradeStatus) {
                System.out.println("Received trade status: " + tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed.");
            }
        };

        StreamObserver<StockOrder> stockOrderStreamObserver = this.serviceStub.liveTrading(responseObserver);

        try {

            Path path = Paths.get("src/main/resources/stream_orders.json");
            List<String> lines = Files.readAllLines(path);

            ObjectMapper objectMapper = new ObjectMapper();

            for (String line : lines) {
                // Parse each line as a JSON object
                JsonNode node = objectMapper.readTree(line);

                // Create StockOrder object
                com.nadeex.grpc.StockOrder order = com.nadeex.grpc.StockOrder.newBuilder()
                        .setOrderId(node.get("order_id").asText())
                        .setStockSymbol(node.get("stock_symbol").asText())
                        .setQuantity(node.get("quantity").asInt())
                        .setPrice(node.get("price").asDouble())
                        .setOrderType(node.get("order_type").asText())
                        .build();

                // Send order to the server
                stockOrderStreamObserver.onNext(order);

                // Add a small delay to avoid overwhelming the server
                Thread.sleep(100);
            }

            System.out.println("All trading sent successfully");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stockOrderStreamObserver.onCompleted();
        }
    }

}
