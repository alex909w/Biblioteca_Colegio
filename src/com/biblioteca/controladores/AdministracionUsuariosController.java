package com.biblioteca.controladores;

import com.biblioteca.acciones.Usuarios.AdministracionUsuarios;
import com.biblioteca.dao.GestionUsuariosDAO;
import com.biblioteca.utilidades.Validaciones;
import javax.swing.*;
import java.util.Date;

public class AdministracionUsuariosController {

    private final AdministracionUsuarios vista;
    private final GestionUsuariosDAO dao;

    public AdministracionUsuariosController(AdministracionUsuarios vista) {
        this.vista = vista;
        this.dao = new GestionUsuariosDAO();
    }

    // Método para manejar el botón "Buscar"
    public void buscarUsuarios() {
        String termino = vista.getTxtBuscar().getText().trim();
        String filtro = (String) vista.getCbFiltroBusqueda().getSelectedItem();
        dao.buscarUsuarioEnTabla(vista.getTableModel(), filtro, termino);
    }

    // Método para manejar el botón "Limpiar"
    public void limpiarBusqueda() {
        vista.getTxtBuscar().setText("");
        vista.getCbFiltroBusqueda().setSelectedIndex(0);
        cargarUsuariosEnTabla();
    }

    // Método para cargar usuarios en la tabla
    public void cargarUsuariosEnTabla() {
        dao.cargarUsuariosEnTabla(vista.getTableModel(), vista);
    }

    // Método para generar el próximo ID de usuario
    public void actualizarIdDinamicamente() {
        if (!vista.isEditMode()) {
            String nuevoId = generarIdUsuario((String) vista.getCbTipoUsuario().getSelectedItem());
            vista.getTxtId().setText(nuevoId);
        }
    }

    // Método para generar ID de usuario
    public String generarIdUsuario(String tipoUsuario) {
        int siguienteId = dao.generarSiguienteId(tipoUsuario);
        String inicial = "";

        switch (tipoUsuario) {
            case "Administrador":
                inicial = "AD";
                break;
            case "Profesor":
                inicial = "PR";
                break;
            case "Alumno":
                inicial = "AL";
                break;
        }

        return inicial + siguienteId;
    }

    public void nuevoUsuario() {
    String nombre = vista.getTxtNombres().getText();
    String apellidos = vista.getTxtApellidos().getText();
    String email = vista.getTxtEmail().getText();
    String telefono = vista.getTxtTelefono().getText();
    String clave = new String(vista.getTxtClave().getPassword());
    String tipoUsuario = vista.getCbTipoUsuario().getSelectedItem().toString();
    int limitePrestamos = (int) vista.getSpinnerLimitePrestamos().getValue();

    // Llamada al DAO para agregar el usuario
    boolean usuarioAgregado = dao.agregarUsuario(vista.getTxtId().getText(), nombre, apellidos, clave, email, tipoUsuario, telefono, "NOW()", limitePrestamos);

    if (usuarioAgregado) {
        cargarUsuariosEnTabla();
        JOptionPane.showMessageDialog(vista, "Usuario creado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(vista, "No se pudo agregar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

public void actualizarUsuario() {
    // Obtén los valores de los campos del formulario
    String id = vista.getTxtId().getText();  // Obtener el ID del campo de texto
    String nombre = vista.getTxtNombres().getText();  // Obtener el nombre
    String apellidos = vista.getTxtApellidos().getText();  // Obtener los apellidos
    String email = vista.getTxtEmail().getText();  // Obtener el email
    String telefono = vista.getTxtTelefono().getText();  // Obtener el teléfono
    String clave = new String(vista.getTxtClave().getPassword());  // Obtener la contraseña
    String tipoUsuario = vista.getCbTipoUsuario().getSelectedItem().toString();  // Obtener el tipo de usuario
    int limitePrestamos = (int) vista.getSpinnerLimitePrestamos().getValue();  // Obtener el límite de préstamos

    // Verificar que los campos no estén vacíos antes de intentar actualizar
    if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Por favor, complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Llamada al DAO para actualizar el usuario
    boolean usuarioActualizado = dao.actualizarUsuario(id, nombre, apellidos, clave, email, tipoUsuario, telefono, limitePrestamos);

    // Si el usuario fue actualizado correctamente, actualizar la tabla y mostrar mensaje
    if (usuarioActualizado) {
        cargarUsuariosEnTabla();  // Actualizar la tabla de usuarios
        JOptionPane.showMessageDialog(vista, "Usuario actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } else {
        // Mostrar un mensaje si no se pudo actualizar el usuario
        JOptionPane.showMessageDialog(vista, "No se pudo actualizar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


public void eliminarUsuario() {
    String id = vista.getTxtId().getText();

    // Llamada al DAO para eliminar el usuario
    boolean usuarioEliminado = dao.eliminarUsuario(id);

    if (usuarioEliminado) {
        cargarUsuariosEnTabla();
        JOptionPane.showMessageDialog(vista, "Usuario eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(vista, "No se pudo eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

}
