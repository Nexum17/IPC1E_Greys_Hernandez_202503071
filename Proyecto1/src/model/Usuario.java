package model;

public class Usuario {

    public static final int MAX = 10;

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_AUXILIAR = "AUXILIAR";

    public static final int INTENTOS_MAXIMOS = 3;

    private String usuario; // "admin1", 4-15 caracteres alfanuméricos
    private String contrasena; // mínimo 6 caracteres
    private String rol; // ADMIN o AUXILIAR

    public Usuario(String usuario, String contrasena, String rol) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }

    public boolean esAdmin() {
        return ROL_ADMIN.equals(rol);
    }

    public boolean validarContrasena(String intento) {
        return contrasena != null && contrasena.equals(intento);
    }
}