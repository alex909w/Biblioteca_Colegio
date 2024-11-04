package login;

import paneles.MenuAdministrador;
import paneles.MenuProfesor;
import paneles.MenuAlumno;
import bd.GestionUsuarios;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BibliotecaLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblMensaje;

    public BibliotecaLogin() {
        setTitle("Login - Biblioteca ");
        setSize(400, 200);
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
        btnLogin.setBounds(10, 100, 120, 30);
        btnLogin.addActionListener(new LoginActionListener());
        add(btnLogin);

        btnRegister = new JButton("Registrarse");
        btnRegister.setBounds(240, 100, 120, 30);
        btnRegister.addActionListener(new RegisterButtonListener());
        add(btnRegister);

        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(10, 150, 350, 25);
        add(lblMensaje);
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String correo = txtCorreo.getText();
            String contrasena = String.valueOf(txtContrasena.getPassword());

            if (GestionUsuarios.validarCredenciales(correo, contrasena)) {
                String tipoUsuario = GestionUsuarios.obtenerTipoUsuario(correo);
                lblMensaje.setText("Login exitoso!");
                JOptionPane.showMessageDialog(null, "Bienvenido, " + correo);
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
                        lblMensaje.setText("Tipo de usuario no válido");
                        System.err.println("Error: Tipo de usuario desconocido: " + tipoUsuario);
                }
            } else {
                lblMensaje.setText("Correo o contraseña incorrectos");
            }
        }
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Funcionalidad de registro en construcción");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BibliotecaLogin login = new BibliotecaLogin();
            login.setVisible(true);
        });
    }
}
