
package com.techlab.main;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.*;
import com.techlab.servicios.PedidoService;
import com.techlab.servicios.ProductoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {

    // 🧩 Variables globales
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductoService productoService = new ProductoService();
    private static final PedidoService pedidoService = new PedidoService(productoService);

    // 🎨 Códigos de color ANSI
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";

    public static void main(String[] args)  {

        // 🔹 Cargar datos al iniciar
        productoService.cargarDesdeArchivo();
        pedidoService.cargarDesdeArchivo();

        boolean salir = false;

        System.out.println(CYAN + "================================" + RESET);
        System.out.println(BLUE + "=== SISTEMA DE GESTIÓN - TECHLAB ===" + RESET);
        System.out.println(CYAN + "================================" + RESET);

        while (!salir) {
            mostrarMenu();
            System.out.print("Elija una opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> agregarProducto();
                case "2" -> listarProductos();
                case "3" -> buscarActualizarProducto();
                case "4" -> eliminarProducto();
                case "5" -> crearPedido();
                case "6" -> listarPedidos();
                case "7" -> salir = true;
                default -> System.out.println(YELLOW + "⚠️  Opción no válida. Intente nuevamente." + RESET);
            }

            System.out.println(CYAN + "====================================" + RESET);
        }

        // 🔹 Guardar datos antes de salir
        productoService.guardarEnArchivo();
        pedidoService.guardarEnArchivo();

        System.out.println(GREEN + "✅ Programa finalizado correctamente." + RESET);
    }

    // 🔹 Mostrar menú principal
    private static void mostrarMenu() {
        System.out.println("\n=========== MENÚ PRINCIPAL ===========");
        System.out.println("1) Agregar producto");
        System.out.println("2) Listar productos");
        System.out.println("3) Buscar / Actualizar producto");
        System.out.println("4) Eliminar producto");
        System.out.println("5) Crear pedido");
        System.out.println("6) Listar pedidos");
        System.out.println("7) Salir");
        System.out.println("=====================================");
    }

    // ======================================================
    // 🔹 GESTIÓN DE PRODUCTOS
    // ======================================================

    private static void agregarProducto() {
        System.out.println("\n" + CYAN + "=== AGREGAR PRODUCTO ===" + RESET);

        String nombre = leerTexto("Ingrese nombre del producto: ");
        double precio = leerDouble("Ingrese precio: ");
        int stock = leerEntero("Ingrese stock: ");

        String tipoSeleccionado;
        while (true) {
            System.out.println("\nSeleccione tipo de producto:");
            System.out.println("1) Genérico");
            System.out.println("2) Bebida");
            System.out.println("3) Comida");
            System.out.print("Opción: ");
            tipoSeleccionado = scanner.nextLine().trim();

            if (tipoSeleccionado.matches("[1-3]")) break;
            System.out.println(RED + "⚠️  Opción inválida. Debe ingresar 1, 2 o 3." + RESET);
        }

        Producto nuevo;

        switch (tipoSeleccionado) {
            case "2" -> {
                double litros = leerDouble("Ingrese cantidad de litros (use punto): ");
                nuevo = productoService.agregarBebida(nombre, precio, stock, litros);
                System.out.println(GREEN + "✅ Bebida agregada o actualizada: " + nuevo + RESET);
            }
            case "3" -> {
                LocalDate fecha = leerFecha("Ingrese fecha de vencimiento");
                nuevo = productoService.agregarComida(nombre, precio, stock, fecha);
                System.out.println(GREEN + "✅ Comida agregada o actualizada: " + nuevo + RESET);
            }
            default -> {
                nuevo = productoService.agregarProducto(nombre, precio, stock);
                System.out.println(GREEN + "✅ Producto agregado o actualizado: " + nuevo + RESET);
            }
        }
    }

    private static void listarProductos() {
        List<Producto> lista = productoService.listarProductos();

        if (lista.isEmpty()) {
            System.out.println(YELLOW + "⚠️  No hay productos cargados." + RESET);
        } else {
            System.out.println("\n" + CYAN + "=== LISTA DE PRODUCTOS ===" + RESET);
            System.out.printf("%-5s %-20s %-10s %-10s %-15s%n", "ID", "Nombre", "Precio", "Stock", "Detalle");
            System.out.println("-----------------------------------------------------------");
            for (Producto p : lista) {
                if (p instanceof Bebida b)
                    System.out.printf("%-5d %-20s $%-9.2f %-10d %.1f L%n",
                            b.getId(), b.getNombre(), b.getPrecio(), b.getStock(), b.getLitros());
                else if (p instanceof Comida c)
                    System.out.printf("%-5d %-20s $%-9.2f %-10d Vence: %s%n",
                            c.getId(), c.getNombre(), c.getPrecio(), c.getStock(), c.getFechaVencimiento());
                else
                    System.out.printf("%-5d %-20s $%-9.2f %-10d -%n",
                            p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        }
    }

    private static void buscarActualizarProducto() {
        System.out.print("Ingrese el ID o nombre del producto: ");
        String entrada = scanner.nextLine();
        Optional<Producto> producto;

        try {
            int id = Integer.parseInt(entrada);
            producto = productoService.buscarPorId(id);
        } catch (NumberFormatException e) {
            producto = productoService.buscarPorNombre(entrada);
        }

        if (producto.isPresent()) {
            Producto p = producto.get();
            System.out.println(GREEN + "✅ Producto encontrado:" + RESET);
            System.out.println(p);
            System.out.print("¿Desea actualizar este producto? (s/n): ");
            String resp = scanner.nextLine();

            if (resp.equalsIgnoreCase("s")) {
                try {
                    System.out.print("Nuevo nombre (ENTER para mantener): ");
                    String nuevoNombre = scanner.nextLine().trim();
                    nuevoNombre = nuevoNombre.isEmpty() ? null : nuevoNombre;

                    System.out.print("Nuevo precio (ENTER para mantener): ");
                    String nuevoPrecioStr = scanner.nextLine().trim();
                    Double nuevoPrecio = nuevoPrecioStr.isEmpty() ? null : Double.parseDouble(nuevoPrecioStr);

                    System.out.print("Nuevo stock (ENTER para mantener): ");
                    String nuevoStockStr = scanner.nextLine().trim();
                    Integer nuevoStock = nuevoStockStr.isEmpty() ? null : Integer.parseInt(nuevoStockStr);

                    boolean actualizado = productoService.actualizarProducto(p.getId(), nuevoNombre, nuevoPrecio, nuevoStock);
                    if (actualizado)
                        System.out.println(GREEN + "✅ Producto actualizado correctamente." + RESET);
                    else
                        System.out.println(RED + "⚠️  No se pudo actualizar el producto." + RESET);

                } catch (NumberFormatException e) {
                    System.out.println(RED + "⚠️  Error: Ingrese valores numéricos válidos." + RESET);
                } catch (IllegalArgumentException e) {
                    System.out.println(RED + "⚠️  " + e.getMessage() + RESET);
                }
            }
        } else {
            System.out.println(YELLOW + "⚠️  Producto no encontrado." + RESET);
        }
    }

    private static void eliminarProducto() {
        int id = leerEntero("Ingrese el ID del producto a eliminar: ");
        boolean eliminado = productoService.eliminarProducto(id);
        if (eliminado)
            System.out.println(GREEN + "🗑️  Producto eliminado correctamente." + RESET);
        else
            System.out.println(RED + "⚠️  No se encontró un producto con ese ID." + RESET);
    }

    // ======================================================
    // 🔹 GESTIÓN DE PEDIDOS
    // ======================================================

    private static void crearPedido() {
        if (productoService.estaVacio()) {
            System.out.println(YELLOW + "⚠️  No hay productos disponibles para crear un pedido." + RESET);
            return;
        }

        List<LineaPedido> lineas = new ArrayList<>();
        System.out.println(GREEN + "--- CREAR NUEVO PEDIDO ---" + RESET);
        boolean seguir = true;

        while (seguir) {
            listarProductos();
            int id = leerEntero("Ingrese ID del producto que desea agregar: ");

            Optional<Producto> optProducto = productoService.buscarPorId(id);
            if (optProducto.isEmpty()) {
                System.out.println(RED + "⚠️  Producto no encontrado." + RESET);
                continue;
            }

            Producto producto = optProducto.get();
            int cantidad = leerEntero("Ingrese cantidad deseada: ");
            lineas.add(new LineaPedido(producto, cantidad));

            System.out.print("¿Agregar otro producto? (s/n): ");
            seguir = scanner.nextLine().equalsIgnoreCase("s");
        }

        try {
            Pedido pedido = pedidoService.crearPedido(lineas);
            System.out.println(GREEN + "✅ Pedido creado exitosamente:" + RESET);
            System.out.println(pedido);
        } catch (StockInsuficienteException e) {
            System.out.println(RED + "❌ Error al crear pedido: " + e.getMessage() + RESET);
        }
    }

    private static void listarPedidos() {
        if (!pedidoService.hayPedidos()) {
            System.out.println(YELLOW + "⚠️  No hay pedidos registrados." + RESET);
            return;
        }

        System.out.println("\n--- LISTA DE PEDIDOS ---");
        pedidoService.listarPedidos().forEach(System.out::println);
    }

    // ======================================================
    // 🔹 MÉTODOS DE ENTRADA SEGURA
    // ======================================================

    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println(RED + "⚠️  El texto no puede estar vacío." + RESET);
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "⚠️  Ingrese un número entero válido (ejemplo: 5)." + RESET);
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                if (input.contains(",")) {
                    System.out.println(RED + "⚠️  Use punto (.) en lugar de coma (,) para los decimales." + RESET);
                    continue;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "⚠️  Ingrese un número decimal válido. Ejemplo: 1.5" + RESET);
            }
        }
    }

    private static LocalDate leerFecha(String mensaje) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                System.out.print(mensaje + " (formato: yyyy-MM-dd): ");
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println(RED + "⚠️  Fecha inválida. Ejemplo correcto: 2025-10-30" + RESET);
            }
        }
    }
}
