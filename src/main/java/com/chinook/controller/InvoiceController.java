package com.chinook.controller;

import com.chinook.client.BackendClient;
import com.chinook.model.InvoiceHeader;
import com.chinook.model.InvoiceLine;
import com.chinook.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final BackendClient backendClient;

    public InvoiceController(InvoiceService invoiceService, BackendClient backendClient) {
        this.invoiceService = invoiceService;
        this.backendClient = backendClient;
    }

    @GetMapping("/invoice/new")
    public String showForm(Model model) {
        model.addAttribute("header", new InvoiceHeader());
        model.addAttribute("customers", backendClient.getCustomers());
        return "invoice";
    }

    @PostMapping("/invoice")
    public String registerInvoice(@ModelAttribute InvoiceHeader header,
                                   @RequestParam List<Integer> trackIds,
                                   @RequestParam List<Double> unitPrices,
                                   @RequestParam List<Integer> quantities) {
        List<InvoiceLine> lines = new ArrayList<>();
        for (int i = 0; i < trackIds.size(); i++) {
            InvoiceLine line = new InvoiceLine();
            line.setInvoiceId(header.getInvoiceId());
            line.setTrackId(trackIds.get(i));
            line.setUnitPrice(unitPrices.get(i));
            line.setQuantity(quantities.get(i));
            lines.add(line);
        }
        invoiceService.registerInvoice(header, lines);
        return "redirect:/invoice/new?success";
    }
}