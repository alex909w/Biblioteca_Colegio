package com.biblioteca.acciones;

import com.biblioteca.dao.GestionProductoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class AgregarFormulario extends JPanel {

    private GestionProductoDAO tipoDocumentoDAO;
    private JTextField txtNombreFormulario;
    private JTextField txtNumeroColumnas;
    private JPanel panelColumnas;
    private ArrayList<JTextField> listaNombresColumnas;

    public AgregarFormulario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            tipoDocumentoDAO = new GestionProductoDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Panel superior para ingresar el nombre y número de campos
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

        txtNumeroColumnas = new JTextField(5);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelSuperior.add(txtNumeroColumnas, gbc);

        // Botón "Generar Campos"
        JButton btnGenerarCampos = new JButton("Generar Campos");
        btnGenerarCampos.addActionListener(e -> generarCampos());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelSuperior.add(btnGenerarCampos, gbc);

        add(panelSuperior, BorderLayout.NORTH);

        panelColumnas = new JPanel();
        panelColumnas.setLayout(new BoxLayout(panelColumnas, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelColumnas);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar Formulario");
        btnGuardar.addActionListener(e -> guardarFormulario());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.add(btnGuardar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void generarCampos() {
        panelColumnas.removeAll();
        listaNombresColumnas = new ArrayList<>();

        String nombreFormulario = txtNombreFormulario.getText().trim();
        if (nombreFormulario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del formulario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String numColumnasStr = txtNumeroColumnas.getText().trim();
        if (numColumnasStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el número de campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int numColumnas;
        try {
            numColumnas = Integer.parseInt(numColumnasStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de campos inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String nombreFormularioLower = nombreFormulario.toLowerCase();

            if (tipoDocumentoDAO.existeTabla(nombreFormularioLower)) {
                int opcion = JOptionPane.showOptionDialog(this,
                        "El formulario ya existe. ¿Qué deseas hacer?",
                        "Formulario Existente",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Usar Existente", "Eliminar y Crear Nuevo", "Cancelar"},
                        "Cancelar");

                switch (opcion) {
                    case 0: // Usar Existente
                        JOptionPane.showMessageDialog(this, "Usando el formulario existente.");
                        return;
                    case 1: // Eliminar y Crear Nuevo
                        tipoDocumentoDAO.eliminarTabla(nombreFormularioLower);
                        JOptionPane.showMessageDialog(this, "Formulario eliminado. Puedes crear un nuevo formulario con este nombre.");
                        break;
                    case 2: // Cancelar
                    default:
                        return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar la existencia del formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generar los campos para ingresar los nombres de las columnas
        for (int i = 0; i < numColumnas; i++) {
            JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panelCampo.add(new JLabel("Nombre del Campo " + (i + 1) + ":"));
            JTextField txtNombreCampo = new JTextField(20);
            listaNombresColumnas.add(txtNombreCampo);
            panelCampo.add(txtNombreCampo);
            panelColumnas.add(panelCampo);
        }

        panelColumnas.revalidate();
        panelColumnas.repaint();
    }

    private void guardarFormulario() {
        String nombreFormulario = txtNombreFormulario.getText().trim();
        if (nombreFormulario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del formulario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (listaNombresColumnas == null || listaNombresColumnas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, genere los campos primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> nombresColumnas = new ArrayList<>();
        for (JTextField txtCampo : listaNombresColumnas) {
            String nombreColumna = txtCampo.getText().trim();
            if (nombreColumna.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los nombres de los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            nombresColumnas.add(nombreColumna);
        }

        try {
            tipoDocumentoDAO.crearTablaParaDocumento(nombreFormulario, nombresColumnas);
            tipoDocumentoDAO.agregarTipoDocumento(nombreFormulario);
            JOptionPane.showMessageDialog(this, "Formulario creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
