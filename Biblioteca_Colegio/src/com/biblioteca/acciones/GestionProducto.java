package com.biblioteca.acciones;

import com.biblioteca.controladores.GestionProductoController;
import com.biblioteca.ui.MenuAdministrador;
import com.biblioteca.dao.GestionProductoDAO;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jdatepicker.impl.JDatePanelImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Properties;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final String datePattern = "yyyy-MM-dd";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return text == null || text.isEmpty() ? null : dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value instanceof Date) return dateFormatter.format((Date) value);
        if (value instanceof Calendar) return dateFormatter.format(((Calendar) value).getTime());
        return "";
    }
}

public class GestionProducto extends JFrame {
    private final JComboBox<String> tipoDocumentoComboBox;
    private GestionProductoController controlador;
    private GestionProductoDAO tipoDocumentoDAO;

    private final JPanel panelDinamico;
    private JButton btnGuardar, btnLimpiar, btnVolver, btnCrearNuevoTipo, btnEliminarFormulario;
    private JTextField idGeneradoField;

    public GestionProducto() {
        setTitle("Gestión de Documentos");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            controlador = new GestionProductoController();
            tipoDocumentoDAO = new GestionProductoDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al inicializar la conexión a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] tiposDocumentos = cargarTiposDocumentos();
        tipoDocumentoComboBox = new JComboBox<>(tiposDocumentos);
        tipoDocumentoComboBox.setPreferredSize(new Dimension(250, 40));
        tipoDocumentoComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        tipoDocumentoComboBox.addActionListener(e -> actualizarFormularioYID());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panelSuperior.add(new JLabel("Tipo de Documento:"));
        panelSuperior.add(tipoDocumentoComboBox);
        add(panelSuperior, BorderLayout.NORTH);

        if (tiposDocumentos.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay formularios disponibles. Por favor, cree uno nuevo.");
        }

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        btnGuardar = crearBotonBasico("Guardar", "Guardar los datos ingresados.");
        btnLimpiar = crearBotonBasico("Limpiar", "Limpiar todos los campos del formulario.");
        btnCrearNuevoTipo = crearBotonBasico("Crear Nuevo Tipo", "Crear un nuevo documento.");
        btnVolver = crearBotonBasico("Volver al Menú Anterior", "Volver al menú principal.");
        btnEliminarFormulario = crearBotonBasico("Eliminar Formulario", "Eliminar todos los datos del formulario seleccionado.");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCrearNuevoTipo);
        panelBotones.add(btnEliminarFormulario);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        panelDinamico = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(panelDinamico);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        actualizarFormularioYID();
    }

    private void actualizarListaTiposDocumentos() {
        try {
            tipoDocumentoComboBox.removeAllItems();
            String[] tiposDocumentos = cargarTiposDocumentos();
            for (String tipo : tiposDocumentos) {
                tipoDocumentoComboBox.addItem(tipo);
            }
            if (tiposDocumentos.length == 0) {
                JOptionPane.showMessageDialog(this, "No hay formularios disponibles. Por favor, cree uno nuevo.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] cargarTiposDocumentos() {
        try {
            ArrayList<String> tipos = tipoDocumentoDAO.obtenerTiposDocumentos();
            return tipos.isEmpty() ? new String[0] : tipos.toArray(new String[0]);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los tipos de documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void actualizarFormularioYID() {
        String tipoDocumento = (String) tipoDocumentoComboBox.getSelectedItem();
        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            try {
                if (tipoDocumentoDAO.existeTabla(tipoDocumento)) {
                    cargarFormularioParaDocumento(tipoDocumento);
                    String nuevoID = generarIDParaDocumento(tipoDocumento);
                    idGeneradoField.setText(nuevoID);
                } else {
                    JOptionPane.showMessageDialog(this, "La tabla para este tipo de documento no existe. Por favor, créala primero.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al verificar la existencia de la tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generarIDParaDocumento(String tipoDocumento) {
        String prefix = tipoDocumento.substring(0, Math.min(3, tipoDocumento.length())).toUpperCase();
        int numero = 0;

        try {
            if (tipoDocumentoDAO.existeTabla(tipoDocumento)) {
                numero = tipoDocumentoDAO.obtenerNumeroActual(tipoDocumento);
            } else {
                numero = 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return prefix + "001";
        }
        return prefix + String.format("%03d", numero + 1);
    }

    private void cargarFormularioParaDocumento(String nombreDocumento) {
        panelDinamico.removeAll();
        ArrayList<Map<String, String>> columnasInfo;
        try {
            columnasInfo = tipoDocumentoDAO.obtenerColumnasInfo(nombreDocumento);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int row = 0;

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            JLabel idLabel = new JLabel("ID Documento:");
            idLabel.setFont(new Font("Arial", Font.BOLD, 18));
            panelDinamico.add(idLabel, gbc);

            gbc.gridx = 1;
            idGeneradoField = new JTextField(20);
            idGeneradoField.setEditable(false);
            idGeneradoField.setFont(new Font("Arial", Font.PLAIN, 18));
            panelDinamico.add(idGeneradoField, gbc);

            row++;

            for (int i = 0; i < columnasInfo.size(); i++) {
                Map<String, String> columnInfo = columnasInfo.get(i);
                String columna = columnInfo.get("Field");
                String tipoDato = columnInfo.get("Type");

                if (columna.equalsIgnoreCase("id")) {
                    continue;
                }

                gbc.gridx = 0;
                gbc.gridy = row;
                JLabel label = new JLabel(columna + ":");
                label.setFont(new Font("Arial", Font.BOLD, 18));
                panelDinamico.add(label, gbc);

                gbc.gridx = 1;
                Component campo = crearCampoPorTipo(tipoDato);
                campo.setFont(new Font("Arial", Font.PLAIN, 18));
                ((JComponent) campo).setPreferredSize(new Dimension(400, 30));
                ((JComponent) campo).setName(columna); // Set component name
                panelDinamico.add(campo, gbc);

                if (i + 1 < columnasInfo.size()) {
                    Map<String, String> nextColumnInfo = columnasInfo.get(i + 1);
                    String nextColumna = nextColumnInfo.get("Field");
                    String nextTipoDato = nextColumnInfo.get("Type");

                    if (nextColumna.equalsIgnoreCase("id")) {
                        i++;
                        continue;
                    }

                    gbc.gridx = 2;
                    JLabel nextLabel = new JLabel(nextColumna + ":");
                    nextLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    panelDinamico.add(nextLabel, gbc);

                    gbc.gridx = 3;
                    Component nextCampo = crearCampoPorTipo(nextTipoDato);
                    nextCampo.setFont(new Font("Arial", Font.PLAIN, 18));
                    ((JComponent) nextCampo).setPreferredSize(new Dimension(400, 30));
                    ((JComponent) nextCampo).setName(nextColumna); // Set component name
                    panelDinamico.add(nextCampo, gbc);

                    row++;
                    i++;
                } else {
                    gbc.gridx = 2;
                    gbc.gridwidth = 2;
                    panelDinamico.add(new JLabel(""), gbc);
                    gbc.gridwidth = 1;
                    row++;
                }
            }

            panelDinamico.revalidate();
            panelDinamico.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el formulario para el documento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   private Component crearCampoPorTipo(String tipoDato) {
    if (tipoDato.toLowerCase().contains("int")) {
        JFormattedTextField campoNumero = new JFormattedTextField();
        campoNumero.setColumns(15);
        campoNumero.setValue(0);
        campoNumero.setFont(new Font("Arial", Font.PLAIN, 16));
        return campoNumero;
    } else if (tipoDato.toLowerCase().contains("date")) {
        UtilDateModel model = new UtilDateModel();
        Calendar cal = Calendar.getInstance();
        model.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Hoy");
        p.put("text.month", "Mes");
        p.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setPreferredSize(new Dimension(200, 30));

        // Mostrar calendario al enfocar
        datePicker.getComponent(1).addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                datePicker.getComponent(0).requestFocusInWindow();
                datePicker.getComponent(0).dispatchEvent(new MouseEvent(
                    datePicker.getComponent(0),
                    MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(),
                    0,
                    0,
                    0,
                    1,
                    false
                ));
            }
        });

        return datePicker;
    } else {
        JTextField campoTexto = new JTextField(15);
        campoTexto.setFont(new Font("Arial", Font.PLAIN, 16));
        return campoTexto;
    }
}

    private JButton crearBotonBasico(String texto, String tooltip) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(250, 50));
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setToolTipText(tooltip);
        boton.setHorizontalAlignment(SwingConstants.CENTER);

        boton.addActionListener(e -> {
            switch (texto) {
                case "Guardar":
                    guardarDocumento();
                    break;
                case "Limpiar":
                    limpiarFormulario();
                    break;
                case "Volver al Menú Anterior":
                    volverAlMenu();
                    break;
                case "Crear Nuevo Tipo":
                    crearNuevoTipoDeDocumento();
                    break;
                case "Eliminar Formulario":
                    eliminarFormulario();
                    break;
                default:
                    break;
            }
        });

        return boton;
    }

   private void guardarDocumento() {
    try {
        String tipoDocumento = (String) tipoDocumentoComboBox.getSelectedItem();
        ArrayList<String> datos = new ArrayList<>();
        ArrayList<String> nombresColumnas = new ArrayList<>();
        ArrayList<String> tiposColumnas = new ArrayList<>();

        datos.add(idGeneradoField.getText());
        nombresColumnas.add("id");
        tiposColumnas.add("varchar");

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            for (Component comp : panelDinamico.getComponents()) {
                if (comp instanceof JTextField && comp != idGeneradoField) {
                    String nombreColumna = comp.getName();
                    String valor = ((JTextField) comp).getText().trim();
                    nombresColumnas.add(nombreColumna);
                    datos.add(valor);
                    tiposColumnas.add("varchar");
                } else if (comp instanceof JFormattedTextField) {
                    String nombreColumna = comp.getName();
                    String valor = ((JFormattedTextField) comp).getText().trim();
                    nombresColumnas.add(nombreColumna);
                    datos.add(valor);
                    tiposColumnas.add("int");
                } else if (comp instanceof JDatePickerImpl) {
                    String nombreColumna = comp.getName();
                    JDatePickerImpl datePicker = (JDatePickerImpl) comp;
                    Date selectedDate = (Date) datePicker.getModel().getValue();
                    if (selectedDate != null) {
                        datos.add(new SimpleDateFormat("yyyy-MM-dd").format(selectedDate));
                    } else {
                        datos.add(null);
                    }
                    nombresColumnas.add(nombreColumna);
                    tiposColumnas.add("date");
                }
            }
        }

        System.out.println("Datos a guardar: " + Arrays.toString(datos.toArray()));
        System.out.println("Nombres de columnas: " + Arrays.toString(nombresColumnas.toArray()));

        boolean exito = controlador.guardarDocumento(tipoDocumento, nombresColumnas.toArray(new String[0]), datos.toArray(new String[0]), tiposColumnas.toArray(new String[0]));
        JOptionPane.showMessageDialog(this, exito ? "Documento guardado exitosamente." : "Error al guardar el documento.");

        if (exito) {
            limpiarFormulario();
            actualizarFormularioYID();
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar el documento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


    private void limpiarFormulario() {
        for (Component comp : panelDinamico.getComponents()) {
            if (comp instanceof JTextField && comp != idGeneradoField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JDatePickerImpl) {
                ((JDatePickerImpl) comp).getModel().setValue(null);
            } else if (comp instanceof JFormattedTextField) {
                ((JFormattedTextField) comp).setText("");
            }
        }
    }

    private void volverAlMenu() {
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea volver al menú anterior?", "Confirmar Salida", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose();
            new MenuAdministrador().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionProducto().setVisible(true));
    }

    private void crearNuevoTipoDeDocumento() {
        String nombreDocumento = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo tipo de documento:");
        if (nombreDocumento == null || nombreDocumento.trim().isEmpty()) return;

        try {
            if (tipoDocumentoDAO.existeTabla(nombreDocumento)) {
                int opcion = JOptionPane.showOptionDialog(this,
                        "La tabla ya existe. ¿Qué deseas hacer?",
                        "Tabla Existente",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Usar Existente", "Eliminar y Crear Nueva", "Editar Columnas", "Cancelar"},
                        "Cancelar");

                switch (opcion) {
                    case 0:
                        if (!documentoEnComboBox(nombreDocumento)) {
                            tipoDocumentoComboBox.addItem(nombreDocumento);
                        }
                        tipoDocumentoComboBox.setSelectedItem(nombreDocumento);
                        actualizarFormularioYID();
                        JOptionPane.showMessageDialog(this, "Usando la tabla existente.");
                        return;
                    case 1:
                        tipoDocumentoDAO.eliminarTabla(nombreDocumento);
                        JOptionPane.showMessageDialog(this, "Tabla eliminada.");
                        break;
                    case 2:
                        editarColumnasTabla(nombreDocumento);
                        actualizarFormularioYID();
                        return;
                    case 3:
                        return;
                    default:
                        return;
                }
            }

            String numColumnasStr = JOptionPane.showInputDialog(this, "Número de columnas (excluyendo ID):");
            if (numColumnasStr == null || numColumnasStr.trim().isEmpty()) return;
            int columnas = Integer.parseInt(numColumnasStr);
            ArrayList<String> nombresColumnas = new ArrayList<>();

            for (int i = 0; i < columnas; i++) {
                String nombreColumna = JOptionPane.showInputDialog(this, "Nombre de la columna " + (i + 1) + ":");
                if (nombreColumna == null || nombreColumna.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre de columna inválido. Operación cancelada.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                nombresColumnas.add(nombreColumna);
            }

            tipoDocumentoDAO.crearTablaParaDocumento(nombreDocumento, nombresColumnas);
            tipoDocumentoDAO.agregarTipoDocumento(nombreDocumento);

            if (!documentoEnComboBox(nombreDocumento)) {
                tipoDocumentoComboBox.addItem(nombreDocumento);
            }
            tipoDocumentoComboBox.setSelectedItem(nombreDocumento);
            actualizarFormularioYID();
            JOptionPane.showMessageDialog(this, "Nuevo tipo de documento creado exitosamente.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de columnas inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el nuevo tipo de documento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean documentoEnComboBox(String nombreDocumento) {
        for (int i = 0; i < tipoDocumentoComboBox.getItemCount(); i++) {
            if (tipoDocumentoComboBox.getItemAt(i).equals(nombreDocumento)) {
                return true;
            }
        }
        return false;
    }

    private void editarColumnasTabla(String nombreDocumento) {
        try {
            ArrayList<String> columnasActuales = tipoDocumentoDAO.obtenerColumnasDeTabla(nombreDocumento);
            ArrayList<String> nuevasColumnas = new ArrayList<>(columnasActuales);

            for (int i = 0; i < columnasActuales.size(); i++) {
                String columna = columnasActuales.get(i);
                String nuevaColumna = JOptionPane.showInputDialog(this, "Nombre de la columna " + (i + 1) + " (actual: " + columna + "):", columna);
                if (nuevaColumna != null && !nuevaColumna.trim().isEmpty()) {
                    nuevasColumnas.set(i, nuevaColumna.trim());
                }
            }

            tipoDocumentoDAO.actualizarColumnasTabla(nombreDocumento, nuevasColumnas);
            JOptionPane.showMessageDialog(this, "Columnas actualizadas para la tabla " + nombreDocumento);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al editar columnas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarFormulario() {
        String tipoDocumento = (String) tipoDocumentoComboBox.getSelectedItem();
        if (tipoDocumento == null || tipoDocumento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay un tipo de documento seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar el formulario y sus datos?",
                "Confirmación de Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Confirmas que deseas eliminar todos los datos y el formulario " + tipoDocumento + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    boolean exito = tipoDocumentoDAO.eliminarDatosDeTabla(tipoDocumento);
                    if (exito) {
                        JOptionPane.showMessageDialog(this, "El formulario y sus datos fueron eliminados exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        actualizarListaTiposDocumentos();
                        panelDinamico.removeAll();
                        panelDinamico.revalidate();
                        panelDinamico.repaint();
                        idGeneradoField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar el formulario y sus datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el formulario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
