package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.CustoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.CustoResponse;
import com.br.pdvpostocombustivel_frontend.service.CustoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaCustoPanel extends JPanel {
    private JFormattedTextField txtDataProcessamento;
    private JTextField txtImposto;
    private JTextField txtCustoVariavel;
    private JTextField txtCustoFixo;
    private JTextField txtMargemLucro;
    private JTextField txtId;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable table;
    private DefaultTableModel tableModel;

    private final CustoService custoService;

    private MaskFormatter dateFormatter;

    public TelaCustoPanel(CustoService custoService) {
        this.custoService = custoService;
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Custo"));
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

        // Imposto e Custo Variável (linha 1)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Imposto:*"), gbc);
        txtImposto = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtImposto, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Custo Variável:*"), gbc);
        txtCustoVariavel = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtCustoVariavel, gbc);

        // Custo Fixo e Margem Lucro (linha 2)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Custo Fixo:*"), gbc);
        txtCustoFixo = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtCustoFixo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Margem Lucro:*"), gbc);
        txtMargemLucro = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtMargemLucro, gbc);

        // Data Processamento (linha 3)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Processamento:*"), gbc);
        txtDataProcessamento = new JFormattedTextField();
        dateFormatter.install(txtDataProcessamento);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtDataProcessamento, gbc);

        // Botões (linha 4)
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

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Imposto", "Custo Variável", "Custo Fixo", "Margem Lucro", "Data Processamento"};
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
        new SwingWorker<List<CustoResponse>, Void>() {
            @Override
            protected List<CustoResponse> doInBackground() throws Exception {
                return custoService.listAll();
            }

            @Override
            protected void done() {
                try {
                    List<CustoResponse> list = get();
                    tableModel.setRowCount(0);
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    for (CustoResponse r : list) {
                        tableModel.addRow(new Object[]{
                                r.id(),
                                r.imposto(),
                                r.custoVarivel(),
                                r.custoFixo(),
                                r.margemLucro(),
                                r.dataProcessamento()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaCustoPanel.this,
                            "Erro ao buscar custos: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtImposto.getText().isBlank() || txtCustoVariavel.getText().isBlank() ||
                txtCustoFixo.getText().isBlank() || txtMargemLucro.getText().isBlank() ||
                txtDataProcessamento.getText().replace("_", "").trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Double imposto, custoVariavel, custoFixo, margemLucro;
                try {
                    imposto = Double.parseDouble(txtImposto.getText().trim().replace(",", "."));
                    custoVariavel = Double.parseDouble(txtCustoVariavel.getText().trim().replace(",", "."));
                    custoFixo = Double.parseDouble(txtCustoFixo.getText().trim().replace(",", "."));
                    margemLucro = Double.parseDouble(txtMargemLucro.getText().trim().replace(",", "."));
                } catch (Exception ex) {
                    throw new RuntimeException("Valores numéricos inválidos");
                }

                CustoRequest req = new CustoRequest(
                        imposto,
                        custoVariavel,
                        custoFixo,
                        margemLucro,
                        java.sql.Date.valueOf(
                                java.time.LocalDate.parse(txtDataProcessamento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        )
                );

                if (id == null) {
                    custoService.create(req);
                } else {
                    custoService.update(id, req);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaCustoPanel.this, "Custo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaCustoPanel.this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um custo para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    custoService.delete(id);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TelaCustoPanel.this, "Custo excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaCustoPanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void preencherFormulario() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtImposto.setText(tableModel.getValueAt(row, 1).toString());
            txtCustoVariavel.setText(tableModel.getValueAt(row, 2).toString());
            txtCustoFixo.setText(tableModel.getValueAt(row, 3).toString());
            txtMargemLucro.setText(tableModel.getValueAt(row, 4).toString());

            Object dt = tableModel.getValueAt(row, 5);
            if (dt instanceof java.util.Date) {
                txtDataProcessamento.setValue(dt);
            } else if (dt != null) {
                txtDataProcessamento.setText(dt.toString());
            } else {
                txtDataProcessamento.setText("");
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtImposto.setText("");
        txtCustoVariavel.setText("");
        txtCustoFixo.setText("");
        txtMargemLucro.setText("");
        txtDataProcessamento.setText("");
        table.clearSelection();
    }
}