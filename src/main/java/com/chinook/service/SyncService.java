package com.chinook.service;

import com.chinook.client.BackendClient;
import com.chinook.model.InvoiceHeader;
import com.chinook.repository.LocalRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncService {

    private final LocalRepository localRepository;
    private final BackendClient backendClient;

    public SyncService(LocalRepository localRepository, BackendClient backendClient) {
        this.localRepository = localRepository;
        this.backendClient = backendClient;
    }

    @Async
    public void syncPendingInvoices() {
        List<InvoiceHeader> pending = localRepository.getPendingInvoices();
        System.out.println(">>> Facturas pendientes encontradas: " + pending.size());
        if (pending.isEmpty()) return;

        int sincronizadas = 0;
        int fallidas = 0;

        // Se sincroniza una por una: si una factura falla (ej. cliente inexistente),
        // las demás igual se sincronizan en vez de bloquearse todas juntas.
        for (InvoiceHeader invoice : pending) {
            boolean success = backendClient.sendInvoiceHeader(invoice);
            System.out.println(">>> Sincronizando factura " + invoice.getInvoiceId() + ": " + success);
            if (success) {
                localRepository.markAsSynced(List.of(invoice.getInvoiceId()));
                sincronizadas++;
            } else {
                fallidas++;
            }
        }

        System.out.println(">>> Sincronización terminada. OK: " + sincronizadas + " | Fallidas: " + fallidas);
    }
}
