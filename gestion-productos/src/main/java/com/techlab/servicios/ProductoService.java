package com.techlab.servicios;

import com.techlab.productos.*;
import com.techlab.util.ArchivoUtil;

import java.time.LocalDate;
import java.util.*;

/**
 * Servicio para manejar productos y su persistencia.
 */
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();
    private int nextId = 1;
    private final String RUTA_PRODUCTOS = "data/productos.txt";

    public Producto agregarProducto(String nombre, double precio, int stock) {
        Producto p = new Producto(nextId++, nombre, precio, stock);
        productos.add(p);
        return p;
    }

    public Bebida agregarBebida(String nombre, double precio, int stock, double litros) {
        Bebida b = new Bebida(nextId++, nombre, precio, stock, litros);
        productos.add(b);
        return b;
    }

    public Comida agregarComida(String nombre, double precio, int stock, LocalDate fechaVencimiento) {
        Comida c = new Comida(nextId++, nombre, precio, stock, fechaVencimiento);
        productos.add(c);
        return c;
    }

    public List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    public Optional<Producto> buscarPorId(int id) {
        return productos.stream().filter(p -> p.getId() == id).findFirst();
    }

    public Optional<Producto> buscarPorNombre(String nombre) {
        return productos.stream().filter(p -> p.getNombre().equalsIgnoreCase(nombre)).findFirst();
    }

    public boolean actualizarProducto(int id, Double nuevoPrecio, Integer nuevoStock) {
        Optional<Producto> encontrado = buscarPorId(id);
        if (encontrado.isPresent()) {
            Producto p = encontrado.get();
            if (nuevoPrecio != null && nuevoPrecio >= 0) p.setPrecio(nuevoPrecio);
            if (nuevoStock != null && nuevoStock >= 0) p.setStock(nuevoStock);
            return true;
        }
        return false;
    }

    public boolean eliminarProducto(int id) {
        return productos.removeIf(p -> p.getId() == id);
    }

    public boolean estaVacio() {
        return productos.isEmpty();
    }

    // 🔹 Guardar productos en archivo
    public void guardarEnArchivo() {
        List<String> lineas = new ArrayList<>();

        for (Producto p : productos) {
            String tipo = p.getClass().getSimpleName();

            if (p instanceof Bebida b) {
                lineas.add(String.format("%s;%d;%s;%.2f;%d;%.1f",
                        tipo, p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), b.getLitros()));
            } else if (p instanceof Comida c) {
                lineas.add(String.format("%s;%d;%s;%.2f;%d;%s",
                        tipo, p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), c.getFechaVencimiento()));
            } else {
                lineas.add(String.format("%s;%d;%s;%.2f;%d",
                        tipo, p.getId(), p.getNombre(), p.getPrecio(), p.getStock()));
            }
        }

        ArchivoUtil.escribirLineas(RUTA_PRODUCTOS, lineas);
    }


    // 🔹 Cargar productos desde archivo
    public void cargarDesdeArchivo() {
        List<String> lineas = ArchivoUtil.leerLineas(RUTA_PRODUCTOS);

        for (String linea : lineas) {
            try {
                String[] datos = linea.split(";");
                String tipo = datos[0];
                int id = Integer.parseInt(datos[1]);
                String nombre = datos[2];

                // 🔹 Convertimos comas a puntos antes de parsear
                double precio = Double.parseDouble(datos[3].replace(",", "."));
                int stock = Integer.parseInt(datos[4]);

                switch (tipo) {
                    case "Bebida" -> {
                        double litros = Double.parseDouble(datos[5].replace(",", "."));
                        productos.add(new Bebida(id, nombre, precio, stock, litros));
                    }
                    case "Comida" -> {
                        LocalDate fecha = LocalDate.parse(datos[5]);
                        productos.add(new Comida(id, nombre, precio, stock, fecha));
                    }
                    default -> productos.add(new Producto(id, nombre, precio, stock));
                }

                // mantener actualizado el id máximo
                nextId = Math.max(nextId, id + 1);

            } catch (Exception e) {
                System.out.println("⚠️ Error al leer línea: " + linea + " (" + e.getMessage() + ")");
            }
        }
    }
}

