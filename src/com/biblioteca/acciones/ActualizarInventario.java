package com.biblioteca.acciones;

import javax.swing.JPanel;
import javax.swing.JLabel;

import com.biblioteca.dao.GestionInventarioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActualizarInventario extends JPanel {

    private GestionInventarioDAO gestionInventarioDAO;
    private JTable tablaInventarios;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboFormularios;
    private String nombreFormularioSeleccionado;

    public ActualizarInventario() {
         add(new JLabel("Actualizar Entrada/Salida de Inventario"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionInventarioDAO = new GestionInventarioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Seleccione el Formulario de Inventario:"));
        comboFormularios = new JComboBox<>(cargarTiposDocumentos());
        comboFormularios.addActionListener(e -> cargarTabla());
        panelSuperior.add(comboFormularios);
        add(panelSuperior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        tablaInventarios = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaInventarios);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarInventarioSeleccionado());
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarInventarioSeleccionado());

        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = gestionInventarioDAO.obtenerTiposDocumentos();
            return tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        nombreFormularioSeleccionado = (String) comboFormularios.getSelectedItem();
        if (nombreFormularioSeleccionado == null) return;

        try {
            ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreFormularioSeleccionado);
            for (String columna : columnas) {
                modeloTabla.addColumn(columna);
            }

            ArrayList<ArrayList<String>> datos = gestionInventarioDAO.obtenerDatosDeTabla(nombreFormularioSeleccionado);
            for (ArrayList<String> fila : datos) {
                modeloTabla.addRow(fila.toArray());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarInventarioSeleccionado() {
        int filaSeleccionada = tablaInventarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un inventario para editar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idInventario = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        EditarInventario editarInventarioPanel = new EditarInventario(nombreFormularioSeleccionado, idInventario);
        JOptionPane.showMessageDialog(this, editarInventarioPanel, "Editar Inventario", JOptionPane.PLAIN_MESSAGE);
    }

    private void eliminarInventarioSeleccionado() {
        int filaSeleccionada = tablaInventarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un inventario para eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idInventario = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar el inventario con ID: " + idInventario + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                gestionInventarioDAO.eliminarInventarioPorId(nombreFormularioSeleccionado, idInventario);
                JOptionPane.showMessageDialog(this, "Inventario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    


}
