package com.biblioteca.acciones;

import com.biblioteca.dao.GestionInventarioDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class GestionInventario extends JPanel {

    private GestionInventarioDAO gestionInventarioDAO;
    private JComboBox<String> tipoInventarioComboBox;
    private JTextField idGeneradoField;
    private JPanel panelDinamico;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnEntrada, btnSalida;
    private JTextField cantidadField;

    public GestionInventario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionInventarioDAO = new GestionInventarioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al inicializar la conexión a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        String[] tiposInventario = cargarTiposInventario();
        tipoInventarioComboBox = new JComboBox<>(tiposInventario);
        tipoInventarioComboBox.setPreferredSize(new Dimension(250, 40));
        tipoInventarioComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        tipoInventarioComboBox.addActionListener(e -> actualizarFormularioYID());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panelSuperior.add(new JLabel("Tipo de Inventario:"));
        panelSuperior.add(tipoInventarioComboBox);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnAgregar = crearBoton("Agregar", "Agregar un nuevo artículo");
        btnActualizar = crearBoton("Actualizar", "Actualizar los detalles del artículo seleccionado");
        btnEliminar = crearBoton("Eliminar", "Eliminar el artículo seleccionado");
        btnEntrada = crearBoton("Entrada", "Añadir al inventario la cantidad especificada");
        btnSalida = crearBoton("Salida", "Retirar del inventario la cantidad especificada");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnEntrada);
        panelBotones.add(btnSalida);
        add(panelBotones, BorderLayout.SOUTH);

        panelDinamico = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(panelDinamico);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        actualizarFormularioYID();
    }

    private String[] cargarTiposInventario() {
        try {
            ArrayList<String> tipos = gestionInventarioDAO.obtenerTiposDocumentos();
            return tipos.isEmpty() ? new String[0] : tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void actualizarFormularioYID() {
        String tipoInventario = (String) tipoInventarioComboBox.getSelectedItem();
        if (tipoInventario != null && !tipoInventario.isEmpty()) {
            try {
                cargarFormularioParaInventario(tipoInventario);
                String nuevoID = gestionInventarioDAO.generarNuevoIdConPrefijo(tipoInventario);
                idGeneradoField.setText(nuevoID);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar el formulario de inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarFormularioParaInventario(String nombreInventario) {
        panelDinamico.removeAll();
        ArrayList<String> columnas;
        try {
            columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreInventario);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int row = 0;

            gbc.gridx = 0;
            gbc.gridy = row;
            JLabel idLabel = new JLabel("ID Inventario:");
            idLabel.setFont(new Font("Arial", Font.BOLD, 18));
            panelDinamico.add(idLabel, gbc);

            gbc.gridx = 1;
            idGeneradoField = new JTextField(20);
            idGeneradoField.setEditable(false);
            idGeneradoField.setFont(new Font("Arial", Font.PLAIN, 18));
            panelDinamico.add(idGeneradoField, gbc);

            row++;

            for (String columna : columnas) {
                if (columna.equalsIgnoreCase("id") || columna.equalsIgnoreCase("Disponibles")) continue;

                gbc.gridx = 0;
                gbc.gridy = row;
                JLabel label = new JLabel(columna + ":");
                label.setFont(new Font("Arial", Font.BOLD, 18));
                panelDinamico.add(label, gbc);

                gbc.gridx = 1;
                JTextField textField = new JTextField(20);
                textField.setFont(new Font("Arial", Font.PLAIN, 18));
                textField.setName(columna);
                panelDinamico.add(textField, gbc);

                row++;
            }

            gbc.gridx = 0;
            gbc.gridy = row;
            panelDinamico.add(new JLabel("Cantidad:"), gbc);
            gbc.gridx = 1;
            cantidadField = new JTextField(20);
            panelDinamico.add(cantidadField, gbc);

            panelDinamico.revalidate();
            panelDinamico.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el formulario para el inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton crearBoton(String texto, String tooltip) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(150, 40));
        boton.setToolTipText(tooltip);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.addActionListener(e -> {
            switch (texto) {
                case "Agregar":
                    agregarInventario();
                    break;
                case "Actualizar":
                    actualizarInventario();
                    break;
                case "Eliminar":
                    eliminarInventario();
                    break;
                case "Entrada":
                    modificarCantidad(true);
                    break;
                case "Salida":
                    modificarCantidad(false);
                    break;
            }
        });
        return boton;
    }

    private void agregarInventario() {
        try {
            String tipoInventario = (String) tipoInventarioComboBox.getSelectedItem();
            ArrayList<String> columnas = new ArrayList<>();
            ArrayList<String> valores = new ArrayList<>();

            columnas.add("id");
            valores.add(idGeneradoField.getText());

            for (Component comp : panelDinamico.getComponents()) {
                if (comp instanceof JTextField && comp != idGeneradoField) {
                    JTextField field = (JTextField) comp;
                    columnas.add(field.getName());
                    valores.add(field.getText().trim());
                }
            }

            gestionInventarioDAO.insertarDatosEnTabla(tipoInventario, columnas, valores);
            JOptionPane.showMessageDialog(this, "Inventario agregado exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarInventario() {
        try {
            String tipoInventario = (String) tipoInventarioComboBox.getSelectedItem();
            ArrayList<String> columnas = new ArrayList<>();
            ArrayList<String> valores = new ArrayList<>();

            for (Component comp : panelDinamico.getComponents()) {
                if (comp instanceof JTextField && comp != idGeneradoField) {
                    JTextField field = (JTextField) comp;
                    columnas.add(field.getName());
                    valores.add(field.getText().trim());
                }
            }

            gestionInventarioDAO.actualizarDatosEnTabla(tipoInventario, columnas, valores, idGeneradoField.getText());
            JOptionPane.showMessageDialog(this, "Inventario actualizado exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarInventario() {
        try {
            String tipoInventario = (String) tipoInventarioComboBox.getSelectedItem();
            gestionInventarioDAO.eliminarInventarioPorId(tipoInventario, idGeneradoField.getText());
            JOptionPane.showMessageDialog(this, "Inventario eliminado exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarCantidad(boolean isEntrada) {
        try {
            String tipoInventario = (String) tipoInventarioComboBox.getSelectedItem();
            int cantidadActual = Integer.parseInt(cantidadField.getText().trim());
            int cantidadModificacion = isEntrada ? cantidadActual : -cantidadActual;
            gestionInventarioDAO.modificarCantidadInventario(tipoInventario, idGeneradoField.getText(), cantidadModificacion);
            JOptionPane.showMessageDialog(this, (isEntrada ? "Entrada" : "Salida") + " realizada exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al modificar la cantidad de inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
