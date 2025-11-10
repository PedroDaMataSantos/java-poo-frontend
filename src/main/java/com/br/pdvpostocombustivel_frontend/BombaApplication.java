package com.br.pdvpostocombustivel_frontend;

import com.br.pdvpostocombustivel_frontend.model.dto.ItemVendaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.VendaRequest;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoVenda;
import com.br.pdvpostocombustivel_frontend.service.VendaService;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BombaApplication extends JFrame {

    private final JTextField txtLitros = new JTextField();
    private final JTextField txtValor = new JTextField();
    private final JTextField txtPrecoLitro = new JTextField();
    private final JComboBox<TipoVenda> cbTipoVenda = new JComboBox<>(TipoVenda.values());
    private final JButton btnInverter = new JButton("⇆ Alternar");
    private final JButton btnConfirmar = new JButton("Confirmar Venda");

    private final JLabel lblLitros = new JLabel("Quantidade (L):");
    private final JLabel lblValor = new JLabel("Valor Total (R$):");

    private boolean modoPorLitro = true;

    private final String tipoCombustivel;
    private final int numeroBomba;
    private final BigDecimal precoLitro;
    private final Long idProduto;

    private final VendaService vendaService;

    public BombaApplication(int numeroBomba, String tipoCombustivel, BigDecimal precoLitro, Long idProduto) {
        this.numeroBomba = numeroBomba;
        this.tipoCombustivel = tipoCombustivel;
        this.precoLitro = precoLitro;
        this.idProduto = idProduto;
        this.vendaService = new VendaService(new RestTemplate());

        setTitle("Bomba " + numeroBomba + " - " + tipoCombustivel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 8, 8));

        txtPrecoLitro.setText(precoLitro.setScale(2, RoundingMode.HALF_UP).toString());
        txtPrecoLitro.setEditable(false);

        add(new JLabel("Preço/Litro (R$):"));
        add(txtPrecoLitro);
        add(lblLitros);
        add(txtLitros);
        add(lblValor);
        add(txtValor);
        add(new JLabel("Forma de Pagamento:"));
        add(cbTipoVenda);
        add(btnInverter);
        add(btnConfirmar);

        btnInverter.addActionListener(this::alternarModo);
        btnConfirmar.addActionListener(this::confirmarVenda);
    }

    private void alternarModo(ActionEvent e) {
        modoPorLitro = !modoPorLitro;
        lblLitros.setText(modoPorLitro ? "Quantidade (L):" : "Valor Total (R$):");
        lblValor.setText(modoPorLitro ? "Valor Total (R$):" : "Quantidade (L):");
        txtLitros.setText("");
        txtValor.setText("");
    }

    private void confirmarVenda(ActionEvent e) {
        try {
            BigDecimal imposto = switch (tipoCombustivel.toUpperCase()) {
                case "ETANOL" -> new BigDecimal("0.28");
                case "GASOLINA COMUM", "GASOLINA ADITIVADA" -> new BigDecimal("0.42");
                case "DIESEL" -> new BigDecimal("0.38");
                default -> BigDecimal.ZERO;
            };

            BigDecimal litros;
            BigDecimal valorBase;

            if (modoPorLitro) {
                litros = parseBigDecimal(txtLitros.getText());
                valorBase = litros.multiply(precoLitro);
                txtValor.setText(valorBase.setScale(2, RoundingMode.HALF_UP).toString());
            } else {
                BigDecimal valorInformado = parseBigDecimal(txtValor.getText());
                litros = valorInformado.divide(precoLitro, 3, RoundingMode.HALF_UP);
                valorBase = valorInformado;
                txtLitros.setText(litros.setScale(3, RoundingMode.HALF_UP).toString());
            }

            BigDecimal valorImposto = valorBase.multiply(imposto).setScale(2, RoundingMode.HALF_UP);
            BigDecimal valorFinal = valorBase.add(valorImposto);

            TipoVenda tipoVenda = (TipoVenda) cbTipoVenda.getSelectedItem();
            enviarVendaBackend(litros, valorBase, valorFinal, tipoVenda);
            imprimirCupom(litros, valorBase, valorImposto, valorFinal, imposto, tipoVenda);

            JOptionPane.showMessageDialog(this,
                    "Venda registrada com sucesso!\nValor final: R$ " + valorFinal.setScale(2, RoundingMode.HALF_UP),
                    "Confirmação", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro: " + ex.getMessage(),
                    "Erro na venda",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseBigDecimal(String texto) {
        if (texto == null || texto.isBlank())
            throw new IllegalArgumentException("Campo numérico vazio.");
        texto = texto.replace(",", ".").trim();
        return new BigDecimal(texto);
    }

    private void enviarVendaBackend(BigDecimal litros, BigDecimal valorBase, BigDecimal valorFinal, TipoVenda tipoVenda) {
        ItemVendaRequest item = new ItemVendaRequest(idProduto, litros, precoLitro, valorBase);

        VendaRequest venda = new VendaRequest(
                LocalDateTime.now(),
                tipoVenda,
                valorFinal,
                List.of(item)
        );

        vendaService.registrar(venda);
    }

    private void imprimirCupom(BigDecimal litros, BigDecimal valorBase, BigDecimal valorImposto, BigDecimal valorFinal,
                               BigDecimal imposto, TipoVenda tipoVenda) {

        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        System.out.println("""
                =====================================
                      CUPOM FISCAL ELETRÔNICO
                =====================================
                BOMBA: #%d – %s
                DATA: %s
                -------------------------------------
                LITROS: %s L
                PREÇO POR LITRO: R$ %s
                VALOR BASE: R$ %s
                IMPOSTO (%.0f%%): R$ %s
                -------------------------------------
                VALOR TOTAL: R$ %s
                FORMA DE PAGAMENTO: %s
                =====================================
                """.formatted(
                numeroBomba, tipoCombustivel,
                data,
                litros.setScale(2, RoundingMode.HALF_UP),
                precoLitro.setScale(2, RoundingMode.HALF_UP),
                valorBase.setScale(2, RoundingMode.HALF_UP),
                imposto.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).doubleValue(),
                valorImposto.setScale(2, RoundingMode.HALF_UP),
                valorFinal.setScale(2, RoundingMode.HALF_UP),
                tipoVenda
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new BombaApplication(1, "Gasolina Comum", new BigDecimal("5.89"), 1L)
                        .setVisible(true));
    }
}
