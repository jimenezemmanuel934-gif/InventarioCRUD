package co.edu.sena.inventario.controller;

import java.util.ArrayList;
import java.util.List;

import co.edu.sena.inventario.model.Producto;
import co.edu.sena.inventario.model.ResumenInventario;
import co.edu.sena.inventario.service.ProductoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }


    //Listar Productos get/productos
    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }


    //BUSCAR PRODUCTO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProducto(@PathVariable Long id) {

        Producto producto = productoService.buscarPorId(id);

        if (producto == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado");
        }

        return ResponseEntity.ok(producto);
    }


    //INGRESAR NUEVO PRODUCTO
    @PostMapping
public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {

    String error = validarProducto(producto);

    if (error != null) {
        return ResponseEntity
                .badRequest()
                .body(error);
    }

    try {

        productoService.agregarProducto(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(producto);

    } catch (IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}



    //ACTUALIZAR PRODUCTO
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto nuevoProducto) {

        Producto producto = productoService.buscarPorId(id);

        if (producto == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado");
        }

        producto.setNombre(nuevoProducto.getNombre());
        producto.setPrecio(nuevoProducto.getPrecio());
        producto.setCantidad(nuevoProducto.getCantidad());
        producto.setCategoria(nuevoProducto.getCategoria());

        return ResponseEntity.ok(producto);
    }


    //BORRAR PRODUCTO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {

        Producto producto = productoService.buscarPorId(id);

        if (producto == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado");
        }

        productoService.eliminarProducto(id);

        return ResponseEntity.ok("Producto eliminado correctamente");
    }


    //BUSCAR POR CATEGORIA
    @GetMapping("/categoria")
    public List<Producto> buscarCategoria(@RequestParam String nombre) {

        List<Producto> resultado = new ArrayList<>();

        for (Producto producto : productoService.listarProductos()) {

            if (producto.getCategoria().equalsIgnoreCase(nombre)) {
                resultado.add(producto);
            }
        }

        return resultado;
    }


    //COMPRAR UNA CANTIDAD
    @PutMapping("/{id}/comprar/{cantidad}")
    public String comprarProducto(
            @PathVariable Long id,
            @PathVariable Integer cantidad) {

        Producto producto = productoService.buscarPorId(id);

        if (producto == null) {
            return "Producto no encontrado";
        }

        if (cantidad <= 0) {
            return "La cantidad debe ser mayor que cero";
        }

        if (cantidad <= producto.getCantidad()) {

            int nuevaCantidad =
                    producto.getCantidad() - cantidad;

            producto.setCantidad(nuevaCantidad);

            return "Compra realizada. Quedan "
                    + nuevaCantidad + " unidades de "
                    + producto.getNombre();
        }

        return "No hay suficiente cantidad disponible";
    }


    //BUSCAR POR NOMBRE
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(
            @RequestParam String nombre) {

        List<Producto> resultado = new ArrayList<>();

        for (Producto producto : productoService.listarProductos()) {

            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                resultado.add(producto);
            }
        }

        return resultado;
    }


    //PRECIO MAYOR
    @GetMapping("/precio")
    public List<Producto> buscarPorPrecio(
            @RequestParam Double maximo) {

        List<Producto> resultado = new ArrayList<>();

        for (Producto producto : productoService.listarProductos()) {

            if (producto.getPrecio() <= maximo) {
                resultado.add(producto);
            }
        }

        return resultado;
    }


    //VALIDACIONES DE BUSQUEDA
    private String validarProducto(Producto producto) {

        if (producto.getNombre() == null
                || producto.getNombre().trim().isEmpty()) {

            return "El nombre no puede estar vacío";
        }

        if (producto.getPrecio() == null
                || producto.getPrecio() <= 0) {

            return "El precio debe ser mayor que cero";
        }

        if (producto.getCantidad() == null
                || producto.getCantidad() < 0) {

            return "La cantidad no puede ser menor que cero";
        }

        if (producto.getCategoria() == null
                || producto.getCategoria().trim().isEmpty()) {

            return "La categoria no puede estar vacía";
        }

        return null;
    }


    //BUSCAR POR STOCK MAS BAJO
    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {

        List<Producto> resultado = new ArrayList<>();

        for (Producto producto : productoService.listarProductos()) {

            if (producto.getCantidad() < 10) {
                resultado.add(producto);
            }
        }

        return resultado;
    }


    //RESUMEN DE INVENTARIO
    @GetMapping("/resumen")
    public ResumenInventario resumen() {

        List<Producto> productos =
                productoService.listarProductos();

    //Si no hay productos, devolvemos resumen vacio
    if(productos.isEmpty()){
        return new ResumenInventario(
            0,
             0,
                "No hay Productos",
                 "No hay Productos");
    }
        int totalProductos = productos.size();
        int stockBajo = 0;

        Producto masCostoso = productos.get(0);
        Producto masEconomico = productos.get(0);

        for (Producto producto : productos) {

            if (producto.getCantidad() < 10) {
                stockBajo++;
            }

            if (producto.getPrecio() > masCostoso.getPrecio()) {
                masCostoso = producto;
            }

            if (producto.getPrecio() < masEconomico.getPrecio()) {
                masEconomico = producto;
            }
        }

        return new ResumenInventario(
                totalProductos,
                stockBajo,
                masCostoso.getNombre(),
                masEconomico.getNombre()
        );
    }


    //BUSCAR POR CATEGORIA y precio maximo
    @GetMapping("/filtrar")
    public List<Producto> filtrarProductos(
            @RequestParam String categoria,
            @RequestParam Double precioMaximo) {

        List<Producto> resultado = new ArrayList<>();

        for (Producto producto : productoService.listarProductos()) {

            if (producto.getCategoria().equalsIgnoreCase(categoria)
                    && producto.getPrecio() <= precioMaximo) {

                resultado.add(producto);
            }
        }

        return resultado;
    }
}
