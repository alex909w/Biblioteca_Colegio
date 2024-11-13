package com.biblioteca.acciones.Usuarios;

import com.biblioteca.utilidades.DateLabelFormatter;
import com.biblioteca.controladores.AdministracionUsuariosController;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Properties;

public class AdministracionUsuarios extends JPanel {
    // Colores personalizados
    private static final Color PRIMARY_COLOR = new Color(51, 102, 204);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    // Fuentes personalizadas
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private JTextField txtId, txtNombres, txtApellidos, txtEmail, txtTelefono, txtBuscar;
    private JPasswordField txtClave;
    private JComboBox<String> cbTipoUsuario, cbFiltroBusqueda;
    private JDatePickerImpl datePicker;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;
    private JSpinner spinnerLimitePrestamos;
    private JPanel panelBusqueda;
    private JButton btnBuscar, btnLimpiarBusqueda;

    private boolean editMode = false;
    private boolean nuevoModo = false;

    private AdministracionUsuariosController controlador;

    public AdministracionUsuarios() {
        controlador = new AdministracionUsuariosController(this);
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();  // Inicializa los componentes
        setupLayout();     // Configura el layout
        setupListeners();  // Añade los listeners
        controlador.cargarUsuariosEnTabla();  // Carga usuarios en la tabla
        controlador.actualizarIdDinamicamente(); // Actualiza el ID
    }

    // Inicialización de componentes
    private void initComponents() {
        // Inicializa componentes de manera modular
        txtId = createTextField(false);
        txtNombres = createTextField(true);
        txtApellidos = createTextField(true);
        txtClave = createPasswordField(true);
        txtEmail = createTextField(true);
        txtTelefono = createTextField(true);
        txtBuscar = createTextField(true);

        cbTipoUsuario = createComboBox(new String[]{"Administrador", "Profesor", "Alumno"});
        cbFiltroBusqueda = createComboBox(new String[]{"Nombre", "Correo", "Tipo de Usuario"});
        spinnerLimitePrestamos = createSpinner();
        datePicker = createDatePicker();

        btnBuscar = createButton("Buscar", PRIMARY_COLOR);
        btnLimpiarBusqueda = createButton("Limpiar", ACCENT_COLOR);

        initTable();
    }

    // Métodos para crear componentes
    private JTextField createTextField(boolean editable) {
        JTextField field = new JTextField(5);
        field.setFont(FIELD_FONT);
        field.setEditable(editable);
        field.setForeground(Color.GRAY); // Color del texto del placeholder
        field.setBorder(createFieldBorder());
        field.setPreferredSize(new Dimension(150, 30));
        return field;
    }

    private JPasswordField createPasswordField(boolean editable) {
        JPasswordField field = new JPasswordField(5);
        field.setFont(FIELD_FONT);
        field.setEditable(editable);
        field.setForeground(Color.GRAY);
        field.setBorder(createFieldBorder());
        field.setPreferredSize(new Dimension(150, 30));
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(FIELD_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(150, 30));
        return comboBox;
    }

    private JSpinner createSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinner.setFont(FIELD_FONT);
        return spinner;
    }

    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Hoy");
        properties.put("text.month", "Mes");
        properties.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );
    }

    // Inicializa la tabla
    private void initTable() {
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nombres", "Apellidos", "Tipo de Usuario", 
                        "Correo Electrónico", "Clave", "Teléfono", 
                        "Fecha de Registro", "Límite de Préstamos"}, 0
        );
        tableUsuarios = new JTable(tableModel);
        tableUsuarios.setFont(FIELD_FONT);
        tableUsuarios.setRowHeight(20);
        tableUsuarios.setGridColor(SECONDARY_COLOR);
        tableUsuarios.setSelectionBackground(PRIMARY_COLOR.brighter());
        tableUsuarios.setSelectionForeground(Color.WHITE);

        // Encabezado de la tabla
        JTableHeader header = tableUsuarios.getTableHeader();
        header.setFont(TITLE_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
    }

    // Configura el layout de la interfaz
    private void setupLayout() {
        JPanel panelCampos = createPanelWithLayout();
        addFieldsToPanel(panelCampos);

        // Panel de búsqueda
        setupSearchPanel();

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 15));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.add(panelCampos, BorderLayout.NORTH);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);

        // Agregar todo al layout principal
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createPanelWithLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder("Información del Usuario"));
        return panel;
    }

    private void addFieldsToPanel(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Columna 1
        addFormRow(panel, "ID:", txtId, gbc, 0, 0);
        addFormRow(panel, "Nombres:", txtNombres, gbc, 1, 0);
        addFormRow(panel, "Contraseña:", txtClave, gbc, 2, 0);
        addFormRow(panel, "Teléfono:", txtTelefono, gbc, 3, 0);
        
        // Columna 2
        addFormRow(panel, "Apellidos:", txtApellidos, gbc, 0, 1);
        addFormRow(panel, "Correo Electrónico:", txtEmail, gbc, 1, 1);
        addFormRow(panel, "Límite de Préstamos:", spinnerLimitePrestamos, gbc, 2, 1);
        addFormRow(panel, "Fecha de Registro:", datePicker, gbc, 3, 1);
    }

    private void addFormRow(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int row, int column) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = column + 1;  // La casilla está en la columna siguiente
        gbc.weightx = 1.0;
        panel.add(component, gbc);
        gbc.weightx = 0.0;
    }

    // Panel de búsqueda
    private void setupSearchPanel() {
        panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBusqueda.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(LABEL_FONT);

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(cbFiltroBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiarBusqueda);
    }

    private Border createSectionBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            TITLE_FONT,
            TEXT_COLOR
        );
        return titledBorder;
    }

    // Agregar listeners para los botones y eventos
    private void setupListeners() {
        btnBuscar.addActionListener(e -> controlador.buscarUsuarios());
        btnLimpiarBusqueda.addActionListener(e -> controlador.limpiarBusqueda());
        cbTipoUsuario.addActionListener(e -> controlador.actualizarIdDinamicamente());
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

    // Getter para el Panel de Búsqueda
    public JPanel getPanelBusqueda() {
        return panelBusqueda;
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