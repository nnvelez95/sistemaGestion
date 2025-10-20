package com.techlab.servicios;

import com.techlab.productos.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar operaciones sobre los productos.
 */
public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();
    private int nextId = 1;

    /**
     * Agrega un nuevo producto a la lista.
     */
    public Producto agregarProducto(String nombre, double precio, int stock) {
        Producto p = new Producto(nextId++, nombre, precio, stock);
        productos.add(p);
        return p;
    }

    /**
     * Devuelve la lista completa de productos.
     */
    public List<Producto> listarProductos() {
        return new ArrayList<>(productos); // se devuelve una copia para seguridad
    }

    /**
     * Busca un producto por su ID.
     */
    public Optional<Producto> buscarPorId(int id) {
        return productos.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    /**
     * Busca un producto por su nombre (ignora mayúsculas/minúsculas).
     */
    public Optional<Producto> buscarPorNombre(String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    /**
     * Actualiza el precio o stock de un producto.
     */
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

    /**
     * Elimina un producto por ID.
     */
    public boolean eliminarProducto(int id) {
        return productos.removeIf(p -> p.getId() == id);
    }

    /**
     * Verifica si la lista está vacía.
     */
    public boolean estaVacio() {
        return productos.isEmpty();
    }
}
