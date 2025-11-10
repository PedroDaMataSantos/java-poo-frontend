package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import com.br.pdvpostocombustivel_frontend.service.EstoqueService;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaBomba extends JFrame {

    private final EstoqueService estoqueService;
    private JPanel painelBombas;

    public TelaBomba(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
        configurarJanela();
        montarLayout();
        carregarBombas();
    }

    private void configurarJanela() {
        setTitle("⛽ Painel das Bombas - PDV Posto");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250)); // cinza claro moderno
    }

    private void montarLayout() {
        // Título superior
        JLabel titulo = new JLabel("Bombas de Combustível", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        titulo.setForeground(new Color(20, 40, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Painel de bombas
        painelBombas = new JPanel(new GridLayout(0, 3, 20, 20));
        painelBombas.setBackground(new Color(245, 247, 250));
        painelBombas.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JScrollPane scrollPane = new JScrollPane(painelBombas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarBombas() {
        try {
            List<EstoqueResponse> estoques = estoqueService.listAll();
            painelBombas.removeAll();

            if (estoques.isEmpty()) {
                JLabel vazio = new JLabel("Nenhuma bomba cadastrada.", SwingConstants.CENTER);
                vazio.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                painelBombas.add(vazio);
            } else {
                for (EstoqueResponse estoque : estoques) {
                    painelBombas.add(criarBotaoBomba(estoque));
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

    private JButton criarBotaoBomba(EstoqueResponse estoque) {
        String nome = estoque.produtoNome() != null ? estoque.produtoNome() : "Combustível";
        Color cor = definirCorPorProduto(nome);

        JButton btn = new JButton("<html><center><b>" + nome + "</b><br>Tanque: " +
                estoque.localTanque() + "<br><small>ID: " + estoque.id() + "</small></center></html>");

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));

        btn.addActionListener(e -> abrirDialogVenda(estoque));

        // Efeito hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(cor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(cor);
            }
        });

        return btn;
    }

    private Color definirCorPorProduto(String nomeProduto) {
        nomeProduto = nomeProduto.toUpperCase();
        if (nomeProduto.contains("GASOLINA COMUM")) return new Color(220, 20, 60);
        if (nomeProduto.contains("ADITIVADA")) return new Color(255, 140, 0);
        if (nomeProduto.contains("ETANOL")) return new Color(34, 139, 34);
        if (nomeProduto.contains("DIESEL")) return new Color(30, 144, 255);
        return new Color(128, 128, 128);
    }

    private void abrirDialogVenda(EstoqueResponse estoque) {
        DialogVenda dialog = new DialogVenda(this, estoque);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TelaBomba(new EstoqueService(new RestTemplate())).setVisible(true)
        );
    }
}
