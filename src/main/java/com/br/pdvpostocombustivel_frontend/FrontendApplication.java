package com.br.pdvpostocombustivel_frontend;

import com.br.pdvpostocombustivel_frontend.view.TelaPrincipal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

@SpringBootApplication
public class FrontendApplication implements CommandLineRunner {

    private final TelaPrincipal telaPrincipal;

    public FrontendApplication(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
    }

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            telaPrincipal.setVisible(true);
        });
    }
}