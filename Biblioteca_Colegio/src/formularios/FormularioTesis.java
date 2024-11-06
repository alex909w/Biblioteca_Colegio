package formularios;

import javax.swing.*;

public class FormularioTesis extends FormularioDocumento {

    private JTextField txtTitulo, txtAutor, txtUniversidad, txtAnoPublicacion;

    public FormularioTesis() {
        super("Formulario para Tesis");

        txtTitulo = new JTextField();
        txtAutor = new JTextField();
        txtUniversidad = new JTextField();
        txtAnoPublicacion = new JTextField();

        panelCampos.add(new JLabel("Título:"));
        panelCampos.add(txtTitulo);

        panelCampos.add(new JLabel("Autor:"));
        panelCampos.add(txtAutor);

        panelCampos.add(new JLabel("Universidad:"));
        panelCampos.add(txtUniversidad);

        panelCampos.add(new JLabel("Año de Publicación:"));
        panelCampos.add(txtAnoPublicacion);
    }

    @Override
    protected void guardarDocumento() {
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String universidad = txtUniversidad.getText();
        String anoPublicacion = txtAnoPublicacion.getText();
        
        JOptionPane.showMessageDialog(this, "Tesis Guardada:\nTítulo: " + titulo + "\nAutor: " + autor + "\nUniversidad: " + universidad + "\nAño de Publicación: " + anoPublicacion);
        dispose();
    }
}
