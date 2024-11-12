package com.biblioteca.acciones;

import com.biblioteca.dao.GestionProductoDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class AgregarProducto extends JPanel {

    private GestionProductoDAO gestionProductoDAO;
    private final JComboBox<String> comboFormularios;
    private final JPanel panelCampos;
    private ArrayList<Component> listaCampos;
    private ArrayList<String> listaNombresColumnas;
    private String nombreFormularioSeleccionado;

    public AgregarProducto() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionProductoDAO = new GestionProductoDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Panel superior para seleccionar el formulario
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Seleccione el Formulario:"));
        comboFormularios = new JComboBox<>(cargarTiposDocumentos());
        panelSuperior.add(comboFormularios);
        add(panelSuperior, BorderLayout.NORTH);

        // Agregar ActionListener para cargar los campos automáticamente al seleccionar un formulario
        comboFormularios.addActionListener(e -> cargarCampos());

        // Panel central para los campos de entrada
        panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelCampos);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Botones inferiores
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarProducto());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarProducto());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarCampos());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);

        // Cargar los campos del formulario seleccionado al iniciar
        if (comboFormularios.getItemCount() > 0) {
            comboFormularios.setSelectedIndex(0);
            cargarCampos();
        }
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = gestionProductoDAO.obtenerTiposDocumentos();
            return tipos.isEmpty() ? new String[0] : tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void cargarCampos() {
        panelCampos.removeAll();
        listaCampos = new ArrayList<>();
        listaNombresColumnas = new ArrayList<>();
        nombreFormularioSeleccionado = (String) comboFormularios.getSelectedItem();

        if (nombreFormularioSeleccionado == null || nombreFormularioSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un formulario.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ArrayList<String> columnas = gestionProductoDAO.obtenerColumnasDeTabla(nombreFormularioSeleccionado);

            // Generar el ID automáticamente usando gestionProductoDAO
            String nuevoId = gestionProductoDAO.generarNuevoId(nombreFormularioSeleccionado);

            for (String columna : columnas) {
                JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                panelCampo.add(new JLabel(columna + ":"));

                if (columna.equalsIgnoreCase("id")) {
                    JTextField txtId = new JTextField(20);
                    txtId.setText(nuevoId);
                    txtId.setEditable(false); // Deshabilitar edición
                    listaCampos.add(txtId);
                    panelCampo.add(txtId);
                } else if (columna.toLowerCase().contains("fecha")) {
                    UtilDateModel model = new UtilDateModel();
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
                    listaCampos.add(txtCampo);
                    panelCampo.add(txtCampo);
                }

                listaNombresColumnas.add(columna);
                panelCampos.add(panelCampo);
            }

            panelCampos.revalidate();
            panelCampos.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los campos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarProducto() {
        if (listaCampos == null || listaCampos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay campos para guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
            gestionProductoDAO.insertarDatosEnTabla(nombreFormularioSeleccionado, listaNombresColumnas, valores);
            JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarCampos(); // Recargar los campos para actualizar el ID
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        if (listaCampos != null) {
            for (Component campo : listaCampos) {
                if (campo instanceof JTextField) {
                    if (campo.isEnabled()) { // No limpiar el campo ID
                        ((JTextField) campo).setText("");
                    }
                } else if (campo instanceof JDatePickerImpl) {
                    ((JDatePickerImpl) campo).getModel().setValue(null);
                }
            }
        }
    }

    private void actualizarProducto() {
        if (listaCampos == null || listaCampos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay campos para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener el ID del campo correspondiente
        String id = "";
        for (int i = 0; i < listaNombresColumnas.size(); i++) {
            if (listaNombresColumnas.get(i).equalsIgnoreCase("id")) {
                Component campo = listaCampos.get(i);
                if (campo instanceof JTextField) {
                    id = ((JTextField) campo).getText().trim();
                }
                break;
            }
        }

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo ID está vacío. Por favor, ingrese un ID válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar actualización
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea actualizar el producto con ID: " + id + "?", "Confirmar Actualización", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

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
            gestionProductoDAO.actualizarDatosEnTabla(nombreFormularioSeleccionado, listaNombresColumnas, valores, id);
            JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarCampos(); // Recargar los campos para reflejar los cambios
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
