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

    private static final Color BG_DARK = new Color(20, 20, 20);
    private static final Color PANEL_BG = new Color(20, 20, 20);
    private static final Color FIELD_BG = new Color(40, 40, 40);
    private static final Color FIELD_FG = Color.WHITE;
    private static final Color ACCENT = new Color(0, 255, 200);
    private static final Color BTN_PRIMARY = new Color(0, 180, 120);
    private static final Color BTN_SECONDARY = new Color(60, 63, 65);

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
        setBackground(BG_DARK);

        criarFormulario();
        criarTabela();
        atualizarTabela();
    }

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
        botao.setBackground(new Color(204, 68, 68));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return botao;
    }

    private JButton criarBotaoTerciario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(60, 63, 65)); // ⚪ Cinza escuro
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
                "Cadastro de Produtos",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                ACCENT
        ));
        formPanel.setBackground(PANEL_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(criarLabel("ID:"), gbc);
        txtId = criarCampoTexto();
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtId, gbc);

        // Tipo Produto
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Tipo:"), gbc);
        comboTipoProduto = new JComboBox<>(TipoProduto.values());
        comboTipoProduto.setBackground(FIELD_BG);
        comboTipoProduto.setForeground(FIELD_FG);
        comboTipoProduto.setFont(new Font("Arial", Font.PLAIN, 12));
        comboTipoProduto.setBorder(BorderFactory.createLineBorder(ACCENT));
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(comboTipoProduto, gbc);

        // Nome
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(criarLabel("Nome:*"), gbc);
        txtNome = criarCampoTexto();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtNome, gbc);
        gbc.gridwidth = 1;

        // Referência
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Referência:*"), gbc);
        txtReferencia = criarCampoTexto();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtReferencia, gbc);
        gbc.gridwidth = 1;

        // Fornecedor e Marca
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(criarLabel("Fornecedor:*"), gbc);
        txtFornecedor = criarCampoTexto();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtFornecedor, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Marca:*"), gbc);
        txtMarca = criarCampoTexto();
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtMarca, gbc);

        // Categoria
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(criarLabel("Categoria:*"), gbc);
        txtCategoria = criarCampoTexto();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtCategoria, gbc);
        gbc.gridwidth = 1;

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_BG);
        btnSalvar  = criarBotaoPrimario("💾 Salvar");
        btnExcluir = criarBotaoSecundario("🗑️ Excluir");
        btnLimpar  = criarBotaoTerciario("🔄 Limpar");

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
        String[] colunas = {"ID", "Nome", "Referência", "Fornecedor", "Categoria", "Marca", "Tipo Produto"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
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

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
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
                        tableModel.addRow(new Object[] {
                                p.id(),
                                p.nome(),
                                p.referencia(),
                                p.fornecedor(),
                                p.categoria(),
                                p.marca(),
                                p.tipoProduto() != null ? p.tipoProduto().getDescricao() : ""
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
