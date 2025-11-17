package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import com.br.pdvpostocombustivel_frontend.model.dto.ProdutoResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoEstoque;
import com.br.pdvpostocombustivel_frontend.service.EstoqueService;
import com.br.pdvpostocombustivel_frontend.service.ProdutoService;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaEstoquePanel extends JPanel {

    // 🎨 Cores do Tema Dark
    private static final Color BG_DARK = new Color(20, 20, 20);
    private static final Color PANEL_BG = new Color(20, 20, 20);
    private static final Color FIELD_BG = new Color(40, 40, 40);
    private static final Color FIELD_FG = Color.WHITE;
    private static final Color ACCENT = new Color(0, 255, 200);
    private static final Color BTN_PRIMARY = new Color(0, 180, 120);
    private static final Color BTN_DANGER = new Color(204, 68, 68);
    private static final Color BTN_SECONDARY = new Color(60, 63, 65);

    private static final BigDecimal LIMITE_TANQUE = new BigDecimal("60000");

    private JFormattedTextField txtDataValidade;
    private JTextField txtQuantidade;
    private JTextField txtLocalTanque;
    private JTextField txtLoteEndereco;
    private JTextField txtLoteFabricacao;
    private JComboBox<ProdutoComboItem> comboProduto;
    private JLabel lblTipoCalculado;
    private JLabel lblPercentual;
    private JProgressBar progressBar;
    private JTextField txtId;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable table;
    private DefaultTableModel tableModel;

    private final EstoqueService estoqueService;
    private final ProdutoService produtoService;

    private MaskFormatter dateFormatter;
    private List<ProdutoResponse> produtosDisponiveis;

    public TelaEstoquePanel(EstoqueService estoqueService) {
        this(estoqueService, new ProdutoService(new org.springframework.web.client.RestTemplate()));
    }

    public TelaEstoquePanel(EstoqueService estoqueService, ProdutoService produtoService) {
        this.estoqueService = estoqueService;
        this.produtoService = produtoService;
        this.produtosDisponiveis = java.util.List.of();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(BG_DARK);

        inicializarFormatadores();
        criarFormulario();
        criarTabela();
        adicionarListenerDeAbas();

        carregarProdutosAsync();
        atualizarTabela();
    }

    private void inicializarFormatadores() {
        try {
            dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            dateFormatter.setValueContainsLiteralCharacters(false);
        } catch (ParseException e) {
            System.err.println("Erro ao criar formatadores: " + e.getMessage());
        }
    }

    private void adicionarListenerDeAbas() {
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (produtosDisponiveis == null || produtosDisponiveis.isEmpty()) {
                    carregarProdutosAsync();
                }
            }

            @Override public void ancestorRemoved(AncestorEvent event) {}
            @Override public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void carregarProdutosAsync() {
        new SwingWorker<List<ProdutoResponse>, Void>() {
            @Override
            protected List<ProdutoResponse> doInBackground() throws Exception {
                return produtoService.listarProdutos();
            }

            @Override
            protected void done() {
                try {
                    produtosDisponiveis = get();
                    preencherComboProdutos();
                } catch (Exception e) {
                    produtosDisponiveis = List.of();
                    preencherComboProdutos();
                    System.err.println("Erro ao carregar produtos: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void preencherComboProdutos() {
        comboProduto.removeAllItems();
        comboProduto.addItem(new ProdutoComboItem(null, "-- Selecione um Produto --"));
        if (produtosDisponiveis == null || produtosDisponiveis.isEmpty()) return;

        for (ProdutoResponse p : produtosDisponiveis) {
            String nomeExibicao = p.nome() + (p.tipoProduto() != null ? " - " + p.tipoProduto().getDescricao() : "");
            comboProduto.addItem(new ProdutoComboItem(p.id(), nomeExibicao));
        }

        comboProduto.setSelectedIndex(0);
    }

    // 🎨 Métodos auxiliares para criar componentes estilizados
    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(ACCENT);
        return label;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBackground(FIELD_BG);
        campo.setForeground(FIELD_FG);
        campo.setCaretColor(ACCENT);
        campo.setBorder(BorderFactory.createLineBorder(ACCENT));
        return campo;
    }

    private JFormattedTextField criarCampoFormatado() {
        JFormattedTextField campo = new JFormattedTextField();
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBackground(FIELD_BG);
        campo.setForeground(FIELD_FG);
        campo.setCaretColor(ACCENT);
        campo.setBorder(BorderFactory.createLineBorder(ACCENT));
        return campo;
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(BTN_PRIMARY);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return botao;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(BTN_DANGER);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return botao;
    }

    private JButton criarBotaoTerciario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(BTN_SECONDARY);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return botao;
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                "Cadastro de Estoque",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                ACCENT
        ));
        formPanel.setBackground(PANEL_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID e Status
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(criarLabel("ID:"), gbc);
        txtId = criarCampoTexto();
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Status:"), gbc);
        lblTipoCalculado = new JLabel("---");
        lblTipoCalculado.setFont(new Font("Arial", Font.BOLD, 12));
        lblTipoCalculado.setForeground(ACCENT);
        lblTipoCalculado.setOpaque(true);
        lblTipoCalculado.setBackground(FIELD_BG);
        lblTipoCalculado.setBorder(BorderFactory.createLineBorder(ACCENT));
        lblTipoCalculado.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(lblTipoCalculado, gbc);

        // Produto
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(criarLabel("Produto:*"), gbc);
        comboProduto = new JComboBox<>();
        comboProduto.setBackground(FIELD_BG);
        comboProduto.setForeground(FIELD_FG);
        comboProduto.setFont(new Font("Arial", Font.PLAIN, 12));
        comboProduto.setBorder(BorderFactory.createLineBorder(ACCENT));
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(comboProduto, gbc);
        gbc.gridwidth = 1;

        // Local Tanque
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Local Tanque:*"), gbc);
        txtLocalTanque = criarCampoTexto();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLocalTanque, gbc);
        gbc.gridwidth = 1;

        // Quantidade e Capacidade
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(criarLabel("Quantidade (L):*"), gbc);
        txtQuantidade = criarCampoTexto();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtQuantidade, gbc);
        adicionarCalculoAutomatico();

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Capacidade:"), gbc);
        lblPercentual = new JLabel("0% (0/60.000L)");
        lblPercentual.setForeground(FIELD_FG);
        lblPercentual.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(lblPercentual, gbc);

        // Nível (Barra de Progresso)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(criarLabel("Nível:"), gbc);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setBackground(FIELD_BG);
        progressBar.setForeground(new Color(34, 139, 34));
        progressBar.setBorder(BorderFactory.createLineBorder(ACCENT));
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(progressBar, gbc);
        gbc.gridwidth = 1;

        // Lote Endereço e Data Validade
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(criarLabel("Lote Endereço:"), gbc);
        txtLoteEndereco = criarCampoTexto();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtLoteEndereco, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Data Validade:"), gbc);
        txtDataValidade = criarCampoFormatado();
        dateFormatter.install(txtDataValidade);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtDataValidade, gbc);

        // Lote Fabricação
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(criarLabel("Lote Fabricação:"), gbc);
        txtLoteFabricacao = criarCampoTexto();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLoteFabricacao, gbc);
        gbc.gridwidth = 1;

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_BG);

        btnSalvar = criarBotaoPrimario("💾 Salvar");
        btnExcluir = criarBotaoSecundario("🗑️ Excluir");
        btnLimpar = criarBotaoTerciario("🔄 Limpar");

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.NORTH);
    }

    private void adicionarCalculoAutomatico() {
        txtQuantidade.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calcularTipoEPercentual(); }
            @Override public void removeUpdate(DocumentEvent e) { calcularTipoEPercentual(); }
            @Override public void changedUpdate(DocumentEvent e) { calcularTipoEPercentual(); }
        });
    }

    private void calcularTipoEPercentual() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (txtQuantidade.getText().trim().isEmpty()) {
                    lblTipoCalculado.setText("---");
                    lblTipoCalculado.setForeground(ACCENT);
                    lblPercentual.setText("0% (0/60.000L)");
                    progressBar.setValue(0);
                    progressBar.setForeground(Color.LIGHT_GRAY);
                    return;
                }
                BigDecimal quantidade = new BigDecimal(txtQuantidade.getText().replace(".", "").replace(",", ".").replace("L", "").trim());
                BigDecimal percentual = quantidade.multiply(BigDecimal.valueOf(100))
                        .divide(LIMITE_TANQUE, 2, RoundingMode.HALF_UP);
                TipoEstoque tipo = calcularTipo(quantidade);

                lblTipoCalculado.setText(tipo.getDescricao());
                lblPercentual.setText(String.format("%.1f%% (%.0f/60.000L)", percentual.doubleValue(), quantidade.doubleValue()));
                progressBar.setValue(percentual.intValue());

                // Cores sincronizadas com o tipo
                if (percentual.compareTo(BigDecimal.valueOf(50)) >= 0) {
                    progressBar.setForeground(new Color(34, 139, 34));
                    lblTipoCalculado.setForeground(new Color(34, 139, 34));
                } else if (percentual.compareTo(BigDecimal.valueOf(25)) >= 0) {
                    progressBar.setForeground(new Color(255, 193, 7));
                    lblTipoCalculado.setForeground(new Color(255, 193, 7));
                } else if (percentual.compareTo(BigDecimal.ZERO) > 0) {
                    progressBar.setForeground(new Color(220, 53, 69));
                    lblTipoCalculado.setForeground(new Color(220, 53, 69));
                } else {
                    progressBar.setForeground(Color.LIGHT_GRAY);
                    lblTipoCalculado.setForeground(Color.LIGHT_GRAY);
                }

            } catch (Exception ignored) {}
        });
    }

    private TipoEstoque calcularTipo(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0)
            return TipoEstoque.INDISPONIVEL;
        BigDecimal perc = quantidade.multiply(BigDecimal.valueOf(100))
                .divide(LIMITE_TANQUE, 2, RoundingMode.HALF_UP);
        if (perc.compareTo(BigDecimal.valueOf(50)) >= 0) return TipoEstoque.OK;
        if (perc.compareTo(BigDecimal.valueOf(25)) >= 0) return TipoEstoque.BAIXO;
        if (perc.compareTo(BigDecimal.ZERO) > 0) return TipoEstoque.CRITICO;
        return TipoEstoque.INDISPONIVEL;
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Produto", "Quantidade", "Local Tanque", "Lote End.", "Lote Fabr.", "Data Val.", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 70));
        table.setSelectionBackground(new Color(0, 120, 90));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial Black", Font.BOLD, 12));
        header.setForeground(ACCENT);
        header.setBackground(new Color(15, 15, 15));
        header.setPreferredSize(new Dimension(header.getWidth(), 25));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<EstoqueResponse>, Void>() {
            @Override protected List<EstoqueResponse> doInBackground() throws Exception {
                return estoqueService.listAll();
            }

            @Override protected void done() {
                try {
                    List<EstoqueResponse> list = get();
                    tableModel.setRowCount(0);
                    for (EstoqueResponse r : list) {
                        tableModel.addRow(new Object[]{
                                r.id(),
                                r.nomeProduto(),
                                String.format("%,.0f L", r.quantidade()),
                                r.localTanque(),
                                r.loteEndereco(),
                                r.loteFabricacao(),
                                r.dataValidade(),
                                r.tipo().getDescricao()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this,
                            "Erro ao carregar estoques: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtQuantidade.getText().isBlank() || txtLocalTanque.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Quantidade e Local Tanque são obrigatórios.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProdutoComboItem itemSelecionado = (ProdutoComboItem) comboProduto.getSelectedItem();
        if (itemSelecionado == null || itemSelecionado.getId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<EstoqueResponse, Void>() {
            @Override
            protected EstoqueResponse doInBackground() throws Exception {
                BigDecimal quantidade = new BigDecimal(txtQuantidade.getText()
                        .replace(".", "")
                        .replace(",", ".")
                        .replace("L", "")
                        .trim());

                java.util.Date dataValidade = null;
                String dataText = txtDataValidade.getText().replace("_", "").trim();
                if (!dataText.isEmpty()) {
                    dataValidade = java.sql.Date.valueOf(
                            java.time.LocalDate.parse(dataText, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                }

                EstoqueRequest req = new EstoqueRequest(
                        quantidade,
                        txtLocalTanque.getText(),
                        txtLoteEndereco.getText().isBlank() ? null : txtLoteEndereco.getText(),
                        txtLoteFabricacao.getText().isBlank() ? null : txtLoteFabricacao.getText(),
                        dataValidade,
                        itemSelecionado.getId()
                );

                if (id == null) {
                    return estoqueService.create(req);
                } else {
                    return estoqueService.update(id, req);
                }
            }

            @Override
            protected void done() {
                try {
                    EstoqueResponse response = get();
                    if (response != null) {
                        JOptionPane.showMessageDialog(TelaEstoquePanel.this,
                                "Estoque salvo com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        atualizarTabela();
                    } else {
                        JOptionPane.showMessageDialog(TelaEstoquePanel.this,
                                "Erro ao salvar estoque (resposta nula).",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this,
                            "Erro ao salvar: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um estoque para excluir.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                estoqueService.delete(id);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this,
                            "Estoque excluído!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this,
                            "Erro ao excluir: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void limparFormulario() {
        txtId.setText("");
        txtQuantidade.setText("");
        txtLocalTanque.setText("");
        txtLoteEndereco.setText("");
        txtLoteFabricacao.setText("");
        txtDataValidade.setText("");
        comboProduto.setSelectedIndex(0);
        lblTipoCalculado.setText("---");
        lblTipoCalculado.setForeground(ACCENT);
        lblPercentual.setText("0% (0/60.000L)");
        progressBar.setValue(0);
        progressBar.setForeground(Color.LIGHT_GRAY);
        table.clearSelection();
    }

    private static class ProdutoComboItem {
        private final Long id;
        private final String nome;
        public ProdutoComboItem(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }
        public Long getId() { return id; }
        @Override public String toString() { return nome; }
    }
}