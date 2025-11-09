package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoEstoque;
import com.br.pdvpostocombustivel_frontend.service.EstoqueService;

import javax.swing.*;
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

    // CONSTANTE DO LIMITE DO TANQUE
    private static final BigDecimal LIMITE_TANQUE = new BigDecimal("60000");

    private JFormattedTextField txtDataValidade;
    private JTextField txtQuantidade;
    private JTextField txtLocalTanque;
    private JTextField txtLoteEndereco;
    private JTextField txtLoteFabricacao;
    // REMOVIDO: comboTipo
    private JLabel lblTipoCalculado; // NOVO: Label para mostrar o tipo calculado
    private JProgressBar progressBar; // NOVO: Barra de progresso visual
    private JTextField txtId;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable table;
    private DefaultTableModel tableModel;

    private final EstoqueService estoqueService;

    private MaskFormatter dateFormatter;
    private boolean isFormatting = false;

    public TelaEstoquePanel(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarFormatadores();
        criarFormulario();
        criarTabela();
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

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Estoque"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        txtId = new JTextField(10);
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtId, gbc);

        // NOVO: Status do Tanque (Campo inativo como ID)
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

        // Local Tanque (linha 1 full width)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Local Tanque:*"), gbc);
        txtLocalTanque = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLocalTanque, gbc);
        gbc.gridwidth = 1;

        // Quantidade (linha 2, lado esquerdo)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantidade (L):*"), gbc);
        txtQuantidade = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtQuantidade, gbc);

        // Adicionar listener para calcular tipo automaticamente e formatar com "L"
        adicionarCalculoAutomatico();
        adicionarFormatacaoLitros();

        // Lote Endereço (ao lado direito)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Lote Endereço:"), gbc);
        txtLoteEndereco = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtLoteEndereco, gbc);

        // NOVO: Barra de progresso visual (linha 3, full width)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Nível:"), gbc);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(progressBar, gbc);
        gbc.gridwidth = 1;

        // Lote Endereço (linha 4, lado esquerdo)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Lote Endereço:"), gbc);
        txtLoteEndereco = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtLoteEndereco, gbc);

        // Data Validade (linha 4, lado direito)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Validade:"), gbc);
        txtDataValidade = new JFormattedTextField();
        dateFormatter.install(txtDataValidade);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtDataValidade, gbc);

        // Lote Fabricação (linha 5, full width)
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(new JLabel("Lote Fabricação:"), gbc);
        txtLoteFabricacao = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLoteFabricacao, gbc);
        gbc.gridwidth = 1;

        // Botões (linha 6)
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

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.NORTH);
    }

    // NOVO MÉTODO: Adiciona formatação com "L" no final
    private void adicionarFormatacaoLitros() {
        txtQuantidade.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                formatarComLitros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                formatarComLitros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                formatarComLitros();
            }

            private void formatarComLitros() {
                if (isFormatting) return;

                SwingUtilities.invokeLater(() -> {
                    isFormatting = true;
                    try {
                        String texto = txtQuantidade.getText();

                        // Remove tudo exceto números, vírgula e ponto
                        String apenasNumeros = texto.replaceAll("[^0-9,\\.]", "");

                        if (apenasNumeros.isEmpty()) {
                            txtQuantidade.setText("");
                            return;
                        }

                        // Adiciona " L" no final se não tiver
                        if (!texto.trim().endsWith("L")) {
                            int caretPos = txtQuantidade.getCaretPosition();
                            txtQuantidade.setText(apenasNumeros + " L");
                            // Posiciona cursor antes do " L"
                            txtQuantidade.setCaretPosition(Math.min(caretPos, apenasNumeros.length()));
                        }
                    } finally {
                        isFormatting = false;
                    }
                });
            }
        });
    }

    // NOVO MÉTODO: Adiciona cálculo automático do tipo
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

    // NOVO MÉTODO: Calcula tipo e percentual automaticamente
    private void calcularTipoEPercentual() {
        SwingUtilities.invokeLater(() -> {
            try {
                String texto = txtQuantidade.getText().trim();
                // Remove " L" para processar
                texto = texto.replace(" L", "").trim();

                if (texto.isEmpty()) {
                    lblTipoCalculado.setText("---");
                    lblTipoCalculado.setForeground(Color.BLACK);
                    lblTipoCalculado.setBackground(new Color(240, 240, 240));
                    progressBar.setValue(0);
                    progressBar.setForeground(Color.GRAY);
                    progressBar.setString("0%");
                    return;
                }

                // Remove caracteres não numéricos exceto vírgula e ponto
                String numeros = texto.replaceAll("[^0-9,\\.]", "");
                numeros = numeros.replace(",", ".");

                BigDecimal quantidade = new BigDecimal(numeros);

                // Valida se não ultrapassa o limite
                if (quantidade.compareTo(LIMITE_TANQUE) > 0) {
                    lblTipoCalculado.setText("EXCEDE LIMITE!");
                    lblTipoCalculado.setForeground(Color.WHITE);
                    lblTipoCalculado.setBackground(Color.RED);
                    progressBar.setValue(100);
                    progressBar.setForeground(Color.RED);
                    progressBar.setString("MÁXIMO: 60.000L");
                    return;
                }

                // Calcula percentual
                BigDecimal percentual = quantidade
                        .multiply(new BigDecimal("100"))
                        .divide(LIMITE_TANQUE, 2, RoundingMode.HALF_UP);

                // Determina o tipo
                TipoEstoque tipo = calcularTipo(quantidade);

                // Atualiza label do tipo
                lblTipoCalculado.setText(tipo.getDescricao());
                lblTipoCalculado.setForeground(Color.WHITE);
                lblTipoCalculado.setBackground(getCorPorTipo(tipo));

                // Atualiza barra de progresso
                progressBar.setValue(percentual.intValue());
                progressBar.setForeground(getCorPorTipo(tipo));
                progressBar.setString(String.format("%.1f%% (%.0f/60.000L)",
                        percentual.doubleValue(), quantidade.doubleValue()));

            } catch (Exception ex) {
                lblTipoCalculado.setText("---");
                lblTipoCalculado.setForeground(Color.BLACK);
                lblTipoCalculado.setBackground(new Color(240, 240, 240));
                progressBar.setValue(0);
                progressBar.setString("Valor inválido");
            }
        });
    }

    // NOVO MÉTODO: Calcula o tipo baseado na quantidade
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

    // NOVO MÉTODO: Retorna cor por tipo
    private Color getCorPorTipo(TipoEstoque tipo) {
        return switch (tipo) {
            case OK -> new Color(34, 139, 34); // Verde
            case BAIXO -> new Color(255, 165, 0); // Laranja
            case CRITICO -> new Color(255, 69, 0); // Vermelho
            case INDISPONIVEL -> Color.GRAY;
        };
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Quantidade", "Local Tanque", "Lote End.", "Lote Fabr.", "Data Val.", "Status"};
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
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    for (EstoqueResponse r : list) {
                        tableModel.addRow(new Object[]{
                                r.id(),
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

    private void salvar() {
        if (txtQuantidade.getText().isBlank() || txtLocalTanque.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Quantidade e Local Tanque são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                BigDecimal quantidade;
                try {
                    String q = txtQuantidade.getText().trim();
                    // Remove " L" e outros caracteres
                    q = q.replace(" L", "").replaceAll("[^0-9,\\.]", "");
                    q = q.replace(",", ".");
                    quantidade = new BigDecimal(q);
                } catch (Exception ex) {
                    throw new RuntimeException("Quantidade inválida");
                }

                // VALIDAÇÃO: Verifica se não ultrapassa o limite
                if (quantidade.compareTo(LIMITE_TANQUE) > 0) {
                    throw new RuntimeException("Quantidade não pode ultrapassar 60.000 litros!");
                }

                // Data de validade
                java.util.Date dataValidade = null;
                String dataText = txtDataValidade.getText().replace("_", "").trim();
                if (!dataText.isEmpty()) {
                    dataValidade = java.sql.Date.valueOf(
                            java.time.LocalDate.parse(dataText, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                }

                // CRIA O REQUEST SEM O TIPO (será calculado no backend)
                EstoqueRequest req = new EstoqueRequest(
                        quantidade,
                        txtLocalTanque.getText(),
                        txtLoteEndereco.getText().isBlank() ? null : txtLoteEndereco.getText(),
                        txtLoteFabricacao.getText().isBlank() ? null : txtLoteFabricacao.getText(),
                        dataValidade

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

            // Extrai apenas o número da quantidade (remove "L")
            String quantidadeStr = tableModel.getValueAt(row, 1).toString();
            quantidadeStr = quantidadeStr.replace(" L", "").trim();
            txtQuantidade.setText(quantidadeStr);

            txtLocalTanque.setText(tableModel.getValueAt(row, 2).toString());
            txtLoteEndereco.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
            txtLoteFabricacao.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
            Object dt = tableModel.getValueAt(row, 5);
            if (dt instanceof java.util.Date) {
                txtDataValidade.setValue(dt);
            } else if (dt != null) {
                txtDataValidade.setText(dt.toString());
            } else {
                txtDataValidade.setText("");
            }
            // REMOVIDO: comboTipo.setSelectedItem

            // Atualiza os indicadores visuais
            calcularTipoEPercentual();
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtQuantidade.setText("");
        txtLocalTanque.setText("");
        txtLoteEndereco.setText("");
        txtLoteFabricacao.setText("");
        txtDataValidade.setText("");
        lblTipoCalculado.setText("---");
        lblTipoCalculado.setForeground(Color.BLACK);
        lblTipoCalculado.setBackground(new Color(240, 240, 240));
        progressBar.setValue(0);
        progressBar.setForeground(Color.GRAY);
        progressBar.setString("0%");
        table.clearSelection();
    }
}