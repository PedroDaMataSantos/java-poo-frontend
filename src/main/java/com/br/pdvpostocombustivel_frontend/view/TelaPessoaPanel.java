package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.PessoaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PessoaResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoPessoa;
import com.br.pdvpostocombustivel_frontend.service.PessoaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaPessoaPanel extends JPanel {

    // === CORES DO TEMA (igual Produto) ===
    private static final Color BG_DARK       = new Color(20, 20, 20);
    private static final Color FIELD_BG      = new Color(40, 40, 40);
    private static final Color FIELD_FG      = Color.WHITE;
    private static final Color ACCENT        = new Color(0, 255, 200);
    private static final Color BTN_PRIMARY   = new Color(0, 180, 120);   // Salvar
    private static final Color BTN_DANGER    = new Color(204, 68, 68);   // Excluir
    private static final Color BTN_SECONDARY = new Color(60, 63, 65);    // Limpar

    private JFormattedTextField txtCpfCnpj;
    private MaskFormatter cpfFormatter;
    private MaskFormatter cnpjFormatter;
    private JTextField txtCtps;
    private JFormattedTextField txtDataNascimento;
    private JComboBox<TipoPessoa> comboTipoPessoa;
    private JTextField txtNome;
    private JTextField txtId;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JTable table;
    private DefaultTableModel tableModel;

    private final PessoaService pessoaService;

    public TelaPessoaPanel(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(BG_DARK);

        inicializarFormatadores();
        criarFormulario();
        criarTabela();
        atualizarTabela();
    }

    // ========= HELPER DE ESTILO (NÃO MUDA LAYOUT) =========

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(ACCENT);
        return label;
    }

    private JTextField estilizarCampo(JTextField campo) {
        campo.setBackground(FIELD_BG);
        campo.setForeground(FIELD_FG);
        campo.setCaretColor(ACCENT);
        campo.setBorder(BorderFactory.createLineBorder(ACCENT));
        return campo;
    }

    // ================= LÓGICA ORIGINAL =================

    private void inicializarFormatadores() {
        try {
            cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            cpfFormatter.setValueContainsLiteralCharacters(false);

            cnpjFormatter = new MaskFormatter("##.###.###/####-##");
            cnpjFormatter.setPlaceholderCharacter('_');
            cnpjFormatter.setValueContainsLiteralCharacters(false);
        } catch (ParseException e) {
            System.err.println("Erro ao criar formatadores: " + e.getMessage());
        }
    }

    private void criarFormulario() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                "Cadastro de Pessoas",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                ACCENT
        ));
        formPanel.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(criarLabel("ID:"), gbc);
        txtId = estilizarCampo(new JTextField(10));
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtId, gbc);

        // Tipo Pessoa
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Tipo:"), gbc);
        comboTipoPessoa = new JComboBox<>(TipoPessoa.values());
        comboTipoPessoa.setBackground(FIELD_BG);
        comboTipoPessoa.setForeground(FIELD_FG);
        comboTipoPessoa.setBorder(BorderFactory.createLineBorder(ACCENT));
        comboTipoPessoa.addActionListener(e -> alterarFormatadorCpfCnpj());
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(comboTipoPessoa, gbc);

        // Nome
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(criarLabel("Nome Completo:*"), gbc);
        txtNome = estilizarCampo(new JTextField());
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtNome, gbc);
        gbc.gridwidth = 1;

        // CPF/CNPJ
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("CPF/CNPJ:*"), gbc);
        txtCpfCnpj = new JFormattedTextField();
        cpfFormatter.install(txtCpfCnpj);
        estilizarCampo(txtCpfCnpj);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtCpfCnpj, gbc);

        // CTPS
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("CTPS:"), gbc);
        txtCtps = estilizarCampo(new JTextField());
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtCtps, gbc);

        // Data Nascimento
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(criarLabel("Data Nascimento:"), gbc);
        txtDataNascimento = new JFormattedTextField();
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            dateMask.install(txtDataNascimento);
            estilizarCampo(txtDataNascimento);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtDataNascimento, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_DARK);

        btnSalvar = new JButton("💾 Salvar");
        btnExcluir = new JButton("🗑️ Excluir");
        btnLimpar = new JButton("🔄 Limpar");

        btnSalvar.setBackground(BTN_PRIMARY);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);

        btnExcluir.setBackground(BTN_DANGER);
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setFocusPainted(false);

        btnLimpar.setBackground(BTN_SECONDARY);
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);

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

    private void criarTabela() {
        String[] colunas = {"ID", "Nome Completo", "CPF/CNPJ", "CTPS", "Data Nascimento", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
            if (!e.getValueIsAdjusting()) {
                preencherFormulario();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        new SwingWorker<List<PessoaResponse>, Void>() {
            @Override
            protected List<PessoaResponse> doInBackground() throws Exception {
                return pessoaService.listarPessoas();
            }

            @Override
            protected void done() {
                try {
                    List<PessoaResponse> pessoas = get();
                    tableModel.setRowCount(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    for (PessoaResponse p : pessoas) {
                        tableModel.addRow(new Object[]{
                                p.id(),
                                p.nomeCompleto(),
                                p.cpfCnpj(),
                                p.numeroCtps(),
                                p.dataNascimento() != null ? p.dataNascimento().format(formatter) : "",
                                p.tipoPessoa()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaPessoaPanel.this,
                            "Erro ao buscar pessoas: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void salvar() {
        if (txtNome.getText().isBlank() || txtCpfCnpj.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF/CNPJ são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimento = null;

        try {
            String dataTexto = txtDataNascimento.getText().replace("_", "").trim();
            if (!dataTexto.isEmpty()) {
                dataNascimento = LocalDate.parse(dataTexto, formatter);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long numeroCtps = null;
        try {
            String ctpsTexto = txtCtps.getText().trim();
            if (!ctpsTexto.isEmpty()) {
                numeroCtps = Long.parseLong(ctpsTexto);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de CTPS inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cpfCnpj = txtCpfCnpj.getText().replaceAll("[^0-9]", "");
        PessoaRequest request = new PessoaRequest(
                txtNome.getText(),
                cpfCnpj,
                numeroCtps,
                dataNascimento,
                (TipoPessoa) comboTipoPessoa.getSelectedItem()
        );

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                pessoaService.salvarPessoa(request, id);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(TelaPessoaPanel.this, "Pessoa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TelaPessoaPanel.this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(table.getSelectedRow(), 0);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    pessoaService.excluirPessoa(id);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(TelaPessoaPanel.this, "Pessoa excluída!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparFormulario();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TelaPessoaPanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

            TipoPessoa tipo = (TipoPessoa) tableModel.getValueAt(row, 5);
            comboTipoPessoa.setSelectedItem(tipo);

            String cpfCnpj = tableModel.getValueAt(row, 2).toString();
            txtCpfCnpj.setText(cpfCnpj.replaceAll("[^0-9]", ""));

            Object ctps = tableModel.getValueAt(row, 3);
            txtCtps.setText(ctps != null ? ctps.toString() : "");
            txtDataNascimento.setText(tableModel.getValueAt(row, 4).toString());
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtCpfCnpj.setText("");
        txtCtps.setText("");
        txtDataNascimento.setText("");
        comboTipoPessoa.setSelectedIndex(0);
        table.clearSelection();
        alterarFormatadorCpfCnpj();
    }

    private void alterarFormatadorCpfCnpj() {
        TipoPessoa tipoSelecionado = (TipoPessoa) comboTipoPessoa.getSelectedItem();
        String valorAtual = txtCpfCnpj.getText().replaceAll("[^0-9]", "");

        try {
            if (tipoSelecionado == TipoPessoa.JURIDICA) {
                cnpjFormatter.install(txtCpfCnpj);
            } else {
                cpfFormatter.install(txtCpfCnpj);
            }

            if (!valorAtual.isEmpty()) {
                txtCpfCnpj.setText(valorAtual);
            }
        } catch (Exception e) {
            System.err.println("Erro ao alternar formatador: " + e.getMessage());
        }
    }
}