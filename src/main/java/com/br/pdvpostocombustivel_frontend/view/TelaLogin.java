package com.br.pdvpostocombustivel_frontend.view;

import com.br.pdvpostocombustivel_frontend.model.dto.AcessoResponse;
import com.br.pdvpostocombustivel_frontend.service.AcessoService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {

    private final AcessoService acessoService;
    private final ConfigurableApplicationContext context;

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar, btnRegistrar;

    public TelaLogin(AcessoService acessoService, ConfigurableApplicationContext context) {
        this.acessoService = acessoService;
        this.context = context;

        configurarJanela();
        criarFormulario();
    }

    private void configurarJanela() {
        setTitle("Login - Sistema PDV");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(20, 20, 20)); // fundo geral
    }

    private void criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 200), 1),
                "Autenticação",
                0, 0, new Font("Arial", Font.BOLD, 12),
                new Color(0, 255, 200)
        ));
        painel.setBackground(new Color(20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsuario = criarLabel("Usuário:");
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(lblUsuario, gbc);

        txtUsuario = criarCampoTexto();
        gbc.gridx = 1;
        painel.add(txtUsuario, gbc);

        JLabel lblSenha = criarLabel("Senha:");
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(lblSenha, gbc);

        txtSenha = new JPasswordField(15);
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 13));
        txtSenha.setBackground(new Color(40, 40, 40));
        txtSenha.setForeground(Color.WHITE);
        txtSenha.setCaretColor(new Color(0, 255, 200));
        txtSenha.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 200)));
        gbc.gridx = 1;
        painel.add(txtSenha, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botoes.setBackground(new Color(20, 20, 20));

        btnEntrar = criarBotaoVerde("Entrar");
        btnRegistrar = criarBotaoCinza("Cadastrar-se");

        botoes.add(btnEntrar);
        botoes.add(btnRegistrar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        painel.add(botoes, gbc);

        btnEntrar.addActionListener(e -> autenticar());
        btnRegistrar.addActionListener(e -> registrar());

        add(painel, BorderLayout.CENTER);
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(0, 255, 200));
        return label;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField(15);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBackground(new Color(40, 40, 40));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(new Color(0, 255, 200));
        campo.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 200)));
        return campo;
    }

    private JButton criarBotaoVerde(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(0, 180, 120));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return botao;
    }

    private JButton criarBotaoCinza(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(60, 63, 65));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return botao;
    }

    private void autenticar() {
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            AcessoResponse acesso = acessoService.login(usuario, senha);

            if (acesso == null) {
                JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Bem-vindo, " + acesso.usuario() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            abrirTelaPrincipal(acesso);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao autenticar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaPrincipal(AcessoResponse acesso) {
        EventQueue.invokeLater(() -> {
            TelaPrincipal tela = context.getBean(TelaPrincipal.class);
            tela.definirPermissao(acesso.perfil().name());
            tela.setVisible(true);
            dispose();
        });
    }

    private void registrar() {
        JOptionPane.showMessageDialog(this, """
                Para cadastrar um novo usuário, entre em contato com o administrador.
                O acesso é restrito a usuários cadastrados no sistema.
                """, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }
}
