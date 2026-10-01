package com.chinook.client;

import com.chinook.model.InvoiceHeader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class BackendClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @Value("${backend.base-url:http://localhost:8000}")
    private String baseUrl;

    public boolean isServerAlive() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/health"))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println(">>> Excepción en isServerAlive: " + e);
            return false;
        }
    }

    public boolean sendInvoiceHeader(InvoiceHeader header) {
        try {
            String json = mapper.writeValueAsString(header);
            System.out.println(">>> JSON enviado (single): " + json);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/invoice"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(">>> Status: " + response.statusCode() + " Body: " + response.body());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println(">>> Excepción en sendInvoiceHeader: " + e);
            return false;
        }
    }

    public boolean sendBulkInvoices(List<InvoiceHeader> headers) {
        try {
            String json = mapper.writeValueAsString(headers);
            System.out.println(">>> JSON enviado (bulk): " + json);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/invoice/bulk"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(">>> Status: " + response.statusCode() + " Body: " + response.body());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println(">>> Excepción en sendBulkInvoices: " + e);
            return false;
        }
    }

    // Trae la lista de clientes reales desde el backend, para mostrarlos en un <select>
    // y evitar que el usuario escriba un customer_id que no existe.
    public List<Map<String, Object>> getCustomers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/customers"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
            }
            System.out.println(">>> getCustomers status: " + response.statusCode());
            return List.of();
        } catch (Exception e) {
            System.out.println(">>> Excepción en getCustomers: " + e);
            return List.of();
        }
    }
}
