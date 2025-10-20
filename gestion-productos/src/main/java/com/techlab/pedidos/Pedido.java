package com.techlab.pedidos;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un pedido con varias líneas de productos.
 */
public class Pedido {
    private int id;
    private List<LineaPedido> lineas = new ArrayList<>();

    public Pedido(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }

    public void agregarLinea(LineaPedido linea) {
        lineas.add(linea);
    }

    public double calcularTotal() {
        return lineas.stream()
                .mapToDouble(LineaPedido::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nPedido ID: ").append(id).append("\n");
        for (LineaPedido lp : lineas) {
            sb.append("  - ").append(lp).append("\n");
        }
        sb.append(String.format("TOTAL: $%.2f\n", calcularTotal()));
        return sb.toString();
    }
}
