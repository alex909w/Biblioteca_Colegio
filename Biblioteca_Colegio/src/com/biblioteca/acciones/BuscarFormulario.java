package com.biblioteca.acciones;

import com.biblioteca.dao.GestionProductoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuscarFormulario extends JPanel {

    private GestionProductoDAO tipoDocumentoDAO;
    private JTable tableDatos;

    public BuscarFormulario() {
        setLayout(new BorderLayout(10, 10));

        try {
            tipoDocumentoDAO = new GestionProductoDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JLabel lblTitulo = new JLabel("Lista de Formularios Disponibles");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo, BorderLayout.NORTH);

        tableDatos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableDatos);
        add(scrollPane, BorderLayout.CENTER);

        // Mostrar los datos al cargar el panel
        mostrarDatos();
    }

    private void mostrarDatos() {
        try {
            // Obtener columnas y datos de tiposdocumentos
            ArrayList<String> columnasTiposDocumentos = tipoDocumentoDAO.obtenerColumnasTiposDocumentos();
            ArrayList<ArrayList<String>> datosTiposDocumentos = tipoDocumentoDAO.obtenerDatosTiposDocumentos();

            if (datosTiposDocumentos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para mostrar.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Mostrar los datos en la tabla
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columnasTiposDocumentos.toArray());

            for (ArrayList<String> fila : datosTiposDocumentos) {
                model.addRow(fila.toArray());
            }

            tableDatos.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
