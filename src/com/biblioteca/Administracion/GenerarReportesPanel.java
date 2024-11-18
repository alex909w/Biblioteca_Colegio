package com.biblioteca.Administracion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import com.biblioteca.dao.GestionInventarioDAO;

public class GenerarReportesPanel extends JPanel {
    private GestionInventarioDAO gestionInventarioDAO;
    private JComboBox<String> comboTablas;
    private JTextArea areaReporte;
    private JButton btnGenerarReporte;

    public GenerarReportesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionInventarioDAO = new GestionInventarioDAO();
            inicializarComponentes();
            cargarTablasEnComboBox();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inicializarComponentes() {
        // Panel superior para selección de tabla
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSeleccionarTabla = new JLabel("Seleccione una Tabla:");
        comboTablas = new JComboBox<>();

        panelSuperior.add(lblSeleccionarTabla);
        panelSuperior.add(comboTablas);
        add(panelSuperior, BorderLayout.NORTH);

        // Área de texto para mostrar el reporte
        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaReporte);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Botón para generar el reporte
        btnGenerarReporte = new JButton("Generar Reporte");
        btnGenerarReporte.addActionListener(e -> generarReporte());
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnGenerarReporte);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private void cargarTablasEnComboBox() {
        try {
            ArrayList<String> tablas = gestionInventarioDAO.obtenerTiposDocumentos();
            for (String tabla : tablas) {
                comboTablas.addItem(tabla);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las tablas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReporte() {
        String nombreTablaSeleccionada = (String) comboTablas.getSelectedItem();
        if (nombreTablaSeleccionada == null || nombreTablaSeleccionada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una tabla.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreTablaSeleccionada);
            ArrayList<ArrayList<String>> datos = gestionInventarioDAO.obtenerDatosDeTabla(nombreTablaSeleccionada);

            // Generar el reporte
            StringBuilder reporte = new StringBuilder();
            reporte.append("Reporte de la tabla: ").append(nombreTablaSeleccionada).append("\n\n");

            // Agregar encabezado de columnas
            for (String columna : columnas) {
                reporte.append(String.format("%-20s", columna));
            }
            reporte.append("\n");
            reporte.append(new String(new char[columnas.size() * 20]).replace("\0", "=")).append("\n");

            // Agregar datos de cada fila
            for (ArrayList<String> fila : datos) {
                for (String valor : fila) {
                    reporte.append(String.format("%-20s", valor));
                }
                reporte.append("\n");
            }

            areaReporte.setText(reporte.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
