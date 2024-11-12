package com.biblioteca.controladores;

import com.biblioteca.acciones.AdministracionUsuarios;
import com.biblioteca.dao.GestionUsuariosDAO;
import com.biblioteca.utilidades.Validaciones;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class AdministracionUsuariosController {

    private final AdministracionUsuarios vista;
    private final GestionUsuariosDAO dao;

    public AdministracionUsuariosController(AdministracionUsuarios vista) {
        this.vista = vista;
        this.dao = new GestionUsuariosDAO();
    }

    // Método para manejar el botón "Agregar/Nuevo/Guardar"
    public void manejarBotonAgregar() {
        if (!vista.isNuevoModo() && !vista.isEditMode()) {
            int confirmCrear = JOptionPane.showConfirmDialog(vista,
                    "¿Quieres crear un nuevo usuario?",
                    "Confirmar Nuevo",
                    JOptionPane.YES_NO_OPTION);
            if (confirmCrear == JOptionPane.NO_OPTION) {
                return;
            }

            vista.limpiarCampos();
            vista.habilitarCampos(true);
            actualizarIdDinamicamente();
            vista.getBtnAgregar().setText("Guardar");
            vista.setNuevoModo(true);
            vista.setEditMode(false);

            vista.getBtnActualizar().setEnabled(false);
            vista.getBtnActualizar().setVisible(false);

            vista.getBtnEliminar().setEnabled(false);
            vista.getPanelBotones().remove(vista.getBtnAgregar());
            vista.getPanelBotones().remove(vista.getBtnActualizar());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 20, 0, 20);
            gbc.gridy = 0;

            gbc.gridx = 0;
            vista.getPanelBotones().add(vista.getBtnAgregar(), gbc);

            gbc.gridx = 1;
            vista.getPanelBotones().add(vista.getBtnCancelar(), gbc);
            vista.getBtnCancelar().setVisible(true);

            vista.getPanelBotones().revalidate();
            vista.getPanelBotones().repaint();
        } else if (vista.isNuevoModo()) {
            agregarUsuario();
        }
    }

    // Método para manejar el botón "Actualizar"
    public void manejarBotonActualizar() {
        if (!vista.isEditMode() && !vista.isNuevoModo()) {
            int selectedRow = vista.getTableUsuarios().getSelectedRow();
            if (selectedRow >= 0) {
                int confirmEditar = JOptionPane.showConfirmDialog(vista,
                        "¿Quieres editar la información del usuario seleccionado?",
                        "Confirmar Edición",
                        JOptionPane.YES_NO_OPTION);
                if (confirmEditar == JOptionPane.YES_OPTION) {
                    cargarDatosParaEdicion(selectedRow);

                    vista.getBtnActualizar().setText("Guardar");
                    vista.setEditMode(true);
                    vista.setNuevoModo(false);

                    vista.getBtnAgregar().setVisible(false);

                    vista.getBtnEliminar().setEnabled(false);

                    vista.getPanelBotones().remove(vista.getBtnActualizar());
                    vista.getPanelBotones().remove(vista.getBtnCancelarActualizacion());

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 20, 0, 20);
                    gbc.gridy = 0;

                    gbc.gridx = 0;
                    vista.getPanelBotones().add(vista.getBtnActualizar(), gbc);

                    gbc.gridx = 1;
                    vista.getPanelBotones().add(vista.getBtnCancelarActualizacion(), gbc);
                    vista.getBtnCancelarActualizacion().setVisible(true);

                    vista.getPanelBotones().revalidate();
                    vista.getPanelBotones().repaint();
                }
            } else {
                JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario para actualizar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (vista.isEditMode()) {
            guardarActualizacion();
        }
    }

    // Método para agregar un nuevo usuario
    public void agregarUsuario() {
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

            if (nombre.isEmpty() || apellidos.isEmpty() || clave.isEmpty() || email.isEmpty() || telefono.isEmpty() || fechaRegistro.isEmpty()) {
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

            boolean agregado = dao.agregarUsuario(id, nombre, apellidos, clave, email, tipoUsuario, telefono, fechaRegistro, limitePrestamos);
            if (agregado) {
                vista.limpiarCampos();
                vista.habilitarCampos(false);
                vista.getBtnAgregar().setText("Nuevo");
                vista.setNuevoModo(false);

                vista.getBtnActualizar().setEnabled(true);
                vista.getBtnActualizar().setVisible(true);

                vista.getBtnEliminar().setEnabled(true);

                vista.getPanelBotones().remove(vista.getBtnAgregar());
                vista.getPanelBotones().remove(vista.getBtnCancelar());

                vista.getPanelBotones().add(vista.getBtnAgregar(), vista.getGbcNuevo());
                vista.getPanelBotones().add(vista.getBtnActualizar(), vista.getGbcActualizar());

                vista.getBtnAgregar().setVisible(true);

                vista.getPanelBotones().revalidate();
                vista.getPanelBotones().repaint();

                cargarUsuariosEnTabla();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al agregar el usuario. Verifique los datos e inténtelo nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para guardar la actualización de un usuario existente
    public void guardarActualizacion() {
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Desea guardar los cambios realizados al usuario?",
                "Confirmar Guardar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String id = vista.getTxtId().getText();
            String nombre = vista.getTxtNombres().getText().trim();
            String apellidos = vista.getTxtApellidos().getText().trim();
            String clave = new String(vista.getTxtClave().getPassword()).trim();
            String email = vista.getTxtEmail().getText().trim();
            String telefono = vista.getTxtTelefono().getText().trim();
            String tipoUsuario = (String) vista.getCbTipoUsuario().getSelectedItem();
            int limitePrestamos = (int) vista.getSpinnerLimitePrestamos().getValue();

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

            boolean actualizado = dao.actualizarUsuario(id, nombre, apellidos, clave, email, tipoUsuario, telefono, limitePrestamos);
            if (actualizado) {
                JOptionPane.showMessageDialog(vista, "Usuario actualizado correctamente.");
                cargarUsuariosEnTabla();
                vista.limpiarCampos();
                vista.habilitarCampos(false);
                vista.getBtnActualizar().setText("Actualizar");
                vista.setEditMode(false);
                vista.setNuevoModo(false);

                vista.getBtnAgregar().setVisible(true);

                vista.getBtnActualizar().setEnabled(true);
                vista.getBtnActualizar().setVisible(true);

                vista.getBtnEliminar().setEnabled(true);

                vista.getPanelBotones().remove(vista.getBtnActualizar());
                vista.getPanelBotones().remove(vista.getBtnCancelarActualizacion());

                vista.getPanelBotones().add(vista.getBtnAgregar(), vista.getGbcNuevo());
                vista.getPanelBotones().add(vista.getBtnActualizar(), vista.getGbcActualizar());

                vista.getBtnCancelarActualizacion().setVisible(false);

                vista.getPanelBotones().revalidate();
                vista.getPanelBotones().repaint();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el usuario. Verifique los datos ingresados e intente nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Método para cancelar la operación actual (Cancelación de Nuevo Usuario)
    public void cancelarOperacion() {
        int confirmCancelar = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de que deseas cancelar la operación?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION);
        if (confirmCancelar == JOptionPane.YES_OPTION) {
            vista.limpiarCampos();
            vista.habilitarCampos(false);
            vista.getBtnAgregar().setText("Nuevo");
            vista.setNuevoModo(false);
            vista.setEditMode(false);

            vista.getBtnActualizar().setEnabled(true);
            vista.getBtnActualizar().setVisible(true);

            vista.getBtnEliminar().setEnabled(true);

            vista.getPanelBotones().remove(vista.getBtnAgregar());
            vista.getPanelBotones().remove(vista.getBtnCancelar());

            vista.getPanelBotones().add(vista.getBtnAgregar(), vista.getGbcNuevo());
            vista.getPanelBotones().add(vista.getBtnActualizar(), vista.getGbcActualizar());

            vista.getBtnAgregar().setVisible(true);

            vista.getPanelBotones().revalidate();
            vista.getPanelBotones().repaint();
        }
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

    // Método para cargar datos en el formulario para edición
    public void cargarDatosParaEdicion(int rowIndex) {
        vista.setEditMode(true);
        vista.setNuevoModo(false);

        String id = vista.getTableModel().getValueAt(rowIndex, 0).toString();
        vista.getTxtId().setText(id);
        vista.getTxtId().setEditable(false);

        vista.getTxtNombres().setText(vista.getTableModel().getValueAt(rowIndex, 1).toString());
        vista.getTxtApellidos().setText(vista.getTableModel().getValueAt(rowIndex, 2).toString());
        vista.getCbTipoUsuario().setSelectedItem(vista.getTableModel().getValueAt(rowIndex, 3).toString());
        vista.getCbTipoUsuario().setEnabled(false);
        vista.getTxtEmail().setText(vista.getTableModel().getValueAt(rowIndex, 4).toString());
        vista.getTxtClave().setText(vista.getTableModel().getValueAt(rowIndex, 5).toString());
        vista.getTxtTelefono().setText(vista.getTableModel().getValueAt(rowIndex, 6).toString());

        try {
            String fechaStr = vista.getTableModel().getValueAt(rowIndex, 7).toString();
            Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            vista.getDatePicker().getModel().setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            vista.getDatePicker().getModel().setSelected(true);
        } catch (Exception e) {
            vista.getDatePicker().getModel().setValue(null);
        }

        String limiteStr = vista.getTableModel().getValueAt(rowIndex, 8).toString();
        try {
            int limite = Integer.parseInt(limiteStr);
            vista.getSpinnerLimitePrestamos().setValue(limite);
        } catch (NumberFormatException e) {
            vista.getSpinnerLimitePrestamos().setValue(1);
        }

        vista.habilitarCampos(true);
    }

    // Método para eliminar un usuario seleccionado
    public void eliminarUsuario() {
        int selectedRow = vista.getTableUsuarios().getSelectedRow();
        if (selectedRow >= 0) {
            String id = vista.getTableModel().getValueAt(selectedRow, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(vista,
                    "¿Desea eliminar el usuario con ID: " + id + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean eliminado = dao.eliminarUsuario(id);
                if (eliminado) {
                    JOptionPane.showMessageDialog(vista, "Usuario eliminado correctamente.");
                    cargarUsuariosEnTabla();
                    vista.limpiarCampos();
                    vista.habilitarCampos(false);
                    vista.getBtnActualizar().setText("Actualizar");
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario para eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Método para buscar usuarios según el filtro y término ingresados
    public void buscarUsuarios() {
        String termino = vista.getTxtBuscar().getText().trim();
        String filtro = (String) vista.getCbFiltroBusqueda().getSelectedItem();
        dao.buscarUsuarioEnTabla(vista.getTableModel(), filtro, termino);
    }

    // Método para limpiar la búsqueda y recargar todos los usuarios
    public void limpiarBusqueda() {
        vista.getTxtBuscar().setText("");
        vista.getCbFiltroBusqueda().setSelectedIndex(0);
        cargarUsuariosEnTabla();
    }

    public void cancelarActualizacion() {
        int confirmCancelar = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de que deseas cancelar la actualización?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION);
        if (confirmCancelar == JOptionPane.YES_OPTION) {
            vista.limpiarCampos();
            vista.habilitarCampos(false);
            vista.getBtnActualizar().setText("Actualizar");
            vista.setEditMode(false);
            vista.setNuevoModo(false);

            vista.getBtnAgregar().setVisible(true);
            vista.getBtnActualizar().setEnabled(true);
            vista.getBtnActualizar().setVisible(true);
            vista.getBtnEliminar().setEnabled(true);

            vista.getPanelBotones().remove(vista.getBtnActualizar());
            vista.getPanelBotones().remove(vista.getBtnCancelarActualizacion());

            vista.getPanelBotones().add(vista.getBtnAgregar(), vista.getGbcNuevo());
            vista.getPanelBotones().add(vista.getBtnActualizar(), vista.getGbcActualizar());

            vista.getBtnCancelarActualizacion().setVisible(false);

            vista.getPanelBotones().revalidate();
            vista.getPanelBotones().repaint();
        }
    }
}
