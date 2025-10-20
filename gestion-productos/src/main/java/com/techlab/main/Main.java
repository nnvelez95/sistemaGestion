package com.techlab.main;

import com.techlab.productos.Producto;
import com.techlab.servicios.ProductoService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTIÓN - TECHLAB ===");

        ProductoService service = new ProductoService();

        // 🔹 Agregar productos
        service.agregarProducto("Café Premium", 1500.0, 20);
        service.agregarProducto("Té Verde", 900.0, 15);
        service.agregarProducto("Chocolate", 1200.0, 10);

        // 🔹 Listar productos
        System.out.println("\n--- Lista de productos ---");
        for (Producto p : service.listarProductos()) {
            System.out.println(p);
        }

        // 🔹 Buscar producto por nombre
        System.out.println("\nBuscando 'Café Premium'...");
        service.buscarPorNombre("Café Premium")
                .ifPresentOrElse(
                        p -> System.out.println("Encontrado: " + p),
                        () -> System.out.println("No encontrado.")
                );

        // 🔹 Actualizar precio
        service.actualizarProducto(1, 1600.0, null);

        // 🔹 Eliminar producto
        service.eliminarProducto(2);

        // 🔹 Mostrar lista final
        System.out.println("\n--- Lista final ---");
        service.listarProductos().forEach(System.out::println);
    }
}
