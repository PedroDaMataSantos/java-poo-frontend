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

        // Imposto e Custo Variável
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Imposto (Automático):"), gbc);
        txtImposto = new JTextField("15%");
        txtImposto.setEditable(false);
        txtImposto.setBackground(new Color(240, 240, 240));
        txtImposto.setForeground(new Color(0, 102, 0));
        txtImposto.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtImposto, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Custo Variável:*"), gbc);
        txtCustoVariavel = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtCustoVariavel, gbc);

        // Custo Fixo e Margem Lucro
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

        // Listener para atualizar imposto automaticamente
        txtMargemLucro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizarImposto(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizarImposto(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { atualizarImposto(); }
        });

        // Data Processamento
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Processamento:*"), gbc);
        txtDataProcessamento = new JFormattedTextField();
        dateFormatter.install(txtDataProcessamento);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtDataProcessamento, gbc);

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

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);
    }

    private void atualizarImposto() {
        try {
            double margem = Double.parseDouble(txtMargemLucro.getText().trim().replace(",", "."));
            if (margem > 0) {
                txtImposto.setText("15%");
                txtImposto.setForeground(new Color(0, 102, 0));
            } else {
                txtImposto.setText("0%");
                txtImposto.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            txtImposto.setText("---");
            txtImposto.setForeground(Color.GRAY);
        }
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
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<CustoResponse>, Void>() {
            @Override protected List<CustoResponse> doInBackground() { return custoService.listAll(); }
            @Override protected void done() {
                try {
                    List<CustoResponse> list = get();
                    tableModel.setRowCount(0);
                    for (CustoResponse r : list) {
                        tableModel.addRow(new Object[]{
                                r.id(),
                                String.format("%.0f%%", r.imposto() * 100),
                                r.custoVariavel(),
                                r.custoFixo(),
                                r.margemLucro(),
                                r.dataProcessamento()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaCustoPanel.this, "Erro ao buscar custos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtCustoVariavel.getText().isBlank() || txtCustoFixo.getText().isBlank() ||
                txtMargemLucro.getText().isBlank() || txtDataProcessamento.getText().replace("_", "").trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos obrigatórios devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                try {
                    double custoVariavel = Double.parseDouble(txtCustoVariavel.getText().replace(",", "."));
                    double custoFixo = Double.parseDouble(txtCustoFixo.getText().replace(",", "."));
                    double margem = Double.parseDouble(txtMargemLucro.getText().replace(",", "."));
                    double imposto = margem > 0 ? 0.15 : 0.0;

                    CustoRequest req = new CustoRequest(
                            imposto,
                            custoVariavel,
                            custoFixo,
                            margem,
                            java.sql.Date.valueOf(java.time.LocalDate.parse(txtDataProcessamento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    );

                    if (id == null) custoService.create(req);
                    else custoService.update(id, req);

                } catch (Exception e) {
                    throw new RuntimeException("Erro ao salvar: " + e.getMessage());
                }
                return null;
            }

            @Override protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaCustoPanel.this, "Custo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaCustoPanel.this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um custo para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Excluir este registro?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() { custoService.delete(id); return null; }
                @Override protected void done() {
                    JOptionPane.showMessageDialog(TelaCustoPanel.this, "Excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    atualizarTabela();
                }
            }.execute();
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtImposto.setText("15%");
        txtCustoVariavel.setText("");
        txtCustoFixo.setText("");
        txtMargemLucro.setText("");
        txtDataProcessamento.setText("");
        txtImposto.setForeground(new Color(0, 102, 0));
        table.clearSelection();
    }
}
