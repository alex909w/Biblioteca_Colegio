package com.biblioteca.acciones;

import com.biblioteca.dao.GestionProductoDAO;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class ActualizarProducto extends JPanel {

    private GestionProductoDAO gestionProductoDAO;
    private JComboBox<String> comboFormularos;
    private JPanel panelCampos;
    private ArrayList<Component> listaCampos;
    private ArrayList<String> listaNombresColumnas;
    private String nombreFormularioSeleccionado;

    private JButton btnActualizar;
    private JButton btnEliminar;
    
    // Nuevo JTable para mostrar todos los productos
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    public ActualizarProducto() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        try {
            gestionProductoDAO = new GestionProductoDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Panel superior para seleccionar el formulario, centrado
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblFormulario = new JLabel("Seleccione el Formulario:");
        panelSuperior.add(lblFormulario, gbc);

        // Inicialización del JComboBox con los tipos de documentos
        comboFormularos = new JComboBox<>(cargarTiposDocumentos());
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelSuperior.add(comboFormularos, gbc);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel central que contiene tanto la tabla como los campos de actualización
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setPreferredSize(new Dimension(800, 600));

       // Parte superior del panel central: JTable (Tabla de productos)
        modeloTabla = new DefaultTableModel();
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPaneTabla = new JScrollPane(tablaProductos);
        panelCentral.add(scrollPaneTabla, BorderLayout.CENTER);

        // Parte inferior del panel central: Campos de actualización
        panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCentral.add(panelCampos, BorderLayout.SOUTH);

        // Añadir el panel central al panel principal
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior para botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> abrirVentanaEditarProducto());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarProductoSeleccionado());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);

        // Añadir listeners
        agregarListeners();

        // Cargar automáticamente la tabla del formulario seleccionado al iniciar
        if (comboFormularos.getItemCount() > 0) {
            comboFormularos.setSelectedIndex(0);
            String seleccionado = (String) comboFormularos.getSelectedItem();
            if (seleccionado != null && !seleccionado.isEmpty()) {
                cargarTabla(seleccionado);
            }
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

    private void agregarListeners() {
        // Listener para cuando se selecciona un formulario
        comboFormularos.addActionListener(e -> {
            String seleccionado = (String) comboFormularos.getSelectedItem();
            if (seleccionado != null && !seleccionado.isEmpty()) {
                cargarTabla(seleccionado);
            } else {
                limpiarTabla();
            }
        });

        // Listener para cuando se selecciona una fila en la tabla
        tablaProductos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Doble clic para abrir la ventana de edición
                    int filaSeleccionada = tablaProductos.getSelectedRow();
                    if (filaSeleccionada != -1) {
                        abrirVentanaEditarProducto();
                    }
                }
            }
        });

        // Listener para habilitar los botones "Actualizar" y "Eliminar" al seleccionar una fila
        tablaProductos.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                boolean filaSeleccionada = tablaProductos.getSelectedRow() != -1;
                btnActualizar.setEnabled(filaSeleccionada);
                btnEliminar.setEnabled(filaSeleccionada);
            }
        });
    }

    private void cargarTabla(String nombreTabla) {
        try {
            ArrayList<String> columnas = gestionProductoDAO.obtenerColumnasDeTabla(nombreTabla);
            modeloTabla.setColumnCount(0); // Limpiar columnas existentes
            for (String columna : columnas) {
                modeloTabla.addColumn(columna);
            }

            ArrayList<ArrayList<String>> datos = gestionProductoDAO.obtenerDatosDeTabla(nombreTabla);
            modeloTabla.setRowCount(0); // Limpiar filas existentes
            for (ArrayList<String> fila : datos) {
                modeloTabla.addRow(fila.toArray());
            }

            nombreFormularioSeleccionado = nombreTabla;

            // Reajustar el tamaño del JTable para mostrar solo una fila
            if (modeloTabla.getRowCount() > 0) {
                int rowHeight = tablaProductos.getRowHeight();
                int headerHeight = tablaProductos.getTableHeader().getPreferredSize().height;
                int totalHeight = rowHeight * 1 + headerHeight; // Solo una fila
                tablaProductos.setPreferredScrollableViewportSize(new Dimension(tablaProductos.getPreferredSize().width, totalHeight));
            }

            revalidate();
            repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de la tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        nombreFormularioSeleccionado = null;
        revalidate();
        repaint();
    }

    private void abrirVentanaEditarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila para actualizar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtener los datos de la fila seleccionada
        ArrayList<String> datosProducto = new ArrayList<>();
        for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
            Object valor = modeloTabla.getValueAt(filaSeleccionada, i);
            datosProducto.add(valor != null ? valor.toString() : "");
        }

        // Abrir el panel de edición (esto asume que `EditarProducto` es otro `JPanel`)
        EditarProducto editarProducto = new EditarProducto(nombreFormularioSeleccionado, datosProducto, gestionProductoDAO, this);
        JOptionPane.showMessageDialog(this, editarProducto, "Editar Producto", JOptionPane.PLAIN_MESSAGE);
    }

    private void eliminarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila para eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtener el ID del producto seleccionado (asumiendo que es la primera columna)
        String idProducto = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Confirmar la eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el producto con ID: " + idProducto + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                gestionProductoDAO.eliminarProductoPorId(nombreFormularioSeleccionado, idProducto);
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refrescarTabla() {
        if (nombreFormularioSeleccionado != null && !nombreFormularioSeleccionado.isEmpty()) {
            cargarTabla(nombreFormularioSeleccionado);
        }
    }
}
