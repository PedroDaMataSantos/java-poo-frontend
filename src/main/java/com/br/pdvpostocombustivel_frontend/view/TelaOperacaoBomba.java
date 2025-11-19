package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.ItemVendaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.VendaRequest;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoVenda;
import com.br.pdvpostocombustivel_frontend.service.VendaService;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TelaOperacaoBomba extends JFrame {

    // 🎨 Cores do tema claro
    private static final Color BG_LIGHT = new Color(240, 243, 247);
    private static final Color FIELD_BG = Color.WHITE;
    private static final Color FIELD_FG = new Color(33, 37, 41);
    private static final Color ACCENT = new Color(25, 45, 85);
    private static final Color BTN_PRIMARY = new Color(0, 123, 255);
    private static final Color BTN_SECONDARY = new Color(108, 117, 125);

    private final JTextField txtLitros = new JTextField();
    private final JTextField txtValor = new JTextField();
    private final JTextField txtPrecoLitro = new JTextField();

    private final JComboBox<TipoVenda> cbTipoVenda = new JComboBox<>(TipoVenda.values());
    private final JButton btnInverter = new JButton("⇆ Alternar Modo");
    private final JButton btnConfirmar = new JButton("✓ Confirmar Venda");

    private boolean modoPorLitro = true;
    private boolean isFormatting = false;

    private final String tipoCombustivel;
    private final int numeroBomba;
    private final BigDecimal precoLitro;
    private final Long idProduto;

    private final VendaService vendaService;

    public TelaOperacaoBomba(int numeroBomba, String tipoCombustivel, BigDecimal precoLitro, Long idProduto) {
        this.numeroBomba = numeroBomba;
        this.tipoCombustivel = tipoCombustivel;
        this.precoLitro = precoLitro != null ? precoLitro : BigDecimal.ZERO;
        this.idProduto = idProduto;
        this.vendaService = new VendaService(new RestTemplate());

        configurarTela();
        adicionarListeners();
    }

    private void configurarTela() {
        setTitle("⛽ Bomba " + numeroBomba + " - " + tipoCombustivel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(550, 450);
        setLocationRelativeTo(null);

        // Layout principal com bordas
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_LIGHT);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                "Dados da Venda",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                ACCENT
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Preço por Litro (somente leitura)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(criarLabel("Preço/Litro:"), gbc);

        txtPrecoLitro.setText(formatarMoeda(precoLitro));
        txtPrecoLitro.setEditable(false);
        estilizarCampo(txtPrecoLitro);
        txtPrecoLitro.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtPrecoLitro, gbc);

        // Quantidade em Litros
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(criarLabel("Quantidade (L):"), gbc);

        estilizarCampo(txtLitros);
        txtLitros.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtLitros, gbc);

        // Valor Total
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(criarLabel("Valor Total:"), gbc);

        estilizarCampo(txtValor);
        txtValor.setFont(new Font("Arial", Font.BOLD, 16));
        txtValor.setEnabled(false);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtValor, gbc);

        // Forma de Pagamento
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(criarLabel("Pagamento:"), gbc);

        cbTipoVenda.setBackground(FIELD_BG);
        cbTipoVenda.setForeground(FIELD_FG);
        cbTipoVenda.setFont(new Font("Arial", Font.PLAIN, 14));
        cbTipoVenda.setBorder(BorderFactory.createLineBorder(ACCENT));
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cbTipoVenda, gbc);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BG_LIGHT);

        btnInverter.setBackground(BTN_SECONDARY);
        btnInverter.setForeground(Color.WHITE);
        btnInverter.setFocusPainted(false);
        btnInverter.setFont(new Font("Arial", Font.BOLD, 13));
        btnInverter.setPreferredSize(new Dimension(180, 45));

        btnConfirmar.setBackground(BTN_PRIMARY);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 13));
        btnConfirmar.setPreferredSize(new Dimension(180, 45));

        buttonPanel.add(btnInverter);
        buttonPanel.add(btnConfirmar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(ACCENT);
        return label;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(FIELD_BG);
        campo.setForeground(FIELD_FG);
        campo.setCaretColor(ACCENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void adicionarListeners() {
        btnInverter.addActionListener(this::alternarModo);
        btnConfirmar.addActionListener(this::confirmarVenda);

        // Formatação em tempo real para LITROS
        txtLitros.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (modoPorLitro && !isFormatting) formatarLitrosEmTempoReal();
                recalcular(true);
            }
            public void removeUpdate(DocumentEvent e) {
                if (modoPorLitro && !isFormatting) formatarLitrosEmTempoReal();
                recalcular(true);
            }
            public void changedUpdate(DocumentEvent e) {}
        });

        // Formatação em tempo real para VALOR
        txtValor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (!modoPorLitro && !isFormatting) formatarValorEmTempoReal();
                recalcular(false);
            }
            public void removeUpdate(DocumentEvent e) {
                if (!modoPorLitro && !isFormatting) formatarValorEmTempoReal();
                recalcular(false);
            }
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void formatarLitrosEmTempoReal() {
        SwingUtilities.invokeLater(() -> {
            if (isFormatting) return;
            isFormatting = true;

            try {
                String texto = txtLitros.getText();
                int caretPos = txtLitros.getCaretPosition();

                // Remove tudo exceto números e vírgula
                String apenasNumeros = texto.replaceAll("[^0-9,]", "");

                if (apenasNumeros.isEmpty()) {
                    txtLitros.setText("");
                    return;
                }

                // Limita a uma vírgula
                String[] partes = apenasNumeros.split(",", 2);
                String parteInteira = partes[0];
                String parteDecimal = partes.length > 1 ? partes[1] : "";

                // Limita decimais a 3 dígitos
                if (parteDecimal.length() > 3) {
                    parteDecimal = parteDecimal.substring(0, 3);
                }

                // Formata com L no final
                String valorFormatado = parteInteira;
                if (!parteDecimal.isEmpty()) {
                    valorFormatado += "," + parteDecimal;
                }
                valorFormatado += " L";

                txtLitros.setText(valorFormatado);

                // Mantém cursor antes do " L"
                int novaPosicao = Math.min(valorFormatado.length() - 2, caretPos);
                txtLitros.setCaretPosition(Math.max(0, novaPosicao));

            } finally {
                isFormatting = false;
            }
        });
    }

    private void formatarValorEmTempoReal() {
        SwingUtilities.invokeLater(() -> {
            if (isFormatting) return;
            isFormatting = true;

            try {
                String texto = txtValor.getText();
                int caretPos = txtValor.getCaretPosition();

                // Remove tudo exceto números e vírgula
                String apenasNumeros = texto.replaceAll("[^0-9,]", "");

                if (apenasNumeros.isEmpty()) {
                    txtValor.setText("");
                    return;
                }

                // Limita a uma vírgula
                String[] partes = apenasNumeros.split(",", 2);
                String parteInteira = partes[0];
                String parteDecimal = partes.length > 1 ? partes[1] : "";

                // Limita centavos a 2 dígitos
                if (parteDecimal.length() > 2) {
                    parteDecimal = parteDecimal.substring(0, 2);
                }

                // Formata parte inteira com pontos
                String inteiraFormatada = formatarParteInteira(parteInteira);

                // Monta valor com R$
                String valorFormatado = "R$ " + inteiraFormatada;
                if (!parteDecimal.isEmpty()) {
                    valorFormatado += "," + parteDecimal;
                }

                txtValor.setText(valorFormatado);

                int novaPosicao = Math.min(valorFormatado.length(), caretPos + 1);
                txtValor.setCaretPosition(Math.max(3, novaPosicao));

            } finally {
                isFormatting = false;
            }
        });
    }

    private String formatarParteInteira(String numero) {
        if (numero.isEmpty()) return "0";
        numero = numero.replaceFirst("^0+(?!$)", "");

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

    private void alternarModo(ActionEvent e) {
        modoPorLitro = !modoPorLitro;

        txtLitros.setEnabled(modoPorLitro);
        txtValor.setEnabled(!modoPorLitro);

        txtLitros.setText("");
        txtValor.setText("");

        if (modoPorLitro) {
            txtLitros.requestFocus();
            btnInverter.setText("⇆ Digitar por Valor");
        } else {
            txtValor.requestFocus();
            btnInverter.setText("⇆ Digitar por Litros");
        }
    }

    private void recalcular(boolean digitandoLitros) {
        if (isFormatting) return;

        try {
            if (modoPorLitro && digitandoLitros) {
                BigDecimal litros = parseBigDecimal(txtLitros.getText());
                BigDecimal valor = litros.multiply(precoLitro).setScale(2, RoundingMode.HALF_UP);

                isFormatting = true;
                txtValor.setText(formatarMoeda(valor));
                isFormatting = false;

            } else if (!modoPorLitro && !digitandoLitros) {
                BigDecimal valor = parseBigDecimal(txtValor.getText());
                BigDecimal litros = precoLitro.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : valor.divide(precoLitro, 3, RoundingMode.HALF_UP);

                isFormatting = true;
                txtLitros.setText(formatarLitros(litros));
                isFormatting = false;
            }
        } catch (Exception ignored) {}
    }

    private void confirmarVenda(ActionEvent e) {
        try {
            BigDecimal litros = parseBigDecimal(txtLitros.getText());

            if (litros.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, informe uma quantidade válida!",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal valorBase = litros.multiply(precoLitro).setScale(2, RoundingMode.HALF_UP);

            TipoVenda tipoVenda = (TipoVenda) cbTipoVenda.getSelectedItem();
            enviarVendaBackend(litros, valorBase, tipoVenda);

            exibirCupomFiscal(litros, precoLitro, valorBase, tipoVenda);

            JOptionPane.showMessageDialog(this,
                    "Venda registrada com sucesso!\nValor final: " + formatarMoeda(valorBase),
                    "Confirmação", JOptionPane.INFORMATION_MESSAGE);

            // Limpa campos após venda
            txtLitros.setText("");
            txtValor.setText("");
            txtLitros.requestFocus();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro: " + ex.getMessage(),
                    "Erro na venda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirCupomFiscal(BigDecimal litros, BigDecimal precoLitro, BigDecimal valorBase, TipoVenda tipoVenda) {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String cupom = """
                =======================================
                         CUPOM FISCAL ELETRÔNICO
                =======================================
                Data: %s
                ---------------------------------------
                Produto: %s
                Preço por litro: %s
                Quantidade: %s
                ---------------------------------------
                Valor base: %s
                TOTAL: %s
                Pagamento: %s
                =======================================
                Obrigado pela preferência!
                """.formatted(
                data,
                tipoCombustivel,
                formatarMoeda(precoLitro),
                formatarLitros(litros),
                formatarMoeda(valorBase),
                formatarMoeda(valorBase),
                tipoVenda.name().replace("_", " ")
        );

        JTextArea area = new JTextArea(cupom);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(450, 400));

        JOptionPane.showMessageDialog(this, scroll, "Cupom Fiscal", JOptionPane.INFORMATION_MESSAGE);
    }

    private BigDecimal parseBigDecimal(String texto) {
        if (texto == null || texto.isBlank()) return BigDecimal.ZERO;
        texto = texto.replace("R$", "").replace("L", "").replace(".", "").replace(",", ".").trim();
        return new BigDecimal(texto.isEmpty() ? "0" : texto);
    }

    private String formatarMoeda(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        return df.format(valor);
    }

    private String formatarLitros(BigDecimal litros) {
        DecimalFormat df = new DecimalFormat("#,##0.###", new DecimalFormatSymbols(new Locale("pt", "BR")));
        return df.format(litros) + " L";
    }

    private void enviarVendaBackend(BigDecimal litros, BigDecimal valorBase, TipoVenda tipoVenda) {
        ItemVendaRequest item = new ItemVendaRequest(idProduto, litros, precoLitro, valorBase);
        VendaRequest venda = new VendaRequest(LocalDateTime.now(), tipoVenda, valorBase, List.of(item));
        vendaService.registrar(venda);
    }
}