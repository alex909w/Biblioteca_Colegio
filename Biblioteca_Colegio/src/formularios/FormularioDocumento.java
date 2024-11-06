package formularios;

import javax.swing.*;
import java.awt.*;

public abstract class FormularioDocumento extends JFrame {

    protected JPanel panelCampos;
    protected JButton btnGuardar;
    protected JButton btnCancelar;

    public FormularioDocumento(String titulo) {
        setTitle(titulo);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        panelCampos = new JPanel(new GridLayout(0, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panelCampos, BorderLayout.CENTER);

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarDocumento());
    }

    // Método abstracto para que cada formulario implemente su propio guardado
    protected abstract void guardarDocumento();
}
