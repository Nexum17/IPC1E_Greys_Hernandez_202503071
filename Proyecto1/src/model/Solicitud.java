package model;
 
public class Solicitud {

    public static final int MAX = 150;

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_APROBADA = "APROBADA";
    public static final String ESTADO_RECHAZADA = "RECHAZADA";
    public static final String ESTADO_COMPLETADA = "COMPLETADA";

    private String codigo; // "S-021"
    private String codigoAnimal; // "A-014", debe existir en Animal
    private String codigoAdoptante; // "AD-007", debe existir en Adoptante
    private String fecha; // "18/08/2026"
    private String estado;

    public Solicitud(String codigo, String codigoAnimal, String codigoAdoptante, String fecha) {
        this.codigo = codigo;
        this.codigoAnimal = codigoAnimal;
        this.codigoAdoptante = codigoAdoptante;
        this.fecha = fecha;
        this.estado = ESTADO_PENDIENTE; // toda solicitud nace pendiente
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoAnimal() {
        return codigoAnimal;
    }

    public String getCodigoAdoptante() {
        return codigoAdoptante;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean estaPendiente() {
        return ESTADO_PENDIENTE.equals(estado);
    }

    // Línea para persistencia: codigo|codigoAnimal|codigoAdoptante|fecha|estado
    public String aLineaArchivo() {
        return codigo + "|" + codigoAnimal + "|" + codigoAdoptante + "|" + fecha + "|" + estado;
    }

    // Fila para JTable, sin DefaultTableModel
    public Object[] aFilaTabla() {
        return new Object[] { codigo, codigoAnimal, codigoAdoptante, fecha, estado };
    }
}
