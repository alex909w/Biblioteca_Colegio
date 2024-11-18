package com.biblioteca.Administracion;

import com.biblioteca.acciones.EditarInventario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import com.biblioteca.dao.GestionInventarioDAO;

public class EditarEliminarRegistrosPanel extends JPanel {
    private GestionInventarioDAO gestionInventarioDAO;
    private JComboBox<String> comboTablas;
    private JTable tablaDatos;
    private DefaultTableModel modeloTabla;
    private JButton btnEditar;
    private JButton btnEliminar;
    
    public EditarEliminarRegistrosPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        try {
            gestionInventarioDAO = new GestionInventarioDAO();
            inicializarComponentes();
            cargarTablasEnComboBox();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void inicializarComponentes() {
        // Panel superior para selección de tabla
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSeleccionarTabla = new JLabel("Seleccione una Tabla:");
        comboTablas = new JComboBox<>();
        
        comboTablas.addActionListener(e -> cargarDatosTablaSeleccionada());
        
        panelSuperior.add(lblSeleccionarTabla);
        panelSuperior.add(comboTablas);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla para mostrar datos
        modeloTabla = new DefaultTableModel();
        tablaDatos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaDatos);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarRegistroSeleccionado();
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarRegistroSeleccionado();
            }
        });
        
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarTablasEnComboBox() {
        try {
            ArrayList<String> tablas = gestionInventarioDAO.obtenerTiposDocumentos();
            for (String tabla : tablas) {
                comboTablas.addItem(tabla);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las tablas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDatosTablaSeleccionada() {
        String nombreTablaSeleccionada = (String) comboTablas.getSelectedItem();
        if (nombreTablaSeleccionada == null) return;
        
        try {
            modeloTabla.setRowCount(0); // Limpiar tabla
            modeloTabla.setColumnCount(0);
            
            ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreTablaSeleccionada);
            for (String columna : columnas) {
                modeloTabla.addColumn(columna);
            }
            
            ArrayList<ArrayList<String>> datos = gestionInventarioDAO.obtenerDatosDeTabla(nombreTablaSeleccionada);
            for (ArrayList<String> fila : datos) {
                modeloTabla.addRow(fila.toArray());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de la tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarRegistroSeleccionado() {
        int filaSeleccionada = tablaDatos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro para editar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String nombreTablaSeleccionada = (String) comboTablas.getSelectedItem();
        String idRegistro = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        
        EditarInventario editarPanel = new EditarInventario(nombreTablaSeleccionada, idRegistro);
        int result = JOptionPane.showConfirmDialog(this, editarPanel, "Editar Registro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            cargarDatosTablaSeleccionada(); // Recargar datos después de editar
        }
    }
    
    private void eliminarRegistroSeleccionado() {
        int filaSeleccionada = tablaDatos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro para eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String nombreTablaSeleccionada = (String) comboTablas.getSelectedItem();
        String idRegistro = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                gestionInventarioDAO.eliminarInventarioPorId(nombreTablaSeleccionada, idRegistro);
                modeloTabla.removeRow(filaSeleccionada); // Eliminar de la tabla
                JOptionPane.showMessageDialog(this, "Registro eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el registro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
