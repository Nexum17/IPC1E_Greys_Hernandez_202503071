package model;

public class Almacen {

    public static final int FILAS = 2; // fila 0 = Zona Perros, fila 1 = Zona Gatos
    public static final int COLUMNAS = 10; // 10 jaulas por zona
// Instancia los arreglos donde se guardan las entidades del sistema (animales,
// adoptantes, solicitudes, etc.) limitados por su respectiva constante .MAX.
    public static Animal[] animales = new Animal[Animal.MAX];
    public static Adoptante[] adoptantes = new Adoptante[Adoptante.MAX];
    public static Solicitud[] solicitudes = new Solicitud[Solicitud.MAX];
    public static Rescate[] rescates = new Rescate[Rescate.MAX];
    public static Usuario[] usuarios = new Usuario[Usuario.MAX];
    public static Evento[] acciones = new Evento[Evento.MAX];
    public static Evento[] errores = new Evento[Evento.MAX];

    public static String[][] ubicaciones = new String[FILAS][COLUMNAS];
// Variables int iniciadas en 0 que sirven como punteros para saber exactamente
// en qué posición del arreglo guardar el siguiente elemento y cuántos registros
// válidos existen actualmente en memoria.
    public static int cantidadAnimales = 0;
    public static int cantidadAdoptantes = 0;
    public static int cantidadSolicitudes = 0;
    public static int cantidadRescates = 0;
    public static int cantidadUsuarios = 0;
    public static int cantidadAcciones = 0;
    public static int cantidadErrores = 0;
}
// "La clase Almacen es nuestra base de datos centralizada en memoria RAM.
// Define la matriz física de jaulas (ubicaciones), crea los arreglos estáticos
// donde guardamos las entidades y mantiene los contadores numéricos para saber
// exactamente cuántos registros hay almacenados de cada tipo