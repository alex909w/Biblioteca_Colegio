package com.biblioteca.ui;

import com.biblioteca.control.BibliotecaLoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BibliotecaLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JLabel lblMensaje;
    private JLabel lblRecuperarContrasena;

    private BibliotecaLoginController controlador;

    public BibliotecaLogin() {
        controlador = new BibliotecaLoginController(this);

        setTitle("Sistema Bibliotecario");
        setSize(490, 320); // Adjusted height for layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Header label for "Iniciar Sesión"
        JLabel lblHeader = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 24));
        lblHeader.setForeground(new Color(0, 70, 130)); // Dark blue color
        lblHeader.setBounds(0, 20, 450, 30);
        add(lblHeader);

        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCorreo.setForeground(new Color(60, 60, 60)); // Soft gray color
        lblCorreo.setBounds(50, 80, 150, 25); // Adjusted width for full text
        add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(210, 80, 170, 25); // Adjusted position to align with wider label
        txtCorreo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Light gray border
        add(txtCorreo);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
        lblContrasena.setForeground(new Color(60, 60, 60)); // Soft gray color
        lblContrasena.setBounds(50, 120, 150, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(210, 120, 170, 25);
        txtContrasena.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Light gray border
        add(txtContrasena);

        // Centered Iniciar Sesión button
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLogin.setBounds(165, 180, 120, 30); // Centered position
        btnLogin.setBackground(Color.WHITE);
        btnLogin.setForeground(new Color(0, 70, 130)); // Dark blue text
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(0, 70, 130))); // Dark blue border
        btnLogin.addActionListener(e -> controlador.iniciarSesion());
        add(btnLogin);

        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(10, 220, 430, 25);
        add(lblMensaje);

        // Recover Password label
        lblRecuperarContrasena = new JLabel("¿Olvidaste tu contraseña?");
        lblRecuperarContrasena.setFont(new Font("Arial", Font.PLAIN, 12));
        lblRecuperarContrasena.setForeground(new Color(0, 70, 130)); // Dark blue color for link effect
        lblRecuperarContrasena.setBounds(160, 240, 170, 30); // Moved closer to the button
        lblRecuperarContrasena.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand pointer
        lblRecuperarContrasena.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controlador.recuperarContrasena(); // Method in controller to handle password recovery
            }
        });
        add(lblRecuperarContrasena);
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
