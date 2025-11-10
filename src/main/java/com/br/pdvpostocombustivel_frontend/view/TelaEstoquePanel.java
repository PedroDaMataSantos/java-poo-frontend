package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import com.br.pdvpostocombustivel_frontend.model.dto.ProdutoResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoEstoque;
import com.br.pdvpostocombustivel_frontend.service.EstoqueService;
import com.br.pdvpostocombustivel_frontend.service.ProdutoService;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private ProdutoService produtoService;

    private MaskFormatter dateFormatter;
    private List<ProdutoResponse> produtosDisponiveis;

    /**
     * Construtor padrão: cria um ProdutoService com RestTemplate para uso standalone.
     * Se você estiver integrando com injeção Spring, use o outro construtor.
     */
    public TelaEstoquePanel(EstoqueService estoqueService) {
        this(estoqueService, new ProdutoService(new org.springframework.web.client.RestTemplate()));
    }

    public TelaEstoquePanel(EstoqueService estoqueService, ProdutoService produtoService) {
        this.estoqueService = estoqueService;
        this.produtoService = produtoService != null ? produtoService
                : new ProdutoService(new org.springframework.web.client.RestTemplate());
        this.produtosDisponiveis = java.util.List.of();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarFormatadores();
        criarFormulario();
        criarTabela();
        adicionarListenerDeAbas();

        // Carrega produtos imediatamente (assíncrono)
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
                // Recarrega apenas se não houver produtos carregados (evita chamadas repetidas)
                if (produtoService != null && (produtosDisponiveis == null || produtosDisponiveis.isEmpty())) {
                    carregarProdutosAsync();
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) { }

            @Override
            public void ancestorMoved(AncestorEvent event) { }
        });
    }

    private void carregarProdutosAsync() {
        if (produtoService == null) {
            System.err.println("ProdutoService não inicializado. Não é possível carregar produtos.");
            return;
        }

        new SwingWorker<List<ProdutoResponse>, Void>() {
            @Override
            protected List<ProdutoResponse> doInBackground() throws Exception {
                return produtoService.listarProdutos();
            }

            @Override
            protected void done() {
                try {
                    List<ProdutoResponse> list = get();
                    produtosDisponiveis = list != null ? list : List.of();
                    System.out.println("Produtos carregados: " + produtosDisponiveis.size());
                    preencherComboProdutos();
                } catch (Exception e) {
                    System.err.println("Erro ao carregar produtos: " + e.getMessage());
                    e.printStackTrace();
                    produtosDisponiveis = List.of();
                    preencherComboProdutos(); // garante que o combo seja resetado
                }
            }
        }.execute();
    }

    private void preencherComboProdutos() {
        comboProduto.removeAllItems();
        comboProduto.addItem(new ProdutoComboItem(null, "-- Selecione um Produto --"));

        if (produtosDisponiveis == null || produtosDisponiveis.isEmpty()) {
            System.out.println("Nenhum produto disponível para preencher o combo.");
            comboProduto.setSelectedIndex(0);
            return;
        }

        for (ProdutoResponse p : produtosDisponiveis) {
            String nomeExibicao = safeNomeProduto(p);
            comboProduto.addItem(new ProdutoComboItem(p.id(), nomeExibicao));
        }

        // garante que o índice 0 (placeholder) esteja selecionado após preencher
        comboProduto.setSelectedIndex(0);

        System.out.println("Combo preenchido com " + comboProduto.getItemCount() + " itens");
    }

    // Helper para montar a string de exibição com segurança contra nulls
    private String safeNomeProduto(ProdutoResponse p) {
        if (p == null) return "Produto inválido";
        String nome = p.nome() != null ? p.nome() : "Sem nome";
        try {
            if (p.tipoProduto() != null) {
                nome += " - " + p.tipoProduto().getDescricao();
            }
        } catch (Exception ignore) {
            // caso p.tipoProduto() não exista ou esteja em formato inesperado
        }
        return nome;
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Estoque"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        txtId = new JTextField(10);
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        lblTipoCalculado = new JLabel("---");
        lblTipoCalculado.setFont(new Font("Arial", Font.BOLD, 12));
        lblTipoCalculado.setOpaque(true);
        lblTipoCalculado.setBackground(new Color(240, 240, 240));
        lblTipoCalculado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(lblTipoCalculado, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Produto:*"), gbc);
        comboProduto = new JComboBox<>();
        comboProduto.addItem(new ProdutoComboItem(null, "-- Selecione um Produto --"));
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(comboProduto, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Local Tanque:*"), gbc);
        txtLocalTanque = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLocalTanque, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantidade (L):*"), gbc);
        txtQuantidade = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtQuantidade, gbc);
        adicionarCalculoAutomatico();

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Capacidade:"), gbc);
        lblPercentual = new JLabel("0% (0/60.000L)");
        lblPercentual.setFont(new Font("Arial", Font.PLAIN, 11));
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(lblPercentual, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Nível:"), gbc);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(progressBar, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(new JLabel("Lote Endereço:"), gbc);
        txtLoteEndereco = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtLoteEndereco, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Validade:"), gbc);
        txtDataValidade = new JFormattedTextField();
        dateFormatter.install(txtDataValidade);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtDataValidade, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(new JLabel("Lote Fabricação:"), gbc);
        txtLoteFabricacao = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLoteFabricacao, gbc);
        gbc.gridwidth = 1;

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
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.NORTH);
    }

    private void adicionarCalculoAutomatico() {
        txtQuantidade.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calcularTipoEPercentual();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calcularTipoEPercentual();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calcularTipoEPercentual();
            }
        });
    }

    private void calcularTipoEPercentual() {
        SwingUtilities.invokeLater(() -> {
            try {
                String texto = txtQuantidade.getText().trim();
                if (texto.isEmpty()) {
                    lblTipoCalculado.setText("---");
                    lblTipoCalculado.setForeground(Color.BLACK);
                    lblPercentual.setText("0% (0/60.000L)");
                    progressBar.setValue(0);
                    progressBar.setForeground(Color.GRAY);
                    return;
                }

                String numeros = texto.replaceAll("[^0-9,\\.]", "");
                numeros = numeros.replace(",", ".");

                BigDecimal quantidade = new BigDecimal(numeros);

                if (quantidade.compareTo(LIMITE_TANQUE) > 0) {
                    lblTipoCalculado.setText("EXCEDE LIMITE!");
                    lblTipoCalculado.setForeground(Color.RED);
                    lblPercentual.setText("MÁXIMO: 60.000L");
                    progressBar.setValue(100);
                    progressBar.setForeground(Color.RED);
                    return;
                }

                BigDecimal percentual = quantidade
                        .multiply(new BigDecimal("100"))
                        .divide(LIMITE_TANQUE, 2, RoundingMode.HALF_UP);

                TipoEstoque tipo = calcularTipo(quantidade);

                lblTipoCalculado.setText(tipo.getDescricao());
                lblTipoCalculado.setForeground(getCorPorTipo(tipo));

                lblPercentual.setText(String.format("%.1f%% (%.0f/60.000L)",
                        percentual.doubleValue(), quantidade.doubleValue()));

                progressBar.setValue(percentual.intValue());
                progressBar.setForeground(getCorPorTipo(tipo));

            } catch (Exception ex) {
                lblTipoCalculado.setText("---");
                lblTipoCalculado.setForeground(Color.BLACK);
                lblPercentual.setText("Valor inválido");
                progressBar.setValue(0);
            }
        });
    }

    private TipoEstoque calcularTipo(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
            return TipoEstoque.INDISPONIVEL;
        }

        BigDecimal percentual = quantidade
                .multiply(new BigDecimal("100"))
                .divide(LIMITE_TANQUE, 2, RoundingMode.HALF_UP);

        if (percentual.compareTo(new BigDecimal("50")) >= 0) {
            return TipoEstoque.OK;
        } else if (percentual.compareTo(new BigDecimal("25")) >= 0) {
            return TipoEstoque.BAIXO;
        } else if (percentual.compareTo(BigDecimal.ZERO) > 0) {
            return TipoEstoque.CRITICO;
        } else {
            return TipoEstoque.INDISPONIVEL;
        }
    }

    private Color getCorPorTipo(TipoEstoque tipo) {
        return switch (tipo) {
            case OK -> new Color(34, 139, 34);
            case BAIXO -> new Color(255, 165, 0);
            case CRITICO -> new Color(255, 69, 0);
            case INDISPONIVEL -> Color.GRAY;
        };
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Produto", "Quantidade", "Local Tanque", "Lote End.", "Lote Fabr.", "Data Val.", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial Black", Font.BOLD, 12));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 25));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<EstoqueResponse>, Void>() {
            @Override
            protected List<EstoqueResponse> doInBackground() throws Exception {
                return estoqueService.listAll();
            }

            @Override
            protected void done() {
                try {
                    List<EstoqueResponse> list = get();
                    tableModel.setRowCount(0);

                    for (EstoqueResponse r : list) {
                        String nomeProduto = obterNomeProduto(r.idProduto());

                        tableModel.addRow(new Object[]{
                                r.id(),
                                nomeProduto,
                                String.format("%.2f L", r.quantidade()),
                                r.localTanque(),
                                r.loteEndereco(),
                                r.loteFabricacao(),
                                r.dataValidade(),
                                r.tipo().getDescricao()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaEstoquePanel.this,
                            "Erro ao buscar estoques: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private String obterNomeProduto(Long idProduto) {
        if (produtosDisponiveis == null || produtosDisponiveis.isEmpty()) {
            return "Produto não carregado";
        }
        return produtosDisponiveis.stream()
                .filter(p -> p.id() != null && p.id().equals(idProduto))
                .map(p -> {
                    try {
                        return p.nome() + " - " + (p.tipoProduto() != null ? p.tipoProduto().getDescricao() : "");
                    } catch (Exception ex) {
                        return p.nome();
                    }
                })
                .findFirst()
                .orElse("Produto não encontrado");
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
                BigDecimal quantidade;
                try {
                    String q = txtQuantidade.getText().trim().replaceAll("[^0-9,\\.]", "");
                    q = q.replace(",", ".");
                    quantidade = new BigDecimal(q);
                } catch (Exception ex) {
                    throw new RuntimeException("Quantidade inválida");
                }

                if (quantidade.compareTo(LIMITE_TANQUE) > 0) {
                    throw new RuntimeException("Quantidade não pode ultrapassar 60.000 litros!");
                }

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
                    estoqueService.create(req);
                } else {
                    estoqueService.update(id, req);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Estoque salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    // recarrega produtos e tabela para garantir consistência visual
                    carregarProdutosAsync();
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

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
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
                        JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Estoque excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        carregarProdutosAsync();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaEstoquePanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void preencherFormulario() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());

            String quantidadeStr = tableModel.getValueAt(row, 2).toString();
            quantidadeStr = quantidadeStr.replace(" L", "").trim();
            txtQuantidade.setText(quantidadeStr);

            txtLocalTanque.setText(tableModel.getValueAt(row, 3).toString());
            txtLoteEndereco.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
            txtLoteFabricacao.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");

            Object dt = tableModel.getValueAt(row, 6);
            if (dt instanceof java.util.Date) {
                txtDataValidade.setValue(dt);
            } else if (dt != null) {
                txtDataValidade.setText(dt.toString());
            } else {
                txtDataValidade.setText("");
            }

            Long idEstoque = (Long) tableModel.getValueAt(row, 0);
            try {
                EstoqueResponse estoque = estoqueService.getById(idEstoque);
                selecionarProdutoNoCombo(estoque.idProduto());
            } catch (Exception e) {
                System.err.println("Erro ao buscar produto do estoque: " + e.getMessage());
            }

            calcularTipoEPercentual();
        }
    }

    private void selecionarProdutoNoCombo(Long idProduto) {
        for (int i = 0; i < comboProduto.getItemCount(); i++) {
            ProdutoComboItem item = comboProduto.getItemAt(i);
            if (item.getId() != null && item.getId().equals(idProduto)) {
                comboProduto.setSelectedIndex(i);
                break;
            }
        }
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
        lblTipoCalculado.setForeground(Color.BLACK);
        lblPercentual.setText("0% (0/60.000L)");
        progressBar.setValue(0);
        progressBar.setForeground(Color.GRAY);
        table.clearSelection();
    }

    private static class ProdutoComboItem {
        private final Long id;
        private final String nome;

        public ProdutoComboItem(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return nome;
        }
    }
}
