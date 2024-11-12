package com.biblioteca.acciones.GestionProductos;

import com.biblioteca.dao.GestionFormularioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class AgregarFormulario extends JPanel {

    private GestionFormularioDAO tipoDocumentoDAO;
    private JTextField txtNombreFormulario;
    private JTextField txtNumeroColumnas;
    private JTable tableColumnas;
    private DefaultTableModel tableModel;

    // Constructor: Inicializa los componentes de la interfaz y establece la conexión con la base de datos
    public AgregarFormulario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            tipoDocumentoDAO = new GestionFormularioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Panel superior: Campos de entrada para el nombre del formulario y número de campos
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblNombreFormulario = new JLabel("Nombre del Formulario:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelSuperior.add(lblNombreFormulario, gbc);

        txtNombreFormulario = new JTextField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelSuperior.add(txtNombreFormulario, gbc);

        JLabel lblNumeroColumnas = new JLabel("Número de Campos:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelSuperior.add(lblNumeroColumnas, gbc);

        txtNumeroColumnas = new JTextField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelSuperior.add(txtNumeroColumnas, gbc);

        JButton btnGenerarCampos = new JButton("Generar Campos");
        btnGenerarCampos.addActionListener(e -> generarCampos());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelSuperior.add(btnGenerarCampos, gbc);

        add(panelSuperior, BorderLayout.NORTH);

        // Configuración de tabla: Muestra las columnas "Número" y "Nombre del Campo" (editable)
        String[] columnNames = {"Número", "Nombre del Campo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Solo la columna "Nombre del Campo" es editable
            }
        };
        tableColumnas = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableColumnas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        tableColumnas.getColumnModel().getColumn(0).setMaxWidth(100);
        tableColumnas.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(tableColumnas);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones con opciones de "Guardar" y "Cancelar"
        JButton btnGuardar = new JButton("Guardar Formulario");
        btnGuardar.addActionListener(e -> guardarFormulario());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> limpiarCampos());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    // Método para generar los campos según el número ingresado por el usuario
    private void generarCampos() {
        tableModel.setRowCount(0);

        String nombreFormulario = txtNombreFormulario.getText().trim();
        String numColumnasStr = txtNumeroColumnas.getText().trim();

        if (nombreFormulario.isEmpty() || numColumnasStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre y el número de campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (tipoDocumentoDAO.existeTabla(nombreFormulario.toLowerCase())) {
                int response = JOptionPane.showOptionDialog(this,
                        "La tabla ya existe. ¿Qué desea hacer?",
                        "Tabla Existente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Usar Existente", "Eliminar y Crear Nueva"},
                        "Usar Existente");

                if (response == JOptionPane.NO_OPTION) {
                    tipoDocumentoDAO.eliminarTabla(nombreFormulario.toLowerCase());
                    JOptionPane.showMessageDialog(this, "Tabla eliminada y lista para crear nueva.", "Información", JOptionPane.INFORMATION_MESSAGE);
                } else if (response == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "Redirigiendo a la sección de edición de formularios...", "Información", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    return;
                } else {
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar o manejar la tabla existente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int numColumnas;
        try {
            numColumnas = Integer.parseInt(numColumnasStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de campos inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < numColumnas; i++) {
            tableModel.addRow(new Object[]{"Columna " + (i + 1), ""});
        }
    }

   // Método para guardar el formulario y sus campos en la base de datos
private void guardarFormulario() {
    String nombreFormulario = txtNombreFormulario.getText().trim().toUpperCase();
    if (nombreFormulario.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del formulario.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (tableModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Por favor, genere los campos primero.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    ArrayList<String> nombresColumnas = new ArrayList<>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        String nombreColumna = tableModel.getValueAt(i, 1).toString().trim().toUpperCase();
        if (nombreColumna.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los nombres de los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        nombresColumnas.add(nombreColumna);
    }

    try {
        // Generar nombre de la tabla y crearla en la base de datos
        String nombreTabla = nombreFormulario.toLowerCase().replace(" ", "_") + "_tabla"; // Generar nombre_tabla
        tipoDocumentoDAO.crearTablaParaDocumento(nombreTabla, nombresColumnas);

        // Generar el ID para el nuevo tipo de documento
        String generatedId = tipoDocumentoDAO.generarNuevoId("tiposdocumentos");

        // Insertar el tipo de documento en `tiposdocumentos` con el nombre de la tabla
        tipoDocumentoDAO.insertarTipoDocumento(generatedId, nombreFormulario, nombreTabla);

        JOptionPane.showMessageDialog(this, "Formulario guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        limpiarCampos();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    // Limpia los campos de entrada y las filas de la tabla
    private void limpiarCampos() {
        txtNombreFormulario.setText("");
        txtNumeroColumnas.setText("");
        tableModel.setRowCount(0);
    }
}
