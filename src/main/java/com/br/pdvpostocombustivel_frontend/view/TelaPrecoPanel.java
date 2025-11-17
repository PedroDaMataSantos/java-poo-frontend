package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.PrecoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PrecoResponse;
import com.br.pdvpostocombustivel_frontend.model.dto.ProdutoResponse;
import com.br.pdvpostocombustivel_frontend.service.PrecoService;
import com.br.pdvpostocombustivel_frontend.service.ProdutoService;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    // 🎨 Cores do Tema Dark
    private static final Color BG_DARK = new Color(20, 20, 20);
    private static final Color PANEL_BG = new Color(20, 20, 20);
    private static final Color FIELD_BG = new Color(40, 40, 40);
    private static final Color FIELD_FG = Color.WHITE;
    private static final Color ACCENT = new Color(0, 255, 200);
    private static final Color BTN_PRIMARY = new Color(0, 180, 120);
    private static final Color BTN_DANGER = new Color(204, 68, 68);
    private static final Color BTN_SECONDARY = new Color(60, 63, 65);

    private JFormattedTextField txtDataAlteracao;
    private JFormattedTextField txtHoraAlteracao;
    private JTextField txtValor;
    private JComboBox<ProdutoComboItem> comboProduto;
    private JTextField txtId;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JTable table;
    private DefaultTableModel tableModel;

    private final PrecoService precoService;
    private final ProdutoService produtoService;

    private MaskFormatter dateFormatter;
    private MaskFormatter timeFormatter;
    private boolean isFormatting = false;
    private List<ProdutoResponse> produtosDisponiveis;

    public TelaPrecoPanel(PrecoService precoService, ProdutoService produtoService) {
        this.precoService = precoService;
        this.produtoService = produtoService;
        this.produtosDisponiveis = java.util.List.of();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(BG_DARK);

        inicializarFormatadores();
        criarFormulario();
        criarTabela();
        adicionarListenerDeAbas();

        carregarProdutosAsync();
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

    private void adicionarListenerDeAbas() {
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (produtosDisponiveis == null || produtosDisponiveis.isEmpty()) {
                    carregarProdutosAsync();
                }
            }

            @Override public void ancestorRemoved(AncestorEvent event) {}
            @Override public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void carregarProdutosAsync() {
        new SwingWorker<List<ProdutoResponse>, Void>() {
            @Override
            protected List<ProdutoResponse> doInBackground() throws Exception {
                return produtoService.listarProdutos();
            }

            @Override
            protected void done() {
                try {
                    produtosDisponiveis = get();
                    preencherComboProdutos();
                } catch (Exception e) {
                    produtosDisponiveis = List.of();
                    preencherComboProdutos();
                    System.err.println("Erro ao carregar produtos: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void preencherComboProdutos() {
        comboProduto.removeAllItems();
        comboProduto.addItem(new ProdutoComboItem(null, "-- Selecione um Produto --"));
        if (produtosDisponiveis == null || produtosDisponiveis.isEmpty()) return;

        for (ProdutoResponse p : produtosDisponiveis) {
            String nomeExibicao = p.nome() + (p.tipoProduto() != null ? " - " + p.tipoProduto().getDescricao() : "");
            comboProduto.addItem(new ProdutoComboItem(p.id(), nomeExibicao));
        }

        comboProduto.setSelectedIndex(0);
    }

    // 🎨 Métodos auxiliares para criar componentes estilizados
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

    private JFormattedTextField criarCampoFormatado() {
        JFormattedTextField campo = new JFormattedTextField();
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
        botao.setBackground(BTN_DANGER);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return botao;
    }

    private JButton criarBotaoTerciario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(BTN_SECONDARY);
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
                "Cadastro de Preços",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                ACCENT
        ));
        formPanel.setBackground(PANEL_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: ID e Produto (lado a lado)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(criarLabel("ID:"), gbc);
        txtId = criarCampoTexto();
        txtId.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Produto:"), gbc);
        comboProduto = new JComboBox<>();
        comboProduto.setBackground(FIELD_BG);
        comboProduto.setForeground(FIELD_FG);
        comboProduto.setFont(new Font("Arial", Font.PLAIN, 12));
        comboProduto.setBorder(BorderFactory.createLineBorder(ACCENT));
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(comboProduto, gbc);

        // Linha 1: Valor (full width)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(criarLabel("Valor:*"), gbc);
        txtValor = criarCampoTexto();
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        formPanel.add(txtValor, gbc);
        gbc.gridwidth = 1;
        adicionarFormatacaoMonetaria(txtValor);

        // Linha 2: Data Alteração e Hora Alteração (lado a lado)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Data Alteração:"), gbc);
        txtDataAlteracao = criarCampoFormatado();
        dateFormatter.install(txtDataAlteracao);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formPanel.add(txtDataAlteracao, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Hora Alteração:"), gbc);
        txtHoraAlteracao = criarCampoFormatado();
        timeFormatter.install(txtHoraAlteracao);
        gbc.gridx = 3; gbc.weightx = 0.3;
        formPanel.add(txtHoraAlteracao, gbc);

        // Linha 3: Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_BG);

        btnSalvar = criarBotaoPrimario("💾 Salvar");
        btnExcluir = criarBotaoSecundario("🗑️ Excluir");
        btnLimpar = criarBotaoTerciario("🔄 Limpar");

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

    private void adicionarFormatacaoMonetaria(JTextField campo) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                formatarValor();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                formatarValor();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                formatarValor();
            }

            private void formatarValor() {
                if (isFormatting) return;

                SwingUtilities.invokeLater(() -> {
                    isFormatting = true;
                    try {
                        String texto = campo.getText();
                        int caretPosition = campo.getCaretPosition();

                        // Remove tudo exceto números e vírgula
                        String apenasNumeros = texto.replaceAll("[^0-9,]", "");

                        // Se está vazio, limpa o campo
                        if (apenasNumeros.isEmpty()) {
                            campo.setText("");
                            return;
                        }

                        // Verifica se tem vírgula
                        if (apenasNumeros.contains(",")) {
                            String[] partes = apenasNumeros.split(",", 2);
                            String parteInteira = partes[0];
                            String parteDecimal = partes.length > 1 ? partes[1] : "";

                            // Limita centavos a 2 dígitos
                            if (parteDecimal.length() > 2) {
                                parteDecimal = parteDecimal.substring(0, 2);
                            }

                            // Formata a parte inteira com pontos
                            String inteiraFormatada = formatarParteInteira(parteInteira);

                            // Monta o valor formatado
                            String valorFormatado = "R$ " + inteiraFormatada + "," + parteDecimal;

                            campo.setText(valorFormatado);

                            // Mantém o cursor após a vírgula ou no final
                            int novaPosicao = Math.min(valorFormatado.length(), caretPosition);
                            campo.setCaretPosition(novaPosicao);
                        } else {
                            // Sem vírgula: formata apenas a parte inteira, SEM adicionar ,00
                            String inteiraFormatada = formatarParteInteira(apenasNumeros);
                            String valorFormatado = "R$ " + inteiraFormatada;

                            campo.setText(valorFormatado);

                            // Posiciona o cursor no final
                            campo.setCaretPosition(valorFormatado.length());
                        }
                    } finally {
                        isFormatting = false;
                    }
                });
            }

            private String formatarParteInteira(String numero) {
                if (numero.isEmpty()) return "0";

                // Remove zeros à esquerda, exceto se for só zero
                numero = numero.replaceFirst("^0+(?!$)", "");

                // Adiciona pontos de milhar
                StringBuilder resultado = new StringBuilder();
                int count = 0;
                for (int i = numero.length() - 1; i >= 0; i--) {
                    if (count == 3) {
                        resultado.insert(0, ".");
                        count = 0;
                    }
                    resultado.insert(0, numero.charAt(i));
                    count++;
                }

                return resultado.toString();
            }
        });
    }

    private void criarTabela() {
        String[] colunas = {"ID", "Produto", "Valor", "Data Alteração", "Hora Alteração"};
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
                            if (p.horaAlteracao() != null) {
                                horaStr = timeFmt.format(p.horaAlteracao());
                            }
                        } catch (Exception ignored) {
                        }

                        tableModel.addRow(new Object[]{
                                p.id(),
                                p.idProduto(),
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

    private void salvar() {
        ProdutoComboItem itemSelecionado = (ProdutoComboItem) comboProduto.getSelectedItem();
        if (itemSelecionado == null || itemSelecionado.getId() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtValor.getText().isBlank() || txtDataAlteracao.getText().contains("_")) {
            JOptionPane.showMessageDialog(this, "Valor e Data Alteração são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal valor;
        try {
            String v = txtValor.getText().trim();
            // Remove "R$" e espaços
            v = v.replace("R$", "").trim();
            // Remove pontos de milhar
            v = v.replace(".", "");

            // Se não tem vírgula, adiciona ,00
            if (!v.contains(",")) {
                v = v + ",00";
            }

            // Troca vírgula por ponto para BigDecimal
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
        } catch (Exception ignored) {
        }

        PrecoRequest request = new PrecoRequest(
                itemSelecionado.getId(),
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

            // Busca o produto pelo ID
            Long idProduto = (Long) tableModel.getValueAt(row, 1);
            for (int i = 0; i < comboProduto.getItemCount(); i++) {
                ProdutoComboItem prod = comboProduto.getItemAt(i);
                if (prod.getId() != null && prod.getId().equals(idProduto)) {
                    comboProduto.setSelectedIndex(i);
                    break;
                }
            }

            // Desabilita formatação temporariamente ao preencher
            isFormatting = true;
            txtValor.setText(tableModel.getValueAt(row, 2).toString());
            isFormatting = false;

            txtDataAlteracao.setText(tableModel.getValueAt(row, 3).toString());
            txtHoraAlteracao.setText(tableModel.getValueAt(row, 4).toString());
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtValor.setText("");
        txtDataAlteracao.setText("");
        txtHoraAlteracao.setText("");
        comboProduto.setSelectedIndex(0);
        table.clearSelection();
    }

    private static class ProdutoComboItem {
        private final Long id;
        private final String nome;

        public ProdutoComboItem(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return nome;
        }
    }
}