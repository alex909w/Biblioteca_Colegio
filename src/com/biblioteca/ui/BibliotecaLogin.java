package com.biblioteca.ui;

import com.biblioteca.controladores.BibliotecaLoginController;

import javax.swing.*;
import java.awt.*;

public class BibliotecaLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblMensaje;

    private BibliotecaLoginController controlador;

    public BibliotecaLogin() {
        controlador = new BibliotecaLoginController(this);

        setTitle("Login - Biblioteca ");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setBounds(10, 10, 120, 25);
        add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(140, 10, 220, 25);
        add(txtCorreo);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(10, 50, 120, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(140, 50, 220, 25);
        add(txtContrasena);

        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(10, 100, 150, 40);
        btnLogin.addActionListener(e -> controlador.iniciarSesion());
        add(btnLogin);

        btnRegister = new JButton("Registrarse");
        btnRegister.setBounds(210, 100, 150, 40);
        btnRegister.addActionListener(e -> controlador.registrarUsuario());
        add(btnRegister);

        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(10, 160, 350, 25);
        add(lblMensaje);
    }

    // Métodos para acceder a los componentes desde el controlador
    public String getCorreo() {
        return txtCorreo.getText().trim();
    }

    public String getContrasena() {
        return new String(txtContrasena.getPassword()).trim();
    }

    public void mostrarMensaje(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    public void mostrarDialogo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void limpiarCampos() {
        txtCorreo.setText("");
        txtContrasena.setText("");
        lblMensaje.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BibliotecaLogin login = new BibliotecaLogin();
            login.setVisible(true);
        });
    }
}
