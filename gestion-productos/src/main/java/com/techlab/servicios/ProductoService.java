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
    private static final String RUTA_PRODUCTOS = "data/productos.txt";

    // ==========================================================
    // 🔹 MÉTODOS PRINCIPALES DE AGREGADO
    // ==========================================================

    public Producto agregarProducto(String nombre, double precio, int stock) {
        return agregarGenerico(new Producto(nextId, nombre, precio, stock), stock);
    }

    public Producto agregarBebida(String nombre, double precio, int stock, double litros) {
        return agregarGenerico(new Bebida(nextId, nombre, precio, stock, litros), stock);
    }

    public Producto agregarComida(String nombre, double precio, int stock, LocalDate fechaVencimiento) {
        return agregarGenerico(new Comida(nextId, nombre, precio, stock, fechaVencimiento), stock);
    }

    // 🔸 Método interno reutilizado por todos los agregadores
    private Producto agregarGenerico(Producto nuevo, int stock) {
        Optional<Producto> existente = buscarPorNombreYPrecio(nuevo.getNombre(), nuevo.getPrecio());
        if (existente.isPresent()) {
            Producto p = existente.get();
            p.setStock(p.getStock() + stock);
            System.out.println("ℹ️  Producto existente, se actualizó el stock.");
            return p;
        } else {
            nuevo.setId(nextId++);
            productos.add(nuevo);
            return nuevo;
        }
    }

    // ==========================================================
    // 🔹 MÉTODOS AUXILIARES
    // ==========================================================

    public List<Producto> listarProductos() {
        return productos;
    }

    public boolean estaVacio() {
        return productos.isEmpty();
    }

    public Optional<Producto> buscarPorId(int id) {
        for (Producto p : productos) {
            if (p.getId() == id) return Optional.of(p);
        }
        return Optional.empty();
    }

    public Optional<Producto> buscarPorNombre(String nombre) {
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombre)) return Optional.of(p);
        }
        return Optional.empty();
    }

    public Optional<Producto> buscarPorNombreYPrecio(String nombre, double precio) {
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombre) && p.getPrecio() == precio)
                return Optional.of(p);
        }
        return Optional.empty();
    }

    public boolean eliminarProducto(int id) {
        return productos.removeIf(p -> p.getId() == id);
    }

    /**
     * Actualiza los datos de un producto existente (nombre, precio y/o stock).
     * Solo actualiza los campos no nulos o válidos.
     *
     * @param id            ID del producto a modificar
     * @param nuevoNombre   Nuevo nombre (o null si no se cambia)
     * @param nuevoPrecio   Nuevo precio (o null si no se cambia)
     * @param nuevoStock    Nuevo stock (o null si no se cambia)
     * @return true si se actualizó correctamente, false si no se encontró el producto.
     */
    public boolean actualizarProducto(int id, String nuevoNombre, Double nuevoPrecio, Integer nuevoStock) {
        Optional<Producto> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Producto p = opt.get();

            if (nuevoNombre != null && !nuevoNombre.isBlank()) {
                p.setNombre(nuevoNombre.trim());
            }

            if (nuevoPrecio != null && nuevoPrecio >= 0) {
                p.setPrecio(nuevoPrecio);
            }

            if (nuevoStock != null && nuevoStock >= 0) {
                p.setStock(nuevoStock);
            }

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
            if (p instanceof Bebida b) {
                lineas.add(String.format("Bebida;%d;%s;%.2f;%d;%.1f",
                        b.getId(), b.getNombre(), b.getPrecio(), b.getStock(), b.getLitros()));
            } else if (p instanceof Comida c) {
                lineas.add(String.format("Comida;%d;%s;%.2f;%d;%s",
                        c.getId(), c.getNombre(), c.getPrecio(), c.getStock(), c.getFechaVencimiento()));
            } else {
                lineas.add(String.format("Producto;%d;%s;%.2f;%d",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock()));
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
                if (datos.length < 5) {
                    System.out.println("⚠️ Línea inválida (faltan datos): " + linea);
                    continue;
                }

                String tipo = datos[0];
                int id = Integer.parseInt(datos[1]);
                String nombre = datos[2];
                double precio = Double.parseDouble(datos[3].replace(",", "."));
                int stock = Integer.parseInt(datos[4]);

                switch (tipo) {
                    case "Bebida" -> {
                        if (datos.length >= 6) {
                            double litros = Double.parseDouble(datos[5].replace(",", "."));
                            productos.add(new Bebida(id, nombre, precio, stock, litros));
                        }
                    }
                    case "Comida" -> {
                        if (datos.length >= 6) {
                            LocalDate fecha = LocalDate.parse(datos[5]);
                            productos.add(new Comida(id, nombre, precio, stock, fecha));
                        }
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
