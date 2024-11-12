package com.biblioteca.acciones;

import javax.swing.JPanel;
import javax.swing.JLabel;

import com.biblioteca.utilidades.DateLabelFormatter;
import com.biblioteca.dao.GestionInventarioDAO;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class EditarInventario extends JPanel {

    private GestionInventarioDAO gestionInventarioDAO;
    private ArrayList<Component> listaCampos;
    private ArrayList<String> listaNombresColumnas;
    private String nombreFormularioSeleccionado;
    private String idInventario;

    // Constructor modificado para recibir los parámetros necesarios
    public EditarInventario(String nombreFormulario, String idInventario) {
        add(new JLabel("Editar Inventario"));
        this.nombreFormularioSeleccionado = nombreFormulario;
        this.idInventario = idInventario;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionInventarioDAO = new GestionInventarioDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cargarCamposConDatos();
    }

    public EditarInventario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void cargarCamposConDatos() {
        listaCampos = new ArrayList<>();
        listaNombresColumnas = new ArrayList<>();

        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelCampos);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(e -> guardarCambios());
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        try {
            ArrayList<String> columnas = gestionInventarioDAO.obtenerColumnasDeTabla(nombreFormularioSeleccionado);
            ArrayList<String> datosInventario = gestionInventarioDAO.obtenerDatosPorId(nombreFormularioSeleccionado, idInventario);

            for (int i = 0; i < columnas.size(); i++) {
                String columna = columnas.get(i);
                String valor = datosInventario.get(i);

                JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                panelCampo.add(new JLabel(columna + ":"));

                if (columna.equalsIgnoreCase("id")) {
                    JTextField txtId = new JTextField(20);
                    txtId.setText(valor);
                    txtId.setEditable(false);
                    listaCampos.add(txtId);
                    panelCampo.add(txtId);
                } else if (columna.toLowerCase().contains("fecha")) {
                    UtilDateModel model = new UtilDateModel();
                    if (valor != null && !valor.isEmpty()) {
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(valor);
                            model.setValue(date);
                        } catch (Exception e) {
                            model.setValue(null);
                        }
                    }
                    Properties p = new Properties();
                    p.put("text.today", "Hoy");
                    JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
                    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                    listaCampos.add(datePicker);
                    panelCampo.add(datePicker);
                } else {
                    JTextField txtCampo = new JTextField(20);
                    txtCampo.setText(valor);
                    listaCampos.add(txtCampo);
                    panelCampo.add(txtCampo);
                }

                listaNombresColumnas.add(columna);
                panelCampos.add(panelCampo);
            }

            panelCampos.revalidate();
            panelCampos.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
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
            gestionInventarioDAO.actualizarDatosEnTabla(nombreFormularioSeleccionado, listaNombresColumnas, valores, idInventario);
            JOptionPane.showMessageDialog(this, "Inventario actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
