package view;

import controller.AdoptanteControlador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelAdoptantes extends JPanel {

    private AdoptanteControlador adoptanteControlador;

    private JTextField campoCodigo, campoNombre, campoDpi, campoTelefono;
    private JTable tablaAdoptantes;
    private JScrollPane scrollTabla;
    private JLabel etiquetaMensaje;

    private static final String[] COLUMNAS = { "Código", "Nombre", "DPI", "Teléfono" };

    public PanelAdoptantes(AdoptanteControlador adoptanteControlador) {
        this.adoptanteControlador = adoptanteControlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirFormulario(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);

        refrescarTabla();
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrar / Editar Adoptante"));
        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(4, 4, 4, 4);
        r.fill = GridBagConstraints.HORIZONTAL;

        campoCodigo = new JTextField(8);
        campoNombre = new JTextField(14);
        campoDpi = new JTextField(14);
        campoTelefono = new JTextField(10);

        agregarCampo(panel, r, 0, 0, "Código (AD-000):", campoCodigo);
        agregarCampo(panel, r, 2, 0, "Nombre:", campoNombre);
        agregarCampo(panel, r, 0, 1, "DPI (13 dígitos):", campoDpi);
        agregarCampo(panel, r, 2, 1, "Teléfono (8 dígitos):", campoTelefono);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(this::alRegistrar);
        r.gridx = 0;
        r.gridy = 2;
        r.gridwidth = 2;
        panel.add(botonRegistrar, r);

        JButton botonEditar = new JButton("Editar seleccionado");
        botonEditar.addActionListener(this::alEditar);
        r.gridx = 2;
        r.gridy = 2;
        r.gridwidth = 2;
        panel.add(botonEditar, r);

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

    private JScrollPane construirTabla() {
        tablaAdoptantes = new JTable(new Object[0][0], COLUMNAS);
        tablaAdoptantes.setEnabled(false);

        // Al seleccionar una fila, se cargan sus datos al formulario para poder
        // editarlos
        tablaAdoptantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaAdoptantes.getSelectedRow() != -1) {
                cargarFilaEnFormulario(tablaAdoptantes.getSelectedRow());
            }
        });

        scrollTabla = new JScrollPane(tablaAdoptantes);
        return scrollTabla;
    }

    private void cargarFilaEnFormulario(int fila) {
        campoCodigo.setText((String) tablaAdoptantes.getValueAt(fila, 0));
        campoNombre.setText((String) tablaAdoptantes.getValueAt(fila, 1));
        campoDpi.setText((String) tablaAdoptantes.getValueAt(fila, 2));
        campoTelefono.setText((String) tablaAdoptantes.getValueAt(fila, 3));
        campoCodigo.setEditable(false); // evita que "editar" intente cambiar el código
        campoDpi.setEditable(false); // el DPI tampoco se puede editar, es identidad
    }

    private void alRegistrar(ActionEvent evento) {
        String codigo = campoCodigo.getText().trim();
        String nombre = campoNombre.getText().trim();
        String dpi = campoDpi.getText().trim();
        String telefono = campoTelefono.getText().trim();

        String resultado = adoptanteControlador.registrar(codigo, nombre, dpi, telefono);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
            limpiarFormulario();
            refrescarTabla();
        }
    }

    private void alEditar(ActionEvent evento) {
        String codigo = campoCodigo.getText().trim();
        if (codigo.isEmpty()) {
            etiquetaMensaje.setText("Seleccione un adoptante de la tabla primero");
            return;
        }

        String nuevoNombre = campoNombre.getText().trim();
        String nuevoTelefono = campoTelefono.getText().trim();

        String resultado = adoptanteControlador.editar(codigo, nuevoNombre, nuevoTelefono);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
            limpiarFormulario();
            refrescarTabla();
        }
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
        campoNombre.setText("");
        campoDpi.setText("");
        campoTelefono.setText("");
        campoCodigo.setEditable(true);
        campoDpi.setEditable(true);
    }

    private void refrescarTabla() {
        Object[][] datos = adoptanteControlador.listarTodos();
        tablaAdoptantes = new JTable(datos, COLUMNAS);
        tablaAdoptantes.setEnabled(false);
        tablaAdoptantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaAdoptantes.getSelectedRow() != -1) {
                cargarFilaEnFormulario(tablaAdoptantes.getSelectedRow());
            }
        });
        scrollTabla.setViewportView(tablaAdoptantes);
    }
}