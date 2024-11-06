package formularios;

import javax.swing.*;
import java.awt.*;

public class FormularioLibro extends FormularioDocumento {

    public FormularioLibro() {
        super("Formulario de Libro");
        
        // Agrega campos específicos para libros
        panelCampos.add(new JLabel("Título:"));
        panelCampos.add(new JTextField());
        
        panelCampos.add(new JLabel("Autor:"));
        panelCampos.add(new JTextField());
        
        panelCampos.add(new JLabel("ISBN:"));
        panelCampos.add(new JTextField());

        // Puedes agregar más campos necesarios para el formulario de libro
    }

    @Override
    protected void guardarDocumento() {
        // Implementación para guardar un libro
        JOptionPane.showMessageDialog(this, "Libro guardado.");
        dispose();
    }
}
