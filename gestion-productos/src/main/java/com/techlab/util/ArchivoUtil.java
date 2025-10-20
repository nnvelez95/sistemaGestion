package com.techlab.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para leer y escribir archivos de texto.
 */
public class ArchivoUtil {

    public static void escribirLineas(String ruta, List<String> lineas) {
        try {
            File file = new File(ruta);
            file.getParentFile().mkdirs(); // crea carpeta /data si no existe
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            for (String linea : lineas) {
                writer.println(linea);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("⚠️ Error al escribir archivo: " + e.getMessage());
        }
    }

    public static List<String> leerLineas(String ruta) {
        List<String> lineas = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(ruta))) return lineas; // archivo vacío
            BufferedReader br = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("⚠️ Error al leer archivo: " + e.getMessage());
        }
        return lineas;
    }
}
