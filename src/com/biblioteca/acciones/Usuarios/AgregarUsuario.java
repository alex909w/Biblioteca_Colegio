package com.biblioteca.acciones.Usuarios;

import com.biblioteca.utilidades.DateLabelFormatter;
import com.biblioteca.control.AdministracionUsuariosController;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class AgregarUsuario extends JPanel {

    private JTextField txtId, txtNombres, txtApellidos, txtEmail, txtTelefono, txtBuscar;
    private JPasswordField txtClave;
    private JComboBox<String> cbTipoUsuario, cbFiltroBusqueda;
    private JDatePickerImpl datePicker;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;
    private JSpinner spinnerLimitePrestamos;
    private JPanel panelBusqueda;
    private JButton btnBuscar, btnLimpiarBusqueda;

    private JButton btnNuevo, btnActualizar, btnEliminar, btnGuardar, btnCancelar;

    private boolean editMode = false;
    private boolean nuevoModo = false;

    private AdministracionUsuariosController controlador;

    public AgregarUsuario() {
        controlador = new AdministracionUsuariosController(this);

        setLayout(new BorderLayout());

        // Panel de Campos
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear los labels y campos de texto
        JLabel lblId = new JLabel("ID:");
        txtId = new JTextField(10);
        txtId.setEditable(false);
        JLabel lblNombres = new JLabel("Nombres:");
        txtNombres = new JTextField(15);
        JLabel lblApellidos = new JLabel("Apellidos:");
        txtApellidos = new JTextField(15);
        JLabel lblClave = new JLabel("Contraseña:");
        txtClave = new JPasswordField(15);
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        txtEmail = new JTextField(15);
        JLabel lblTelefono = new JLabel("Teléfono:");
        txtTelefono = new JTextField(15);
        JLabel lblTipoUsuario = new JLabel("Tipo de Usuario:");
        cbTipoUsuario = new JComboBox<>(new String[]{"Administrador", "Profesor", "Alumno"});
        JLabel lblLimitePrestamos = new JLabel("Límite de Préstamos:");
        spinnerLimitePrestamos = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JLabel lblFechaRegistro = new JLabel("Fecha de Registro:");

        // Date Picker
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Hoy");
        properties.put("text.month", "Mes");
        properties.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Inicialización de botones de acción
        btnNuevo = new JButton("Nuevo");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        // Inicializar cbFiltroBusqueda
        cbFiltroBusqueda = new JComboBox<>(new String[]{"Nombre", "Correo", "Tipo de Usuario"});
        btnBuscar = new JButton("Buscar");
        btnLimpiarBusqueda = new JButton("Limpiar");

        // Configurar fuentes y tamaños para los botones
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        btnNuevo.setFont(buttonFont);
        btnActualizar.setFont(buttonFont);
        btnEliminar.setFont(buttonFont);
        btnGuardar.setFont(buttonFont);
        btnCancelar.setFont(buttonFont);
        btnBuscar.setFont(buttonFont);
        btnLimpiarBusqueda.setFont(buttonFont);

        Dimension buttonSize = new Dimension(150, 40);
        btnNuevo.setPreferredSize(buttonSize);
        btnActualizar.setPreferredSize(buttonSize);
        btnEliminar.setPreferredSize(buttonSize);
        btnGuardar.setPreferredSize(buttonSize);
        btnCancelar.setPreferredSize(buttonSize);
        btnBuscar.setPreferredSize(new Dimension(100, 40));
        btnLimpiarBusqueda.setPreferredSize(new Dimension(100, 40));

        // Inicialmente, ocultar botones "Guardar" y "Cancelar"
        btnGuardar.setVisible(false);
        btnCancelar.setVisible(false);

        // Configurar GridBagConstraints
        GridBagConstraints gbcCampos = new GridBagConstraints();
        gbcCampos.insets = new Insets(10, 10, 10, 10);
        gbcCampos.anchor = GridBagConstraints.WEST;

        // Añadir componentes al panel de campos
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 0;
        panelCampos.add(lblId, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtId, gbcCampos);
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 1;
        panelCampos.add(lblNombres, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtNombres, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblApellidos, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(txtApellidos, gbcCampos);
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 2;
        panelCampos.add(lblClave, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtClave, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblEmail, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(txtEmail, gbcCampos);
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 3;
        panelCampos.add(lblTelefono, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(txtTelefono, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblTipoUsuario, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(cbTipoUsuario, gbcCampos);
        gbcCampos.gridx = 0;
        gbcCampos.gridy = 4;
        panelCampos.add(lblLimitePrestamos, gbcCampos);
        gbcCampos.gridx = 1;
        panelCampos.add(spinnerLimitePrestamos, gbcCampos);
        gbcCampos.gridx = 2;
        panelCampos.add(lblFechaRegistro, gbcCampos);
        gbcCampos.gridx = 3;
        panelCampos.add(datePicker, gbcCampos);

        // Panel de búsqueda
        panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(20);
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(cbFiltroBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiarBusqueda);

        // Tabla para mostrar los usuarios
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombres", "Apellidos", "Tipo de Usuario", "Correo Electrónico", "Clave", "Teléfono", "Fecha de Registro", "Límite de Préstamos"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableUsuarios = new JTable(tableModel);
        tableUsuarios.getColumn("Clave").setCellRenderer(new PasswordRenderer());
        tableUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);

        // Panel de Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnNuevo);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        // Listeners para los botones
        btnBuscar.addActionListener(e -> controlador.buscarUsuarios());
        btnLimpiarBusqueda.addActionListener(e -> controlador.limpiarBusqueda());

        btnNuevo.addActionListener(e -> controlador.manejarBotonNuevo());
        btnActualizar.addActionListener(e -> controlador.manejarBotonActualizar());
        btnEliminar.addActionListener(e -> controlador.manejarBotonEliminar());
        btnGuardar.addActionListener(e -> controlador.manejarBotonGuardar());
        btnCancelar.addActionListener(e -> controlador.manejarBotonCancelar());

        cbTipoUsuario.addActionListener(e -> controlador.actualizarIdDinamicamente());

        // Panel superior que contiene panelCampos y panelBusqueda
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelCampos, BorderLayout.NORTH);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);

        // Agregar paneles al JPanel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        habilitarCampos(false);
        controlador.cargarUsuariosEnTabla();
        controlador.actualizarIdDinamicamente();
    }

    // Métodos adicionales
    public void habilitarCampos(boolean habilitar) {
        txtNombres.setEnabled(habilitar);
        txtApellidos.setEnabled(habilitar);
        txtClave.setEnabled(habilitar);
        txtEmail.setEnabled(habilitar);
        txtTelefono.setEnabled(habilitar);
        cbTipoUsuario.setEnabled(habilitar && !editMode);
        spinnerLimitePrestamos.setEnabled(habilitar);
        datePicker.setEnabled(habilitar);
    }

    public void limpiarCampos() {
        txtId.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtClave.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        cbTipoUsuario.setSelectedIndex(0);
        cbTipoUsuario.setEnabled(!editMode);
        spinnerLimitePrestamos.setValue(1);
        tableUsuarios.clearSelection();
        cargarFechaActual();
        if (!editMode) {
            controlador.actualizarIdDinamicamente();
        }
    }

    private void cargarFechaActual() {
        Calendar calendar = Calendar.getInstance();
        datePicker.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getModel().setSelected(true);
    }

    // Métodos para actualizar los modos
    public boolean isNuevoModo() {
        return nuevoModo;
    }

    public void setNuevoModo(boolean nuevoModo) {
        this.nuevoModo = nuevoModo;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    // Métodos Getter para el Controlador

    // Getters para los TextFields
    public JTextField getTxtId() {
        return txtId;
    }

    public JTextField getTxtNombres() {
        return txtNombres;
    }

    public JTextField getTxtApellidos() {
        return txtApellidos;
    }

    public JPasswordField getTxtClave() {
        return txtClave;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JTextField getTxtTelefono() {
        return txtTelefono;
    }

    public JTextField getTxtBuscar() {
        return txtBuscar;
    }

    // Getters para los ComboBoxes
    public JComboBox<String> getCbTipoUsuario() {
        return cbTipoUsuario;
    }

    public JComboBox<String> getCbFiltroBusqueda() {
        return cbFiltroBusqueda;
    }

    // Getters para otros componentes
    public JDatePickerImpl getDatePicker() {
        return datePicker;
    }

    public JSpinner getSpinnerLimitePrestamos() {
        return spinnerLimitePrestamos;
    }

    public JTable getTableUsuarios() {
        return tableUsuarios;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Getters para los Botones de búsqueda
    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnLimpiarBusqueda() {
        return btnLimpiarBusqueda;
    }

    // Getters para los Botones de Acción
    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    // Renderer para ocultar la contraseña en la tabla
    private class PasswordRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel && value instanceof String) {
                ((JLabel) c).setText(((String) value).replaceAll(".", "*"));
            }
            return c;
        }
    }
    
    
}
