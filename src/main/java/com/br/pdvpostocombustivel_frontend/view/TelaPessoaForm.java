package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.PessoaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PessoaResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoPessoa;
import com.br.pdvpostocombustivel_frontend.service.PessoaService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TelaPessoaForm extends JFrame {
    private JPanel panel1;
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
    private JTable table1;
    private JScrollPane table;

    private final PessoaService pessoaService;
    private DefaultTableModel tableModel;

    public TelaPessoaForm(PessoaService pessoaService) {
        this.pessoaService = pessoaService;

        // Configurações da janela
        setTitle("Cadastro de Pessoas");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(panel1);

        // Adicionar margens ao painel principal
        panel1.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Configurar txtId como não editável
        txtId.setEditable(false);

        // Configurar ComboBox com os valores do enum TipoPessoa
        comboTipoPessoa.setModel(new DefaultComboBoxModel<>(TipoPessoa.values()));

        // IMPORTANTE: Configurar máscaras ANTES de adicionar listeners
        configurarMascaraData();
        inicializarFormatadores();
        configurarCampoCpfCnpj();

        // Configurar tabela com as colunas
        configurarTabela();

        // Configurar header da tabela
        configurarHeaderTabela();

        // Adicionar listeners (ações) aos botões
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        // Listener para preencher formulário ao clicar em uma linha da tabela
        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComLinhaSelecionada();
            }
        });

        // Carregar dados iniciais da API
        atualizarTabela();
    }

    private void configurarMascaraData() {
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            dateMask.install(txtDataNascimento);
        } catch (ParseException e) {
            System.err.println("Erro ao configurar máscara de data: " + e.getMessage());
        }
    }

    private void inicializarFormatadores() {
        try {
            // Criar formatador de CPF (###.###.###-##)
            cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            cpfFormatter.setValueContainsLiteralCharacters(false);

            // Criar formatador de CNPJ (##.###.###/####-##)
            cnpjFormatter = new MaskFormatter("##.###.###/####-##");
            cnpjFormatter.setPlaceholderCharacter('_');
            cnpjFormatter.setValueContainsLiteralCharacters(false);
        } catch (ParseException e) {
            System.err.println("Erro ao criar formatadores: " + e.getMessage());
        }
    }

    private void configurarCampoCpfCnpj() {
        // Começar com máscara de CPF por padrão
        try {
            cpfFormatter.install(txtCpfCnpj);
        } catch (Exception e) {
            System.err.println("Erro ao configurar campo CPF/CNPJ: " + e.getMessage());
        }

        // Listener no ComboBox de Tipo de Pessoa
        comboTipoPessoa.addActionListener(e -> {
            TipoPessoa tipoSelecionado = (TipoPessoa) comboTipoPessoa.getSelectedItem();

            if (tipoSelecionado != null) {
                // Salvar o texto atual (apenas números)
                String textoAtual = txtCpfCnpj.getText().replaceAll("[^0-9]", "");


                SwingUtilities.invokeLater(() -> {
                    try {
                        if (tipoSelecionado == TipoPessoa.JURIDICA) {
                            // Pessoa Jurídica = CNPJ
                            cnpjFormatter.install(txtCpfCnpj);
                        } else if (tipoSelecionado == TipoPessoa.FISICA) {
                            // Pessoa Física = CPF
                            cpfFormatter.install(txtCpfCnpj);
                        }

                        txtCpfCnpj.setText("");  // Limpar primeiro
                        txtCpfCnpj.setText(textoAtual);  // Aplicar o texto com a nova máscara

                    } catch (Exception ex) {
                        System.err.println("Erro ao trocar máscara: " + ex.getMessage());
                    }
                });
            }
        });

    }

    private void configurarTabela() {
        String[] columnNames = {"ID", "Nome Completo", "CPF/CNPJ", "CTPS", "Data Nascimento", "Tipo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table1.setModel(tableModel);
    }

    private void configurarHeaderTabela() {
        JTableHeader header = table1.getTableHeader();

        // Configurar fonte do cabeçalho
        header.setFont(new Font("Arial Black", Font.BOLD, 12));
        header.setForeground(Color.BLACK);

        // Altura do header
        header.setPreferredSize(new Dimension(header.getWidth(), 25));
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
                            TelaPessoaForm.this,
                            "Erro ao buscar pessoas: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void salvar() {
        // Validações
        if (txtNome.getText().isBlank() || txtCpfCnpj.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome e CPF/CNPJ são obrigatórios.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE
            );
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
            JOptionPane.showMessageDialog(
                    this,
                    "Formato de data inválido. Use dd/mm/aaaa.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Long numeroCtps = null;
        try {
            String ctpsTexto = txtCtps.getText().trim();
            if (!ctpsTexto.isEmpty()) {
                numeroCtps = Long.parseLong(ctpsTexto);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Número de CTPS inválido.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ✨ MUDANÇA: Remover formatação do CPF/CNPJ antes de enviar para API
        String cpfCnpj = txtCpfCnpj.getText().replaceAll("[^0-9]", "");

        PessoaRequest request = new PessoaRequest(
                txtNome.getText(),
                cpfCnpj,  // Enviar apenas números (sem pontos, traços, barras)
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
                    JOptionPane.showMessageDialog(
                            TelaPessoaForm.this,
                            "Pessoa salva com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    limparFormulario();
                    atualizarTabela();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TelaPessoaForm.this,
                            "Erro ao salvar pessoa: " + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void excluir() {
        if (table1.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma pessoa para excluir.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir esta pessoa?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(table1.getSelectedRow(), 0);

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
                        JOptionPane.showMessageDialog(
                                TelaPessoaForm.this,
                                "Pessoa excluída com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        limparFormulario();
                        atualizarTabela();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                                TelaPessoaForm.this,
                                "Erro ao excluir pessoa: " + e.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }.execute();
        }
    }

    private void preencherFormularioComLinhaSelecionada() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow >= 0) {
            txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtNome.setText(tableModel.getValueAt(selectedRow, 1).toString());

            Object ctps = tableModel.getValueAt(selectedRow, 3);
            txtCtps.setText(ctps != null ? ctps.toString() : "");

            txtDataNascimento.setText(tableModel.getValueAt(selectedRow, 4).toString());

            // ✨ MUDANÇA IMPORTANTE: Preencher Tipo de Pessoa PRIMEIRO
            // Isso vai disparar o listener que troca a máscara automaticamente
            TipoPessoa tipoPessoa = (TipoPessoa) tableModel.getValueAt(selectedRow, 5);
            comboTipoPessoa.setSelectedItem(tipoPessoa);

            // DEPOIS preencher CPF/CNPJ (a máscara já estará correta!)
            String cpfCnpj = tableModel.getValueAt(selectedRow, 2).toString();
            String apenasNumeros = cpfCnpj.replaceAll("[^0-9]", "");
            txtCpfCnpj.setText(apenasNumeros);
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtCpfCnpj.setText("");
        txtCtps.setText("");
        txtDataNascimento.setText("");
        comboTipoPessoa.setSelectedIndex(0);  // Volta para o primeiro (FISICA)
        table1.clearSelection();

        // ✨ MUDANÇA: Resetar para máscara de CPF ao limpar
        // (Como o índice 0 do combo é FISICA, a máscara já será de CPF)
        try {
            cpfFormatter.install(txtCpfCnpj);
        } catch (Exception e) {
            // Ignora erro
        }
    }
}