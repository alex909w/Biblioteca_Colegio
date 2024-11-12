package com.biblioteca.acciones.GestionProductos;

import com.biblioteca.dao.GestionFormularioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditarFormulario extends JPanel {

    private GestionFormularioDAO tipoDocumentoDAO;
    private JComboBox<String> comboFormularios;
    private JTextField txtFiltro;
    private JTable tableColumnas;
    private DefaultTableModel tableModel;
    private ArrayList<String> columnasOriginales;
    private List<String> todosLosFormularios;

    private final ArrayList<String> columnasConstantes = new ArrayList<>(Arrays.asList(
        "Disponibles", "Estado", "Palabra clave", "Ubicación Física"
    ));

    public EditarFormulario() {
    setLayout(new BorderLayout(10, 10));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    try {
        tipoDocumentoDAO = new GestionFormularioDAO();
        todosLosFormularios = tipoDocumentoDAO.obtenerTiposDocumentos();
        
        // Eliminar la extensión "_tabla" para mostrar solo el nombre
        todosLosFormularios = todosLosFormularios.stream()
            .map(nombre -> nombre.replace("_tabla", "")) // Eliminar la extensión "_tabla"
            .collect(Collectors.toList());
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    panelSuperior.add(new JLabel("Seleccione el Formulario a Editar:"));

    txtFiltro = new JTextField(20);
    txtFiltro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarComboBox(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarComboBox(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarComboBox(); }
    });
    panelSuperior.add(txtFiltro);

    comboFormularios = new JComboBox<>(todosLosFormularios.toArray(new String[0]));
    comboFormularios.addActionListener(e -> cargarCampos());
    panelSuperior.add(comboFormularios);

    add(panelSuperior, BorderLayout.NORTH);

    // Configuración de la tabla
    String[] columnNames = {"Número", "Nombre del Campo"};
    tableModel = new DefaultTableModel(columnNames, 0) {
        public boolean isCellEditable(int row, int column) {
            String fieldName = getValueAt(row, column).toString();
            return column == 1 && !columnasConstantes.contains(fieldName); // Solo columnas no constantes son editables
        }
    };
    tableColumnas = new JTable(tableModel);

    // Renderizar el número de columna centrado
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    tableColumnas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

    tableColumnas.getColumnModel().getColumn(0).setMaxWidth(100);
    tableColumnas.setRowHeight(30);

    JScrollPane scrollPane = new JScrollPane(tableColumnas);
    add(scrollPane, BorderLayout.CENTER);

    // Panel de botones para Guardar, Cancelar y Eliminar
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    JButton btnGuardar = new JButton("Guardar Cambios");
    btnGuardar.addActionListener(e -> guardarCambios());
    panelBotones.add(btnGuardar);

    JButton btnCancelar = new JButton("Cancelar");
    btnCancelar.addActionListener(e -> cancelarEdicion());
    panelBotones.add(btnCancelar);

    JButton btnEliminar = new JButton("Eliminar Formulario");
    btnEliminar.addActionListener(e -> eliminarFormulario());
    panelBotones.add(btnEliminar);

    add(panelBotones, BorderLayout.SOUTH);
}

    private void guardarCambios() {
    String nombreFormulario = ((String) comboFormularios.getSelectedItem()).toUpperCase();
    if (nombreFormulario == null || nombreFormulario.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un formulario.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (tableModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay campos para guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    ArrayList<String> nuevasColumnas = new ArrayList<>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        String nombreColumna = tableModel.getValueAt(i, 1).toString().trim().toUpperCase();
        if (nombreColumna.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los nombres de los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!columnasConstantes.contains(nombreColumna)) {
            nuevasColumnas.add(nombreColumna);
        }
    }

    // Añadir la extensión "_tabla" al nombre de la tabla
    String nombreTablaConExtension = nombreFormulario + "_tabla";

    try {
        tipoDocumentoDAO.actualizarColumnasTabla(nombreTablaConExtension, nuevasColumnas);
        JOptionPane.showMessageDialog(this, "Formulario actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormulario();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al actualizar el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void eliminarFormulario() {
    String nombreFormulario = (String) comboFormularios.getSelectedItem();
    if (nombreFormulario == null || nombreFormulario.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un formulario.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea eliminar el formulario " + nombreFormulario + " y todos sus datos?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

    if (confirmacion == JOptionPane.YES_OPTION) {
        // Añadir la extensión "_tabla" al nombre de la tabla
        String nombreTablaConExtension = nombreFormulario + "_tabla";

        try {
            tipoDocumentoDAO.eliminarTabla(nombreTablaConExtension);
            JOptionPane.showMessageDialog(this, "Formulario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            comboFormularios.setModel(new DefaultComboBoxModel<>(cargarTiposDocumentos().toArray(new String[0])));
            limpiarFormulario();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


    private void filtrarComboBox() {
        String filtro = txtFiltro.getText().toLowerCase();
        new SwingWorker<List<String>, Void>() {
            protected List<String> doInBackground() {
                return todosLosFormularios.stream()
                        .filter(nombre -> nombre.toLowerCase().contains(filtro))
                        .collect(Collectors.toList());
            }
            protected void done() {
                try {
                    List<String> resultadosFiltrados = get();
                    comboFormularios.setModel(new DefaultComboBoxModel<>(resultadosFiltrados.toArray(new String[0])));
                    comboFormularios.showPopup();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

   private void cargarCampos() {
    tableModel.setRowCount(0);
    String nombreFormulario = (String) comboFormularios.getSelectedItem();
    if (nombreFormulario == null || nombreFormulario.isEmpty()) {
        return;
    }
    txtFiltro.setText("");  // Limpia el campo de filtro

    // Añadir la extensión "_tabla" al nombre de la tabla
    String nombreTablaConExtension = nombreFormulario + "_tabla";

    try {
        columnasOriginales = tipoDocumentoDAO.obtenerColumnasDeTabla(nombreTablaConExtension);

        // Filtrar y mostrar solo columnas no constantes
        columnasOriginales.remove("id");
        columnasOriginales.removeAll(columnasConstantes);

        for (int i = 0; i < columnasOriginales.size(); i++) {
            tableModel.addRow(new Object[]{"Columna " + (i + 1), columnasOriginales.get(i)});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar los campos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void cancelarEdicion() {
        limpiarFormulario();
        JOptionPane.showMessageDialog(this, "Edición cancelada.", "Cancelación", JOptionPane.INFORMATION_MESSAGE);
    }

   

    private void limpiarFormulario() {
        comboFormularios.setSelectedIndex(-1);
        tableModel.setRowCount(0);
    }
    
    private List<String> cargarTiposDocumentos() {
        try {
            return tipoDocumentoDAO.obtenerTiposDocumentos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }
}
