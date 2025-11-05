package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.service.*;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class TelaPrincipal extends JFrame {

    private final PessoaService pessoaService;
    private final ProdutoService produtoService;

    public TelaPrincipal(
            PessoaService pessoaService,
            ProdutoService produtoService

    ) {
        this.pessoaService = pessoaService;
        this.produtoService = produtoService;

        configurarJanela();
        criarAbas();
    }

    private void configurarJanela() {
        setTitle("Sistema PDV - Posto de Combustível");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarAbas() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Criar painéis para cada aba
        TelaPessoaPanel pessoaPanel = new TelaPessoaPanel(pessoaService);

        TelaProdutoPanel produtoPanel = new TelaProdutoPanel(produtoService);


        // Adicionar abas
        tabbedPane.addTab(" Pessoas", pessoaPanel);
        tabbedPane.addTab("Produtos", produtoPanel);


        add(tabbedPane,BorderLayout.CENTER);
    }
}