package com.biblioteca.acciones;

import com.biblioteca.controladores.DocumentoController;
import com.biblioteca.ui.MenuAdministrador;
import com.biblioteca.validaciones.TipoDocumentoDAO;
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
    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

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

public class GestionDocumentos extends JFrame {
    private JComboBox<String> tipoDocumentoComboBox;
    private DocumentoController controlador;
    private TipoDocumentoDAO tipoDocumentoDAO;

    private JPanel panelDinamico;
    private JButton btnGuardar, btnLimpiar, btnVolver, btnCrearNuevoTipo;
    private JTextField idGeneradoField;

    public GestionDocumentos() {
        setTitle("Gestión de Documentos");
        setSize(1200, 800); // Aumentado para acomodar botones más anchos
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        controlador = new DocumentoController();
        tipoDocumentoDAO = new TipoDocumentoDAO();

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // ComboBox para seleccionar el tipo de documento
        String[] tiposDocumentos = cargarTiposDocumentos();
        tipoDocumentoComboBox = new JComboBox<>(tiposDocumentos);
        tipoDocumentoComboBox.setPreferredSize(new Dimension(250, 40)); // Ancho aumentado
        tipoDocumentoComboBox.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente más grande
        tipoDocumentoComboBox.addActionListener(e -> actualizarFormularioYID());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panelSuperior.add(new JLabel("Tipo de Documento:"));
        panelSuperior.add(tipoDocumentoComboBox);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel de botones en la parte inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)); // Espaciado aumentado
        btnGuardar = crearBotonBasico("Guardar", "Guardar los datos ingresados.");
        btnLimpiar = crearBotonBasico("Limpiar", "Limpiar todos los campos del formulario.");
        btnCrearNuevoTipo = crearBotonBasico("Crear Nuevo Tipo", "Crear un nuevo documento.");
        btnVolver = crearBotonBasico("Volver al Menú Anterior", "Volver al menú principal.");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCrearNuevoTipo);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Panel dinámico con JScrollPane
        panelDinamico = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(panelDinamico);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        actualizarFormularioYID();
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
            cargarFormularioParaDocumento(tipoDocumento);
            String nuevoID = generarIDParaDocumento(tipoDocumento);
            idGeneradoField.setText(nuevoID);
        } else {
            JOptionPane.showMessageDialog(this, "No hay formularios disponibles. Por favor, cree uno nuevo.");
        }
    }

    private String generarIDParaDocumento(String tipoDocumento) {
        String prefix = tipoDocumento.substring(0, Math.min(3, tipoDocumento.length())).toUpperCase();
        int numero = 0;

        try {
            if (!tipoDocumentoDAO.existeTabla(tipoDocumento)) {
                ArrayList<String> columnas = new ArrayList<>();
                columnas.add("Nombre");
                columnas.add("Descripción");
                tipoDocumentoDAO.crearTablaParaDocumento(tipoDocumento, columnas);
                return prefix + "001";
            }
            numero = tipoDocumentoDAO.obtenerNumeroActual(prefix);
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

            // Agregar el campo de ID generado automáticamente en la primera fila
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1; // Cada etiqueta y campo ocupa una celda

            panelDinamico.add(new JLabel("ID Documento:"), gbc);

            gbc.gridx = 1;
            idGeneradoField = new JTextField(15);
            idGeneradoField.setEditable(false);
            idGeneradoField.setFont(new Font("Arial", Font.PLAIN, 16));
            panelDinamico.add(idGeneradoField, gbc);

            row++;

            // Preparar para agregar los pares de campos en dos columnas
            for (int i = 0; i < columnasInfo.size(); i++) {
                Map<String, String> columnInfo = columnasInfo.get(i);
                String columna = columnInfo.get("Field");
                String tipoDato = columnInfo.get("Type");

                if (columna.equalsIgnoreCase("id")) {
                    continue;
                }

                // Primera columna del par
                gbc.gridx = 0;
                gbc.gridy = row;
                panelDinamico.add(new JLabel(columna + ":"), gbc);

                gbc.gridx = 1;
                Component campo = crearCampoPorTipo(tipoDato);
                panelDinamico.add(campo, gbc);

                // Segunda columna del par (si existe)
                if (i + 1 < columnasInfo.size()) {
                    Map<String, String> nextColumnInfo = columnasInfo.get(i + 1);
                    String nextColumna = nextColumnInfo.get("Field");
                    String nextTipoDato = nextColumnInfo.get("Type");

                    gbc.gridx = 2;
                    panelDinamico.add(new JLabel(nextColumna + ":"), gbc);

                    gbc.gridx = 3;
                    Component nextCampo = crearCampoPorTipo(nextTipoDato);
                    panelDinamico.add(nextCampo, gbc);

                    row++;
                    i++; // Incrementar adicionalmente para manejar el siguiente par
                } else {
                    // Si no hay par, dejar el último campo alineado a la izquierda
                    gbc.gridx = 2;
                    gbc.gridwidth = 2; // Ocupa las dos celdas restantes
                    panelDinamico.add(new JLabel(""), gbc); // Espacio vacío
                    gbc.gridwidth = 1; // Resetear el gridwidth
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
            Properties p = new Properties();
            p.put("text.today", "Hoy");
            p.put("text.month", "Mes");
            p.put("text.year", "Año");
            JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
            JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            datePicker.setPreferredSize(new Dimension(200, 30));
            return datePicker;
        } else {
            JTextField campoTexto = new JTextField(15);
            campoTexto.setFont(new Font("Arial", Font.PLAIN, 16));
            return campoTexto;
        }
    }

    private JButton crearBotonBasico(String texto, String tooltip) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(250, 50)); // Aumentado para que el texto sea más visible
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 16)); // Fuente más grande para mejor visibilidad
        boton.setToolTipText(tooltip);
        boton.setHorizontalAlignment(SwingConstants.CENTER);

        boton.addActionListener(e -> {
            if ("Guardar".equals(texto)) guardarDocumento();
            else if ("Limpiar".equals(texto)) limpiarFormulario();
            else if ("Volver al Menú Anterior".equals(texto)) volverAlMenu();
            else if ("Crear Nuevo Tipo".equals(texto)) crearNuevoTipoDeDocumento();
        });

        return boton;
    }

    private void guardarDocumento() {
        String tipoDocumento = (String) tipoDocumentoComboBox.getSelectedItem();
        ArrayList<String> datos = new ArrayList<>();
        ArrayList<String> tiposColumnas = new ArrayList<>();

        // Agregar el ID generado automáticamente
        datos.add(idGeneradoField.getText());
        tiposColumnas.add("varchar"); // Suponiendo que el ID es VARCHAR

        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            for (Component comp : panelDinamico.getComponents()) {
                if (comp instanceof JTextField && comp != idGeneradoField) {
                    datos.add(((JTextField) comp).getText().trim());
                    tiposColumnas.add("varchar"); // Suponiendo VARCHAR para texto
                } else if (comp instanceof JDatePickerImpl) {
                    JDatePickerImpl datePicker = (JDatePickerImpl) comp;
                    Date selectedDate = (Date) datePicker.getModel().getValue();
                    if (selectedDate != null) {
                        datos.add(new SimpleDateFormat("yyyy-MM-dd").format(selectedDate));
                    } else {
                        datos.add(null);
                    }
                    tiposColumnas.add("date"); // Especifica DATE para columnas de fecha
                }
            }
        }

        // **Eliminar los campos predeterminados**
        // datos.add("Sección A, Estantería 3, Nivel 2"); // UBICACION_FISICA
        // tiposColumnas.add("varchar"); // Tipo de dato correspondiente

        // datos.add("5"); // CANTIDAD_TOTAL
        // tiposColumnas.add("int"); // Tipo de dato correspondiente

        // datos.add("3"); // CANTIDAD_DISPONIBLE
        // tiposColumnas.add("int"); // Tipo de dato correspondiente

        // datos.add("Bueno"); // ESTADO_DEL_DOCUMENTO
        // tiposColumnas.add("varchar"); // Tipo de dato correspondiente

        // datos.add("Programación, Java, Orientación a Objetos"); // PALABRAS_CLAVE
        // tiposColumnas.add("varchar"); // Tipo de dato correspondiente

        System.out.println("Datos a guardar: " + Arrays.toString(datos.toArray()));

        boolean exito = controlador.guardarDocumento(tipoDocumento, datos.toArray(new String[0]), tiposColumnas.toArray(new String[0]));
        JOptionPane.showMessageDialog(this, exito ? "Documento guardado exitosamente." : "Error al guardar el documento.");

        if (exito) {
            limpiarFormulario();
            actualizarFormularioYID();
        }
    }

    private void limpiarFormulario() {
        for (Component comp : panelDinamico.getComponents()) {
            if (comp instanceof JTextField && comp != idGeneradoField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JDatePickerImpl) {
                ((JDatePickerImpl) comp).getModel().setValue(null);
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
        SwingUtilities.invokeLater(() -> new GestionDocumentos().setVisible(true));
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
                        tipoDocumentoComboBox.addItem(nombreDocumento);
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

            // Crear la tabla en la base de datos con las columnas especificadas
            tipoDocumentoDAO.crearTablaParaDocumento(nombreDocumento, nombresColumnas);

            // Agregar el tipo de documento a la lista de documentos
            tipoDocumentoDAO.agregarTipoDocumento(nombreDocumento);
            tipoDocumentoComboBox.addItem(nombreDocumento);

            actualizarFormularioYID();
            JOptionPane.showMessageDialog(this, "Nuevo tipo de documento creado exitosamente.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de columnas inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el nuevo tipo de documento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
}
