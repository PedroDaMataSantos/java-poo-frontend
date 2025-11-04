package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.CustoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.CustoResponse;
import com.br.pdvpostocombustivel_frontend.service.CustoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaCustoPanel extends JPanel {
    private JTextField txtId;
    private JTextField txtImposto;
    private JTextField txtCustoFixo;
    private JTextField txtCustoVariavel;
    private JTextField txtMargemLucro;
    private JLabel lblDataProcessamento;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JTable table;
    private DefaultTableModel tableModel;

    private final CustoService custoService;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public TelaCustoPanel(CustoService custoService) {
        this.custoService = custoService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        criarFormulario();
        criarTabela();
        atualizarTabela();
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Custos"));
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

        // Data Processamento (Label apenas, será preenchida automaticamente)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Data/Hora:"), gbc);
        lblDataProcessamento = new JLabel("Automático ao salvar");
        lblDataProcessamento.setForeground(Color.GRAY);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(lblDataProcessamento, gbc);

        // Imposto
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Imposto (%):*"), gbc);
        txtImposto = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtImposto, gbc);

        // Custo Fixo
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Custo Fixo (R$):*"), gbc);
        txtCustoFixo = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtCustoFixo, gbc);

        // Custo Variável
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Custo Variável (R$):*"), gbc);
        txtCustoVariavel = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtCustoVariavel, gbc);

        // Margem Lucro
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Margem Lucro (%):*"), gbc);
        txtMargemLucro = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtMargemLucro, gbc);

        // Botões
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

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Imposto (%)", "Custo Fixo", "Custo Variável", "Margem Lucro (%)", "Data Processamento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial Black", Font.BOLD, 12));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 25));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<CustoResponse>, Void>() {
            @Override
            protected List<CustoResponse> doInBackground() throws Exception {
                return custoService.listarCustos();
            }

            @Override
            protected void done() {
                try {
                    List<CustoResponse> custos = get();
                    tableModel.setRowCount(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    for (CustoResponse c : custos) {
                        tableModel.addRow(new Object[]{
                                c.id(),
                                decimalFormat.format(c.imposto()),
                                "R$ " + decimalFormat.format(c.custoFixo()),
                                "R$ " + decimalFormat.format(c.custoVariavel()),
                                decimalFormat.format(c.margemLucro()),
                                sdf.format(c.dataProcessamento())
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
        if (txtImposto.getText().isBlank() || txtCustoFixo.getText().isBlank() ||
                txtCustoVariavel.getText().isBlank() || txtMargemLucro.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Double imposto = Double.parseDouble(txtImposto.getText().replace(",", "."));
            Double custoFixo = Double.parseDouble(txtCustoFixo.getText().replace(",", "."));
            Double custoVariavel = Double.parseDouble(txtCustoVariavel.getText().replace(",", "."));
            Double margemLucro = Double.parseDouble(txtMargemLucro.getText().replace(",", "."));

            CustoRequest request = new CustoRequest(
                    imposto,
                    custoVariavel,
                    custoFixo,
                    margemLucro,
                    new Date()
            );

            Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    custoService.salvarCusto(request, id);
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

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores inválidos. Use números com ponto ou vírgula.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
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
                    custoService.excluirCusto(id);
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

            String imposto = tableModel.getValueAt(row, 1).toString().replace(".", "").replace(",", ".");
            txtImposto.setText(imposto);

            String custoFixo = tableModel.getValueAt(row, 2).toString().replace("R$ ", "").replace(".", "").replace(",", ".");
            txtCustoFixo.setText(custoFixo);

            String custoVariavel = tableModel.getValueAt(row, 3).toString().replace("R$ ", "").replace(".", "").replace(",", ".");
            txtCustoVariavel.setText(custoVariavel);

            String margemLucro = tableModel.getValueAt(row, 4).toString().replace(".", "").replace(",", ".");
            txtMargemLucro.setText(margemLucro);

            lblDataProcessamento.setText(tableModel.getValueAt(row, 5).toString());
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtImposto.setText("");
        txtCustoFixo.setText("");
        txtCustoVariavel.setText("");
        txtMargemLucro.setText("");
        lblDataProcessamento.setText("Automático ao salvar");
        lblDataProcessamento.setForeground(Color.GRAY);
        table.clearSelection();
    }
}