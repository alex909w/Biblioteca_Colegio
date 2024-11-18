package com.biblioteca.acciones;

import com.biblioteca.acciones.EditarInventario;
import javax.swing.JPanel;
import javax.swing.JLabel;
import com.biblioteca.dao.GestionInventarioDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ActualizarInventario extends JPanel {
    private GestionInventarioDAO gestionInventarioDAO;
    private JTable tablaInventarios;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboFormularios;
    private JTextField txtBuscar;
    private String nombreTablaSeleccionada;
    private JButton btnEditar;
    private ArrayList<ArrayList<String>> datosOriginales;

    public ActualizarInventario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Título del panel
        add(new JLabel("Actualizar Entrada/Salida de Inventario", SwingConstants.CENTER), BorderLayout.NORTH);

        // Inicializar DAO
        try {
            gestionInventarioDAO = new GestionInventarioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Panel Superior: ComboBox y Búsqueda
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Seleccione el Formulario de Inventario:"));

        // Cargar las tablas en el combo box, con la opción "Todas las Tablas"
        comboFormularios = new JComboBox<>(cargarTiposDocumentos());
        comboFormularios.addActionListener(e -> cargarTabla());
        panelSuperior.add(comboFormularios);

        // Campo de búsqueda
        panelSuperior.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(15);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarTabla(); }
            public void removeUpdate(DocumentEvent e) { filtrarTabla(); }
            public void changedUpdate(DocumentEvent e) { filtrarTabla(); }
        });
        panelSuperior.add(txtBuscar);

        add(panelSuperior, BorderLayout.NORTH);

        // Modelo y Tabla
        modeloTabla = new DefaultTableModel();
        tablaInventarios = new JTable(modeloTabla);
        tablaInventarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaInventarios.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Listener para habilitar/deshabilitar el botón Editar
        tablaInventarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                btnEditar.setEnabled(tablaInventarios.getSelectedRow() != -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaInventarios);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de Botones: Editar y Eliminar
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

    /**
     * Carga los tipos de documentos desde la base de datos y los devuelve como un arreglo de Strings.
     * Incluye la opción "Todas las Tablas" al inicio.
     */
    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = gestionInventarioDAO.obtenerTiposDocumentos();
            tipos.add(0, "Todas las Tablas"); // Agregar opción para ver todas las tablas
            return tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    /**
     * Carga los datos en la tabla según la selección del ComboBox.
     * Si se selecciona "Todas las Tablas", combina los datos de todas las tablas.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        datosOriginales = new ArrayList<>();

        String seleccion = (String) comboFormularios.getSelectedItem();
        if (seleccion == null) return;

        try {
            if ("Todas las Tablas".equals(seleccion)) {
                // Obtener todas las columnas únicas de todas las tablas
                ArrayList<String> todasColumnas = new ArrayList<>();
                ArrayList<ArrayList<String>> datosTotales = new ArrayList<>();
                ArrayList<String> tipos = gestionInventarioDAO.obtenerTiposDocumentos();

                for (String tipo : tipos) {
                    String nombreTabla = gestionInventarioDAO.obtenerNombreTablaPorTipo(tipo);
                    if (nombreTabla != null) {
                        ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreTabla);
                        // Agregar columnas si no existen
                        for (String columna : columnas) {
                            if (!todasColumnas.contains(columna)) {
                                todasColumnas.add(columna);
                                modeloTabla.addColumn(columna);
                            }
                        }

                        ArrayList<ArrayList<String>> datos = gestionInventarioDAO.obtenerDatosDeTabla(nombreTabla);
                        for (ArrayList<String> fila : datos) {
                            ArrayList<String> filaCompleta = new ArrayList<>();
                            // Mapear los valores a las columnas correspondientes
                            for (String columna : todasColumnas) {
                                int index = columnas.indexOf(columna);
                                if (index != -1 && index < fila.size()) {
                                    filaCompleta.add(fila.get(index));
                                } else {
                                    filaCompleta.add(""); // Vacío si la columna no existe en esta tabla
                                }
                            }
                            datosTotales.add(filaCompleta);
                        }
                    }
                }

                datosOriginales = datosTotales;
                for (ArrayList<String> fila : datosTotales) {
                    modeloTabla.addRow(fila.toArray());
                }
            } else {
                // Cargar datos de una sola tabla
                nombreTablaSeleccionada = gestionInventarioDAO.obtenerNombreTablaPorTipo(seleccion);
                if (nombreTablaSeleccionada == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró la tabla para el tipo de documento seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreTablaSeleccionada);
                for (String columna : columnas) {
                    modeloTabla.addColumn(columna);
                }

                ArrayList<ArrayList<String>> datos = gestionInventarioDAO.obtenerDatosDeTabla(nombreTablaSeleccionada);
                datosOriginales = datos;
                for (ArrayList<String> fila : datos) {
                    modeloTabla.addRow(fila.toArray());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filtra la tabla según el texto ingresado en el campo de búsqueda.
     */
    private void filtrarTabla() {
        String filtro = txtBuscar.getText().toLowerCase();
        modeloTabla.setRowCount(0); // Limpiar la tabla antes de filtrar

        for (ArrayList<String> fila : datosOriginales) {
            boolean coincide = fila.stream().anyMatch(valor -> valor.toLowerCase().contains(filtro));
            if (coincide) {
                modeloTabla.addRow(fila.toArray());
            }
        }
    }

    /**
     * Edita el inventario seleccionado en la tabla.
     */
    private void editarInventarioSeleccionado() {
        int filaSeleccionada = tablaInventarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un inventario para editar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Asumiendo que la primera columna es el ID del inventario
        String idInventario = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        EditarInventario editarInventarioPanel = new EditarInventario(nombreTablaSeleccionada, idInventario);

        int result = JOptionPane.showConfirmDialog(this, editarInventarioPanel, "Editar Inventario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            cargarTabla();
        }
    }

    /**
     * Elimina el inventario seleccionado en la tabla.
     */
    private void eliminarInventarioSeleccionado() {
        int filaSeleccionada = tablaInventarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un inventario para eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Asumiendo que la primera columna es el ID del inventario
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
