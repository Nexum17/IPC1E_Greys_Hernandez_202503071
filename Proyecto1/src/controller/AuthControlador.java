package controller;

import model.Almacen;
import model.Usuario;

public class AuthControlador {

    private Usuario usuarioActual;
    private int intentosFallidos;
    private boolean bloqueado;

    public AuthControlador() {
        this.usuarioActual = null;
        this.intentosFallidos = 0;
        this.bloqueado = false;
        cargarUsuariosDePrueba();
    }

    // El enunciado pide usuarios "cargados desde memoria", no desde archivo
    private void cargarUsuariosDePrueba() {
        Almacen.usuarios[Almacen.cantidadUsuarios++] = new Usuario("admin1", "Refugio2026", Usuario.ROL_ADMIN);
        Almacen.usuarios[Almacen.cantidadUsuarios++] = new Usuario("auxiliar1", "Auxiliar26", Usuario.ROL_AUXILIAR);
    }

    public String login(String usuario, String contrasena) {
        if (bloqueado) {
            return "ERROR: Sesión bloqueada, reinicie la aplicación";
        }

        if (usuario == null || usuario.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            Bitacora.error(usuario == null ? "DESCONOCIDO" : usuario,
                    "AUTENTICACION", "VALIDACION", "Usuario o contraseña vacíos");
            return "ERROR: Usuario y contraseña son obligatorios";
        }

        Usuario encontrado = buscarPorNombre(usuario);

        if (encontrado == null || !encontrado.validarContrasena(contrasena)) {
            intentosFallidos++;
            Bitacora.error(usuario, "AUTENTICACION", "LOGIN_FALLIDO",
                    "Contraseña incorrecta (intento " + intentosFallidos + " de " + Usuario.INTENTOS_MAXIMOS + ")");

            if (intentosFallidos >= Usuario.INTENTOS_MAXIMOS) {
                bloqueado = true;
                return "ERROR: Sesión bloqueada, reinicie la aplicación";
            }
            return "ERROR: Usuario o contraseña incorrectos";
        }

        usuarioActual = encontrado;
        intentosFallidos = 0;
        Bitacora.setUsuarioActivo(encontrado.getUsuario());
        Bitacora.accion("AUTENTICACION", "LOGIN_OK", "Inicio de sesión correcto");
        return "OK: Bienvenido " + encontrado.getUsuario();
    }

    public void logout() {
        Bitacora.accion("AUTENTICACION", "LOGOUT", "Cierre de sesión");
        usuarioActual = null;
        Bitacora.setUsuarioActivo(null); // vuelve a "SISTEMA" hasta el próximo login
    }
    
    public boolean esAdminActual() {
        return usuarioActual != null && usuarioActual.esAdmin();
    }

    public String getUsuarioActualNombre() {
        return usuarioActual != null ? usuarioActual.getUsuario() : null;
    }

    private Usuario buscarPorNombre(String usuario) {
        for (int i = 0; i < Almacen.cantidadUsuarios; i++) {
            if (Almacen.usuarios[i].getUsuario().equalsIgnoreCase(usuario)) {
                return Almacen.usuarios[i];
            }
        }
        return null;
    }
}