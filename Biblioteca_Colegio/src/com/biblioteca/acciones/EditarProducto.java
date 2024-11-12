// EditarProducto.java
package com.biblioteca.acciones;

import com.biblioteca.dao.GestionProductoDAO;
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

public class EditarProducto extends JFrame {

    private String nombreTabla;
    private ArrayList<String> datosProducto;
    private GestionProductoDAO gestionProductoDAO;
    private ActualizarProducto ventanaPrincipal;

    private JPanel panelCampos;
    private ArrayList<Component> listaCampos;
    private ArrayList<String> listaNombresColumnas;

    private JButton btnGuardar;
    private JButton btnCancelar;

    public EditarProducto(String nombreTabla, ArrayList<String> datosProducto, GestionProductoDAO gestionProductoDAO, ActualizarProducto ventanaPrincipal) {
        this.nombreTabla = nombreTabla;
        this.datosProducto = datosProducto;
        this.gestionProductoDAO = gestionProductoDAO;
        this.ventanaPrincipal = ventanaPrincipal;

        setTitle("Editar Producto - ID: " + datosProducto.get(0));
        // Eliminar tamaño fijo y usar pack()
        // setSize(500, 600); // Eliminado
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel central para campos
        panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        JScrollPane scrollPaneCampos = new JScrollPane(panelCampos);
        scrollPaneCampos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneCampos, BorderLayout.CENTER);

        // Panel inferior para botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(e -> guardarCambios());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);

        // Cargar los campos con los datos del producto
        cargarCamposConDatos();

        // Ajustar el tamaño de la ventana al contenido
        pack();
        setLocationRelativeTo(null); // Recentrar la ventana después de pack()
    }

    private void cargarCamposConDatos() {
        listaCampos = new ArrayList<>();
        listaNombresColumnas = new ArrayList<>();

        try {
            ArrayList<String> columnas = gestionProductoDAO.obtenerColumnasDeTabla(nombreTabla);

            for (int i = 0; i < columnas.size(); i++) {
                String columna = columnas.get(i);
                String valor = datosProducto.get(i);

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
                    p.put("text.month", "Mes");
                    p.put("text.year", "Año");

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
            JOptionPane.showMessageDialog(this, "Error al cargar los campos del producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        ArrayList<String> valores = new ArrayList<>();

        for (int i = 0; i < listaCampos.size(); i++) {
            Component campo = listaCampos.get(i);
            String valor = "";

            if (campo instanceof JTextField) {
                valor = ((JTextField) campo).getText().trim();
            } else if (campo instanceof JDatePickerImpl) {
                Object selectedDate = ((JDatePickerImpl) campo).getModel().getValue();
                if (selectedDate != null) {
                    Date date = (Date) selectedDate;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    valor = dateFormat.format(date);
                } else {
                    valor = null;
                }
            }

            valores.add(valor);
        }

        try {
            // Obtener el ID del producto (asumiendo que es la primera columna)
            String id = datosProducto.get(0);

            gestionProductoDAO.actualizarDatosEnTabla(nombreTabla, listaNombresColumnas, valores, id);
            JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            ventanaPrincipal.refrescarTabla();
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
