package com.biblioteca.controladores;

import com.biblioteca.dao.GestionUsuariosDAO;
import com.biblioteca.ui.MenuAdministrador;
import com.biblioteca.ui.MenuProfesor;
import com.biblioteca.ui.MenuAlumno;
import com.biblioteca.ui.BibliotecaLogin;
import static com.biblioteca.utilidades.Validaciones.esCorreoValido;

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class BibliotecaLoginController {

    private BibliotecaLogin vista;
    private GestionUsuariosDAO dao;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    public static boolean esCorreoValido(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public BibliotecaLoginController(BibliotecaLogin vista) {
        this.vista = vista;
        this.dao = new GestionUsuariosDAO();
    }

  public void iniciarSesion() {
    String correo = vista.getCorreo();
    String contrasena = vista.getContrasena();

    if (correo.isEmpty() || contrasena.isEmpty()) {
        vista.mostrarMensaje("Por favor, ingrese su correo y contraseña.");
        return;
    }

    // Llamamos a validarCredenciales y obtenemos el tipo de usuario
    String tipoUsuario = dao.validarCredenciales(correo, contrasena);

    if (tipoUsuario != null) {
        vista.mostrarMensaje("Login exitoso!");
        vista.mostrarDialogo("Bienvenido, " + correo);
        vista.dispose();

        // Mostrar el menú adecuado según el tipo de usuario
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
                vista.mostrarMensaje("Tipo de usuario no válido");
                System.err.println("Error: Tipo de usuario desconocido: " + tipoUsuario);
        }
    } else {
        vista.mostrarMensaje("Correo o contraseña incorrectos");
    }
}


    public void recuperarContrasena() {
        // Solicitar el correo electrónico del usuario
        String correo = JOptionPane.showInputDialog(vista, "Ingrese su correo electrónico para recuperar su contraseña:", 
                                                    "Recuperar Contraseña", JOptionPane.INFORMATION_MESSAGE);

        // Verificar si el usuario ingresó un correo
        if (correo == null || correo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor, ingrese un correo electrónico válido.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si el correo está registrado en la base de datos
        if (!dao.correoRegistrado(correo)) {
            JOptionPane.showMessageDialog(vista, "El correo electrónico no está registrado.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generar un token de recuperación único
        String token = UUID.randomUUID().toString();

        // Guardar el token en la base de datos
        dao.guardarTokenRecuperacion(correo, token);

        // Enviar el correo de recuperación
        enviarCorreoRecuperacion(correo, token);

        JOptionPane.showMessageDialog(vista, "Si el correo ingresado está registrado, recibirá un enlace para restablecer su contraseña.", 
                                      "Recuperación de Contraseña", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para enviar el correo electrónico con el enlace de recuperación
    private void enviarCorreoRecuperacion(String correo, String token) {
        String host = "smtp.gmail.com"; // Servidor SMTP de Gmail
        String from = "amigosdonboscoinfo@gmail.com"; // Tu correo de Gmail
        String pass = "Alex909w1992"; // Tu contraseña de Gmail (NO recomendado en producción)
        String linkRecuperacion = "http://tuservidor.com/restablecer?token=" + token; // Enlace para restablecer contraseña

        // Configuración de las propiedades SMTP de Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Habilitar TLS

        // Crear la autenticación con el correo y la contraseña
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass); // Autenticación con tu correo y contraseña
            }
        };

        // Crear una sesión de correo
        Session session = Session.getInstance(props, authenticator);

        // Crear el mensaje de correo
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));
            message.setSubject("Recuperación de Contraseña");
            message.setText("Para restablecer su contraseña, haga clic en el siguiente enlace:\n" + linkRecuperacion);

            // Enviar el correo
            Transport.send(message);
            System.out.println("Correo enviado con éxito.");
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al enviar el correo de recuperación.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static String validarDatosUsuario(String nombre, String apellido, String clave, String email, String telefono, int limitePrestamos) {
        StringBuilder errorBuilder = new StringBuilder();

        if (nombre == null || nombre.trim().isEmpty()) {
            errorBuilder.append("El nombre es obligatorio.\n");
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            errorBuilder.append("El apellido es obligatorio.\n");
        }

        if (clave == null || clave.trim().isEmpty()) {
            errorBuilder.append("La contraseña es obligatoria.\n");
        }

        if (email == null || email.trim().isEmpty()) {
            errorBuilder.append("El correo electrónico es obligatorio.\n");
        } else if (!esCorreoValido(email)) {
            errorBuilder.append("El correo electrónico no es válido.\n");
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            errorBuilder.append("El teléfono es obligatorio.\n");
        }

        if (limitePrestamos <= 0) {
            errorBuilder.append("El límite de préstamos debe ser un número positivo.\n");
        }

        return errorBuilder.toString();
    }
    
}
