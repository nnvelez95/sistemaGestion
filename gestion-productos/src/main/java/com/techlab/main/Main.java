package com.techlab.main;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;
import com.techlab.servicios.PedidoService;
import com.techlab.servicios.ProductoService;

import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductoService productoService = new ProductoService();
    private static final PedidoService pedidoService = new PedidoService(productoService);

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
                case "5" -> crearPedido();
                case "6" -> listarPedidos();
                case "7" -> salir = true;
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
        System.out.println("5) Crear pedido");
        System.out.println("6) Listar pedidos");
        System.out.println("7) Salir");
        System.out.println("=====================================");
    }

    // Métodos de producto 
    private static void agregarProducto() {
        try {
            System.out.println("Seleccione tipo de producto:");
            System.out.println("1) Producto genérico");
            System.out.println("2) Bebida");
            System.out.println("3) Comida");
            System.out.print("Opción: ");
            String tipo = scanner.nextLine();

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Precio: ");
            double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine());

            switch (tipo) {
                case "2" -> {
                    System.out.print("Litros: ");
                    double litros = Double.parseDouble(scanner.nextLine());
                    var bebida = productoService.agregarBebida(nombre, precio, stock, litros);
                    System.out.println("✅ Bebida agregada: " + bebida);
                }
                case "3" -> {
                    System.out.print("Fecha de vencimiento (YYYY-MM-DD): ");
                    String fechaStr = scanner.nextLine();
                    var fecha = java.time.LocalDate.parse(fechaStr);
                    var comida = productoService.agregarComida(nombre, precio, stock, fecha);
                    System.out.println("✅ Comida agregada: " + comida);
                }
                default -> {
                    var producto = productoService.agregarProducto(nombre, precio, stock);
                    System.out.println("✅ Producto agregado: " + producto);
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️  Error: Entrada inválida (" + e.getMessage() + ")");
        }
    }


    private static void listarProductos() {
        List<Producto> lista = productoService.listarProductos();

        if (lista.isEmpty()) {
            System.out.println("⚠️  No hay productos cargados.");
        } else {
            System.out.println("\n--- LISTA DE PRODUCTOS ---");
            lista.forEach(System.out::println);
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

    // 🔹 Crear pedido
    private static void crearPedido() {
        if (productoService.estaVacio()) {
            System.out.println("⚠️  No hay productos disponibles para crear un pedido.");
            return;
        }

        List<LineaPedido> lineas = new ArrayList<>();

        System.out.println("--- CREAR NUEVO PEDIDO ---");
        boolean seguir = true;

        while (seguir) {
            listarProductos();
            System.out.print("Ingrese ID del producto que desea agregar: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Producto> optProducto = productoService.buscarPorId(id);
            if (optProducto.isEmpty()) {
                System.out.println("⚠️  Producto no encontrado.");
                continue;
            }

            Producto producto = optProducto.get();

            System.out.print("Ingrese cantidad deseada: ");
            int cantidad = Integer.parseInt(scanner.nextLine());

            lineas.add(new LineaPedido(producto, cantidad));

            System.out.print("¿Agregar otro producto? (s/n): ");
            seguir = scanner.nextLine().equalsIgnoreCase("s");
        }

        try {
            Pedido pedido = pedidoService.crearPedido(lineas);
            System.out.println("✅ Pedido creado exitosamente:");
            System.out.println(pedido);
        } catch (StockInsuficienteException e) {
            System.out.println("❌ Error al crear pedido: " + e.getMessage());
        }
    }

    // 🔹 Listar pedidos
    private static void listarPedidos() {
        if (!pedidoService.hayPedidos()) {
            System.out.println("⚠️  No hay pedidos registrados.");
            return;
        }

        System.out.println("\n--- LISTA DE PEDIDOS ---");
        pedidoService.listarPedidos().forEach(System.out::println);
    }
}
