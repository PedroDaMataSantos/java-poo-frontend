package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.enums.TipoAcesso;
import com.br.pdvpostocombustivel_frontend.service.*;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

@Component
public class TelaPrincipal extends JFrame {

    // 🎨 Cores do Tema Dark
    private static final Color BG_DARK = new Color(20, 20, 20);
    private static final Color ACCENT = new Color(0, 255, 200);
    private static final Color TAB_SELECTED = new Color(0, 180, 120);
    private static final Color TAB_UNSELECTED = new Color(40, 40, 40);

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

        // Define o fundo da janela principal
        getContentPane().setBackground(BG_DARK);
    }

    private void criarAbas() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(Color.WHITE);

        // 🎨 Aplica UI customizada para estilizar as abas
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = ACCENT;
                lightHighlight = ACCENT;
                shadow = ACCENT;
                darkShadow = ACCENT;
                focus = ACCENT;
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Borda verde para todas as abas
                g2d.setColor(ACCENT);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x, y, w, h - 1);
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    // Aba selecionada: verde
                    g2d.setColor(TAB_SELECTED);
                } else {
                    // Aba não selecionada: cinza escuro
                    g2d.setColor(TAB_UNSELECTED);
                }
                g2d.fillRect(x, y, w, h);
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                     int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                // Texto branco para todas as abas
                g.setColor(Color.WHITE);
                g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // Remove a borda branca padrão ao redor do conteúdo
                int width = tabbedPane.getWidth();
                int height = tabbedPane.getHeight();
                Insets insets = tabbedPane.getInsets();
                int x = insets.left;
                int y = insets.top;
                int w = width - insets.right - insets.left;
                int h = height - insets.top - insets.bottom;

                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(ACCENT);
                g2d.setStroke(new BasicStroke(2));

                // Desenha apenas a borda externa verde
                switch (tabPlacement) {
                    case LEFT:
                        x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        w -= (x - insets.left);
                        break;
                    case RIGHT:
                        w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                        break;
                    case BOTTOM:
                        h -= calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        break;
                    case TOP:
                    default:
                        y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                        h -= (y - insets.top);
                }

                // Borda verde ao redor do conteúdo
                g2d.drawRect(x, y, w - 1, h - 1);
            }
        });

        tabbedPane.addTab("Pessoas", new TelaPessoaPanel(pessoaService));
        tabbedPane.addTab("Produtos", new TelaProdutoPanel(produtoService));
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