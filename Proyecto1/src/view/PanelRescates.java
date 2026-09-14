package view;

import controller.RescateControlador;
import model.Rescate;
import model.Animal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelRescates extends JPanel {

    private RescateControlador rescateControlador;

    private JTextField campoCodigo, campoFecha;
    private JComboBox<String> comboPrioridad;
    private JTable tablaRescates;
    private JScrollPane scrollTabla;
    private JLabel etiquetaMensaje;

    private static final String[] COLUMNAS = { "Código", "Prioridad", "Estado", "Fecha Reporte", "Animal Vinculado" };

    public PanelRescates(RescateControlador rescateControlador) {
        this.rescateControlador = rescateControlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirFormulario(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);

        refrescarTabla();
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrar Rescate"));
        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(4, 4, 4, 4);
        r.fill = GridBagConstraints.HORIZONTAL;

        campoCodigo = new JTextField(8);
        comboPrioridad = new JComboBox<>(new String[] {
                Rescate.PRIORIDAD_ALTA, Rescate.PRIORIDAD_MEDIA, Rescate.PRIORIDAD_BAJA });
        campoFecha = new JTextField(10);

        agregarCampo(panel, r, 0, 0, "Código (R-000):", campoCodigo);
        agregarCampo(panel, r, 2, 0, "Prioridad:", comboPrioridad);
        agregarCampo(panel, r, 0, 1, "Fecha (dd/mm/aaaa):", campoFecha);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(this::alRegistrar);
        r.gridx = 2;
        r.gridy = 1;
        r.gridwidth = 1;
        panel.add(botonRegistrar, r);

        JButton botonAtender = new JButton("Atender seleccionado");
        botonAtender.addActionListener(this::alAtender);
        r.gridx = 0;
        r.gridy = 2;
        r.gridwidth = 2;
        panel.add(botonAtender, r);

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

    // La tabla debe permitir seleccionar filas (para "Atender"), pero no editar
    // celdas directamente; setEnabled(false) bloquearía también la selección.
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

    private JScrollPane construirTabla() {
        tablaRescates = crearTablaNoEditable(new Object[0][0]);
        scrollTabla = new JScrollPane(tablaRescates);
        return scrollTabla;
    }

    private void alRegistrar(ActionEvent evento) {
        String codigo = campoCodigo.getText().trim();
        String prioridad = (String) comboPrioridad.getSelectedItem();
        String fecha = campoFecha.getText().trim();

        String resultado = rescateControlador.registrar(codigo, prioridad, fecha);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
            limpiarFormulario();
            refrescarTabla();
        }
    }

    private void alAtender(ActionEvent evento) {
        int fila = tablaRescates.getSelectedRow();
        if (fila == -1) {
            etiquetaMensaje.setText("Seleccione un rescate pendiente de la tabla primero");
            return;
        }

        String codigoRescate = (String) tablaRescates.getValueAt(fila, 0);
        String estadoActual = (String) tablaRescates.getValueAt(fila, 2);

        if (!Rescate.ESTADO_PENDIENTE.equals(estadoActual)) {
            etiquetaMensaje.setText("Ese rescate ya fue atendido");
            return;
        }

        abrirDialogoAtender(codigoRescate);
    }

    // Diálogo separado: decide si se vincula un animal existente o se genera uno
    // nuevo
    private void abrirDialogoAtender(String codigoRescate) {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Atender Rescate " + codigoRescate, true);
        dialogo.setSize(380, 320);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new GridBagLayout());
        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(6, 6, 6, 6);
        r.fill = GridBagConstraints.HORIZONTAL;

        JRadioButton radioExistente = new JRadioButton("Vincular animal existente");
        JRadioButton radioNuevo = new JRadioButton("Generar animal nuevo", true);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioExistente);
        grupo.add(radioNuevo);

        JTextField campoCodigoExistente = new JTextField(10);
        campoCodigoExistente.setEnabled(false);

        JTextField campoNombreNuevo = new JTextField(10);
        JComboBox<String> comboEspecie = new JComboBox<>(
                new String[] { Animal.ESPECIE_PERRO, Animal.ESPECIE_GATO });
        JTextField campoEdadNuevo = new JTextField(5);

        radioExistente.addActionListener(e -> {
            campoCodigoExistente.setEnabled(true);
            campoNombreNuevo.setEnabled(false);
            comboEspecie.setEnabled(false);
            campoEdadNuevo.setEnabled(false);
        });
        radioNuevo.addActionListener(e -> {
            campoCodigoExistente.setEnabled(false);
            campoNombreNuevo.setEnabled(true);
            comboEspecie.setEnabled(true);
            campoEdadNuevo.setEnabled(true);
        });

        r.gridx = 0;
        r.gridy = 0;
        r.gridwidth = 2;
        dialogo.add(radioNuevo, r);
        r.gridy = 1;
        dialogo.add(radioExistente, r);

        r.gridwidth = 1;
        r.gridx = 0;
        r.gridy = 2;
        dialogo.add(new JLabel("Código animal:"), r);
        r.gridx = 1;
        dialogo.add(campoCodigoExistente, r);

        r.gridx = 0;
        r.gridy = 3;
        dialogo.add(new JLabel("Nombre (si es nuevo):"), r);
        r.gridx = 1;
        dialogo.add(campoNombreNuevo, r);

        r.gridx = 0;
        r.gridy = 4;
        dialogo.add(new JLabel("Especie (si es nuevo):"), r);
        r.gridx = 1;
        dialogo.add(comboEspecie, r);

        r.gridx = 0;
        r.gridy = 5;
        dialogo.add(new JLabel("Edad (si es nuevo):"), r);
        r.gridx = 1;
        dialogo.add(campoEdadNuevo, r);

        JButton botonConfirmar = new JButton("Confirmar");
        r.gridx = 0;
        r.gridy = 6;
        r.gridwidth = 2;
        dialogo.add(botonConfirmar, r);

        JLabel etiquetaDialogo = new JLabel(" ");
        etiquetaDialogo.setForeground(Color.RED);
        r.gridy = 7;
        dialogo.add(etiquetaDialogo, r);

        botonConfirmar.addActionListener(e -> {
            String codigoExistente = radioExistente.isSelected()
                    ? campoCodigoExistente.getText().trim()
                    : "";
            String nombreNuevo = campoNombreNuevo.getText().trim();
            String especie = (String) comboEspecie.getSelectedItem();
            String edad = campoEdadNuevo.getText().trim();

            String resultado = rescateControlador.atender(codigoRescate, codigoExistente, nombreNuevo, especie, edad);

            if (resultado.startsWith("OK")) {
                mostrarResultado(resultado);
                refrescarTabla();
                dialogo.dispose();
            } else {
                etiquetaDialogo.setText(resultado.replaceFirst("^ERROR: ", ""));
            }
        });

        dialogo.setVisible(true);
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
        campoFecha.setText("");
        comboPrioridad.setSelectedIndex(0);
    }

    private void refrescarTabla() {
        Object[][] datos = rescateControlador.listarActivosPorPrioridad();
        tablaRescates = crearTablaNoEditable(datos);
        scrollTabla.setViewportView(tablaRescates);
    }
}