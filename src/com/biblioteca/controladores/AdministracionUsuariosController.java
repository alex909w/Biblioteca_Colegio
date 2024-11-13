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
}
