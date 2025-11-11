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

    private final JTextField txtLitros = new JTextField();
    private final JTextField txtValor = new JTextField();
    private final JTextField txtPrecoLitro = new JTextField();

    private final JComboBox<TipoVenda> cbTipoVenda = new JComboBox<>(TipoVenda.values());
    private final JButton btnInverter = new JButton("⇆ Alternar");
    private final JButton btnConfirmar = new JButton("Confirmar Venda");

    private boolean modoPorLitro = true;

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
        setTitle("Bomba " + numeroBomba + " - " + tipoCombustivel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 330);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 8, 8));

        txtPrecoLitro.setText(formatarMoeda(precoLitro));
        txtPrecoLitro.setEditable(false);

        add(new JLabel("Preço/Litro (R$):"));
        add(txtPrecoLitro);
        add(new JLabel("Quantidade (L):"));
        add(txtLitros);
        add(new JLabel("Valor Total (R$):"));
        add(txtValor);
        add(new JLabel("Forma de Pagamento:"));
        add(cbTipoVenda);
        add(btnInverter);
        add(btnConfirmar);

        txtValor.setEnabled(false);
    }

    private void adicionarListeners() {
        btnInverter.addActionListener(this::alternarModo);
        btnConfirmar.addActionListener(this::confirmarVenda);

        txtLitros.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcular(true); }
            public void removeUpdate(DocumentEvent e) { recalcular(true); }
            public void changedUpdate(DocumentEvent e) {}
        });

        txtValor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalcular(false); }
            public void removeUpdate(DocumentEvent e) { recalcular(false); }
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void alternarModo(ActionEvent e) {
        modoPorLitro = !modoPorLitro;

        txtLitros.setEnabled(modoPorLitro);
        txtValor.setEnabled(!modoPorLitro);

        txtLitros.setText("");
        txtValor.setText("");
    }

    private void recalcular(boolean digitandoLitros) {
        try {
            if (modoPorLitro && digitandoLitros) {
                BigDecimal litros = parseBigDecimal(txtLitros.getText());
                BigDecimal valor = litros.multiply(precoLitro).setScale(2, RoundingMode.HALF_UP);
                txtValor.setText(formatarMoeda(valor));
                txtLitros.setText(formatarLitros(litros));
            } else if (!modoPorLitro && !digitandoLitros) {
                BigDecimal valor = parseBigDecimal(txtValor.getText());
                BigDecimal litros = precoLitro.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : valor.divide(precoLitro, 3, RoundingMode.HALF_UP);
                txtLitros.setText(formatarLitros(litros));
                txtValor.setText(formatarMoeda(valor));
            }
        } catch (Exception ignored) {}
    }

    private void confirmarVenda(ActionEvent e) {
        try {
            BigDecimal litros = parseBigDecimal(txtLitros.getText().replace("L", ""));
            BigDecimal valorBase = litros.multiply(precoLitro).setScale(2, RoundingMode.HALF_UP);

            TipoVenda tipoVenda = (TipoVenda) cbTipoVenda.getSelectedItem();
            enviarVendaBackend(litros, valorBase, tipoVenda);

            exibirCupomFiscal(litros, precoLitro, valorBase, tipoVenda);

            JOptionPane.showMessageDialog(this,
                    "Venda registrada com sucesso!\nValor final: " + formatarMoeda(valorBase),
                    "Confirmação", JOptionPane.INFORMATION_MESSAGE);

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
        scroll.setPreferredSize(new Dimension(420, 380));

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
