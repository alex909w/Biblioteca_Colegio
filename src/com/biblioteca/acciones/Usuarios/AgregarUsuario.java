package com.biblioteca.acciones.Usuarios;

import com.biblioteca.utilidades.DateLabelFormatter;
import com.biblioteca.control.AdministracionUsuariosController;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Properties;

public class AgregarUsuario extends JPanel {

    // Componentes UI
    private JTextField txtId, txtNombres, txtApellidos, txtEmail, txtTelefono, txtBuscar;
    private JPasswordField txtClave;
    private JComboBox<String> cbTipoUsuario, cbFiltroBusqueda;
    private JDatePickerImpl datePicker;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;
    private JSpinner spinnerLimitePrestamos;
    private JButton btnBuscar, btnLimpiarBusqueda, btnNuevo, btnActualizar, btnEliminar, btnGuardar, btnCancelar;

    // Constantes de diseño
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);    // Material Indigo
    private static final Color ACCENT_COLOR = new Color(255, 64, 129);    // Material Pink
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color FIELD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUBTLE_TEXT = new Color(117, 117, 117);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    private static final int BORDER_RADIUS = 8;
    private static final int PADDING = 20;

    // Variables de modo
    private boolean editMode = false;
    private boolean nuevoModo = false;

    // Controlador
    private AdministracionUsuariosController controlador;

    public AgregarUsuario() {
        controlador = new AdministracionUsuariosController(this);
        setupMainPanel();
        initComponents();
        controlador.cargarUsuariosEnTabla();
        controlador.actualizarIdDinamicamente();
    }

    private void setupMainPanel() {
        setLayout(new BorderLayout(PADDING, PADDING));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
    }

private void initComponents() {
    // Panel principal con título
    JPanel contentPanel = new JPanel(new BorderLayout(PADDING, PADDING));
    contentPanel.setBackground(BACKGROUND_COLOR);
    
    // Título
    JLabel titleLabel = new JLabel("Administración de Usuarios");
    titleLabel.setFont(TITLE_FONT);
    titleLabel.setForeground(PRIMARY_COLOR);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, PADDING, 0));
    
    contentPanel.add(titleLabel, BorderLayout.NORTH);
    contentPanel.add(createMainContent(), BorderLayout.CENTER);

    
    // Crear JScrollPane y agregar el contentPanel
    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    add(scrollPane, BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
}

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout(PADDING, PADDING));
        mainContent.setBackground(BACKGROUND_COLOR);

        mainContent.add(createFormPanel(), BorderLayout.NORTH);
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(FIELD_BACKGROUND);
        formPanel.setBorder(createPanelBorder("Información del Usuario"));

        // Inicialización de componentes
        initializeFormComponents();

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Agregar componentes al panel
        addComponentsToGrid(formPanel, gbc);

        return formPanel;
    }

    private void initializeFormComponents() {
        // Campos de texto
        txtId = createStyledTextField(10, false);
        txtNombres = createStyledTextField(15, true);
        txtApellidos = createStyledTextField(15, true);
        txtClave = createStyledPasswordField();
        txtEmail = createStyledTextField(15, true);
        txtTelefono = createStyledTextField(15, true);
        
        // ComboBox
        cbTipoUsuario = createStyledComboBox(new String[]{"Administrador", "Profesor", "Alumno"});
        
        // Spinner
        spinnerLimitePrestamos = createStyledSpinner();
        
        // DatePicker
        datePicker = createStyledDatePicker();
    }
    
    private void addComponentsToGrid(JPanel panel, GridBagConstraints gbc) {
        // Agregar etiquetas y campos al panel
        addLabelFieldPair(panel, gbc, "ID:", txtId, 0, 0);
        addLabelFieldPair(panel, gbc, "Nombres:", txtNombres, 0, 1);
        addLabelFieldPair(panel, gbc, "Apellidos:", txtApellidos, 2, 1);
        addLabelFieldPair(panel, gbc, "Contraseña:", txtClave, 0, 2);
        addLabelFieldPair(panel, gbc, "Correo Electrónico:", txtEmail, 2, 2);
        addLabelFieldPair(panel, gbc, "Teléfono:", txtTelefono, 0, 3);
        addLabelFieldPair(panel, gbc, "Tipo de Usuario:", cbTipoUsuario, 2, 3);
        addLabelFieldPair(panel, gbc, "Límite de Préstamos:", spinnerLimitePrestamos, 0, 4);
        addLabelFieldPair(panel, gbc, "Fecha de Registro:", datePicker, 2, 4);
    }

    private void addLabelFieldPair(JPanel panel, GridBagConstraints gbc, String label, Component field, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        panel.add(new JLabel(label), gbc);
        gbc.gridx++;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField(int columns, boolean editable) {
        JTextField field = new JTextField(columns);
        field.setFont(REGULAR_FONT);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setEditable(editable);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setFont(REGULAR_FONT);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(REGULAR_FONT);
        comboBox.setBackground(FIELD_BACKGROUND);
        comboBox.setForeground(TEXT_COLOR);
        ((JComponent) comboBox.getRenderer()).setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return comboBox;
    }

    private JSpinner createStyledSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinner.setFont(REGULAR_FONT);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor)editor).getTextField().setBackground(FIELD_BACKGROUND);
        }
        return spinner;
    }

    private JDatePickerImpl createStyledDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Hoy");
        properties.put("text.month", "Mes");
        properties.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        
        // Estilizar el componente
        datePicker.getJFormattedTextField().setBackground(FIELD_BACKGROUND);
        datePicker.getJFormattedTextField().setForeground(TEXT_COLOR);
        datePicker.getJFormattedTextField().setFont(REGULAR_FONT);
        
        return datePicker;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(PADDING, PADDING));
        tablePanel.setBackground(BACKGROUND_COLOR);
        
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        searchPanel.setBackground(FIELD_BACKGROUND);
        searchPanel.setBorder(createPanelBorder("Búsqueda"));
        
        txtBuscar = createStyledTextField(20, true);
        cbFiltroBusqueda = createStyledComboBox(new String[]{"Nombre", "Correo", "Tipo de Usuario"});
        btnBuscar = createStyledButton("Buscar", PRIMARY_COLOR);
        btnLimpiarBusqueda = createStyledButton("Limpiar", null);
        
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(txtBuscar);
        searchPanel.add(cbFiltroBusqueda);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnLimpiarBusqueda);
        
        // Tabla
        tableModel = createTableModel();
        tableUsuarios = createStyledTable();
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        scrollPane.setBackground(FIELD_BACKGROUND);
        scrollPane.setBorder(createPanelBorder("Resultados"));
        
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JTable createStyledTable() {
        JTable table = new JTable(tableModel);
        table.setFont(REGULAR_FONT);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(224, 224, 224));
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
        
        return table;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(
            new Object[]{"ID", "Nombres", "Apellidos", "Tipo de Usuario", 
                        "Correo Electrónico", "Clave", "Teléfono", 
                        "Fecha de Registro", "Límite de Préstamos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        btnNuevo = createStyledButton("Nuevo", PRIMARY_COLOR);
        btnActualizar = createStyledButton("Actualizar", PRIMARY_COLOR);
        btnEliminar = createStyledButton("Eliminar", ACCENT_COLOR);
        btnGuardar = createStyledButton("Guardar", PRIMARY_COLOR);
        btnCancelar = createStyledButton("Cancelar", null);
        
        btnGuardar.setVisible(false);
        btnCancelar.setVisible(false);
        
        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        setupButtonListeners();
        
        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        
        if (bgColor != null) {
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(FIELD_BACKGROUND);
            button.setForeground(TEXT_COLOR);
        }
        
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        
        // Efectos hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (bgColor != null) {
                    button.setBackground(bgColor.darker());
                } else {
                    button.setBackground(new Color(224, 224, 224));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (bgColor != null) {
                    button.setBackground(bgColor);
                } else {
                    button.setBackground(FIELD_BACKGROUND);
                }
            }
        });
        
        return button;
    }

    private Border createPanelBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            title
        );
        titledBorder.setTitleFont(HEADER_FONT);
        titledBorder.setTitleColor(PRIMARY_COLOR);
        
        return BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    private void setupButtonListeners() {
        btnBuscar.addActionListener(e -> controlador.buscarUsuarios());
        btnLimpiarBusqueda.addActionListener(e -> controlador.limpiarBusqueda());
        btnNuevo.addActionListener(e -> controlador.manejarBotonNuevo());
        btnActualizar.addActionListener(e -> controlador.manejarBotonActualizar());
        btnEliminar.addActionListener(e -> controlador.manejarBotonEliminar());
        btnGuardar.addActionListener(e -> controlador.manejarBotonGuardar());
        btnCancelar.addActionListener(e -> controlador.manejarBotonCancelar());
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


    
    
}
