package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.PrecoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PrecoResponse;
import com.br.pdvpostocombustivel_frontend.service.PrecoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaPrecoPanel extends JPanel {
    private JTextField txtId;
    private JTextField txtValor;
    private JLabel lblDataAlteracao;
    private JLabel lblHoraAlteracao;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JTable table;
    private DefaultTableModel tableModel;

    private final PrecoService precoService;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public TelaPrecoPanel(PrecoService precoService) {
        this.precoService = precoService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        criarFormulario();
        criarTabela();
        atualizarTabela();
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Preços"));
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

        // Data Alteração (automático)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Data:"), gbc);
        lblDataAlteracao = new JLabel("Automático ao salvar");
        lblDataAlteracao.setForeground(Color.GRAY);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(lblDataAlteracao, gbc);

        // Valor
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Valor (R$):*"), gbc);
        txtValor = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtValor, gbc);

        // Hora Alteração (automático)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Hora:"), gbc);
        lblHoraAlteracao = new JLabel("Automático ao salvar");
        lblHoraAlteracao.setForeground(Color.GRAY);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(lblHoraAlteracao, gbc);

        // Informação adicional
        JLabel lblInfo = new JLabel("💡 Data e hora são registradas automaticamente ao salvar");
        lblInfo.setForeground(Color.BLUE);
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(lblInfo, gbc);

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
        String[] colunas = {"ID", "Valor", "Data Alteração", "Hora Alteração"};
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
        new SwingWorker<List<PrecoResponse>, Void>() {
            @Override
            protected List<PrecoResponse> doInBackground() throws Exception {
                return precoService.listarPrecos();
            }

            @Override
            protected void done() {
                try {
                    List<PrecoResponse> precos = get();
                    tableModel.setRowCount(0);
                    SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");

                    for (PrecoResponse p : precos) {
                        tableModel.addRow(new Object[]{
                                p.id(),
                                "R$ " + decimalFormat.format(p.valor()),
                                p.dataAlteracao(),
                                sdfHora.format(p.horaAlteracao())
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaPrecoPanel.this,
                            "Erro ao buscar preços: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtValor.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "O valor é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(txtValor.getText().replace(",", "."));

            // Data e hora atuais
            SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
            String dataAlteracao = sdfData.format(new Date());
            Date horaAlteracao = new Date();

            PrecoRequest request = new PrecoRequest(
                    valor,
                    dataAlteracao,
                    horaAlteracao
            );

            Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    precoService.salvarPreco(request, id);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TelaPrecoPanel.this, "Preço salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaPrecoPanel.this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Use números com ponto ou vírgula.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um preço para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    precoService.excluirPreco(id);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TelaPrecoPanel.this, "Preço excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaPrecoPanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void preencherFormulario() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());

            String valor = tableModel.getValueAt(row, 1).toString().replace("R$ ", "").replace(".", "").replace(",", ".");
            txtValor.setText(valor);

            lblDataAlteracao.setText(tableModel.getValueAt(row, 2).toString());
            lblDataAlteracao.setForeground(Color.BLACK);

            lblHoraAlteracao.setText(tableModel.getValueAt(row, 3).toString());
            lblHoraAlteracao.setForeground(Color.BLACK);
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtValor.setText("");
        lblDataAlteracao.setText("Automático ao salvar");
        lblDataAlteracao.setForeground(Color.GRAY);
        lblHoraAlteracao.setText("Automático ao salvar");
        lblHoraAlteracao.setForeground(Color.GRAY);
        table.clearSelection();
    }
}