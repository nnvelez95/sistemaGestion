package com.techlab.productos;

/**
 * Representa un producto con id, nombre, precio y stock.
 */
public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    // 🔹 Constructor
    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // 🔹 Getters y Setters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio >= 0) {
            this.precio = precio;
        }
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    // 🔹 Método toString (para mostrar datos)
    @Override
    public String toString() {
        return String.format("ID: %d | %s | Precio: $%.2f | Stock: %d",
                id, nombre, precio, stock);
    }
}
