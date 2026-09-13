package model;

public class Almacen {

    public static final int FILAS = 2; // fila 0 = Zona Perros, fila 1 = Zona Gatos
    public static final int COLUMNAS = 10; // 10 jaulas por zona

    public static Animal[] animales = new Animal[Animal.MAX];
    public static Adoptante[] adoptantes = new Adoptante[Adoptante.MAX];
    public static Solicitud[] solicitudes = new Solicitud[Solicitud.MAX];
    public static Rescate[] rescates = new Rescate[Rescate.MAX];
    public static Usuario[] usuarios = new Usuario[Usuario.MAX];
    public static Evento[] acciones = new Evento[Evento.MAX];
    public static Evento[] errores = new Evento[Evento.MAX];

    public static String[][] ubicaciones = new String[FILAS][COLUMNAS];

    public static int cantidadAnimales = 0;
    public static int cantidadAdoptantes = 0;
    public static int cantidadSolicitudes = 0;
    public static int cantidadRescates = 0;
    public static int cantidadUsuarios = 0;
    public static int cantidadAcciones = 0;
    public static int cantidadErrores = 0;
}