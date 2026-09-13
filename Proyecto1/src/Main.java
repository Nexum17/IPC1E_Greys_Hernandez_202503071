import controller.ArchivoUtil;
import controller.AuthControlador;
import javax.swing.*;
import view.LoginVista;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            cargarDatosIniciales();

            AuthControlador authControlador = new AuthControlador();
            LoginVista login = new LoginVista(authControlador);
            login.setVisible(true);
        });
    }

    // Si existen archivos de una sesión anterior, los carga.
    // Si no existen (primera vez que corre el programa), arranca vacío sin error
    // visible.
    private static void cargarDatosIniciales() {
        ArchivoUtil.cargarAnimales();
        ArchivoUtil.cargarAdoptantes();
        ArchivoUtil.cargarSolicitudes();
        ArchivoUtil.cargarRescates();
    }
}