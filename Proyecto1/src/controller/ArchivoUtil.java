package controller;

import java.io.*;
import model.Adoptante;
import model.Almacen;
import model.Animal;
import model.Rescate;
import model.Solicitud;

public class ArchivoUtil {
    // Constantes privadas y estáticas que centralizan las rutas inmutables de los
    // archivos .txt para evitar valores fijos en el código.
    private static final String CARPETA = "datos";
    private static final String ARCHIVO_ANIMALES = CARPETA + "/animales.txt";
    private static final String ARCHIVO_ADOPTANTES = CARPETA + "/adoptantes.txt";
    private static final String ARCHIVO_SOLICITUDES = CARPETA + "/solicitudes.txt";
    private static final String ARCHIVO_RESCATES = CARPETA + "/rescates.txt";
    private static final String ARCHIVO_ACCIONES = CARPETA + "/bitacora_acciones.txt";
    private static final String ARCHIVO_ERRORES = CARPETA + "/bitacora_errores.txt";

    //ESCRITURA,// Métodos públicos que llaman al guardado genérico pasando la ruta del archivo, la cantidad de registros y una expresión lambda para obtener cada línea.
    public static String guardarAnimales() {
        return guardarGenerico(ARCHIVO_ANIMALES, Almacen.cantidadAnimales,
                i -> Almacen.animales[i].aLineaArchivo());
    }

    public static String guardarAdoptantes() {
        // "Manda a guardar a los adoptantes en el archivo .txt enviándole tres datos al
        // método genérico: dónde guardarlo (ARCHIVO_ADOPTANTES), cuántos hay que
        // guardar (cantidadAdoptantes), y cómo convertir cada adoptante a texto
        // (.aLineaArchivo()/texto plano separado por |)"
        return guardarGenerico(ARCHIVO_ADOPTANTES, Almacen.cantidadAdoptantes,
                i -> Almacen.adoptantes[i].aLineaArchivo());
    }

    public static String guardarSolicitudes() {
        return guardarGenerico(ARCHIVO_SOLICITUDES, Almacen.cantidadSolicitudes,
                i -> Almacen.solicitudes[i].aLineaArchivo());
    }

    public static String guardarRescates() {
        return guardarGenerico(ARCHIVO_RESCATES, Almacen.cantidadRescates,
                i -> Almacen.rescates[i].aLineaArchivo());
    }

    public static String guardarBitacoraAcciones() {
        return guardarGenerico(ARCHIVO_ACCIONES, Almacen.cantidadAcciones,
                i -> Almacen.acciones[i].aLineaArchivo());
    }

    // Interfaz funcional que define el contrato abstracto para obtener el texto de cada registro según su índice.
    public static String guardarBitacoraErrores() {// Método central de escritura: asegura la carpeta, abre un BufferedWriter con try-with-resources y recorre el arreglo escribiendo cada línea en el archivo
        return guardarGenerico(ARCHIVO_ERRORES, Almacen.cantidadErrores,
                i -> Almacen.errores[i].aLineaArchivo());
    }

    // Interfaz funcional mínima: "dame la línea de texto del índice i"
    private interface ProveedorLinea {
        String obtener(int indice);
    }

    private static String guardarGenerico(String ruta, int cantidad, ProveedorLinea proveedor) {
        crearCarpetaSiNoExiste();
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta))) {
            for (int i = 0; i < cantidad; i++) {
                escritor.write(proveedor.obtener(i));
                escritor.newLine();
            }
            return "OK: Archivo guardado (" + ruta + ")";
        } catch (IOException e) {
            return "ERROR: No se pudo guardar el archivo (" + ruta + ")";
        }
    }

    // ---------- LECTURA ----------

    public static String cargarAnimales() {
        String[] lineas = leerLineas(ARCHIVO_ANIMALES);
        if (lineas == null)
            return "ERROR: No se encontró el archivo de animales";

        Almacen.cantidadAnimales = 0;
        for (String linea : lineas) {
            String[] p = linea.split("\\|");
            if (p.length < 7)
                continue; // línea corrupta, se ignora

            Animal a = new Animal(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[6]);
            a.setEstadoAdopcion(p[5]);

            if (Almacen.cantidadAnimales < Animal.MAX) {
                Almacen.animales[Almacen.cantidadAnimales] = a;
                Almacen.cantidadAnimales++;
            }
        }
        return "OK: " + Almacen.cantidadAnimales + " animales cargados";
    }

    public static String cargarAdoptantes() {
        String[] lineas = leerLineas(ARCHIVO_ADOPTANTES);
        if (lineas == null)
            return "ERROR: No se encontró el archivo de adoptantes";

        Almacen.cantidadAdoptantes = 0;
        for (String linea : lineas) {
            String[] p = linea.split("\\|");
            if (p.length < 4)
                continue;

            Adoptante ad = new Adoptante(p[0], p[1], p[2], p[3]);
            if (Almacen.cantidadAdoptantes < Adoptante.MAX) {
                Almacen.adoptantes[Almacen.cantidadAdoptantes] = ad;
                Almacen.cantidadAdoptantes++;
            }
        }
        return "OK: " + Almacen.cantidadAdoptantes + " adoptantes cargados";
    }

    public static String cargarSolicitudes() {
        String[] lineas = leerLineas(ARCHIVO_SOLICITUDES);
        if (lineas == null)
            return "ERROR: No se encontró el archivo de solicitudes";

        Almacen.cantidadSolicitudes = 0;
        for (String linea : lineas) {
            String[] p = linea.split("\\|");
            if (p.length < 5)
                continue;

            Solicitud s = new Solicitud(p[0], p[1], p[2], p[3]);
            s.setEstado(p[4]);

            if (Almacen.cantidadSolicitudes < Solicitud.MAX) {
                Almacen.solicitudes[Almacen.cantidadSolicitudes] = s;
                Almacen.cantidadSolicitudes++;
            }
        }
        return "OK: " + Almacen.cantidadSolicitudes + " solicitudes cargadas";
    }

    public static String cargarRescates() {
        String[] lineas = leerLineas(ARCHIVO_RESCATES);
        if (lineas == null)
            return "ERROR: No se encontró el archivo de rescates";

        Almacen.cantidadRescates = 0;
        for (String linea : lineas) {
            String[] p = linea.split("\\|");
            if (p.length < 5)
                continue;

            Rescate r = new Rescate(p[0], p[1], p[3]);
            r.setEstado(p[2]);
            r.setCodigoAnimalVinculado(p[4]);

            if (Almacen.cantidadRescates < Rescate.MAX) {
                Almacen.rescates[Almacen.cantidadRescates] = r;
                Almacen.cantidadRescates++;
            }
        }
        return "OK: " + Almacen.cantidadRescates + " rescates cargados";
    }

    private static String[] leerLineas(String ruta) {
        File archivo = new File(ruta);
        if (!archivo.exists())
            return null;

        int cantidad = contarLineasValidas(archivo);
        if (cantidad == 0)
            return new String[0];

        String[] lineas = new String[cantidad];

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int i = 0;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas[i] = linea;
                    i++;
                }
            }
            return lineas;
        } catch (IOException e) {
            return null;
        }
    }
    
    // Primera pasada: solo cuenta cuántas líneas no vacías tiene el archivo
    private static int contarLineasValidas(File archivo) {
        int contador = 0;
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty())
                    contador++;
            }
        } catch (IOException e) {
            return 0;
        }
        return contador;
    }

    private static void crearCarpetaSiNoExiste() {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists())
            carpeta.mkdirs();
    }
}