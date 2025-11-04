package com.br.pdvpostocombustivel_frontend;

import com.br.pdvpostocombustivel_frontend.view.TelaPrincipal;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

@SpringBootApplication
public class FrontendApplication {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        var context = new SpringApplicationBuilder(FrontendApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            var tela = context.getBean(TelaPrincipal.class);
            tela.setVisible(true);
        });
    }

}