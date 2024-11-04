package paneles;

import javax.swing.*;
import java.awt.*;
import acciones.administracionUsuarios;
import login.BibliotecaLogin;

public class MenuAdministrador extends JFrame {

    public MenuAdministrador() {
        setTitle("Menú Administrador - Biblioteca Colegio Amigos De Don Bosco");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de navegación lateral
        JPanel panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new GridLayout(6, 1, 10, 10));
        panelNavegacion.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Botones de navegación
        JButton btnGestionUsuarios = new JButton("Gestión de Usuarios");
        JButton btnGestionDocumentos = new JButton("Gestión de Documentos");
        JButton btnConfiguracion = new JButton("Configuración");
        JButton btnBuscarLibros = new JButton("Buscar Libros");
        JButton btnPrestamos = new JButton("Préstamos");
        JButton btnSalir = new JButton("Salir");

        // Añadir botones al panel de navegación
        panelNavegacion.add(btnGestionUsuarios);
        panelNavegacion.add(btnGestionDocumentos);
        panelNavegacion.add(btnConfiguracion);
        panelNavegacion.add(btnBuscarLibros);
        panelNavegacion.add(btnPrestamos);
        panelNavegacion.add(btnSalir);

        btnGestionUsuarios.addActionListener(e -> {
    new administracionUsuarios().setVisible(true);
    dispose();
});

        // Acción del botón "Salir"
        btnSalir.addActionListener(e -> {
            new BibliotecaLogin().setVisible(true);
            dispose();
        });

        // Panel principal de contenido
        JPanel panelContenido = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Bienvenido Administrador", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        panelContenido.add(lblBienvenida, BorderLayout.CENTER);

        // Añadir paneles al frame
        add(panelNavegacion, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAdministrador().setVisible(true));
    }
}
