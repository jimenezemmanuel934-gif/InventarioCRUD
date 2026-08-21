package co.edu.sena.inventario.controller;

import java.util.ArrayList;
import java.util.List;

import co.edu.sena.inventario.model.Producto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import co.edu.sena.inventario.model.ResumenInventario;


@RestController
@RequestMapping("/productos")
public class ProductoController {

  private final List<Producto> productos = new ArrayList<>( List.of( 
    new Producto(1L, "Papa pastusa", 2500.0, 8, "Tuberculo"),
    new Producto(2L, "Tomate", 3200.0, 30,"Vegetal"),
    new Producto(3L, "Fresa", 8500.0, 20, "Fruta"),
    new Producto(4L, "Mango", 4500.0, 24, "Fruta"),
    new Producto(5L, "Zanahoria", 3900.0, 42, "Vegetal")
  )
);

@GetMapping
public List<Producto> listarProductos() {
    return productos;
}



@GetMapping("/{id}")
public ResponseEntity<?> buscarProducto(@PathVariable Long id) {
    for (Producto producto : productos) {
        if (producto.getId().equals(id)) {
            return ResponseEntity.ok(producto);
        }
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
       .body("Producto no encontrado ");
}







@PostMapping
public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {

    String error = validarProducto(producto);

    if (error != null) {
        return ResponseEntity.badRequest().body(error);
    }

    productos.add(producto);

    return ResponseEntity.status(HttpStatus.CREATED).body(producto);
}




@PutMapping("/{id}")
public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto nuevoProducto) {
    for (Producto producto : productos) {
        if (producto.getId().equals(id)) {

            producto.setNombre(nuevoProducto.getNombre());
            producto.setPrecio(nuevoProducto.getPrecio());
            producto.setCantidad(nuevoProducto.getCantidad());

            return producto;
        }
    }
    return null;
    
}




@DeleteMapping("/{id}")
public String eliminarProducto(@PathVariable Long id) {

    for (Producto producto : productos) {

        if (producto.getId().equals(id)) {
            productos.remove(producto);

            return "Producto eliminado correctamente";
        }
    }

    return "Producto no encontrado";
}




@GetMapping("/categoria")
public List<Producto> buscarCategoria(@RequestParam String nombre) {

    List<Producto> resultado = new ArrayList<>();

    for (Producto producto : productos) {

        if (producto.getCategoria().equalsIgnoreCase(nombre)) {
            resultado.add(producto);
        }
    }

    return resultado;
}




@PutMapping("/{id}/comprar/{cantidad}")
public String comprarProducto(@PathVariable Long id, @PathVariable Integer cantidad) {
    
    for (Producto producto : productos) {

        if (producto.getId().equals(id)) {

            if (cantidad <= producto.getCantidad()) {

                int nuevaCantidad = producto.getCantidad() - cantidad;

                producto.setCantidad(nuevaCantidad);

                return "Compra realizada. Quedan "
                        + nuevaCantidad + " unidades de "
                        + producto.getNombre();
            }

            return "No hay suficiente cantidad disponible";
        }
    }

    return "Producto no encontrado";
}



@GetMapping("/buscar")
public List<Producto> buscarPorNombre(@RequestParam String nombre) {

    List<Producto> resultado = new ArrayList<>();

    for (Producto producto : productos) {

        if (producto.getNombre().equalsIgnoreCase(nombre)) {
            resultado.add(producto);
        }
    }
    return resultado;
}



@GetMapping("/precio")
public List<Producto> buscarPorPrecio(@RequestParam Double maximo) {

    List<Producto> resultado = new ArrayList<>();

    for (Producto producto : productos) {

        if (producto.getPrecio() <= maximo) {
            resultado.add(producto);
        }
    }
    return resultado;
}



private String validarProducto(Producto producto) {

    if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
        return "El nombre no puede estar vacío";
    }

    if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
        return "El precio debe ser mayor que cero";
    }

    if (producto.getCantidad() == null || producto.getCantidad() < 0) {
        return "La cantidad no puede ser menor que cero";
    }

    if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
        return "La categoria no puede estar vacía";
    }

    return null;
}



@GetMapping("/stock-bajo")
 public List<Producto> stockBajo() {
 List<Producto> resultado = new ArrayList<>();

    for (Producto producto : productos) {

        if (producto.getCantidad() < 10) {
            resultado.add(producto);
        }
    }
    return resultado;
}



@GetMapping("/resumen")
public ResumenInventario resumen() {

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


@GetMapping("/filtrar")
public List<Producto> filtrarProductos(
        @RequestParam String categoria,
        @RequestParam Double precioMaximo) {

    List<Producto> resultado = new ArrayList<>();

    for (Producto producto : productos) {

        if (producto.getCategoria().equalsIgnoreCase(categoria)
                && producto.getPrecio() <= precioMaximo) {

            resultado.add(producto);
        }

    }

    return resultado;
}
}



