package view;

import controller.SolicitudControlador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelSolicitudes extends JPanel {

    private SolicitudControlador solicitudControlador;

    private JTextField campoCodigo, campoCodigoAnimal, campoCodigoAdoptante, campoFecha;
    private JTable tablaPendientes, tablaHistorial;
    private JScrollPane scrollPendientes, scrollHistorial;
    private JLabel etiquetaMensaje;

    private static final String[] COLUMNAS = { "Código", "Animal", "Adoptante", "Fecha", "Estado" };

    public PanelSolicitudes(SolicitudControlador solicitudControlador) {
        this.solicitudControlador = solicitudControlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirFormulario(), BorderLayout.NORTH);
        add(construirPestanas(), BorderLayout.CENTER);

        refrescarTablas();
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrar Solicitud"));
        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(4, 4, 4, 4);
        r.fill = GridBagConstraints.HORIZONTAL;

        campoCodigo = new JTextField(8);
        campoCodigoAnimal = new JTextField(8);
        campoCodigoAdoptante = new JTextField(8);
        campoFecha = new JTextField(10);

        agregarCampo(panel, r, 0, 0, "Código (S-000):", campoCodigo);
        agregarCampo(panel, r, 2, 0, "Código Animal:", campoCodigoAnimal);
        agregarCampo(panel, r, 0, 1, "Código Adoptante:", campoCodigoAdoptante);
        agregarCampo(panel, r, 2, 1, "Fecha (dd/mm/aaaa):", campoFecha);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(this::alRegistrar);
        r.gridx = 0;
        r.gridy = 2;
        r.gridwidth = 2;
        panel.add(botonRegistrar, r);

        JButton botonAprobar = new JButton("Aprobar seleccionada");
        botonAprobar.addActionListener(this::alAprobar);
        r.gridx = 2;
        r.gridy = 2;
        r.gridwidth = 1;
        panel.add(botonAprobar, r);

        JButton botonRechazar = new JButton("Rechazar seleccionada");
        botonRechazar.addActionListener(this::alRechazar);
        r.gridx = 3;
        r.gridy = 2;
        r.gridwidth = 1;
        panel.add(botonRechazar, r);

        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setForeground(Color.RED);
        r.gridx = 0;
        r.gridy = 3;
        r.gridwidth = 4;
        panel.add(etiquetaMensaje, r);

        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints r, int x, int y,
            String etiqueta, JComponent componente) {
        r.gridx = x;
        r.gridy = y;
        r.gridwidth = 1;
        panel.add(new JLabel(etiqueta), r);
        r.gridx = x + 1;
        r.gridy = y;
        panel.add(componente, r);
    }

    // Las tablas deben permitir seleccionar filas (para "Aprobar"/"Rechazar"),
    // pero no editar celdas directamente; setEnabled(false) bloquearía también
    // la selección con clic.
    private JTable crearTablaNoEditable(Object[][] datos) {
        JTable tabla = new JTable(datos, COLUMNAS) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        return tabla;
    }

    private JTabbedPane construirPestanas() {
        tablaPendientes = crearTablaNoEditable(new Object[0][0]);
        scrollPendientes = new JScrollPane(tablaPendientes);

        tablaHistorial = crearTablaNoEditable(new Object[0][0]);
        scrollHistorial = new JScrollPane(tablaHistorial);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Pendientes", scrollPendientes);
        pestanas.addTab("Historial completo", scrollHistorial);
        return pestanas;
    }

    private void alRegistrar(ActionEvent evento) {
        String codigo = campoCodigo.getText().trim();
        String codigoAnimal = campoCodigoAnimal.getText().trim();
        String codigoAdoptante = campoCodigoAdoptante.getText().trim();
        String fecha = campoFecha.getText().trim();

        String resultado = solicitudControlador.registrar(codigo, codigoAnimal, codigoAdoptante, fecha);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
            limpiarFormulario();
            refrescarTablas();
        }
    }

    private void alAprobar(ActionEvent evento) {
        String codigo = obtenerCodigoSeleccionado();
        if (codigo == null) {
            etiquetaMensaje.setText("Seleccione una solicitud pendiente primero");
            return;
        }

        String resultado = solicitudControlador.aprobar(codigo);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
            refrescarTablas();
        }
    }

    private void alRechazar(ActionEvent evento) {
        String codigo = obtenerCodigoSeleccionado();
        if (codigo == null) {
            etiquetaMensaje.setText("Seleccione una solicitud pendiente primero");
            return;
        }

        String resultado = solicitudControlador.rechazar(codigo);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
            refrescarTablas();
        }
    }

    // Aprobar/Rechazar solo tienen sentido sobre la pestaña de Pendientes
    private String obtenerCodigoSeleccionado() {
        int fila = tablaPendientes.getSelectedRow();
        if (fila == -1)
            return null;
        return (String) tablaPendientes.getValueAt(fila, 0);
    }

    private void mostrarResultado(String resultado) {
        if (resultado.startsWith("OK")) {
            etiquetaMensaje.setForeground(new Color(0, 130, 0));
        } else {
            etiquetaMensaje.setForeground(Color.RED);
        }
        etiquetaMensaje.setText(resultado.replaceFirst("^(OK|ERROR): ", ""));
    }

    private void limpiarFormulario() {
        campoCodigo.setText("");
        campoCodigoAnimal.setText("");
        campoCodigoAdoptante.setText("");
        campoFecha.setText("");
    }

    private void refrescarTablas() {
        Object[][] datosPendientes = solicitudControlador.listarPendientes();
        tablaPendientes = crearTablaNoEditable(datosPendientes);
        scrollPendientes.setViewportView(tablaPendientes);

        Object[][] datosHistorial = solicitudControlador.listarHistorial();
        tablaHistorial = crearTablaNoEditable(datosHistorial);
        scrollHistorial.setViewportView(tablaHistorial);
    }
}