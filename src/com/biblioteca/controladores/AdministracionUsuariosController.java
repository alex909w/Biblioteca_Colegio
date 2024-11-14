package com.biblioteca.controladores;

import com.biblioteca.acciones.Usuarios.AdministracionUsuarios;
import com.biblioteca.dao.GestionUsuariosDAO;
import com.biblioteca.utilidades.Validaciones;

import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

public class AdministracionUsuariosController {

    private final AdministracionUsuarios vista;
    private final GestionUsuariosDAO dao;

    public AdministracionUsuariosController(AdministracionUsuarios vista) {
        this.vista = vista;
        this.dao = new GestionUsuariosDAO();
    }

    // Método para manejar el botón "Nuevo"
    public void manejarBotonNuevo() {
        vista.limpiarCampos();
        vista.habilitarCampos(true);
        vista.setNuevoModo(true);
        vista.setEditMode(false);
        vista.getBtnNuevo().setVisible(false);
        vista.getBtnActualizar().setVisible(false);
        vista.getBtnEliminar().setVisible(false);
        vista.getBtnGuardar().setVisible(true);
        vista.getBtnCancelar().setVisible(true);
        actualizarIdDinamicamente(); // Generar el ID al entrar en modo Nuevo
    }

    // Método para manejar el botón "Guardar"
    public void manejarBotonGuardar() {
        if (vista.isNuevoModo()) {
            guardarNuevoUsuario();
        } else if (vista.isEditMode()) {
            guardarActualizacion();
        }
    }

    // Método para guardar un nuevo usuario
    private void guardarNuevoUsuario() {
        try {
            String id = vista.getTxtId().getText();
            String nombre = vista.getTxtNombres().getText().trim();
            String apellidos = vista.getTxtApellidos().getText().trim();
            String clave = new String(vista.getTxtClave().getPassword()).trim();
            String email = vista.getTxtEmail().getText().trim();
            String telefono = vista.getTxtTelefono().getText().trim();
            String tipoUsuario = (String) vista.getCbTipoUsuario().getSelectedItem();
            int limitePrestamos = (int) vista.getSpinnerLimitePrestamos().getValue();
            Date selectedDate = (Date) vista.getDatePicker().getModel().getValue();

            if (selectedDate == null) {
                JOptionPane.showMessageDialog(vista, "Por favor, seleccione una fecha válida.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String fechaRegistro = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

            if (nombre.isEmpty() || apellidos.isEmpty() || clave.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validaciones.esCorreoValido(email)) {
                JOptionPane.showMessageDialog(vista, "El correo electrónico no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (limitePrestamos <= 0) {
                JOptionPane.showMessageDialog(vista, "El límite de préstamos debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el correo ya está registrado
            if (dao.correoRegistrado(email)) {
                JOptionPane.showMessageDialog(vista, "El correo electrónico ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean agregado = dao.agregarUsuario(id, nombre, apellidos, clave, email, tipoUsuario, telefono, fechaRegistro, limitePrestamos);
            if (agregado) {
                vista.limpiarCampos();
                vista.habilitarCampos(false);
                vista.setNuevoModo(false);
                vista.getBtnGuardar().setVisible(false);
                vista.getBtnCancelar().setVisible(false);
                vista.getBtnNuevo().setVisible(true);
                vista.getBtnActualizar().setVisible(true);
                vista.getBtnEliminar().setVisible(true);
                cargarUsuariosEnTabla();
                actualizarIdDinamicamente();
                JOptionPane.showMessageDialog(vista, "¡Usuario guardado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al agregar el usuario. Verifique los datos e inténtelo nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para manejar el botón "Actualizar"
    public void manejarBotonActualizar() {
        int selectedRow = vista.getTableUsuarios().getSelectedRow();
        if (selectedRow >= 0) {
            String idUsuario = vista.getTableModel().getValueAt(selectedRow, 0).toString();
            cargarDatosUsuario(idUsuario);
            vista.habilitarCampos(true);
            vista.setEditMode(true);
            vista.setNuevoModo(false);
            vista.getBtnNuevo().setVisible(false);
            vista.getBtnActualizar().setVisible(false);
            vista.getBtnEliminar().setVisible(false);
            vista.getBtnGuardar().setVisible(true);
            vista.getBtnCancelar().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario para editar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para guardar la actualización de un usuario existente
    private void guardarActualizacion() {
        try {
            String id = vista.getTxtId().getText();
            String nombre = vista.getTxtNombres().getText().trim();
            String apellidos = vista.getTxtApellidos().getText().trim();
            String clave = new String(vista.getTxtClave().getPassword()).trim();
            String email = vista.getTxtEmail().getText().trim();
            String telefono = vista.getTxtTelefono().getText().trim();
            String tipoUsuario = (String) vista.getCbTipoUsuario().getSelectedItem();
            int limitePrestamos = (int) vista.getSpinnerLimitePrestamos().getValue();
            Date selectedDate = (Date) vista.getDatePicker().getModel().getValue();

            if (selectedDate == null) {
                JOptionPane.showMessageDialog(vista, "Por favor, seleccione una fecha válida.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String fechaRegistro = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

            if (nombre.isEmpty() || apellidos.isEmpty() || clave.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validaciones.esCorreoValido(email)) {
                JOptionPane.showMessageDialog(vista, "El correo electrónico no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (limitePrestamos <= 0) {
                JOptionPane.showMessageDialog(vista, "El límite de préstamos debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el correo fue cambiado y ya está registrado por otro usuario
            String originalEmail = dao.obtenerEmailPorId(id);
            if (!email.equalsIgnoreCase(originalEmail) && dao.correoRegistrado(email)) {
                JOptionPane.showMessageDialog(vista, "El correo electrónico ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean actualizado = dao.actualizarUsuario(id, nombre, apellidos, clave, email, tipoUsuario, telefono, limitePrestamos);
            if (actualizado) {
                vista.limpiarCampos();
                vista.habilitarCampos(false);
                vista.setEditMode(false);
                vista.setNuevoModo(false);
                vista.getBtnGuardar().setVisible(false);
                vista.getBtnCancelar().setVisible(false);
                vista.getBtnNuevo().setVisible(true);
                vista.getBtnActualizar().setVisible(true);
                vista.getBtnEliminar().setVisible(true);
                cargarUsuariosEnTabla();
                actualizarIdDinamicamente();
                JOptionPane.showMessageDialog(vista, "¡Usuario actualizado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el usuario. Verifique los datos e inténtelo nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para manejar el botón "Eliminar"
    public void manejarBotonEliminar() {
        int selectedRow = vista.getTableUsuarios().getSelectedRow();
        if (selectedRow >= 0) {
            String idUsuario = vista.getTableModel().getValueAt(selectedRow, 0).toString();
            int confirmEliminar = JOptionPane.showConfirmDialog(vista,
                    "¿Estás seguro de que deseas eliminar al usuario con ID: " + idUsuario + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirmEliminar == JOptionPane.YES_OPTION) {
                boolean eliminado = dao.eliminarUsuario(idUsuario);
                if (eliminado) {
                    cargarUsuariosEnTabla();
                    JOptionPane.showMessageDialog(vista, "Usuario eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para manejar el botón "Cancelar"
    public void manejarBotonCancelar() {
        vista.limpiarCampos();
        vista.habilitarCampos(false);
        vista.setEditMode(false);
        vista.setNuevoModo(false);
        vista.getBtnGuardar().setVisible(false);
        vista.getBtnCancelar().setVisible(false);
        vista.getBtnNuevo().setVisible(true);
        vista.getBtnActualizar().setVisible(true);
        vista.getBtnEliminar().setVisible(true);
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
        if (vista.isNuevoModo()) { // Generar ID solo en modo Nuevo
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

    // Método para cargar los datos de un usuario seleccionado
    private void cargarDatosUsuario(String idUsuario) {
        Object[] datosUsuario = dao.obtenerDatosUsuario(idUsuario);
        if (datosUsuario != null) {
            vista.getTxtId().setText((String) datosUsuario[0]);
            vista.getTxtNombres().setText((String) datosUsuario[1]);
            vista.getTxtApellidos().setText((String) datosUsuario[2]);
            vista.getCbTipoUsuario().setSelectedItem((String) datosUsuario[3]);
            vista.getCbTipoUsuario().setEnabled(false);
            vista.getTxtEmail().setText((String) datosUsuario[4]);
            vista.getTxtClave().setText("");
            vista.getTxtTelefono().setText((String) datosUsuario[6]);

            try {
                String fechaStr = (String) datosUsuario[7];
                Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                vista.getDatePicker().getModel().setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                vista.getDatePicker().getModel().setSelected(true);
            } catch (Exception e) {
                vista.getDatePicker().getModel().setValue(null);
            }

            try {
                int limite = (int) datosUsuario[8];
                vista.getSpinnerLimitePrestamos().setValue(limite);
            } catch (Exception e) {
                vista.getSpinnerLimitePrestamos().setValue(1);
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Error al cargar los datos del usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
