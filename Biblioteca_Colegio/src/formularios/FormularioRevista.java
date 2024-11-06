package formularios;

import javax.swing.*;

public class FormularioRevista extends FormularioDocumento {

    private JTextField txtTitulo, txtAutor, txtNumeroEdicion, txtFechaPublicacion;

    public FormularioRevista() {
        super("Formulario para Revista");

        txtTitulo = new JTextField();
        txtAutor = new JTextField();
        txtNumeroEdicion = new JTextField();
        txtFechaPublicacion = new JTextField();

        panelCampos.add(new JLabel("Título:"));
        panelCampos.add(txtTitulo);

        panelCampos.add(new JLabel("Autor:"));
        panelCampos.add(txtAutor);

        panelCampos.add(new JLabel("Número de Edición:"));
        panelCampos.add(txtNumeroEdicion);

        panelCampos.add(new JLabel("Fecha de Publicación:"));
        panelCampos.add(txtFechaPublicacion);
    }

    @Override
    protected void guardarDocumento() {
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String numeroEdicion = txtNumeroEdicion.getText();
        String fechaPublicacion = txtFechaPublicacion.getText();
        
        JOptionPane.showMessageDialog(this, "Revista Guardada:\nTítulo: " + titulo + "\nAutor: " + autor + "\nNúmero de Edición: " + numeroEdicion + "\nFecha de Publicación: " + fechaPublicacion);
        dispose();
    }
}
