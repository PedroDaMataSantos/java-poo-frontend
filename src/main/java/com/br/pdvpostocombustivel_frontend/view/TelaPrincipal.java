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
    private final CustoService custoService;
    private final AcessoService acessoService;
    private final ContatoService contatoService;

    public TelaPrincipal(
            PessoaService pessoaService,
            ProdutoService produtoService,
            PrecoService precoService,
            EstoqueService estoqueService,
            CustoService custoService,
            AcessoService acessoService,
            ContatoService contatoService
    ) {
        this.pessoaService = pessoaService;
        this.produtoService = produtoService;
        this.precoService = precoService;
        this.estoqueService = estoqueService;
        this.custoService = custoService;
        this.acessoService = acessoService;
        this.contatoService = contatoService;

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

        TelaPessoaPanel pessoaPanel = new TelaPessoaPanel(pessoaService);
        TelaProdutoPanel produtoPanel = new TelaProdutoPanel(produtoService);
        TelaPrecoPanel precoPanel = new TelaPrecoPanel(precoService);
        TelaEstoquePanel estoquePanel = new TelaEstoquePanel(estoqueService);
        TelaCustoPanel custoPanel = new TelaCustoPanel(custoService);
        TelaAcessoPanel acessoPanel = new TelaAcessoPanel(acessoService);
        TelaContatoPanel contatoPanel = new TelaContatoPanel(contatoService);

        tabbedPane.addTab("Pessoas", pessoaPanel);
        tabbedPane.addTab("Produtos", produtoPanel);
        tabbedPane.addTab("Preços", precoPanel);
        tabbedPane.addTab("Estoque", estoquePanel);
        tabbedPane.addTab("Custos", custoPanel);
        tabbedPane.addTab("Acessos", acessoPanel);
        tabbedPane.addTab("Contatos", contatoPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}