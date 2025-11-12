package com.br.pdvpostocombustivel_frontend;

import com.br.pdvpostocombustivel_frontend.service.BombaService;
import com.br.pdvpostocombustivel_frontend.service.PrecoService;
import com.br.pdvpostocombustivel_frontend.view.TelaBomba;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.time.Duration;

/**
 * Ponto de entrada da aplicação de controle de bombas.
 * Inicializa o contexto Spring e abre o painel principal de bombas.
 */
@SpringBootApplication
public class BombaApplication {

    public static void main(String[] args) {
        // Inicializa o contexto Spring com suporte a interface Swing
        ConfigurableApplicationContext context = new SpringApplicationBuilder(BombaApplication.class)
                .headless(false)
                .run(args);

        // Abre a tela principal do sistema de bombas
        SwingUtilities.invokeLater(() -> {
            BombaService bombaService = context.getBean(BombaService.class);
            PrecoService precoService = context.getBean(PrecoService.class);

            // ✅ Passa o ApplicationContext como terceiro argumento
            TelaBomba tela = new TelaBomba(bombaService, precoService, context);
            tela.setVisible(true);
        });
    }


}
