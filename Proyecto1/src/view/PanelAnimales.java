package view;

import controller.AnimalControlador;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class PanelAnimales extends JPanel {

    private AnimalControlador animalControlador;

    private JTextField campoCodigo, campoNombre, campoEdad, campoEspecie, campoEstadoClinico;
    private JTable tablaAnimales;
    private JScrollPane scrollTabla;
    private JLabel etiquetaMensaje;

    private static final String[] COLUMNAS = { "Código", "Nombre", "Especie", "Edad", "Estado Clínico",
            "Estado Adopción", "Ingreso" };

    public PanelAnimales(AnimalControlador animalControlador) {
        this.animalControlador = animalControlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirFormulario(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);

        refrescarTabla();
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrar Animal"));
        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(4, 4, 4, 4);
        r.fill = GridBagConstraints.HORIZONTAL;

        campoCodigo = new JTextField(8);
        campoNombre = new JTextField(10);
        campoEdad = new JTextField(4);
        campoEspecie = new JTextField(8);
        campoEstadoClinico = new JTextField(12);

        agregarCampo(panel, r, 0, 0, "Código (A-000):", campoCodigo);
        agregarCampo(panel, r, 2, 0, "Nombre:", campoNombre);
        agregarCampo(panel, r, 0, 1, "Especie (Perro/Gato):", campoEspecie);
        agregarCampo(panel, r, 2, 1, "Edad:", campoEdad);
        agregarCampo(panel, r, 0, 2, "Estado clínico (EN_OBSERVACION/EN_TRATAMIENTO/APTO):", campoEstadoClinico);

        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(this::alRegistrar);
        r.gridx = 2;
        r.gridy = 2;
        r.gridwidth = 2;
        panel.add(botonRegistrar, r);

        JButton botonEliminar = new JButton("Eliminar (baja lógica)");
        botonEliminar.addActionListener(this::alEliminar);
        r.gridx = 0;
        r.gridy = 3;
        r.gridwidth = 2;
        panel.add(botonEliminar, r);

        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setForeground(Color.RED);
        r.gridx = 0;
        r.gridy = 4;
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
        tablaAnimales = crearTablaNoEditable(new Object[0][0]);
        scrollTabla = new JScrollPane(tablaAnimales);
        return scrollTabla;
    }

    // La tabla debe permitir seleccionar filas (para "Eliminar"), pero no editar
    // celdas directamente; por eso NO se usa setEnabled(false), que bloquea todo,
    // incluyendo el clic de selección. En su lugar se sobreescribe isCellEditable.
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

    private void alRegistrar(ActionEvent evento) {
        String codigo = campoCodigo.getText().trim();
        String nombre = campoNombre.getText().trim();
        String especie = campoEspecie.getText().trim();
        String edad = campoEdad.getText().trim();
        String estadoClinico = campoEstadoClinico.getText().trim();

        String resultado = animalControlador.registrar(codigo, nombre, especie, edad, estadoClinico);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
            limpiarFormulario();
            refrescarTabla();
        }
    }

    private void alEliminar(ActionEvent evento) {
        int filaSeleccionada = tablaAnimales.getSelectedRow();
        if (filaSeleccionada == -1) {
            etiquetaMensaje.setText("Seleccione un animal de la tabla primero");
            return;
        }

        String codigo = (String) tablaAnimales.getValueAt(filaSeleccionada, 0);
        String resultado = animalControlador.eliminar(codigo);
        mostrarResultado(resultado);

        if (resultado.startsWith("OK")) {
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
        campoEdad.setText("");
        campoEspecie.setText("");
        campoEstadoClinico.setText("");
    }

    private void refrescarTabla() {
        Object[][] datos = animalControlador.listarActivos();
        tablaAnimales = crearTablaNoEditable(datos);
        scrollTabla.setViewportView(tablaAnimales);
    }
    
}