package model;
 
public class Adoptante {

    public static final int MAX = 50;// : Define que como máximo caben 50 adoptantes en la memoria.

// Son los datos de la ficha (código, nombre, DPI, teléfono). Tienen candado
// (private) para que nadie de afuera los modifique directamente por error.
    private String codigo; // "AD-007"
    private String nombre; // solo letras y espacios
    private String dpi; // 13 dígitos, criterio de duplicado
    private String telefono; // 8 dígitos
// Es el método que llena la ficha por primera vez cuando creas un adoptante
// nuevo.

// Son los permisos de lectura. Sirven para pedirle al programa: "Dame el
// nombre, el código, etc.".
    public Adoptante(String codigo, String nombre, String dpi, String telefono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.dpi = dpi;
        this.telefono = telefono;
    }
// Son los permisos de edición. Permiten cambiar solo el nombre y el teléfono
// (el código y el DPI no se pueden cambiar porque son fijos).
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

    // Línea para persistencia: codigo|nombre|dpi|telefono, Traductor 1. Une los
    // datos con barras (|) para guardarlos dentro del archivo de texto .txt.
    public String aLineaArchivo() {
        return codigo + "|" + nombre + "|" + dpi + "|" + telefono;
    }

    // Fila para JTable, sin DefaultTableModel, acomoda los datos en una lista para
    // mostralos en las casillas de la tabla en pantalla.
    public Object[] aFilaTabla() {
        return new Object[] { codigo, nombre, dpi, telefono };
    }
}
// Es la clase del modelo que define la estructura de un adoptante. Guarda sus
// datos de forma segura con variables privadas, da acceso a ellos con funciones
// get y set, y tiene dos traductores: uno para guardar el texto en el bloc de
// notas y otro para mostrar la información en la tabla de la pantalla.