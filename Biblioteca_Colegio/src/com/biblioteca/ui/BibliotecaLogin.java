package com.biblioteca.ui;

import com.biblioteca.utilidades.Validaciones;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class BibliotecaLogin extends JFrame {
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblMensaje;
    private final Color PRIMARY_COLOR = new Color(51, 102, 153);
    private final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public BibliotecaLogin() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Sistema Bibliotecario");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);
    }

    private void inicializarComponentes() {
        // Panel principal con margen
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        // Título
        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setForeground(PRIMARY_COLOR);
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Campos de texto
        JLabel lblCorreo = new JLabel("Correo Electrónico");
        lblCorreo.setFont(MAIN_FONT);
        txtCorreo = crearCampoTexto();

        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(MAIN_FONT);
        txtContrasena = crearCampoContrasena();

        // Agregar componentes al panel de formulario
        gbc.gridy = 0;
        formPanel.add(lblCorreo, gbc);
        gbc.gridy = 1;
        formPanel.add(txtCorreo, gbc);
        gbc.gridy = 2;
        formPanel.add(lblContrasena, gbc);
        gbc.gridy = 3;
        formPanel.add(txtContrasena, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnLogin = crearBoton("Iniciar Sesión", true);
        btnRegister = crearBoton("Registrarse", false);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        // Label para mensajes
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setForeground(Color.RED);
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(lblMensaje, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Agregar listeners
        btnLogin.addActionListener(new LoginActionListener());
        btnRegister.addActionListener(new RegisterButtonListener());
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(MAIN_FONT);
        campo.setPreferredSize(new Dimension(300, 35));
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return campo;
    }

    private JPasswordField crearCampoContrasena() {
        JPasswordField campo = new JPasswordField();
        campo.setFont(MAIN_FONT);
        campo.setPreferredSize(new Dimension(300, 35));
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return campo;
    }

    private JButton crearBoton(String texto, boolean isPrimary) {
        JButton boton = new JButton(texto);
        boton.setFont(MAIN_FONT);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(140, 40));
        
        if (isPrimary) {
            boton.setBackground(Color.WHITE);
            boton.setForeground(PRIMARY_COLOR);
        } else {
            boton.setBackground(Color.WHITE);
            boton.setForeground(PRIMARY_COLOR);
            boton.setBorder(new LineBorder(PRIMARY_COLOR));
        }

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (isPrimary) {
                    boton.setBackground(new Color(41, 82, 123));
                } else {
                    boton.setBackground(new Color(245, 245, 245));
                }
            }

            public void mouseExited(MouseEvent e) {
                if (isPrimary) {
                    boton.setBackground(PRIMARY_COLOR);
                } else {
                    boton.setBackground(Color.WHITE);
                }
            }
        });

        return boton;
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String correo = txtCorreo.getText();
            String contrasena = String.valueOf(txtContrasena.getPassword());

            if (Validaciones.validarCredenciales(correo, contrasena)) {
                String tipoUsuario = Validaciones.obtenerTipoUsuario(correo);
                lblMensaje.setText("Iniciando sesión...");
                dispose();

                switch (tipoUsuario) {
                    case "Administrador":
                        new MenuAdministrador().setVisible(true);
                        break;
                    case "Profesor":
                        new MenuProfesor().setVisible(true);
                        break;
                    case "Alumno":
                        new MenuAlumno().setVisible(true);
                        break;
                    default:
                        lblMensaje.setText("Error: Tipo de usuario no válido");
                }
            } else {
                lblMensaje.setText("Credenciales incorrectas. Por favor, inténtelo de nuevo.");
                txtContrasena.setText("");
            }
        }
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(
                BibliotecaLogin.this,
                "El registro de nuevos usuarios estará disponible próximamente.",
                "Registro",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new BibliotecaLogin().setVisible(true);
        });
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

}