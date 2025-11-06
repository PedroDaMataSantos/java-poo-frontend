package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.AcessoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.AcessoResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoAcesso;
import com.br.pdvpostocombustivel_frontend.service.AcessoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TelaAcessoPanel extends JPanel {
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JComboBox<String> comboPerfil;
    private JTextField txtId;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable table;
    private DefaultTableModel tableModel;

    private final AcessoService acessoService;

    public TelaAcessoPanel(AcessoService acessoService) {
        this.acessoService = acessoService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        criarFormulario();
        criarTabela();
        atualizarTabela();
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Cadastro de Acesso"));
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

        // Usuário (linha 1, full width)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Usuário:*"), gbc);
        txtUsuario = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtUsuario, gbc);
        gbc.gridwidth = 1;

        // Senha (linha 2, lado esquerdo)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Senha:*"), gbc);
        txtSenha = new JPasswordField();
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtSenha, gbc);

        // Perfil (linha 2, lado direito)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Perfil:*"), gbc);

        // ComboBox com as descrições dos perfis
        String[] perfis = new String[TipoAcesso.values().length];
        for (int i = 0; i < TipoAcesso.values().length; i++) {
            perfis[i] = TipoAcesso.values()[i].getDescricao();
        }
        comboPerfil = new JComboBox<>(perfis);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(comboPerfil, gbc);

        // Botões (linha 3)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("💾 Registrar");
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
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Usuário", "Perfil"};
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
        new SwingWorker<List<AcessoResponse>, Void>() {
            @Override
            protected List<AcessoResponse> doInBackground() throws Exception {
                return acessoService.listAll();
            }

            @Override
            protected void done() {
                try {
                    List<AcessoResponse> list = get();
                    tableModel.setRowCount(0);

                    for (AcessoResponse r : list) {
                        tableModel.addRow(new Object[]{
                                r.id(),
                                r.usuario(),
                                r.perfil().getDescricao()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaAcessoPanel.this,
                            "Erro ao buscar acessos: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtUsuario.getText().isBlank() || txtSenha.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Usuário e Senha são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Converte a descrição selecionada de volta para o enum
                TipoAcesso perfilSelecionado = TipoAcesso.values()[comboPerfil.getSelectedIndex()];

                AcessoRequest req = new AcessoRequest(
                        txtUsuario.getText().trim(),
                        new String(txtSenha.getPassword()),
                        perfilSelecionado
                );

                acessoService.registrar(req);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaAcessoPanel.this, "Acesso registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaAcessoPanel.this, "Erro ao registrar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um acesso para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este acesso?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    acessoService.delete(id);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TelaAcessoPanel.this, "Acesso excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaAcessoPanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void preencherFormulario() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtUsuario.setText(tableModel.getValueAt(row, 1).toString());
            txtSenha.setText(""); // Não mostra senha por segurança

            // Seleciona o perfil correto no combo
            String perfilDescricao = tableModel.getValueAt(row, 2).toString();
            comboPerfil.setSelectedItem(perfilDescricao);
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtUsuario.setText("");
        txtSenha.setText("");
        comboPerfil.setSelectedIndex(0);
        table.clearSelection();
    }
}