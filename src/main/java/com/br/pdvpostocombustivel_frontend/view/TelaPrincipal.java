package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.service.*;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class TelaPrincipal extends JFrame {

    private final PessoaService pessoaService;
    private final AcessoService acessoService;
    private final ContatoService contatoService;
    private final CustoService custoService;
    private final EstoqueService estoqueService;
    private final PrecoService precoService;
    private final ProdutoService produtoService;

    public TelaPrincipal(
            PessoaService pessoaService,
            AcessoService acessoService,
            ContatoService contatoService,
            CustoService custoService,
            EstoqueService estoqueService,
            PrecoService precoService,
            ProdutoService produtoService
    ) {
        this.pessoaService = pessoaService;
        this.acessoService = acessoService;
        this.contatoService = contatoService;
        this.custoService = custoService;
        this.estoqueService = estoqueService;
        this.precoService = precoService;
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
        TelaAcessoPanel acessoPanel = new TelaAcessoPanel(acessoService);
        TelaContatoPanel contatoPanel = new TelaContatoPanel(contatoService);
        TelaCustoPanel custoPanel = new TelaCustoPanel(custoService);
        TelaEstoquePanel estoquePanel = new TelaEstoquePanel(estoqueService);
        TelaPrecoPanel precoPanel = new TelaPrecoPanel(precoService);
        TelaProdutoPanel produtoPanel = new TelaProdutoPanel(produtoService);

        // Adicionar abas
        tabbedPane.addTab("👥 Pessoas", pessoaPanel);
        tabbedPane.addTab("🔐 Acessos", acessoPanel);
        tabbedPane.addTab("📞 Contatos", contatoPanel);
        tabbedPane.addTab("💰 Custos", custoPanel);
        tabbedPane.addTab("📦 Estoque", estoquePanel);
        tabbedPane.addTab("💲 Preços", precoPanel);
        tabbedPane.addTab("🛢️ Produtos", produtoPanel);

        add(tabbedPane);
    }
}