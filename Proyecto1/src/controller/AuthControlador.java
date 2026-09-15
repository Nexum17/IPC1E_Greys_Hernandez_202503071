package controller;

import model.Almacen;
import model.Usuario;

public class AuthControlador {//constructor

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
    //verifica que no falte nada antes de validar credenciales
    public String login(String usuario, String contrasena) {
        if (bloqueado) {
            return "ERROR: Sesión bloqueada, reinicie la aplicación";
        }
//validar que no haya espacios vacíos
        if (usuario == null || usuario.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            Bitacora.error(usuario == null ? "DESCONOCIDO" : usuario,
                    "AUTENTICACION", "VALIDACION", "Usuario o contraseña vacíos");
            return "ERROR: Usuario y contraseña son obligatorios";
        }

        Usuario encontrado = buscarPorNombre(usuario);
        //user existe? para dejarlo entrar. si la contraseña no es valida
        if (encontrado == null || !encontrado.validarContrasena (contrasena)) {
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
        intentosFallidos = 0;//reinicia el contador de errores a 0
        Bitacora.setUsuarioActivo(encontrado.getUsuario());// El usuario guardado aquí es admin1
        Bitacora.accion("AUTENTICACION", "LOGIN_OK", "Inicio de sesión correcto");
        return "OK: Bienvenido " + encontrado.getUsuario();
    }

    public void logout() {
        Bitacora.accion("AUTENTICACION", "LOGOUT", "Cierre de sesión");
        usuarioActual = null;
        Bitacora.setUsuarioActivo(null); //cambia user*un valor nuero y vuelve a "SISTEMA" hasta el próximo login
    }
    
    // Comprueba si hay una sesión activa y si el usuario logueado tiene permisos de Administrador.
    public boolean esAdminActual() {
        return usuarioActual != null && usuarioActual.esAdmin();
    }
    //quien esta dentro?
    public String getUsuarioActualNombre() {
        return usuarioActual != null ? usuarioActual.getUsuario() : null;
    }
    //metodo usable solo en  el controladore auth, protege logica interna de busqyeda, Método privado que realiza una búsqueda lineal en Almacen para retornar el objeto Usuario según su nombre.
    private Usuario buscarPorNombre(String usuario) {
        for (int i = 0; i < Almacen.cantidadUsuarios; i++) {
            if (Almacen.usuarios[i].getUsuario().equalsIgnoreCase(usuario)) {
                return Almacen.usuarios[i];
            }
        }
        return null;
    }
}