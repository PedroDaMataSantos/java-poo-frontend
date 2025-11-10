package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.enums.TipoAcesso;
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

    private JTabbedPane tabbedPane;
    private TelaAcessoPanel acessoPanel;
    private TelaCustoPanel custoPanel;

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
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        tabbedPane.addTab("Pessoas", new TelaPessoaPanel(pessoaService));
        tabbedPane.addTab("Produtos", new TelaProdutoPanel(produtoService));

        // ✅ Corrigido: passa os dois serviços para o construtor
        tabbedPane.addTab("Preços", new TelaPrecoPanel(precoService, produtoService));

        tabbedPane.addTab("Estoque", new TelaEstoquePanel(estoqueService));

        custoPanel = new TelaCustoPanel(custoService);
        acessoPanel = new TelaAcessoPanel(acessoService);

        tabbedPane.addTab("Custos", custoPanel);
        tabbedPane.addTab("Acessos", acessoPanel);
        tabbedPane.addTab("Contatos", new TelaContatoPanel(contatoService));

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Define as permissões visuais e funcionais com base no perfil do usuário logado.
     */
    public void definirPermissao(String perfilUsuario) {
        if (perfilUsuario == null) return;

        TipoAcesso tipo;
        try {
            tipo = TipoAcesso.valueOf(perfilUsuario.toUpperCase());
        } catch (Exception e) {
            tipo = TipoAcesso.OPERADOR_CAIXA;
        }

        if (tipo == TipoAcesso.OPERADOR_CAIXA) {
            removerAba("Custos");
            removerAba("Acessos");
        }
    }

    private void removerAba(String nomeAba) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equalsIgnoreCase(nomeAba)) {
                tabbedPane.remove(i);
                break;
            }
        }
    }
}
