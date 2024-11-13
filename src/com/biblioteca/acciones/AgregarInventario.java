package com.biblioteca.acciones;

import javax.swing.JPanel;
import javax.swing.JLabel;
import com.biblioteca.dao.GestionInventarioDAO;
import com.biblioteca.utilidades.DateLabelFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class AgregarInventario extends JPanel {

    private GestionInventarioDAO gestionInventarioDAO;
    private JComboBox<String> comboFormularios;
    private JTextField txtFiltro;
    private JPanel panelCampos;
    private ArrayList<Component> listaCampos;
    private ArrayList<String> listaNombresColumnas;
    private String nombreFormularioSeleccionado;
    private List<String> todosLosFormularios;

    public AgregarInventario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionInventarioDAO = new GestionInventarioDAO();
            todosLosFormularios = gestionInventarioDAO.obtenerTiposDocumentos();

            // Limpiar la lista para mostrar solo los nombres de los formularios sin la extensión "_tabla"
            todosLosFormularios = todosLosFormularios.stream()
                .map(nombre -> {
                    // Remover la extensión "_tabla" si existe
                    if (nombre.toLowerCase().endsWith("_tabla")) {
                        return nombre.substring(0, nombre.length() - 6);
                    } else {
                        return nombre;
                    }
                })
                .collect(Collectors.toList());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Configuración del panel superior
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Seleccione el Formulario de Inventario:"));

        txtFiltro = new JTextField(15);
        txtFiltro.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarComboBox(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarComboBox(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarComboBox(); }
        });

        panelSuperior.add(txtFiltro);

        comboFormularios = new JComboBox<>(todosLosFormularios.toArray(new String[0]));
        comboFormularios.setPreferredSize(new Dimension(200, 25));
        comboFormularios.addActionListener(e -> cargarCampos());
        panelSuperior.add(comboFormularios);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel para los campos y botones
        panelCampos = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(panelCampos);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarInventario());
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void filtrarComboBox() {
        String filtro = txtFiltro.getText().toLowerCase();
        List<String> resultadosFiltrados = todosLosFormularios.stream()
                .filter(nombre -> nombre.toLowerCase().contains(filtro))
                .collect(Collectors.toList());

        comboFormularios.setModel(new DefaultComboBoxModel<>(resultadosFiltrados.toArray(new String[0])));
    }

    private void cargarCampos() {
        panelCampos.removeAll();
        listaCampos = new ArrayList<>();
        listaNombresColumnas = new ArrayList<>();
        nombreFormularioSeleccionado = (String) comboFormularios.getSelectedItem();

        if (nombreFormularioSeleccionado == null || nombreFormularioSeleccionado.isEmpty()) {
            panelCampos.revalidate();
            panelCampos.repaint();
            return;
        }

        // Añadir "_tabla" al nombre de la tabla seleccionada
        String nombreTablaConExtension = nombreFormularioSeleccionado + "_tabla";

        try {
            ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreTablaConExtension);
            String nuevoId = gestionInventarioDAO.generarNuevoIdConPrefijo(nombreTablaConExtension);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridwidth = 1;

            // Campo de ID automático en la parte superior izquierda
            JLabel lblId = new JLabel("ID:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            panelCampos.add(lblId, gbc);

            JTextField txtId = new JTextField(20);
            txtId.setText(nuevoId);
            txtId.setEditable(false);  // Solo lectura
            gbc.gridx = 1;
            panelCampos.add(txtId, gbc);

            // Añadir txtId a listaCampos
            listaCampos.add(txtId);
            // **No** añadir 'id' a listaNombresColumnas aquí

            // Coloca los demás campos en una cuadrícula de 2 columnas
            int fila = 1;
            int columna = 0;

            for (String columnaNombre : columnas) {
                if (columnaNombre.equalsIgnoreCase("id")) continue;  // Ignora el campo ID manual

                JLabel label = new JLabel(columnaNombre + ":");
                gbc.gridx = columna * 2;
                gbc.gridy = fila;
                panelCampos.add(label, gbc);

                Component campo;
                if (columnaNombre.toLowerCase().contains("fecha")) {
                    UtilDateModel model = new UtilDateModel();
                    Properties p = new Properties();
                    p.put("text.today", "Hoy");
                    JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
                    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                    campo = datePicker;
                } else {
                    JTextField txtCampo = new JTextField(20);
                    campo = txtCampo;
                }

                gbc.gridx = columna * 2 + 1;
                panelCampos.add(campo, gbc);

                listaCampos.add(campo);
                listaNombresColumnas.add(columnaNombre);

                // Alterna la columna; si ya se han colocado dos columnas, pasa a la siguiente fila
                if (columna == 1) {
                    fila++;
                    columna = 0;
                } else {
                    columna = 1;
                }
            }

            panelCampos.revalidate();
            panelCampos.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los campos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarInventario() {
        if (nombreFormularioSeleccionado == null || nombreFormularioSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un formulario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tableModelVacio()) {
            JOptionPane.showMessageDialog(this, "No hay campos para guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String> valores = new ArrayList<>();

        // Obtener el ID generado automáticamente y añadirlo a los valores
        JTextField txtId = (JTextField) listaCampos.get(0);  // El ID es el primer campo
        String id = txtId.getText().trim();
        System.out.println("ID a guardar: " + id); // Depuración
        valores.add(id);

        // Recorremos el resto de los campos para agregar sus valores
        for (int i = 1; i < listaCampos.size(); i++) {  // Empezar desde el segundo elemento
            Component campo = listaCampos.get(i);
            if (campo instanceof JTextField) {
                String valor = ((JTextField) campo).getText().trim();
                System.out.println("Campo " + listaNombresColumnas.get(i - 1) + ": " + valor); // Depuración
                valores.add(valor);
            } else if (campo instanceof JDatePickerImpl) {
                Object selectedDate = ((JDatePickerImpl) campo).getModel().getValue();
                if (selectedDate != null) {
                    Date date = (Date) selectedDate;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String fecha = dateFormat.format(date);
                    System.out.println("Fecha " + listaNombresColumnas.get(i - 1) + ": " + fecha); // Depuración
                    valores.add(fecha);
                } else {
                    valores.add(null);
                }
            }
        }

        // Añadir 'id' a la lista de columnas
        ArrayList<String> columnasConId = new ArrayList<>(listaNombresColumnas);
        columnasConId.add(0, "id"); // Asegúrate de que el 'id' esté en la primera posición

        // Añadir la extensión "_tabla" al nombre de la tabla
        String nombreTablaConExtension = nombreFormularioSeleccionado + "_tabla";

        try {
            gestionInventarioDAO.insertarDatosEnTabla(nombreTablaConExtension, columnasConId, valores);
            JOptionPane.showMessageDialog(this, "Inventario agregado exitosamente en " + nombreFormularioSeleccionado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarCampos(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Método auxiliar para verificar si la tabla está vacía
    private boolean tableModelVacio() {
        return listaNombresColumnas == null || listaNombresColumnas.isEmpty();
    }

}
