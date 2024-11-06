package formularios;

import javax.swing.*;

public class FormularioCD extends FormularioDocumento {

    private JTextField txtTitulo, txtAutor, txtDuracion;

    public FormularioCD() {
        super("Formulario para CD");

        txtTitulo = new JTextField();
        txtAutor = new JTextField();
        txtDuracion = new JTextField();

        panelCampos.add(new JLabel("Título:"));
        panelCampos.add(txtTitulo);

        panelCampos.add(new JLabel("Autor:"));
        panelCampos.add(txtAutor);

        panelCampos.add(new JLabel("Duración (minutos):"));
        panelCampos.add(txtDuracion);
    }

    @Override
    protected void guardarDocumento() {
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String duracion = txtDuracion.getText();
        
        JOptionPane.showMessageDialog(this, "CD Guardado:\nTítulo: " + titulo + "\nAutor: " + autor + "\nDuración: " + duracion + " minutos");
        dispose();
    }
}
