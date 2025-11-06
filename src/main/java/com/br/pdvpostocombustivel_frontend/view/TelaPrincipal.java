package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.service.*;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class TelaPrincipal extends JFrame {

    private final PessoaService pessoaService;
    private final ProdutoService produtoService;
    private final PrecoService precoService;
    private final EstoqueService estoqueService;

    public TelaPrincipal(
            PessoaService pessoaService,
            ProdutoService produtoService,
            PrecoService precoService,
            EstoqueService estoqueService
    ) {
        this.pessoaService = pessoaService;
        this.produtoService = produtoService;
        this.precoService = precoService;
        this.estoqueService = estoqueService;

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

        // Painéis já existentes
        TelaPessoaPanel pessoaPanel = new TelaPessoaPanel(pessoaService);
        TelaProdutoPanel produtoPanel = new TelaProdutoPanel(produtoService);

        // ✅ Novos painéis
        TelaPrecoPanel precoPanel = new TelaPrecoPanel(precoService);
        TelaEstoquePanel estoquePanel = new TelaEstoquePanel(estoqueService);

        // Adicionar abas
        tabbedPane.addTab("Pessoas", pessoaPanel);
        tabbedPane.addTab("Produtos", produtoPanel);
        tabbedPane.addTab("Preços", precoPanel);
        tabbedPane.addTab("Estoque", estoquePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
