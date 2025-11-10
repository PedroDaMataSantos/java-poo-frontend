package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import com.br.pdvpostocombustivel_frontend.service.PrecoService;
import com.br.pdvpostocombustivel_frontend.service.VendaService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DialogVenda extends JDialog {

    private final EstoqueResponse estoque;
    private final VendaService vendaService;
    private final PrecoService precoService;

    private JComboBox<String> modoCalculo;
    private JTextField txtLitros;
    private JTextField txtValor;
    private JButton btnConfirmar;

    public DialogVenda(JFrame parent, EstoqueResponse estoque) {
        super(parent, "Venda - " + estoque.nomeProduto(), true);
        this.estoque = estoque;
        this.vendaService = new VendaService(new org.springframework.web.client.RestTemplate());
        this.precoService = new PrecoService(new org.springframework.web.client.RestTemplate());
        configurarTela();
    }

    private void configurarTela() {
        setSize(400, 250);
        setLayout(new GridLayout(4, 2, 10, 10));
        setLocationRelativeTo(getParent());

        add(new JLabel("Modo de cálculo:"));
        modoCalculo = new JComboBox<>(new String[]{"Por Litros", "Por Valor"});
        add(modoCalculo);

        add(new JLabel("Litros:"));
        txtLitros = new JTextField();
        add(txtLitros);

        add(new JLabel("Valor:"));
        txtValor = new JTextField();
        add(txtValor);

        btnConfirmar = new JButton("Confirmar Venda");
        btnConfirmar.addActionListener(e -> processarVenda());
        add(new JLabel());
        add(btnConfirmar);
    }

    private void processarVenda() {
        try {
            BigDecimal preco = precoService.buscarPrecoAtual(estoque.idProduto());
            BigDecimal litros, valor;

            if (modoCalculo.getSelectedItem().equals("Por Litros")) {
                litros = new BigDecimal(txtLitros.getText());
                valor = preco.multiply(litros).setScale(2, RoundingMode.HALF_UP);
            } else {
                valor = new BigDecimal(txtValor.getText());
                litros = valor.divide(preco, 2, RoundingMode.HALF_UP);
            }

            BigDecimal imposto = calcularImposto(estoque.nomeProduto());
            BigDecimal totalComImposto = valor.add(valor.multiply(imposto)).setScale(2, RoundingMode.HALF_UP);

            // envia venda pro backend
            vendaService.registrarVenda(estoque.idProduto(), litros, preco, totalComImposto);

            exibirCupom(totalComImposto, valor, imposto);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal calcularImposto(String nome) {
        String nomeUpper = nome.toUpperCase();
        if (nomeUpper.contains("ETANOL")) return new BigDecimal("0.28");
        if (nomeUpper.contains("GASOLINA")) return new BigDecimal("0.42");
        if (nomeUpper.contains("DIESEL")) return new BigDecimal("0.38");
        return BigDecimal.ZERO;
    }

    private void exibirCupom(BigDecimal total, BigDecimal valor, BigDecimal imposto) {
        JTextArea cupom = new JTextArea();
        cupom.setText("""
                ==============================
                         CUPOM FISCAL
                ==============================
                Produto: %s
                Valor sem imposto: R$ %.2f
                Imposto aplicado: %.0f%%
                Total a pagar: R$ %.2f
                ==============================
                Obrigado pela preferência!
                """.formatted(
                estoque.nomeProduto(),
                valor,
                imposto.multiply(BigDecimal.valueOf(100)),
                total
        ));
        JOptionPane.showMessageDialog(this, new JScrollPane(cupom), "Cupom Fiscal", JOptionPane.INFORMATION_MESSAGE);
    }
}
