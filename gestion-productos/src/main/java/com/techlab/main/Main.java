package com.techlab.main;

import com.techlab.productos.Producto;
import com.techlab.servicios.ProductoService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductoService productoService = new ProductoService();

    public static void main(String[] args) {
        boolean salir = false;

        System.out.println("================================");
        System.out.println("=== SISTEMA DE GESTIÓN - TECHLAB ===");
        System.out.println("================================");

        while (!salir) {
            mostrarMenu();
            System.out.print("Elija una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> agregarProducto();
                case "2" -> listarProductos();
                case "3" -> buscarActualizarProducto();
                case "4" -> eliminarProducto();
                case "5" -> salir = true;
                default -> System.out.println("⚠️  Opción no válida. Intente nuevamente.");
            }
        }

        System.out.println("\n✅ Programa finalizado. ¡Hasta luego!");
    }

    // 🔹 Mostrar menú principal
    private static void mostrarMenu() {
        System.out.println("\n=========== MENÚ PRINCIPAL ===========");
        System.out.println("1) Agregar producto");
        System.out.println("2) Listar productos");
        System.out.println("3) Buscar / Actualizar producto");
        System.out.println("4) Eliminar producto");
        System.out.println("5) Salir");
        System.out.println("=====================================");
    }

    // 🔹 Agregar producto
    private static void agregarProducto() {
        try {
            System.out.print("Ingrese nombre del producto: ");
            String nombre = scanner.nextLine();

            System.out.print("Ingrese precio: ");
            double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("Ingrese stock: ");
            int stock = Integer.parseInt(scanner.nextLine());

            Producto p = productoService.agregarProducto(nombre, precio, stock);
            System.out.println("✅ Producto agregado: " + p);

        } catch (NumberFormatException e) {
            System.out.println("⚠️  Error: Ingrese números válidos para precio y stock.");
        }
    }

    // 🔹 Listar productos
    private static void listarProductos() {
        List<Producto> lista = productoService.listarProductos();

        if (lista.isEmpty()) {
            System.out.println("⚠️  No hay productos cargados.");
        } else {
            System.out.println("\n--- LISTA DE PRODUCTOS ---");
            lista.forEach(System.out::println);
        }
    }

    // 🔹 Buscar / Actualizar producto
    private static void buscarActualizarProducto() {
        System.out.print("Ingrese el ID o nombre del producto: ");
        String entrada = scanner.nextLine();
        Optional<Producto> producto;

        // Determinar si el usuario ingresó un número (ID) o texto (nombre)
        try {
            int id = Integer.parseInt(entrada);
            producto = productoService.buscarPorId(id);
        } catch (NumberFormatException e) {
            producto = productoService.buscarPorNombre(entrada);
        }

        if (producto.isPresent()) {
            Producto p = producto.get();
            System.out.println("Encontrado: " + p);
            System.out.print("¿Desea actualizar este producto? (s/n): ");
            String resp = scanner.nextLine();

            if (resp.equalsIgnoreCase("s")) {
                try {
                    System.out.print("Nuevo precio (ENTER para mantener): ");
                    String nuevoPrecioStr = scanner.nextLine();
                    Double nuevoPrecio = nuevoPrecioStr.isEmpty() ? null : Double.parseDouble(nuevoPrecioStr);

                    System.out.print("Nuevo stock (ENTER para mantener): ");
                    String nuevoStockStr = scanner.nextLine();
                    Integer nuevoStock = nuevoStockStr.isEmpty() ? null : Integer.parseInt(nuevoStockStr);

                    boolean actualizado = productoService.actualizarProducto(p.getId(), nuevoPrecio, nuevoStock);
                    if (actualizado)
                        System.out.println("✅ Producto actualizado correctamente.");
                    else
                        System.out.println("⚠️  No se pudo actualizar el producto.");

                } catch (NumberFormatException e) {
                    System.out.println("⚠️  Error: Ingrese valores numéricos válidos.");
                }
            }

        } else {
            System.out.println("⚠️  Producto no encontrado.");
        }
    }

    // 🔹 Eliminar producto
    private static void eliminarProducto() {
        System.out.print("Ingrese el ID del producto a eliminar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            boolean eliminado = productoService.eliminarProducto(id);
            if (eliminado)
                System.out.println("🗑️  Producto eliminado correctamente.");
            else
                System.out.println("⚠️  No se encontró un producto con ese ID.");
        } catch (NumberFormatException e) {
            System.out.println("⚠️  Error: Debe ingresar un número de ID válido.");
        }
    }
}

