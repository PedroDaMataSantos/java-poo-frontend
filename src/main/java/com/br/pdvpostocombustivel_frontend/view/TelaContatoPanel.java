package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.ContatoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.ContatoResponse;
import com.br.pdvpostocombustivel_frontend.service.ContatoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;

public class TelaContatoPanel extends JPanel {

    private static final Color BG_DARK = new Color(20, 20, 20);
    private static final Color FIELD_BG = new Color(40, 40, 40);
    private static final Color FIELD_FG = Color.WHITE;
    private static final Color ACCENT = new Color(0, 255, 200);

    private static final Color BTN_PRIMARY = new Color(0, 180, 120);
    private static final Color BTN_DANGER = new Color(204, 68, 68);
    private static final Color BTN_SECONDARY = new Color(60, 63, 65);

    private JFormattedTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtEndereco;
    private JTextField txtId;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable table;
    private DefaultTableModel tableModel;

    private final ContatoService contatoService;
    private MaskFormatter phoneFormatter;

    public TelaContatoPanel(ContatoService contatoService) {
        this.contatoService = contatoService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(BG_DARK);

        inicializarFormatadores();
        criarFormulario();
        criarTabela();
        atualizarTabela();
    }

    private JLabel criarLabel(String nome) {
        JLabel label = new JLabel(nome);
        label.setForeground(ACCENT);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private JTextField estilizarCampo(JTextField campo) {
        campo.setBackground(FIELD_BG);
        campo.setForeground(FIELD_FG);
        campo.setCaretColor(ACCENT);
        campo.setBorder(BorderFactory.createLineBorder(ACCENT));
        return campo;
    }

    private void inicializarFormatadores() {
        try {
            phoneFormatter = new MaskFormatter("(##) #####-####");
            phoneFormatter.setPlaceholderCharacter('_');
            phoneFormatter.setValueContainsLiteralCharacters(false);
        } catch (ParseException e) {
            System.err.println("Erro ao criar formatadores: " + e.getMessage());
        }
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Cadastro de Contato",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                ACCENT
        ));
        formPanel.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(criarLabel("ID:"), gbc);
        txtId = estilizarCampo(new JTextField(10));
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(criarLabel("Telefone:*"), gbc);
        txtTelefone = new JFormattedTextField();
        phoneFormatter.install(txtTelefone);
        estilizarCampo(txtTelefone);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtTelefone, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Email:*"), gbc);
        txtEmail = estilizarCampo(new JTextField());
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Endereço:*"), gbc);
        txtEndereco = estilizarCampo(new JTextField());
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtEndereco, gbc);
        gbc.gridwidth = 1;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_DARK);

        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");

        btnSalvar.setBackground(BTN_PRIMARY);
        btnSalvar.setForeground(Color.WHITE);

        btnExcluir.setBackground(BTN_DANGER);
        btnExcluir.setForeground(Color.WHITE);

        btnLimpar.setBackground(BTN_SECONDARY);
        btnLimpar.setForeground(Color.WHITE);

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;

        add(formPanel, BorderLayout.NORTH);
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Telefone", "Email", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0,120,90));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(70,70,70));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial Black", Font.BOLD, 12));
        header.setForeground(ACCENT);
        header.setBackground(new Color(15,15,15));
        header.setPreferredSize(new Dimension(header.getWidth(), 25));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<ContatoResponse>, Void>() {
            @Override
            protected List<ContatoResponse> doInBackground() throws Exception {
                return contatoService.listAll();
            }

            @Override
            protected void done() {
                try {
                    List<ContatoResponse> list = get();
                    tableModel.setRowCount(0);

                    for (ContatoResponse r : list) {
                        tableModel.addRow(new Object[]{
                                r.id(),
                                r.telefone(),
                                r.email(),
                                r.endereco()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaContatoPanel.this,
                            "Erro ao buscar contatos: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtTelefone.getText().replace("_", "").trim().isEmpty() ||
                txtEmail.getText().isBlank() ||
                txtEndereco.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ContatoRequest req = new ContatoRequest(
                        txtTelefone.getText().trim(),
                        txtEmail.getText().trim(),
                        txtEndereco.getText().trim()
                );

                if (id == null) {
                    contatoService.create(req);
                } else {
                    contatoService.update(id, req);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaContatoPanel.this, "Contato salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limpar();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaContatoPanel.this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um contato para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    contatoService.delete(id);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TelaContatoPanel.this, "Contato excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limpar();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaContatoPanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void preencherFormulario() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtTelefone.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
            txtEmail.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
            txtEndereco.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        }
    }

    private void limpar() {
        txtId.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtEndereco.setText("");
        table.clearSelection();
    }
}
