package com.techlab.servicios;

import com.techlab.productos.*;

import java.time.LocalDate;
import java.util.*;

public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();
    private int nextId = 1;

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
}
