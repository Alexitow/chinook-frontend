package com.chinook.repository;

import com.chinook.model.InvoiceHeader;
import com.chinook.model.InvoiceLine;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LocalRepository {

    public void saveInvoiceLine(InvoiceLine line) {
        String sql = "INSERT INTO invoice_line (invoice_id, track_id, unit_price, quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, line.getInvoiceId());
            ps.setInt(2, line.getTrackId());
            ps.setDouble(3, line.getUnitPrice());
            ps.setInt(4, line.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar detalle local", e);
        }
    }

    public void saveInvoiceHeaderTemp(InvoiceHeader header) {
        String sql = "INSERT OR REPLACE INTO invoice_temp (invoice_id, customer_id, invoice_date, " +
                "billing_address, billing_city, billing_state, billing_country, billing_postal_code, " +
                "total, sync_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, header.getInvoiceId());
            ps.setInt(2, header.getCustomerId());
            ps.setString(3, header.getInvoiceDate());
            ps.setString(4, header.getBillingAddress());
            ps.setString(5, header.getBillingCity());
            ps.setString(6, header.getBillingState());
            ps.setString(7, header.getBillingCountry());
            ps.setString(8, header.getBillingPostalCode());
            ps.setDouble(9, header.getTotal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar cabecera temporal", e);
        }
    }

    public List<InvoiceHeader> getPendingInvoices() {
        String sql = "SELECT * FROM invoice_temp WHERE sync_status = 'PENDING'";
        List<InvoiceHeader> result = new ArrayList<>();
        try (Connection conn = SQLiteConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                InvoiceHeader h = new InvoiceHeader();
                h.setInvoiceId(rs.getInt("invoice_id"));
                h.setCustomerId(rs.getInt("customer_id"));
                h.setInvoiceDate(rs.getString("invoice_date"));
                h.setBillingAddress(rs.getString("billing_address"));
                h.setBillingCity(rs.getString("billing_city"));
                h.setBillingState(rs.getString("billing_state"));
                h.setBillingCountry(rs.getString("billing_country"));
                h.setBillingPostalCode(rs.getString("billing_postal_code"));
                h.setTotal(rs.getDouble("total"));
                result.add(h);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer cabeceras pendientes", e);
        }
        return result;
    }

    public void markAsSynced(List<Integer> invoiceIds) {
        String sql = "UPDATE invoice_temp SET sync_status = 'SYNCED' WHERE invoice_id = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer id : invoiceIds) {
                ps.setInt(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error al marcar como sincronizadas", e);
        }
    }

    /** Crea las tablas locales si no existen (se ejecuta al iniciar la app). */
    public void initSchema() {
        String[] statements = {
            "CREATE TABLE IF NOT EXISTS invoice_line (" +
                "invoice_line_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "invoice_id INTEGER NOT NULL," +
                "track_id INTEGER NOT NULL," +
                "unit_price REAL NOT NULL," +
                "quantity INTEGER NOT NULL)",
            "CREATE TABLE IF NOT EXISTS invoice_temp (" +
                "invoice_id INTEGER PRIMARY KEY," +
                "customer_id INTEGER NOT NULL," +
                "invoice_date TEXT NOT NULL," +
                "billing_address TEXT," +
                "billing_city TEXT," +
                "billing_state TEXT," +
                "billing_country TEXT," +
                "billing_postal_code TEXT," +
                "total REAL NOT NULL," +
                "sync_status TEXT DEFAULT 'PENDING'," +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP)"
        };
        try (Connection conn = SQLiteConnection.getConnection();
             Statement st = conn.createStatement()) {
            for (String s : statements) {
                st.execute(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar esquema local", e);
        }
    }
}
