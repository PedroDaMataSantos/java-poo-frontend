package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.PrecoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PrecoResponse;
import com.br.pdvpostocombustivel_frontend.service.PrecoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaPrecoPanel extends JPanel {
    private JFormattedTextField txtDataAlteracao;
    private JFormattedTextField txtHoraAlteracao;
    private JTextField txtValor;
    private JComboBox<String> comboTipo;
    private JTextField txtId;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JTable table;
    private DefaultTableModel tableModel;

    private final PrecoService precoService;

    // formatadores
    private MaskFormatter dateFormatter;
    private MaskFormatter timeFormatter;

    public TelaPrecoPanel(PrecoService precoService) {
        this.precoService = precoService;
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

            timeFormatter = new MaskFormatter("##:##:##");
            timeFormatter.setPlaceholderCharacter('_');
            timeFormatter.setValueContainsLiteralCharacters(false);
        } catch (ParseException e) {
            System.err.println("Erro ao criar formatadores: " + e.getMessage());
        }
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

        // Tipo (combustível)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        comboTipo = new JComboBox<>(new String[]{
                "GASOLINA_COMUM",
                "GASOLINA_ADITIVADA",
                "DIESEL_S10",
                "ETANOL_HIDRATADO"
        });
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(comboTipo, gbc);

        // Valor (linha 1 full width like Nome)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Valor:*"), gbc);
        txtValor = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtValor, gbc);
        gbc.gridwidth = 1;

        // Data Alteração (linha 2, lado a lado com Hora)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Alteração:"), gbc);
        txtDataAlteracao = new JFormattedTextField();
        dateFormatter.install(txtDataAlteracao);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtDataAlteracao, gbc);

        // Hora Alteração (ao lado, como CTPS)
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Hora Alteração:"), gbc);
        txtHoraAlteracao = new JFormattedTextField();
        timeFormatter.install(txtHoraAlteracao);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtHoraAlteracao, gbc);

        // Botões (linha 4, gridwidth=4)
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
        String[] colunas = {"ID", "Tipo", "Valor", "Data Alteração", "Hora Alteração"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
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
                return precoService.listAll();
            }

            @Override
            protected void done() {
                try {
                    List<PrecoResponse> precos = get();
                    tableModel.setRowCount(0);
                    SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");

                    for (PrecoResponse p : precos) {
                        String horaStr = "";
                        try {
                            if (p.horaAlteracao() != null) horaStr = timeFmt.format(p.horaAlteracao());
                        } catch (Exception ignored) {}

                        tableModel.addRow(new Object[]{
                                p.id(),
                                // tenta usar campo de tipo se existir no response, senão usa vazio
                                (p instanceof PrecoResponse ? getTipoFromResponse(p) : ""),
                                p.valor(),
                                p.dataAlteracao(),
                                horaStr
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

    // helper para extrair tipo do PrecoResponse (se tiver método/field)
    private Object getTipoFromResponse(PrecoResponse p) {
        try {
            // se o record PrecoResponse tiver um método tipo/combustivel, adaptamos:
            // tentamos acessar via reflexão nomes comuns; caso não exista, retornamos vazio
            try {
                return PrecoResponse.class.getMethod("tipoProduto").invoke(p);
            } catch (NoSuchMethodException ignored) {}
            try {
                return PrecoResponse.class.getMethod("tipoCombustivel").invoke(p);
            } catch (NoSuchMethodException ignored) {}
            try {
                return PrecoResponse.class.getMethod("tipo").invoke(p);
            } catch (NoSuchMethodException ignored) {}
        } catch (Exception ex) {
            // fallback para string vazia
        }
        return "";
    }

    private void salvar() {
        if (txtValor.getText().isBlank() || txtDataAlteracao.getText().contains("_")) {
            JOptionPane.showMessageDialog(this, "Valor e Data Alteração são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // montar valor BigDecimal
        BigDecimal valor;
        try {
            String v = txtValor.getText().trim().replaceAll("[^0-9,\\.]", "");
            // aceita tanto 1.234,56 ou 1234.56
            v = v.replace(",", ".");
            valor = new BigDecimal(v);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dataAlteracao = txtDataAlteracao.getText().trim();
        Date hora = null;
        try {
            String horaText = txtHoraAlteracao.getText().trim();
            if (!horaText.isEmpty() && !horaText.contains("_")) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                hora = sdf.parse(horaText);
            }
        } catch (Exception ignored) {}

        PrecoRequest request = new PrecoRequest(

                valor,
                dataAlteracao,
                hora
        );

        Long id = txtId.getText().isEmpty() ? null : Long.valueOf(txtId.getText());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (id == null) {
                    precoService.create(request);
                } else {
                    precoService.update(id, request);
                }
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
                    precoService.delete(id);
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
            Object tipoObj = tableModel.getValueAt(row, 1);
            comboTipo.setSelectedItem(tipoObj != null ? tipoObj.toString() : comboTipo.getItemAt(0));
            txtValor.setText(tableModel.getValueAt(row, 2).toString());
            txtDataAlteracao.setText(tableModel.getValueAt(row, 3).toString());
            txtHoraAlteracao.setText(tableModel.getValueAt(row, 4).toString());
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtValor.setText("");
        txtDataAlteracao.setText("");
        txtHoraAlteracao.setText("");
        comboTipo.setSelectedIndex(0);
        table.clearSelection();
    }
}
