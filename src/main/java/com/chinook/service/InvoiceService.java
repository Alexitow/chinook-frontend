package com.chinook.service;

import com.chinook.client.BackendClient;
import com.chinook.model.InvoiceHeader;
import com.chinook.model.InvoiceLine;
import com.chinook.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    private final LocalRepository localRepository;
    private final BackendClient backendClient;
    private final HealthMonitorService healthMonitor;

    public InvoiceService(LocalRepository localRepository, BackendClient backendClient, HealthMonitorService healthMonitor) {
        this.localRepository = localRepository;
        this.backendClient = backendClient;
        this.healthMonitor = healthMonitor;
    }

    public void registerInvoice(InvoiceHeader header, List<InvoiceLine> lines) {
        // 1. Guardar SIEMPRE el detalle localmente (RF-03)
        for (InvoiceLine line : lines) {
            localRepository.saveInvoiceLine(line);
        }

        // 2. Decidir dónde guardar la cabecera (RF-02 / RF-05)
        if (healthMonitor.isServerOnline()) {
            boolean ok = backendClient.sendInvoiceHeader(header);
            if (!ok) {
                // Fallback: si falla el envío aunque el health check decía online, guardar local
                localRepository.saveInvoiceHeaderTemp(header);
            }
        } else {
            // Modo degradado: guardar cabecera local temporalmente
            localRepository.saveInvoiceHeaderTemp(header);
        }
    }
}
