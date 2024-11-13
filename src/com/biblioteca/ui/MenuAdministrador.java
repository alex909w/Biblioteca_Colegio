package com.biblioteca.ui;

import com.biblioteca.acciones.GestionProductos.AgregarFormulario;
import com.biblioteca.acciones.GestionProductos.EditarFormulario;
import com.biblioteca.acciones.Usuarios.AdministracionUsuarios;
import com.biblioteca.acciones.AgregarInventario;
import com.biblioteca.acciones.ActualizarInventario;
import com.biblioteca.acciones.Prestamos.RegistrarPrestamoPanel;
import com.biblioteca.acciones.Prestamos.ConsultarPrestamosPanel;
import com.biblioteca.acciones.Prestamos.ConfigurarPrestamosPanel;
import com.biblioteca.acciones.Prestamos.RegistrarDevolucionPanel;
import com.biblioteca.acciones.Mora.CalcularMoraPanel;
import com.biblioteca.acciones.Administracion.GenerarReportesPanel;
import com.biblioteca.acciones.Administracion.EditarEliminarRegistrosPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MenuAdministrador extends JFrame {
    private final Color COLOR_PRIMARIO = new Color(51, 102, 153);
    private final Color FONDO_LATERAL = new Color(248, 249, 250);
    private final Color COLOR_HOVER = new Color(233, 236, 239);
    private final Font FUENTE_PRINCIPAL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);

    private final JPanel panelContenido;
    private JPanel subMenuGestionProductos;
    private JPanel subMenuGestionInventario;
    private JPanel subMenuPrestamos; // Submenú para préstamos
    private JPanel subMenuMora; // Submenú para mora
    private JPanel subMenuAdministracion; // Submenú para administración
    private JPanel subMenuGestionUsuarios; // Submenú para gestión de usuarios

    private boolean subMenuProductosVisible = false;
    private boolean subMenuInventarioVisible = false;
    private boolean subMenuPrestamosVisible = false; // Control de visibilidad del submenú de préstamos
    private boolean subMenuMoraVisible = false; // Control de visibilidad del submenú de mora
    private boolean subMenuAdministracionVisible = false; // Control de visibilidad del submenú de administración
    private boolean subMenuGestionUsuariosVisible = false; // Control de visibilidad del submenú de gestión de usuarios

    private JTable tablaInventarios; // Tabla de inventarios

    public MenuAdministrador() {
        setTitle("Sistema Bibliotecario - Panel de Administración");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelNavegacion(), BorderLayout.WEST);

        // Panel de contenido original
        panelContenido = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Bienvenido, Administrador", SwingConstants.CENTER);
        lblBienvenida.setFont(FUENTE_TITULO);
        panelContenido.add(lblBienvenida, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_PRIMARIO);
        panelSuperior.setPreferredSize(new Dimension(0, 60));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("Biblioteca Colegio Amigos De Don Bosco");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        return panelSuperior;
    }

    private JPanel crearPanelNavegacion() {
        JPanel panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new BoxLayout(panelNavegacion, BoxLayout.Y_AXIS));
        panelNavegacion.setBackground(FONDO_LATERAL);
        panelNavegacion.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 0, 1, new Color(218, 220, 224)),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        panelNavegacion.setPreferredSize(new Dimension(250, 0));

        // Botones principales
        JButton btnGestionUsuarios = crearBotonMenu("Gestión de Usuarios", "icon_usuarios.png");
        JButton btnGestionProductos = crearBotonMenu("Gestión de Productos", "icon_productos.png");
        JButton btnGestionInventario = crearBotonMenu("Gestión de Inventario", "icon_inventario.png");
        JButton btnPrestamos = crearBotonPrestamos(); // Botón de Préstamos
        JButton btnMora = crearBotonMora(); // Botón de Mora
        JButton btnAdministracion = crearBotonAdministracion(); // Botón de Administración
        JButton btnSalir = crearBotonMenu("Salir", "icon_salir.png");

        // Agregar ActionListeners para mostrar submenús
        btnGestionProductos.addActionListener(e -> toggleSubMenuProductos());
        btnGestionInventario.addActionListener(e -> toggleSubMenuInventario());
        btnGestionUsuarios.addActionListener(e -> toggleSubMenuGestionUsuarios());
        btnPrestamos.addActionListener(e -> toggleSubMenuPrestamos());
        btnMora.addActionListener(e -> toggleSubMenuMora());
        btnAdministracion.addActionListener(e -> toggleSubMenuAdministracion());
        btnSalir.addActionListener(e -> System.exit(0));

        // Agregar botones y submenús al panel de navegación
        panelNavegacion.add(btnGestionUsuarios);
        panelNavegacion.add(crearSubMenuGestionUsuarios());
        panelNavegacion.add(Box.createVerticalStrut(10));

        panelNavegacion.add(btnGestionProductos);
        panelNavegacion.add(crearSubMenuProductos());
        panelNavegacion.add(Box.createVerticalStrut(10));

        panelNavegacion.add(btnGestionInventario);
        panelNavegacion.add(crearSubMenuInventario());
        panelNavegacion.add(Box.createVerticalStrut(10));

        panelNavegacion.add(btnPrestamos);
        panelNavegacion.add(crearSubMenuPrestamos()); // El submenú de préstamos
        panelNavegacion.add(Box.createVerticalStrut(10));

        panelNavegacion.add(btnMora);
        panelNavegacion.add(crearSubMenuMora()); // El submenú de mora
        panelNavegacion.add(Box.createVerticalStrut(10));

        panelNavegacion.add(btnAdministracion);
        panelNavegacion.add(crearSubMenuAdministracion()); // El submenú de administración
        panelNavegacion.add(Box.createVerticalStrut(10));

        panelNavegacion.add(btnSalir);

        return panelNavegacion;
    }

    private JButton crearBotonMenu(String texto, String icono) {
        JButton boton = new JButton(texto);
        boton.setIcon(new ImageIcon(icono));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setMaximumSize(new Dimension(220, 40));
        boton.setBackground(FONDO_LATERAL);
        boton.setFont(FUENTE_PRINCIPAL);
        boton.setForeground(new Color(33, 37, 41));
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        boton.setFocusPainted(false);

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(FONDO_LATERAL);
            }
        });

        return boton;
    }

    // Crear el botón de "Préstamos"
    private JButton crearBotonPrestamos() {
        JButton btnPrestamos = new JButton("Préstamos");
        btnPrestamos.setIcon(new ImageIcon("icon_prestamos.png"));
        btnPrestamos.setHorizontalAlignment(SwingConstants.LEFT);
        btnPrestamos.setMaximumSize(new Dimension(220, 40));
        btnPrestamos.setBackground(FONDO_LATERAL);
        btnPrestamos.setFont(FUENTE_PRINCIPAL);
        btnPrestamos.setForeground(new Color(33, 37, 41));
        btnPrestamos.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        btnPrestamos.setFocusPainted(false);

        btnPrestamos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnPrestamos.setBackground(COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnPrestamos.setBackground(FONDO_LATERAL);
            }
        });

        btnPrestamos.addActionListener(e -> toggleSubMenuPrestamos()); // Alternar el submenú de préstamos

        return btnPrestamos;
    }

    // Crear el botón de "Mora"
    private JButton crearBotonMora() {
        JButton btnMora = new JButton("Mora");
        btnMora.setIcon(new ImageIcon("icon_mora.png"));
        btnMora.setHorizontalAlignment(SwingConstants.LEFT);
        btnMora.setMaximumSize(new Dimension(220, 40));
        btnMora.setBackground(FONDO_LATERAL);
        btnMora.setFont(FUENTE_PRINCIPAL);
        btnMora.setForeground(new Color(33, 37, 41));
        btnMora.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        btnMora.setFocusPainted(false);

        btnMora.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnMora.setBackground(COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnMora.setBackground(FONDO_LATERAL);
            }
        });

        btnMora.addActionListener(e -> toggleSubMenuMora()); // Alternar el submenú de mora

        return btnMora;
    }

    // Crear el botón de "Administración"
    private JButton crearBotonAdministracion() {
        JButton btnAdministracion = new JButton("Administración");
        btnAdministracion.setIcon(new ImageIcon("icon_administracion.png"));
        btnAdministracion.setHorizontalAlignment(SwingConstants.LEFT);
        btnAdministracion.setMaximumSize(new Dimension(220, 40));
        btnAdministracion.setBackground(FONDO_LATERAL);
        btnAdministracion.setFont(FUENTE_PRINCIPAL);
        btnAdministracion.setForeground(new Color(33, 37, 41));
        btnAdministracion.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        btnAdministracion.setFocusPainted(false);

        btnAdministracion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnAdministracion.setBackground(COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnAdministracion.setBackground(FONDO_LATERAL);
            }
        });

        btnAdministracion.addActionListener(e -> toggleSubMenuAdministracion()); // Alternar el submenú de administración

        return btnAdministracion;
    }

    // Submenú de Préstamos
    private JPanel crearSubMenuPrestamos() {
        subMenuPrestamos = new JPanel();
        subMenuPrestamos.setLayout(new BoxLayout(subMenuPrestamos, BoxLayout.Y_AXIS));
        subMenuPrestamos.setBackground(FONDO_LATERAL);
        subMenuPrestamos.setVisible(false); // Submenú inicialmente oculto

        JButton registrarPrestamo = new JButton("Realizar Préstamo");
        registrarPrestamo.setFont(FUENTE_PRINCIPAL);
        registrarPrestamo.setHorizontalAlignment(SwingConstants.LEFT);
        registrarPrestamo.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        registrarPrestamo.setBackground(FONDO_LATERAL);
        registrarPrestamo.setMaximumSize(new Dimension(220, 30));
        registrarPrestamo.addActionListener(e -> mostrarContenido(new RegistrarPrestamoPanel())); // Reemplaza con el panel adecuado

        JButton configurarPrestamos = new JButton("Configurar Préstamos");
        configurarPrestamos.setFont(FUENTE_PRINCIPAL);
        configurarPrestamos.setHorizontalAlignment(SwingConstants.LEFT);
        configurarPrestamos.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        configurarPrestamos.setBackground(FONDO_LATERAL);
        configurarPrestamos.setMaximumSize(new Dimension(220, 30));
        configurarPrestamos.addActionListener(e -> mostrarContenido(new ConfigurarPrestamosPanel())); // Reemplaza con el panel adecuado

        JButton registrarDevolucion = new JButton("Registrar Devolución");
        registrarDevolucion.setFont(FUENTE_PRINCIPAL);
        registrarDevolucion.setHorizontalAlignment(SwingConstants.LEFT);
        registrarDevolucion.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        registrarDevolucion.setBackground(FONDO_LATERAL);
        registrarDevolucion.setMaximumSize(new Dimension(220, 30));
        registrarDevolucion.addActionListener(e -> mostrarContenido(new RegistrarDevolucionPanel())); // Reemplaza con el panel adecuado

        subMenuPrestamos.add(registrarPrestamo);
        subMenuPrestamos.add(configurarPrestamos);
        subMenuPrestamos.add(registrarDevolucion);

        return subMenuPrestamos;
    }

    // Submenú de Mora
    private JPanel crearSubMenuMora() {
        subMenuMora = new JPanel();
        subMenuMora.setLayout(new BoxLayout(subMenuMora, BoxLayout.Y_AXIS));
        subMenuMora.setBackground(FONDO_LATERAL);
        subMenuMora.setVisible(false); // Submenú inicialmente oculto

        JButton calcularMora = new JButton("Calcular Mora");
        calcularMora.setFont(FUENTE_PRINCIPAL);
        calcularMora.setHorizontalAlignment(SwingConstants.LEFT);
        calcularMora.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        calcularMora.setBackground(FONDO_LATERAL);
        calcularMora.setMaximumSize(new Dimension(220, 30));
        calcularMora.addActionListener(e -> mostrarContenido(new CalcularMoraPanel())); // Reemplaza con el panel adecuado

        subMenuMora.add(calcularMora);

        return subMenuMora;
    }

    // Submenú de Administración
    private JPanel crearSubMenuAdministracion() {
        subMenuAdministracion = new JPanel();
        subMenuAdministracion.setLayout(new BoxLayout(subMenuAdministracion, BoxLayout.Y_AXIS));
        subMenuAdministracion.setBackground(FONDO_LATERAL);
        subMenuAdministracion.setVisible(false); // Submenú inicialmente oculto

        JButton generarReportes = new JButton("Generar Reportes");
        generarReportes.setFont(FUENTE_PRINCIPAL);
        generarReportes.setHorizontalAlignment(SwingConstants.LEFT);
        generarReportes.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        generarReportes.setBackground(FONDO_LATERAL);
        generarReportes.setMaximumSize(new Dimension(220, 30));
        generarReportes.addActionListener(e -> mostrarContenido(new GenerarReportesPanel())); // Reemplaza con el panel adecuado

        JButton editarEliminarRegistros = new JButton("Editar y Eliminar Registros");
        editarEliminarRegistros.setFont(FUENTE_PRINCIPAL);
        editarEliminarRegistros.setHorizontalAlignment(SwingConstants.LEFT);
        editarEliminarRegistros.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        editarEliminarRegistros.setBackground(FONDO_LATERAL);
        editarEliminarRegistros.setMaximumSize(new Dimension(220, 30));
        editarEliminarRegistros.addActionListener(e -> mostrarContenido(new EditarEliminarRegistrosPanel())); // Reemplaza con el panel adecuado

        subMenuAdministracion.add(generarReportes);
        subMenuAdministracion.add(editarEliminarRegistros);

        return subMenuAdministracion;
    }

    // Submenú de Gestión de Usuarios
    private JPanel crearSubMenuGestionUsuarios() {
        subMenuGestionUsuarios = new JPanel();
        subMenuGestionUsuarios.setLayout(new BoxLayout(subMenuGestionUsuarios, BoxLayout.Y_AXIS));
        subMenuGestionUsuarios.setBackground(FONDO_LATERAL);
        subMenuGestionUsuarios.setVisible(false); // Submenú inicialmente oculto

        JButton agregarUsuario = new JButton("Agregar Usuario");
        agregarUsuario.setFont(FUENTE_PRINCIPAL);
        agregarUsuario.setHorizontalAlignment(SwingConstants.LEFT);
        agregarUsuario.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        agregarUsuario.setBackground(FONDO_LATERAL);
        agregarUsuario.setMaximumSize(new Dimension(220, 30));
        agregarUsuario.addActionListener(e -> mostrarContenido(new AdministracionUsuarios())); // Reemplaza con el panel adecuado

        JButton restablecerContrasena = new JButton("Restablecer Contraseña");
        restablecerContrasena.setFont(FUENTE_PRINCIPAL);
        restablecerContrasena.setHorizontalAlignment(SwingConstants.LEFT);
        restablecerContrasena.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        restablecerContrasena.setBackground(FONDO_LATERAL);
        restablecerContrasena.setMaximumSize(new Dimension(220, 30));
        restablecerContrasena.addActionListener(e -> mostrarContenido(new JPanel())); // Reemplaza con el panel adecuado

        subMenuGestionUsuarios.add(agregarUsuario);
        subMenuGestionUsuarios.add(restablecerContrasena);

        return subMenuGestionUsuarios;
    }

    // Alternar la visibilidad del submenú de "Préstamos"
    private void toggleSubMenuPrestamos() {
        subMenuPrestamosVisible = !subMenuPrestamosVisible;
        subMenuPrestamos.setVisible(subMenuPrestamosVisible);
        revalidate();
        repaint();
    }

    // Alternar la visibilidad del submenú de "Mora"
    private void toggleSubMenuMora() {
        subMenuMoraVisible = !subMenuMoraVisible;
        subMenuMora.setVisible(subMenuMoraVisible);
        revalidate();
        repaint();
    }

    // Alternar la visibilidad del submenú de "Administración"
    private void toggleSubMenuAdministracion() {
        subMenuAdministracionVisible = !subMenuAdministracionVisible;
        subMenuAdministracion.setVisible(subMenuAdministracionVisible);
        revalidate();
        repaint();
    }

    // Alternar la visibilidad del submenú de "Gestión de Usuarios"
    private void toggleSubMenuGestionUsuarios() {
        subMenuGestionUsuariosVisible = !subMenuGestionUsuariosVisible;
        subMenuGestionUsuarios.setVisible(subMenuGestionUsuariosVisible);
        revalidate();
        repaint();
    }

    private JPanel crearSubMenuProductos() {
        subMenuGestionProductos = new JPanel();
        subMenuGestionProductos.setLayout(new BoxLayout(subMenuGestionProductos, BoxLayout.Y_AXIS));
        subMenuGestionProductos.setBackground(FONDO_LATERAL);
        subMenuGestionProductos.setVisible(false);

        JButton agregarFormulario = new JButton("Agregar Formulario");
        agregarFormulario.setFont(FUENTE_PRINCIPAL);
        agregarFormulario.setHorizontalAlignment(SwingConstants.LEFT);
        agregarFormulario.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        agregarFormulario.setBackground(FONDO_LATERAL);
        agregarFormulario.setMaximumSize(new Dimension(220, 30));
        agregarFormulario.addActionListener(e -> mostrarContenido(new AgregarFormulario()));

        JButton editarFormulario = new JButton("Editar Formulario");
        editarFormulario.setFont(FUENTE_PRINCIPAL);
        editarFormulario.setHorizontalAlignment(SwingConstants.LEFT);
        editarFormulario.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        editarFormulario.setBackground(FONDO_LATERAL);
        editarFormulario.setMaximumSize(new Dimension(220, 30));
        editarFormulario.addActionListener(e -> mostrarContenido(new EditarFormulario()));

        subMenuGestionProductos.add(agregarFormulario);
        subMenuGestionProductos.add(editarFormulario);

        return subMenuGestionProductos;
    }

    private JPanel crearSubMenuInventario() {
        subMenuGestionInventario = new JPanel();
        subMenuGestionInventario.setLayout(new BoxLayout(subMenuGestionInventario, BoxLayout.Y_AXIS));
        subMenuGestionInventario.setBackground(FONDO_LATERAL);
        subMenuGestionInventario.setVisible(false);

        JButton agregarProducto = new JButton("Agregar Producto");
        agregarProducto.setFont(FUENTE_PRINCIPAL);
        agregarProducto.setHorizontalAlignment(SwingConstants.LEFT);
        agregarProducto.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        agregarProducto.setBackground(FONDO_LATERAL);
        agregarProducto.setMaximumSize(new Dimension(220, 30));
        agregarProducto.addActionListener(e -> mostrarContenido(new AgregarInventario()));

        JButton actualizarEntradaSalida = new JButton("Actualizar / Eliminar");
        actualizarEntradaSalida.setFont(FUENTE_PRINCIPAL);
        actualizarEntradaSalida.setHorizontalAlignment(SwingConstants.LEFT);
        actualizarEntradaSalida.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        actualizarEntradaSalida.setBackground(FONDO_LATERAL);
        actualizarEntradaSalida.setMaximumSize(new Dimension(220, 30));
        actualizarEntradaSalida.addActionListener(e -> mostrarContenido(new ActualizarInventario()));

        JButton buscarProducto = new JButton("Buscar Producto");
        buscarProducto.setFont(FUENTE_PRINCIPAL);
        buscarProducto.setHorizontalAlignment(SwingConstants.LEFT);
        buscarProducto.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 5));
        buscarProducto.setBackground(FONDO_LATERAL);
        buscarProducto.setMaximumSize(new Dimension(220, 30));
        buscarProducto.addActionListener(e -> mostrarContenido(new JPanel())); // Reemplaza con el panel adecuado

        subMenuGestionInventario.add(agregarProducto);
        subMenuGestionInventario.add(buscarProducto);
        subMenuGestionInventario.add(actualizarEntradaSalida);

        return subMenuGestionInventario;
    }

    private void mostrarContenido(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAdministrador().setVisible(true));
    }

    private void toggleSubMenuProductos() {
    // Alterna el estado de visibilidad
    subMenuProductosVisible = !subMenuProductosVisible;
    
    // Establece la visibilidad del submenú de Gestión de Productos
    subMenuGestionProductos.setVisible(subMenuProductosVisible);
    
    // Actualiza el layout para reflejar los cambios
    revalidate();
    repaint();
}

    private void toggleSubMenuInventario() {
    // Alterna el estado de visibilidad
    subMenuInventarioVisible = !subMenuInventarioVisible;
    
    // Establece la visibilidad del submenú de Gestión de Inventario
    subMenuGestionInventario.setVisible(subMenuInventarioVisible);
    
    // Actualiza el layout para reflejar los cambios
    revalidate();
    repaint();
}


}
