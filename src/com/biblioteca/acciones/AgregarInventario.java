package com.biblioteca.acciones;

import javax.swing.JPanel;
import javax.swing.JLabel;

import com.biblioteca.dao.GestionInventarioDAO;
import com.biblioteca.utilidades.DateLabelFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class AgregarInventario extends JPanel {
   

    private GestionInventarioDAO gestionInventarioDAO;
    private JComboBox<String> comboFormularios;
    private JPanel panelCampos;
    private ArrayList<Component> listaCampos;
    private ArrayList<String> listaNombresColumnas;
    private String nombreFormularioSeleccionado;

     public AgregarInventario() {
        add(new JLabel("Agregar Inventario"));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionInventarioDAO = new GestionInventarioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Seleccione el Formulario de Inventario:"));
        comboFormularios = new JComboBox<>(cargarTiposDocumentos());
        comboFormularios.addActionListener(e -> cargarCampos());
        panelSuperior.add(comboFormularios);
        add(panelSuperior, BorderLayout.NORTH);

        panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelCampos);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarInventario());
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = gestionInventarioDAO.obtenerTiposDocumentos();
            return tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void cargarCampos() {
        panelCampos.removeAll();
        listaCampos = new ArrayList<>();
        listaNombresColumnas = new ArrayList<>();
        nombreFormularioSeleccionado = (String) comboFormularios.getSelectedItem();

        try {
            ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreFormularioSeleccionado);
            String nuevoId = gestionInventarioDAO.generarNuevoId(nombreFormularioSeleccionado);

            for (String columna : columnas) {
                JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                panelCampo.add(new JLabel(columna + ":"));

                if (columna.equalsIgnoreCase("id")) {
                    JTextField txtId = new JTextField(20);
                    txtId.setText(nuevoId);
                    txtId.setEditable(false);
                    listaCampos.add(txtId);
                    panelCampo.add(txtId);
                } else if (columna.toLowerCase().contains("fecha")) {
                    UtilDateModel model = new UtilDateModel();
                    Properties p = new Properties();
                    p.put("text.today", "Hoy");
                    JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
                    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                    listaCampos.add(datePicker);
                    panelCampo.add(datePicker);
                } else {
                    JTextField txtCampo = new JTextField(20);
                    listaCampos.add(txtCampo);
                    panelCampo.add(txtCampo);
                }

                listaNombresColumnas.add(columna);
                panelCampos.add(panelCampo);
            }
            panelCampos.revalidate();
            panelCampos.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los campos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarInventario() {
        ArrayList<String> valores = new ArrayList<>();
        for (Component campo : listaCampos) {
            if (campo instanceof JTextField) {
                valores.add(((JTextField) campo).getText().trim());
            } else if (campo instanceof JDatePickerImpl) {
                Object selectedDate = ((JDatePickerImpl) campo).getModel().getValue();
                if (selectedDate != null) {
                    Date date = (Date) selectedDate;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    valores.add(dateFormat.format(date));
                } else {
                    valores.add(null);
                }
            }
        }
        try {
            gestionInventarioDAO.insertarDatosEnTabla(nombreFormularioSeleccionado, listaNombresColumnas, valores);
            JOptionPane.showMessageDialog(this, "Inventario agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarCampos(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
