package com.biblioteca.acciones;

import javax.swing.JPanel;
import javax.swing.JLabel;

import com.biblioteca.dao.GestionInventarioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ActualizarInventario extends JPanel {
    private GestionInventarioDAO gestionInventarioDAO;
    private JTable tablaInventarios;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboFormularios;
    private String nombreFormularioSeleccionado;
    private String nombreTablaSeleccionada;
    private JButton btnEditar;

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
        tablaInventarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablaInventarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                btnEditar.setEnabled(tablaInventarios.getSelectedRow() != -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaInventarios);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnEditar = new JButton("Editar");
        btnEditar.setEnabled(false);
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
            nombreTablaSeleccionada = gestionInventarioDAO.obtenerNombreTablaPorTipo(nombreFormularioSeleccionado);
            if (nombreTablaSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la tabla para el tipo de documento seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreTablaSeleccionada);
            for (String columna : columnas) {
                modeloTabla.addColumn(columna);
            }

            ArrayList<ArrayList<String>> datos = gestionInventarioDAO.obtenerDatosDeTabla(nombreTablaSeleccionada);
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
        EditarInventario editarInventarioPanel = new EditarInventario(nombreTablaSeleccionada, idInventario);

        int result = JOptionPane.showConfirmDialog(this, editarInventarioPanel, "Editar Inventario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            cargarTabla();
        }
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
                gestionInventarioDAO.eliminarInventarioPorId(nombreTablaSeleccionada, idInventario);
                JOptionPane.showMessageDialog(this, "Inventario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
