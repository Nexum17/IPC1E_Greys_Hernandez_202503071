package view;

import controller.*;
import java.awt.*;
import javax.swing.*;

public class MainVista extends JFrame {

    private AuthControlador authControlador;
    private UbicacionControlador ubicacionControlador;
    private AnimalControlador animalControlador;
    private AdoptanteControlador adoptanteControlador;
    private SolicitudControlador solicitudControlador;
    private RescateControlador rescateControlador;

    private CardLayout distribuidor;
    private JPanel panelCentral;

    private static final String VISTA_ANIMALES = "ANIMALES";
    private static final String VISTA_ADOPTANTES = "ADOPTANTES";
    private static final String VISTA_SOLICITUDES = "SOLICITUDES";
    private static final String VISTA_RESCATES = "RESCATES";
    private static final String VISTA_UBICACIONES = "UBICACIONES";
    private static final String VISTA_REPORTES = "REPORTES";

    public MainVista(AuthControlador authControlador) {
        this.authControlador = authControlador;
        construirControladores();
        configurarVentana();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                controller.ArchivoUtil.guardarAnimales();
                controller.ArchivoUtil.guardarAdoptantes();
                controller.ArchivoUtil.guardarSolicitudes();
                controller.ArchivoUtil.guardarRescates();
                controller.ArchivoUtil.guardarBitacoraAcciones();
                controller.ArchivoUtil.guardarBitacoraErrores();
            }
        });

        construirMenu();
        construirPanelCentral();
    }

    private void construirControladores() {
        ubicacionControlador = new UbicacionControlador();
        animalControlador = new AnimalControlador(ubicacionControlador);
        ubicacionControlador.setAnimalControlador(animalControlador);

        adoptanteControlador = new AdoptanteControlador();
        solicitudControlador = new SolicitudControlador(animalControlador, adoptanteControlador);
        rescateControlador = new RescateControlador(animalControlador);
    }

    private void configurarVentana() {
        setTitle("Centro de Rescate Animal: Gestión de Refugio y Adopciones");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void construirMenu() {
        JMenuBar barraMenu = new JMenuBar();

        JMenu menuModulos = new JMenu("Módulos");
        agregarOpcionMenu(menuModulos, "Animales", VISTA_ANIMALES);
        agregarOpcionMenu(menuModulos, "Adoptantes", VISTA_ADOPTANTES);
        agregarOpcionMenu(menuModulos, "Solicitudes", VISTA_SOLICITUDES);
        agregarOpcionMenu(menuModulos, "Rescates", VISTA_RESCATES);
        agregarOpcionMenu(menuModulos, "Ubicaciones", VISTA_UBICACIONES);
        agregarOpcionMenu(menuModulos, "Reportes", VISTA_REPORTES);

        JMenu menuSesion = new JMenu("Sesión");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        menuSesion.add(itemCerrarSesion);

        barraMenu.add(menuModulos);
        barraMenu.add(menuSesion);
        setJMenuBar(barraMenu);
    }

    private void agregarOpcionMenu(JMenu menu, String etiqueta, String nombreVista) {
        JMenuItem item = new JMenuItem(etiqueta);
        item.addActionListener(e -> distribuidor.show(panelCentral, nombreVista));
        menu.add(item);
    }

    private void construirPanelCentral() {
        distribuidor = new CardLayout();
        panelCentral = new JPanel(distribuidor);

        panelCentral.add(new PanelAnimales(animalControlador), VISTA_ANIMALES);
        panelCentral.add(new PanelAdoptantes(adoptanteControlador), VISTA_ADOPTANTES);
        panelCentral.add(new PanelSolicitudes(solicitudControlador), VISTA_SOLICITUDES);
        panelCentral.add(new PanelRescates(rescateControlador), VISTA_RESCATES);
        panelCentral.add(new PanelUbicaciones(ubicacionControlador), VISTA_UBICACIONES);
        panelCentral.add(new PanelReportes(), VISTA_REPORTES);

        add(panelCentral, BorderLayout.CENTER);
    }

    private void cerrarSesion() {
        authControlador.logout();
        dispose();

        LoginVista login = new LoginVista(authControlador);
        login.setVisible(true);
    }
}