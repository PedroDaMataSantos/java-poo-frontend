package com.br.pdvpostocombustivel_frontend;

import com.br.pdvpostocombustivel_frontend.service.AcessoService;
import com.br.pdvpostocombustivel_frontend.view.TelaLogin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import javax.swing.*;
import java.time.Duration;

@SpringBootApplication
public class FrontendApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(FrontendApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            AcessoService acessoService = context.getBean(AcessoService.class);
            TelaLogin telaLogin = new TelaLogin(acessoService, context);
            telaLogin.setVisible(true);
        });
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}
