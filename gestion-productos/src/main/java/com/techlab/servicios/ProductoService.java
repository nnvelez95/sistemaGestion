package com.techlab.servicios;

import com.techlab.productos.*;
import com.techlab.util.ArchivoUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private Producto agregarGenerico(Producto nuevo, int stock) {
        Optional<Producto> existente = buscarPorNombreYPrecio(nuevo.getNombre(), nuevo.getPrecio());
        if (existente.isPresent()) {
            Producto p = existente.get();
            p.setStock(p.getStock() + stock);
            System.out.println("ℹ️  Producto existente, se actualizó el stock.");
            return p;
        } else {
            //  Usar el nextId de forma segura
            nuevo.setId(nextId++);
            productos.add(nuevo);
            return nuevo;
        }
    }

    // ==========================================================
    // 🔹 MÉTODOS AUXILIARES
    // ==========================================================

    public List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    public boolean estaVacio() {
        return productos.isEmpty();
    }

    public Optional<Producto> buscarPorId(int id) {
        //  Uso de Stream API
        return productos.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public Optional<Producto> buscarPorNombre(String nombre) {
        //  Uso de Stream API
        return productos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    public Optional<Producto> buscarPorNombreYPrecio(String nombre, double precio) {
        //  Uso de Stream API
        return productos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre) && p.getPrecio() == precio)
                .findFirst();
    }

    public boolean eliminarProducto(int id) {

        return productos.removeIf(p -> p.getId() == id);
    }


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
        // Uso de Stream API para hacer el mapeo de datos más conciso.
        List<String> lineas = productos.stream()
                .map(this::productoAString) // Uso de método de referencia para encapsular la lógica
                .collect(Collectors.toList());

        ArchivoUtil.escribirLineas(RUTA_PRODUCTOS, lineas);
    }

    // Método privado para generar la línea de texto (limpia guardarEnArchivo)
    private String productoAString(Producto p) {
        if (p instanceof Bebida b) {
            return String.format("Bebida;%d;%s;%.2f;%d;%.1f",
                    b.getId(), b.getNombre(), b.getPrecio(), b.getStock(), b.getLitros());
        } else if (p instanceof Comida c) {
            return String.format("Comida;%d;%s;%.2f;%d;%s",
                    c.getId(), c.getNombre(), c.getPrecio(), c.getStock(), c.getFechaVencimiento());
        } else {
            return String.format("Producto;%d;%s;%.2f;%d",
                    p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
        }
    }


    /**
     * Carga los productos desde el archivo. Si encuentra un error de formato o datos faltantes,
     * lanza una IllegalStateException (RuntimeException) para detener la carga.
     */
    public void cargarDesdeArchivo() {
        List<String> lineas = ArchivoUtil.leerLineas(RUTA_PRODUCTOS);
        productos.clear();
        int maxId = 0;

        for (String linea : lineas) {
            try {
                // Encapsulamos la lógica de parseo en un método auxiliar para limpieza.
                Producto nuevoProducto = parsearLineaAProducto(linea);
                productos.add(nuevoProducto);
                maxId = Math.max(maxId, nuevoProducto.getId());

            } catch (IllegalStateException | NumberFormatException | DateTimeParseException e) {
                // En lugar de solo imprimir, lanzamos una RuntimeException.
                // Esto detiene la carga si hay un error crítico.
                throw new IllegalStateException("Error al cargar producto desde archivo: Datos inválidos en línea: " + linea, e);
            }
        }

        // Uso de maxId para asegurar que nextId no se duplique
        nextId = maxId + 1;
    }

    // Método privado para la lógica compleja de parseo (limpia cargarDesdeArchivo)
    private Producto parsearLineaAProducto(String linea) {
        String[] datos = linea.split(";");

        // Validación de datos mínimos
        if (datos.length < 5) {
            throw new IllegalStateException("Línea de producto con formato inválido (menos de 5 campos): " + linea);
        }

        String tipo = datos[0];
        // Los errores de parseo (e.g. NumberFormatException) serán capturados por el try-catch en el método llamador
        int id = Integer.parseInt(datos[1]);
        String nombre = datos[2];
        double precio = Double.parseDouble(datos[3].replace(",", "."));
        int stock = Integer.parseInt(datos[4]);

        return switch (tipo) {
            case "Bebida" -> {
                if (datos.length < 6) throw new IllegalStateException("Falta el campo 'litros' para la Bebida: " + linea);
                double litros = Double.parseDouble(datos[5].replace(",", "."));
                yield new Bebida(id, nombre, precio, stock, litros);
            }
            case "Comida" -> {
                if (datos.length < 6) throw new IllegalStateException("Falta el campo 'fechaVencimiento' para la Comida: " + linea);
                LocalDate fecha = LocalDate.parse(datos[5]); // Puede lanzar DateTimeParseException
                yield new Comida(id, nombre, precio, stock, fecha);
            }
            default -> new Producto(id, nombre, precio, stock);
        };
    }
}