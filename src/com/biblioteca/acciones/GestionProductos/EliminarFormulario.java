package com.biblioteca.acciones.GestionProductos;

import com.biblioteca.dao.GestionFormularioDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class EliminarFormulario extends JPanel {

    private GestionFormularioDAO tipoDocumentoDAO;
    private JComboBox<String> comboFormularios;

    public EliminarFormulario() {
        setLayout(new GridLayout(3, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            tipoDocumentoDAO = new GestionFormularioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        add(new JLabel("Seleccione el Formulario a Eliminar:"));
        comboFormularios = new JComboBox<>(cargarTiposDocumentos());
        add(comboFormularios);

        JButton btnEliminar = new JButton("Eliminar Formulario");
        btnEliminar.addActionListener(e -> eliminarFormulario());
        add(btnEliminar);
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = tipoDocumentoDAO.obtenerTiposDocumentos();
            return tipos.isEmpty() ? new String[0] : tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void eliminarFormulario() {
        String nombreFormulario = (String) comboFormularios.getSelectedItem();
        if (nombreFormulario == null || nombreFormulario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un formulario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar el formulario " + nombreFormulario + " y todos sus datos?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = tipoDocumentoDAO.eliminarDatosDeTabla(nombreFormulario);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Formulario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Actualizar la lista de formularios después de eliminar
                    comboFormularios.setModel(new DefaultComboBoxModel<>(cargarTiposDocumentos()));
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el formulario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
