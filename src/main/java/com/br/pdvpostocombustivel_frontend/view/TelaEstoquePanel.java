package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoEstoque;
import com.br.pdvpostocombustivel_frontend.service.EstoqueService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaEstoquePanel extends JPanel {
    private JFormattedTextField txtDataValidade;
    private JTextField txtQuantidade;
    private JTextField txtLocalTanque;
    private JTextField txtLoteEndereco;
    private JTextField txtLoteFabricacao;
    private JComboBox<TipoEstoque> comboTipo;
    private JTextField txtId;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable table;
    private DefaultTableModel tableModel;

    private final EstoqueService estoqueService;

    private MaskFormatter dateFormatter;

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

        // Tipo Estoque (mesma linha)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        comboTipo = new JComboBox<>(TipoEstoque.values());
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(comboTipo, gbc);

        // Local Tanque (linha 1 full width like Nome)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Local Tanque:*"), gbc);
        txtLocalTanque = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLocalTanque, gbc);
        gbc.gridwidth = 1;

        // Quantidade e Lote Endereço (linha 2, lado a lado)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantidade:*"), gbc);
        txtQuantidade = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtQuantidade, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Lote Endereço:"), gbc);
        txtLoteEndereco = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtLoteEndereco, gbc);

        // Lote Fabricação (linha 3 full width)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Lote Fabricação:"), gbc);
        txtLoteFabricacao = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtLoteFabricacao, gbc);
        gbc.gridwidth = 1;

        // Data Validade (linha 4, left column like Pessoa)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Validade:"), gbc);
        txtDataValidade = new JFormattedTextField();
        dateFormatter.install(txtDataValidade);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtDataValidade, gbc);

        // Botões (linha 5)
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

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Quantidade", "Local Tanque", "Lote Endereço", "Lote Fabr.", "Data Validade", "Tipo"};
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
                                r.quantidade(),
                                r.localTanque(),
                                r.loteEndereco(),
                                r.loteFabricacao(),
                                r.dataValidade(),
                                r.tipo()
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
                    String q = txtQuantidade.getText().trim().replaceAll("[^0-9,\\.]", "");
                    q = q.replace(",", ".");
                    quantidade = new BigDecimal(q);
                } catch (Exception ex) {
                    throw new RuntimeException("Quantidade inválida");
                }

                EstoqueRequest req = new EstoqueRequest(
                        quantidade,
                        txtLocalTanque.getText(),
                        txtLoteEndereco.getText().isBlank() ? null : txtLoteEndereco.getText(),
                        txtLoteFabricacao.getText().isBlank() ? null : txtLoteFabricacao.getText(),
                        txtDataValidade.getText().replace("_", "").trim().isEmpty() ? null : java.sql.Date.valueOf(
                                java.time.LocalDate.parse(txtDataValidade.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        ),
                        (TipoEstoque) comboTipo.getSelectedItem()
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
            txtQuantidade.setText(tableModel.getValueAt(row, 1).toString());
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
            comboTipo.setSelectedItem(tableModel.getValueAt(row, 6));
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtQuantidade.setText("");
        txtLocalTanque.setText("");
        txtLoteEndereco.setText("");
        txtLoteFabricacao.setText("");
        txtDataValidade.setText("");
        comboTipo.setSelectedIndex(0);
        table.clearSelection();
    }
}
