package formularios;

import javax.swing.*;
import java.awt.*;

public class FormularioDinamico extends FormularioDocumento {

    public FormularioDinamico(String tipoDocumento) {
        super("Formulario para " + tipoDocumento);
        
        // Configuración para que la ventana esté siempre al frente
        setAlwaysOnTop(true);  
        toFront();

        // Campos genéricos para un documento personalizado
        panelCampos.add(new JLabel("Nombre:"));
        panelCampos.add(new JTextField());
        
        panelCampos.add(new JLabel("Descripción:"));
        panelCampos.add(new JTextField());

        // Puedes agregar más campos necesarios para el documento personalizado
    }

    @Override
    protected void guardarDocumento() {
        // Implementación para guardar un documento personalizado
        JOptionPane.showMessageDialog(this, "Documento personalizado guardado.");
        dispose();
    }

    public static void mostrarFormulario(String tipoDocumento) {
        new FormularioDinamico(tipoDocumento).setVisible(true);
    }
}
