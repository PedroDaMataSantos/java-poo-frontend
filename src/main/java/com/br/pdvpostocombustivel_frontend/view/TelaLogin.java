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
        getContentPane().setBackground(Color.WHITE);
    }

    private void criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Autenticação"));
        painel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuário
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Usuário:"), gbc);

        txtUsuario = new JTextField(15);
        gbc.gridx = 1;
        painel.add(txtUsuario, gbc);

        // Senha
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Senha:"), gbc);

        txtSenha = new JPasswordField(15);
        gbc.gridx = 1;
        painel.add(txtSenha, gbc);

        // Botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnEntrar = new JButton("Entrar");
        btnRegistrar = new JButton("Cadastrar-se");
        botoes.add(btnEntrar);
        botoes.add(btnRegistrar);
        botoes.setBackground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        painel.add(botoes, gbc);

        btnEntrar.addActionListener(e -> autenticar());
        btnRegistrar.addActionListener(e -> registrar());

        add(painel, BorderLayout.CENTER);
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
            tela.definirPermissao(acesso.perfil().name()); // define restrições por perfil
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
