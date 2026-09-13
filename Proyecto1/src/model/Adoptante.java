package model;
 
public class Adoptante {

    public static final int MAX = 50;

    private String codigo; // "AD-007"
    private String nombre; // solo letras y espacios
    private String dpi; // 13 dígitos, criterio de duplicado
    private String telefono; // 8 dígitos

    public Adoptante(String codigo, String nombre, String dpi, String telefono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.dpi = dpi;
        this.telefono = telefono;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDpi() {
        return dpi;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Línea para persistencia: codigo|nombre|dpi|telefono
    public String aLineaArchivo() {
        return codigo + "|" + nombre + "|" + dpi + "|" + telefono;
    }

    // Fila para JTable, sin DefaultTableModel
    public Object[] aFilaTabla() {
        return new Object[] { codigo, nombre, dpi, telefono };
    }
}
