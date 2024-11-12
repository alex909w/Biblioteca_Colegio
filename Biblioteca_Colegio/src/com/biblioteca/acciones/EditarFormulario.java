package com.biblioteca.acciones;

import com.biblioteca.dao.GestionProductoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class EditarFormulario extends JPanel {

    private GestionProductoDAO tipoDocumentoDAO;
    private JComboBox<String> comboFormularios;
    private JPanel panelColumnas;
    private ArrayList<JTextField> listaNombresColumnas;
    private ArrayList<String> columnasOriginales;

    // Lista de columnas constantes que no pueden ser editadas
    private final ArrayList<String> columnasConstantes = new ArrayList<>(Arrays.asList(
        "Disponibles", "Estado", "Palabra clave", "Ubicación Física"
    ));

    public EditarFormulario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            tipoDocumentoDAO = new GestionProductoDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Seleccione el Formulario a Editar:"));
        comboFormularios = new JComboBox<>(cargarTiposDocumentos());
        panelSuperior.add(comboFormularios);

        JButton btnCargarCampos = new JButton("Cargar Campos");
        btnCargarCampos.addActionListener(e -> cargarCampos());
        panelSuperior.add(btnCargarCampos);

        add(panelSuperior, BorderLayout.NORTH);

        panelColumnas = new JPanel();
        panelColumnas.setLayout(new BoxLayout(panelColumnas, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelColumnas);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(e -> guardarCambios());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.add(btnGuardar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = tipoDocumentoDAO.obtenerTiposDocumentos();
            // Aquí puedes filtrar si hay formularios que no deben ser editados
            return tipos.isEmpty() ? new String[0] : tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void cargarCampos() {
        panelColumnas.removeAll();
        listaNombresColumnas = new ArrayList<>();

        String nombreFormulario = (String) comboFormularios.getSelectedItem();
        if (nombreFormulario == null || nombreFormulario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un formulario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            columnasOriginales = tipoDocumentoDAO.obtenerColumnasDeTabla(nombreFormulario);

            // Excluir las columnas "id" y las columnas constantes
            columnasOriginales.remove("id");
            columnasOriginales.removeAll(columnasConstantes);

            for (int i = 0; i < columnasOriginales.size(); i++) {
                JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                panelCampo.add(new JLabel("Nombre del Campo " + (i + 1) + ":"));
                JTextField txtNombreCampo = new JTextField(columnasOriginales.get(i), 20);
                listaNombresColumnas.add(txtNombreCampo);
                panelCampo.add(txtNombreCampo);
                panelColumnas.add(panelCampo);
            }

            panelColumnas.revalidate();
            panelColumnas.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los campos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        String nombreFormulario = (String) comboFormularios.getSelectedItem();
        if (nombreFormulario == null || nombreFormulario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un formulario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (listaNombresColumnas == null || listaNombresColumnas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay campos para guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> nuevasColumnas = new ArrayList<>();
        for (JTextField txtCampo : listaNombresColumnas) {
            String nombreColumna = txtCampo.getText().trim();
            if (nombreColumna.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los nombres de los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            nuevasColumnas.add(nombreColumna);
        }

        // Agregar las columnas constantes nuevamente
        nuevasColumnas.addAll(columnasConstantes);

        try {
            tipoDocumentoDAO.actualizarColumnasTabla(nombreFormulario, nuevasColumnas);
            JOptionPane.showMessageDialog(this, "Formulario actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
