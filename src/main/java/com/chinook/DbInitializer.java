package com.chinook;

import com.chinook.repository.SQLiteConnection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;

@Component
public class DbInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        String sql = new String(
                getClass().getResourceAsStream("/schema.sql").readAllBytes()
        );
        try (Connection conn = SQLiteConnection.getConnection();
             Statement st = conn.createStatement()) {
            for (String statement : sql.split(";")) {
                if (!statement.isBlank()) {
                    st.execute(statement);
                }
            }
            System.out.println(">>> Base de datos local inicializada correctamente.");
        } catch (Exception e) {
            System.out.println(">>> Error al inicializar la base de datos: " + e);
        }
    }
}
