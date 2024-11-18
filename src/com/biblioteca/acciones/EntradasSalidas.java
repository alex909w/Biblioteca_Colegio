package com.biblioteca.acciones;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import com.biblioteca.dao.GestionInventarioDAO;

public class EntradasSalidas extends JPanel {

    private GestionInventarioDAO gestionInventarioDAO;
    private JComboBox<String> comboTiposDocumentos;
    private JComboBox<String> comboInventarios;
    private JRadioButton radioEntrada;
    private JRadioButton radioSalida;
    private JTextField campoCantidad;
    private JButton btnProcesar;
    private JButton btnCancelar;

    public EntradasSalidas() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionInventarioDAO = new GestionInventarioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Seleccione el Tipo de Documento:"));
        comboTiposDocumentos = new JComboBox<>(cargarTiposDocumentos());
        comboTiposDocumentos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarComboInventarios();
            }
        });
        panelSuperior.add(comboTiposDocumentos);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Combo de Inventarios
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCentral.add(new JLabel("Seleccione el Inventario:"), gbc);

        gbc.gridx = 1;
        comboInventarios = new JComboBox<>();
        panelCentral.add(comboInventarios, gbc);

        // Radio botones para Entrada y Salida
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCentral.add(new JLabel("Tipo de Movimiento:"), gbc);

        gbc.gridx = 1;
        JPanel panelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioEntrada = new JRadioButton("Entrada");
        radioSalida = new JRadioButton("Salida");
        ButtonGroup grupoMovimiento = new ButtonGroup();
        grupoMovimiento.add(radioEntrada);
        grupoMovimiento.add(radioSalida);
        panelRadio.add(radioEntrada);
        panelRadio.add(radioSalida);
        panelCentral.add(panelRadio, gbc);

        // Campo de Cantidad
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCentral.add(new JLabel("Cantidad:"), gbc);

        gbc.gridx = 1;
        campoCantidad = new JTextField(10);
        panelCentral.add(campoCantidad, gbc);

        add(panelCentral, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnProcesar = new JButton("Procesar");
        btnCancelar = new JButton("Cancelar");

        btnProcesar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarMovimiento();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarFormulario();
            }
        });

        panelBotones.add(btnProcesar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // Inicializar el combo de inventarios
        actualizarComboInventarios();
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = gestionInventarioDAO.obtenerTiposDocumentos();
            return tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void actualizarComboInventarios() {
        comboInventarios.removeAllItems();
        String tipoSeleccionado = (String) comboTiposDocumentos.getSelectedItem();
        if (tipoSeleccionado == null) return;

        try {
            ArrayList<ArrayList<String>> datos = gestionInventarioDAO.obtenerDatosDeTabla(tipoSeleccionado);
            for (ArrayList<String> fila : datos) {
                if (fila.isEmpty()) continue;
                // Asume que la primera columna es el ID y la segunda el nombre o descripción
                String id = fila.get(0);
                String descripcion = fila.size() > 1 ? fila.get(1) : id;
                comboInventarios.addItem(id + " - " + descripcion);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los inventarios: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarMovimiento() {
        String tipoSeleccionado = (String) comboTiposDocumentos.getSelectedItem();
        if (tipoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de documento.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String inventarioSeleccionado = (String) comboInventarios.getSelectedItem();
        if (inventarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un inventario.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idInventario = inventarioSeleccionado.split(" - ")[0];

        String cantidadStr = campoCantidad.getText().trim();
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que cero.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!radioEntrada.isSelected() && !radioSalida.isSelected()) {
            JOptionPane.showMessageDialog(this, "Seleccione el tipo de movimiento (Entrada o Salida).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modificacion = radioEntrada.isSelected() ? cantidad : -cantidad;

        try {
            gestionInventarioDAO.modificarCantidadInventario(tipoSeleccionado, idInventario, modificacion);
            String tipoMovimiento = radioEntrada.isSelected() ? "Entrada" : "Salida";
            JOptionPane.showMessageDialog(this, tipoMovimiento + " procesada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar el movimiento: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        campoCantidad.setText("");
        radioEntrada.setSelected(false);
        radioSalida.setSelected(false);
    }

    private void cerrarFormulario() {
        // Cerrar el diálogo que contiene este panel
        Window ventana = SwingUtilities.getWindowAncestor(this);
        if (ventana != null) {
            ventana.dispose();
        }
    }
}
