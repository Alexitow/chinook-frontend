package com.chinook.service;

import com.chinook.client.BackendClient;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class HealthMonitorService {

    private final BackendClient backendClient;
    private final SyncService syncService;
    private final SseEmitterService sseService;

    private volatile boolean serverOnline = true;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public HealthMonitorService(BackendClient backendClient, SyncService syncService, SseEmitterService sseService) {
        this.backendClient = backendClient;
        this.syncService = syncService;
        this.sseService = sseService;
    }

    @PostConstruct
    public void startMonitoring() {
        // RNF-06: monitoreo periódico cada 5 segundos
        scheduler.scheduleAtFixedRate(this::checkHealth, 0, 5, TimeUnit.SECONDS);
    }

    private void checkHealth() {
        boolean alive = backendClient.isServerAlive();

        if (serverOnline && !alive) {
            // Transición ONLINE -> OFFLINE
            serverOnline = false;
            sseService.sendEvent("server-down",
                    "El servidor VPS no está disponible. Se procederá con la grabación local temporal de la cabecera.");
        } else if (!serverOnline && alive) {
            // Transición OFFLINE -> ONLINE
            serverOnline = true;
            sseService.sendEvent("server-up",
                    "El servidor VPS se ha restaurado. Se procederá a subir los datos almacenados localmente.");
            syncService.syncPendingInvoices();
        }
    }

    public boolean isServerOnline() {
        return serverOnline;
    }
}
