package formularios;

import javax.swing.*;
import java.awt.*;

public class SeleccionarTipoDocumento extends JDialog {

    private JComboBox<String> comboBoxTipo;
    private JButton btnAceptar;
    private JButton btnCancelar;

    public SeleccionarTipoDocumento(JFrame parent) {
        super(parent, "Seleccionar Tipo de Documento", true); // Modalidad verdadera
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Asegura que el diálogo esté al frente del menú
        setAlwaysOnTop(true);  
        toFront();

        // Etiqueta y menú desplegable para tipos de documento
        JLabel lblTipo = new JLabel("Seleccione el tipo de documento:");
        comboBoxTipo = new JComboBox<>(new String[]{"Libro", "Revista", "CD", "Tesis", "Trabajo", "Otro"});
        
        JPanel panelCentro = new JPanel(new GridLayout(2, 1));
        panelCentro.add(lblTipo);
        panelCentro.add(comboBoxTipo);
        
        add(panelCentro, BorderLayout.CENTER);

        // Botones para aceptar o cancelar
        btnAceptar = new JButton("Aceptar");
        btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // Acción para aceptar y abrir el formulario correspondiente
        btnAceptar.addActionListener(e -> {
            String tipoSeleccionado = (String) comboBoxTipo.getSelectedItem();
            abrirFormulario(tipoSeleccionado);
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    private void abrirFormulario(String tipoDocumento) {
        switch (tipoDocumento) {
            case "Libro":
                new FormularioLibro().setVisible(true);
                break;
            case "Revista":
                new FormularioRevista().setVisible(true);
                break;
            case "CD":
                new FormularioCD().setVisible(true);
                break;
            case "Tesis":
                new FormularioTesis().setVisible(true);
                break;
            case "Trabajo":
                new FormularioTrabajo().setVisible(true);
                break;
            case "Otro":
                new FormularioConfigurarTabla((JFrame) getParent()).setVisible(true);
                break;
        }
    }

    public static void mostrarSeleccion(JFrame parent) {
        new SeleccionarTipoDocumento(parent).setVisible(true);
    }
}
