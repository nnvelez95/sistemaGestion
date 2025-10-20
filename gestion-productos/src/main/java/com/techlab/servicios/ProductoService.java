package com.techlab.servicios;

import com.techlab.productos.*;
import com.techlab.util.ArchivoUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();
    private int nextId = 1;
    private final String RUTA_PRODUCTOS = "data/productos.txt";

    // ==========================================================
    // 🔹 MÉTODOS PRINCIPALES DE AGREGADO
    // ==========================================================

    public Producto agregarProducto(String nombre, double precio, int stock) {
        Optional<Producto> existente = buscarPorNombreYPrecio(nombre, precio);

        if (existente.isPresent()) {
            Producto p = existente.get();
            p.setStock(p.getStock() + stock);
            System.out.println("ℹ️  Producto existente, se actualizó el stock.");
            return p;
        } else {
            Producto p = new Producto(nextId++, nombre, precio, stock);
            productos.add(p);
            return p;
        }
    }

    public Producto agregarBebida(String nombre, double precio, int stock, double litros) {
        Optional<Producto> existente = buscarPorNombreYPrecio(nombre, precio);

        if (existente.isPresent()) {
            Producto p = existente.get();
            p.setStock(p.getStock() + stock);
            System.out.println("ℹ️  Bebida existente, se actualizó el stock.");
            return p;
        } else {
            Producto b = new Bebida(nextId++, nombre, precio, stock, litros);
            productos.add(b);
            return b;
        }
    }

    public Producto agregarComida(String nombre, double precio, int stock, LocalDate fechaVencimiento) {
        Optional<Producto> existente = buscarPorNombreYPrecio(nombre, precio);

        if (existente.isPresent()) {
            Producto p = existente.get();
            p.setStock(p.getStock() + stock);
            System.out.println("ℹ️  Comida existente, se actualizó el stock.");
            return p;
        } else {
            Producto c = new Comida(nextId++, nombre, precio, stock, fechaVencimiento);
            productos.add(c);
            return c;
        }
    }

    // ==========================================================
    // 🔹 MÉTODOS AUXILIARES
    // ==========================================================

    /** Devuelve la lista actual de productos */
    public List<Producto> listarProductos() {
        return productos;
    }

    /** Indica si no hay productos cargados */
    public boolean estaVacio() {
        return productos.isEmpty();
    }

    public Optional<Producto> buscarPorId(int id) {
        return productos.stream().filter(p -> p.getId() == id).findFirst();
    }

    public Optional<Producto> buscarPorNombre(String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    public Optional<Producto> buscarPorNombreYPrecio(String nombre, double precio) {
        return productos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre) && p.getPrecio() == precio)
                .findFirst();
    }

    public boolean eliminarProducto(int id) {
        return productos.removeIf(p -> p.getId() == id);
    }

    public boolean actualizarProducto(int id, Double nuevoPrecio, Integer nuevoStock) {
        Optional<Producto> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            if (nuevoPrecio != null) p.setPrecio(nuevoPrecio);
            if (nuevoStock != null) p.setStock(nuevoStock);
            return true;
        }
        return false;
    }

    // ==========================================================
    // 🔹 PERSISTENCIA DE DATOS
    // ==========================================================

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

    public void cargarDesdeArchivo() {
        List<String> lineas = ArchivoUtil.leerLineas(RUTA_PRODUCTOS);
        productos.clear();

        for (String linea : lineas) {
            try {
                String[] datos = linea.split(";");
                String tipo = datos[0];
                int id = Integer.parseInt(datos[1]);
                String nombre = datos[2];
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
                nextId = Math.max(nextId, id + 1);
            } catch (Exception e) {
                System.out.println("⚠️ Error al leer línea: " + linea + " (" + e.getMessage() + ")");
            }
        }
    }
}
