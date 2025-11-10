package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.BombaApplication;
import com.br.pdvpostocombustivel_frontend.model.dto.BombaResponse;
import com.br.pdvpostocombustivel_frontend.service.BombaService;
import com.br.pdvpostocombustivel_frontend.service.PrecoService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TelaBomba extends JFrame {

    private final BombaService bombaService;
    private final PrecoService precoService;
    private JPanel painelBombas;

    public TelaBomba(BombaService bombaService, PrecoService precoService) {
        this.bombaService = bombaService;
        this.precoService = precoService;
        configurarJanela();
        montarLayout();
        carregarBombas();
    }

    private void configurarJanela() {
        setTitle("⛽ Painel das Bombas - PDV Posto");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 243, 247));
    }

    private void montarLayout() {
        JLabel titulo = new JLabel("Bombas de Combustível", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
        titulo.setForeground(new Color(25, 45, 85));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        painelBombas = new JPanel(new GridLayout(0, 3, 25, 25));
        painelBombas.setBackground(new Color(240, 243, 247));
        painelBombas.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JScrollPane scroll = new JScrollPane(painelBombas);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void carregarBombas() {
        try {
            List<BombaResponse> bombas = bombaService.listarTodas();
            painelBombas.removeAll();

            if (bombas == null || bombas.isEmpty()) {
                JLabel vazio = new JLabel("Nenhuma bomba cadastrada.", SwingConstants.CENTER);
                vazio.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                painelBombas.add(vazio);
            } else {
                int contador = 1;
                for (BombaResponse bomba : bombas) {
                    painelBombas.add(criarCardBomba(contador++, bomba));
                }
            }

            painelBombas.revalidate();
            painelBombas.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar bombas: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarCardBomba(int numero, BombaResponse bomba) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(200, 160));

        JLabel lblTitulo = new JLabel("BOMBA " + numero, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 60, 110));

        String tipo = (bomba.nomeProduto() != null ? bomba.nomeProduto() : "DESCONHECIDO").toUpperCase();
        JLabel lblTipo = new JLabel(tipo, SwingConstants.CENTER);
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTipo.setForeground(Color.DARK_GRAY);

        JPanel indicador = new JPanel();
        indicador.setBackground(definirCorPorProduto(tipo));
        indicador.setPreferredSize(new Dimension(25, 25));
        indicador.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(lblTipo, BorderLayout.CENTER);
        centro.add(indicador, BorderLayout.SOUTH);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(230, 235, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abrirTelaBomba(numero, bomba);
            }
        });

        return card;
    }

    private void abrirTelaBomba(int numero, BombaResponse bomba) {
        try {
            BigDecimal precoAtual;
            try {
                precoAtual = precoService.buscarPrecoAtual(bomba.idEstoque());
                if (precoAtual == null) precoAtual = BigDecimal.ZERO;
            } catch (Exception e) {
                precoAtual = BigDecimal.ZERO;
            }

            new BombaApplication(
                    numero,
                    bomba.nomeProduto(),
                    precoAtual,
                    bomba.idEstoque()
            ).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao abrir bomba: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Color definirCorPorProduto(String nomeProduto) {
        nomeProduto = nomeProduto.toUpperCase();
        if (nomeProduto.contains("GASOLINA COMUM")) return new Color(210, 40, 40);
        if (nomeProduto.contains("ADITIVADA")) return new Color(255, 153, 0);
        if (nomeProduto.contains("ETANOL")) return new Color(0, 170, 0);
        if (nomeProduto.contains("DIESEL")) return new Color(0, 120, 215);
        return new Color(120, 120, 120);
    }
}
