package com.techlab.productos;

import java.time.LocalDate;

/**
 * Representa una comida con fecha de vencimiento.
 */
public class Comida extends Producto {

    private LocalDate fechaVencimiento;

    // 🔹 Constructor sin ID (nuevo producto)
    public Comida(String nombre, double precio, int stock, LocalDate fechaVencimiento) {
        super(nombre, precio, stock);
        setFechaVencimiento(fechaVencimiento);
    }

    // 🔹 Constructor con ID (para carga desde archivo)
    public Comida(int id, String nombre, double precio, int stock, LocalDate fechaVencimiento) {
        super(id, nombre, precio, stock);
        setFechaVencimiento(fechaVencimiento);
    }

    // ==========================================================
    // 🔹 Getter / Setter con validación
    // ==========================================================

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("⚠️  La fecha de vencimiento no puede ser nula.");
        }
        this.fechaVencimiento = fechaVencimiento;
    }

    // ==========================================================
    // 🔹 Métodos de utilidad
    // ==========================================================

    /** Devuelve true si la comida ya está vencida. */
    public boolean estaVencida() {
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    @Override
    public String toString() {
        String estado = estaVencida() ? "❌ Vencida" : "✅ Vigente";
        return String.format("[Comida] %s | Vence: %s (%s)",
                super.toString(), fechaVencimiento, estado);
    }
}
