package com.biblioteca.controladores;

import com.biblioteca.dao.GestionUsuariosDAO;
import com.biblioteca.ui.MenuAdministrador;
import com.biblioteca.ui.MenuProfesor;
import com.biblioteca.ui.MenuAlumno;
import com.biblioteca.utilidades.*;
import com.biblioteca.ui.BibliotecaLogin;

import javax.swing.*;

public class BibliotecaLoginController {

    private BibliotecaLogin vista;
    private GestionUsuariosDAO dao;

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

        if (dao.validarCredenciales(correo, contrasena)) {
            String tipoUsuario = dao.obtenerTipoUsuario(correo);
            vista.mostrarMensaje("Login exitoso!");
            vista.mostrarDialogo("Bienvenido, " + correo);
            vista.dispose();

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

    public void registrarUsuario() {
        JOptionPane.showMessageDialog(vista, "Funcionalidad de registro en construcción");
    }
}
