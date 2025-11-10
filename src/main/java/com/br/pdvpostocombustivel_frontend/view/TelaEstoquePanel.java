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

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Estoque"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        txtId = new JTextField(10);
        txtId.setEditable(false);
        gbc.gridx = 1; formPanel.add(txtId, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Status:"), gbc);
        lblTipoCalculado = new JLabel("---");
        lblTipoCalculado.setFont(new Font("Arial", Font.BOLD, 12));
        lblTipoCalculado.setOpaque(true);
        gbc.gridx = 3; formPanel.add(lblTipoCalculado, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Produto:*"), gbc);
        comboProduto = new JComboBox<>();
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(comboProduto, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Local Tanque:*"), gbc);
        txtLocalTanque = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(txtLocalTanque, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Quantidade (L):*"), gbc);
        txtQuantidade = new JTextField();
        gbc.gridx = 1; formPanel.add(txtQuantidade, gbc);
        adicionarCalculoAutomatico();

        gbc.gridx = 2; formPanel.add(new JLabel("Capacidade:"), gbc);
        lblPercentual = new JLabel("0% (0/60.000L)");
        gbc.gridx = 3; formPanel.add(lblPercentual, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Nível:"), gbc);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(progressBar, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Lote Endereço:"), gbc);
        txtLoteEndereco = new JTextField();
        gbc.gridx = 1; formPanel.add(txtLoteEndereco, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Data Validade:"), gbc);
        txtDataValidade = new JFormattedTextField();
        dateFormatter.install(txtDataValidade);
        gbc.gridx = 3; formPanel.add(txtDataValidade, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Lote Fabricação:"), gbc);
        txtLoteFabricacao = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(txtLoteFabricacao, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("💾 Salvar");
        btnExcluir = new JButton("🗑️ Excluir");
        btnLimpar = new JButton("🔄 Limpar");

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
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
                    lblPercentual.setText("0% (0/60.000L)");
                    progressBar.setValue(0);
                    return;
                }
                BigDecimal quantidade = new BigDecimal(txtQuantidade.getText().replace(",", "."));
                BigDecimal percentual = quantidade.multiply(BigDecimal.valueOf(100))
                        .divide(LIMITE_TANQUE, 2, RoundingMode.HALF_UP);
                TipoEstoque tipo = calcularTipo(quantidade);
                lblTipoCalculado.setText(tipo.getDescricao());
                lblPercentual.setText(String.format("%.1f%% (%.0f/60.000L)",
                        percentual.doubleValue(), quantidade.doubleValue()));
                progressBar.setValue(percentual.intValue());
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
        String[] colunas = {"ID", "Produto", "Quantidade", "Local Tanque", "Lote End.", "Lote Fabr.", "Data Val.", "Status", "Bomba"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial Black", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<EstoqueResponse>, Void>() {
            @Override protected List<EstoqueResponse> doInBackground() throws Exception { return estoqueService.listAll(); }
            @Override protected void done() {
                try {
                    List<EstoqueResponse> list = get();
                    tableModel.setRowCount(0);
                    for (EstoqueResponse r : list) {
                        tableModel.addRow(new Object[]{
                                r.id(),
                                r.nomeProduto(),
                                String.format("%.2f L", r.quantidade()),
                                r.localTanque(),
                                r.loteEndereco(),
                                r.loteFabricacao(),
                                r.dataValidade(),
                                r.tipo().getDescricao(),
                                r.numeroBomba() != null ? r.numeroBomba() : "-"
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Erro ao carregar estoques: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtQuantidade.getText().isBlank() || txtLocalTanque.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Quantidade e Local Tanque são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProdutoComboItem itemSelecionado = (ProdutoComboItem) comboProduto.getSelectedItem();
        if (itemSelecionado == null || itemSelecionado.getId() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                BigDecimal quantidade = new BigDecimal(txtQuantidade.getText().trim().replace(",", "."));
                java.util.Date dataValidade = null;
                String dataText = txtDataValidade.getText().replace("_", "").trim();
                if (!dataText.isEmpty()) {
                    dataValidade = java.sql.Date.valueOf(
                            java.time.LocalDate.parse(dataText, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                }

                // Calcula tipo automaticamente
                TipoEstoque tipo = calcularTipo(quantidade);
                // Valor automático para bomba (ajuste se quiser vincular a algo real)
                Integer numeroBomba = 1;

                EstoqueRequest req = new EstoqueRequest(
                        quantidade,
                        txtLocalTanque.getText(),
                        txtLoteEndereco.getText().isBlank() ? null : txtLoteEndereco.getText(),
                        txtLoteFabricacao.getText().isBlank() ? null : txtLoteFabricacao.getText(),
                        dataValidade,
                        tipo,
                        itemSelecionado.getId(),
                        numeroBomba
                );

                if (id == null) estoqueService.create(req);
                else estoqueService.update(id, req);

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Estoque salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um estoque para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception { estoqueService.delete(id); return null; }
            @Override protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Estoque excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
        lblPercentual.setText("0% (0/60.000L)");
        progressBar.setValue(0);
        table.clearSelection();
    }

    private static class ProdutoComboItem {
        private final Long id;
        private final String nome;
        public ProdutoComboItem(Long id, String nome) { this.id = id; this.nome = nome; }
        public Long getId() { return id; }
        @Override public String toString() { return nome; }
    }
}
