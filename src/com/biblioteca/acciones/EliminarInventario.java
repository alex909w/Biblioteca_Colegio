package com.biblioteca.acciones;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import com.biblioteca.dao.GestionInventarioDAO;

public class EliminarInventario extends JPanel {

    private GestionInventarioDAO gestionInventarioDAO;
    private JComboBox<String> comboTiposDocumentos;
    private JComboBox<String> comboInventarios;
    private JButton btnEliminar;
    private JButton btnCancelar;

    public EliminarInventario() {
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

        JPanel panelCentral = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelCentral.add(new JLabel("Seleccione el Inventario a Eliminar:"));
        comboInventarios = new JComboBox<>();
        panelCentral.add(comboInventarios);
        add(panelCentral, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Cancelar");

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarInventario();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarFormulario();
            }
        });

        panelBotones.add(btnEliminar);
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

    private void eliminarInventario() {
        String tipoSeleccionado = (String) comboTiposDocumentos.getSelectedItem();
        if (tipoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de documento.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String inventarioSeleccionado = (String) comboInventarios.getSelectedItem();
        if (inventarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un inventario para eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idInventario = inventarioSeleccionado.split(" - ")[0];

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar el inventario con ID: " + idInventario + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                gestionInventarioDAO.eliminarInventarioPorId(tipoSeleccionado, idInventario);
                JOptionPane.showMessageDialog(this, "Inventario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarComboInventarios();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el inventario: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cerrarFormulario() {
        // Cerrar el diálogo que contiene este panel
        Window ventana = SwingUtilities.getWindowAncestor(this);
        if (ventana != null) {
            ventana.dispose();
        }
    }
}
