package com.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class ThirdPartyMockServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/make_payment", new MakePaymentHandler());
        server.createContext("/receive_payment", new ReceivePaymentHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Third-party mock server started on port " + PORT);
    }

    static class MakePaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestBody = ThirdPartyMockServer.readRequestBody(exchange.getRequestBody());

            // Simulate payment processing
            String paymentId = String.valueOf(System.currentTimeMillis());
            String paymentStatus = "success"; // You can randomize this to simulate different outcomes

            String responseBody = "{\"id\":\"" + paymentId + "\", \"status\":\"" + paymentStatus + "\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBody.length());
            OutputStream os = exchange.getResponseBody();
            os.write(responseBody.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class ReceivePaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestBody = ThirdPartyMockServer.readRequestBody(exchange.getRequestBody());

            // Simulate payment processing
            String paymentId = String.valueOf(System.currentTimeMillis());
            String paymentStatus = "success"; // You can randomize this to simulate different outcomes

            String responseBody = "{\"id\":\"" + paymentId + "\", \"status\":\"" + paymentStatus + "\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBody.length());
            OutputStream os = exchange.getResponseBody();
            os.write(responseBody.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    private static String readRequestBody(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }
}