package model;
 
public class Evento {// . Sirve para registrar qué pasó, quién lo hizo y cuándo ocurrió dentro del sistema.

    public static final int MAX = 500;// Define que en la bitácora caben hasta 500 registros de eventos
// (Variables de auditoría): Guardan la información clave de cada acción o error:
    private String fechaHora; // "18/08/2026 09:15"
    private String usuario; // "admin1"
    private String modulo; // "ANIMALES", "SOLICITUDES", "UBICACIONES", etc.
    private String tipoEvento; // "ALTA", "LOGIN_FALLIDO", "DUPLICADO", "APROBAR"...
    private String descripcion; // en Errores, incluye el motivo del rechazo

    public Evento(String fechaHora, String usuario, String modulo,
            String tipoEvento, String descripcion) {
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.modulo = modulo;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
    }
// Permisos de solo lectura. Nota que no hay setters (set...) porque una vez que un evento de bitácora se registra, nadie debe modificarlo
    public String getFechaHora() {
        return fechaHora;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getModulo() {
        return modulo;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Línea para persistencia: fechaHora|usuario|modulo|tipoEvento|descripcion
    public String aLineaArchivo() {
        return fechaHora + "|" + usuario + "|" + modulo + "|" + tipoEvento + "|" + descripcion;
    }

    // Fila para JTable, sin DefaultTableModel, para poblar el JTable.
    public Object[] aFilaTabla() {
        return new Object[] { fechaHora, usuario, modulo, tipoEvento, descripcion };
    }
}