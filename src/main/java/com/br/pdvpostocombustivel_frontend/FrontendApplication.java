package com.br.pdvpostocombustivel_frontend;
import com.br.pdvpostocombustivel_frontend.view.TelaPrincipal;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import javax.swing.SwingUtilities;
import java.time.Duration;

@SpringBootApplication
public class FrontendApplication {

    public static void main(String[] args) {
        var context = new SpringApplicationBuilder(FrontendApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            var tela = context.getBean(TelaPrincipal.class);
            tela.setVisible(true);
        });
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}


