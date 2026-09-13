package view;

import controller.AuthControlador;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class LoginVista extends JFrame {

    private AuthControlador authControlador;

    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JButton botonIngresar;
    private JLabel etiquetaMensaje;

    public LoginVista(AuthControlador authControlador) {
        this.authControlador = authControlador;
        configurarVentana();
        construirComponentes();
    }

    private void configurarVentana() {
        setTitle("Centro de Rescate Animal - Inicio de Sesión");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centrada en pantalla
        setResizable(false);
    }

    private void construirComponentes() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.insets = new Insets(8, 8, 8, 8);
        restricciones.fill = GridBagConstraints.HORIZONTAL;

        JLabel etiquetaUsuario = new JLabel("Usuario:");
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        panel.add(etiquetaUsuario, restricciones);

        campoUsuario = new JTextField(15);
        restricciones.gridx = 1;
        restricciones.gridy = 0;
        panel.add(campoUsuario, restricciones);

        JLabel etiquetaContrasena = new JLabel("Contraseña:");
        restricciones.gridx = 0;
        restricciones.gridy = 1;
        panel.add(etiquetaContrasena, restricciones);

        campoContrasena = new JPasswordField(15);
        restricciones.gridx = 1;
        restricciones.gridy = 1;
        panel.add(campoContrasena, restricciones);

        botonIngresar = new JButton("Ingresar");
        restricciones.gridx = 0;
        restricciones.gridy = 2;
        restricciones.gridwidth = 2;
        panel.add(botonIngresar, restricciones);

        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setForeground(Color.RED);
        restricciones.gridx = 0;
        restricciones.gridy = 3;
        restricciones.gridwidth = 2;
        panel.add(etiquetaMensaje, restricciones);

        botonIngresar.addActionListener(this::alPresionarIngresar);
        campoContrasena.addActionListener(this::alPresionarIngresar); // Enter también funciona

        add(panel);
    }

    private void alPresionarIngresar(ActionEvent evento) {
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());

        String resultado = authControlador.login(usuario, contrasena);

        if (resultado.startsWith("OK")) {
            abrirVentanaPrincipal();
        } else {
            etiquetaMensaje.setText(resultado.replace("ERROR: ", ""));
            campoContrasena.setText("");

            if (resultado.contains("bloqueada")) {
                botonIngresar.setEnabled(false);
                campoUsuario.setEnabled(false);
                campoContrasena.setEnabled(false);
            }
        }
    }

    private void abrirVentanaPrincipal() {
        dispose();
        MainVista main = new MainVista(authControlador);
        main.setVisible(true);
    }
}