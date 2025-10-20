package com.techlab.pedidos;

import com.techlab.productos.Producto;

/**
 * Representa una línea dentro de un pedido (un producto y la cantidad solicitada).
 */
public class LineaPedido {
    private Producto producto;
    private int cantidad;

    public LineaPedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    @Override
    public String toString() {
        return String.format("%s | Cantidad: %d | Subtotal: $%.2f",
                producto.getNombre(), cantidad, getSubtotal());
    }
}
