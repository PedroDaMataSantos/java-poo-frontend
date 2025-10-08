package com.br.pdvpostocombustivel_frontend;

import com.br.pdvpostocombustivel_frontend.view.TelaPessoaCrud; // Importa nossa tela
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import javax.swing.SwingUtilities;

@SpringBootApplication
public class FrontendApplication {
    public static void main(String[] args) {

        var context = new SpringApplicationBuilder(FrontendApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            var tela = context.getBean(TelaPessoaCrud.class);
            tela.setVisible(true);
        });
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}