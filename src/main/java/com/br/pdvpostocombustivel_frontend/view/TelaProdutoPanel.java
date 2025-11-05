package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.ProdutoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.ProdutoResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoProduto;
import com.br.pdvpostocombustivel_frontend.service.ProdutoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TelaProdutoPanel extends JPanel {
    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtReferencia;
    private JTextField txtFornecedor;
    private JTextField txtCategoria;
    private JTextField txtMarca;
    private JComboBox<TipoProduto> comboTipoProduto;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JTable table;
    private DefaultTableModel tableModel;

    private final ProdutoService produtoService;

    public TelaProdutoPanel(ProdutoService produtoService) {
        this.produtoService = produtoService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        criarFormulario();
        criarTabela();
        atualizarTabela();
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Produtos"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // === Linha 1: ID e Tipo ===
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("ID:"), gbc);
        txtId = new JTextField(10);
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.5;
        formPanel.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        comboTipoProduto = new JComboBox<>(TipoProduto.values());
        gbc.gridx = 3; gbc.weightx = 0.5;
        formPanel.add(comboTipoProduto, gbc);

        // === Linha 2: Nome ===
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Nome:*"), gbc);
        txtNome = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtNome, gbc);
        gbc.gridwidth = 1;

        // === Linha 3: Referência ===
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Referência:*"), gbc);
        txtReferencia = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtReferencia, gbc);
        gbc.gridwidth = 1;

        // === Linha 4: Fornecedor e Marca (lado a lado) ===
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Fornecedor:*"), gbc);
        txtFornecedor = new JTextField();
        gbc.gridx = 1; gbc.weightx = 0.5;
        formPanel.add(txtFornecedor, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Marca:*"), gbc);
        txtMarca = new JTextField();
        gbc.gridx = 3; gbc.weightx = 0.5;
        formPanel.add(txtMarca, gbc);

        // === Linha 5: Categoria (sozinha) ===
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Categoria:*"), gbc);
        txtCategoria = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtCategoria, gbc);
        gbc.gridwidth = 1;

        // === Linha 6: Botões ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
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

        add(formPanel, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Nome", "Referência", "Fornecedor", "Categoria", "Marca", "Tipo Produto"};
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
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<ProdutoResponse>, Void>() {
            @Override
            protected List<ProdutoResponse> doInBackground() throws Exception {
                return produtoService.listarProdutos();
            }

            @Override
            protected void done() {
                try {
                    List<ProdutoResponse> produtos = get();
                    tableModel.setRowCount(0);

                    for (ProdutoResponse p : produtos) {
                        tableModel.addRow(new Object[]{
                                p.id(),
                                p.nome(),
                                p.referencia(),
                                p.fornecedor(),
                                p.categoria(),
                                p.marca(),
                                p.tipoProduto().getDescricao()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaProdutoPanel.this,
                            "Erro ao buscar produtos: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtNome.getText().isBlank() || txtReferencia.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Campos obrigatórios não preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProdutoRequest request = new ProdutoRequest(
                txtNome.getText(),
                txtReferencia.getText(),
                txtFornecedor.getText(),
                txtCategoria.getText(),
                txtMarca.getText(),
                (TipoProduto) comboTipoProduto.getSelectedItem()
        );

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                produtoService.salvarProduto(request, id);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaProdutoPanel.this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaProdutoPanel.this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    produtoService.excluirProduto(id);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TelaProdutoPanel.this, "Produto excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaProdutoPanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void preencherFormulario() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNome.setText(tableModel.getValueAt(row, 1).toString());
            txtReferencia.setText(tableModel.getValueAt(row, 2).toString());
            txtFornecedor.setText(tableModel.getValueAt(row, 3).toString());
            txtCategoria.setText(tableModel.getValueAt(row, 4).toString());
            txtMarca.setText(tableModel.getValueAt(row, 5).toString());

            String tipoDesc = tableModel.getValueAt(row, 6).toString();
            for (TipoProduto tp : TipoProduto.values()) {
                if (tp.getDescricao().equals(tipoDesc)) {
                    comboTipoProduto.setSelectedItem(tp);
                    break;
                }
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtReferencia.setText("");
        txtFornecedor.setText("");
        txtCategoria.setText("");
        txtMarca.setText("");
        comboTipoProduto.setSelectedIndex(0);
        table.clearSelection();
    }
}
