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

@RestController
@RequestMapping("/productos")
public class ProductoController {

  private final List<Producto> productos = new ArrayList<>( List.of( 
    new Producto(1L, "Papa pastusa", 2500.0, 50),
    new Producto(2L, "Tomate", 3200.0, 30),
    new Producto(3L, "Fresa", 8500.0, 20),
    new Producto(4L, "Mango", 4500.0, 24),
    new Producto(5L, "Zanahoria", 3900.0, 42)
  )
);

@GetMapping
public List<Producto> listarProductos() {
    return productos;
}

@GetMapping("/{id}")
public Producto buscarProducto(@PathVariable Long id) {
    for (Producto producto : productos) {
        if (producto.getId().equals(id)) {
            return producto;
        }
    }
    return null;
}

@PostMapping
public Producto crearProducto(@RequestBody Producto producto) {
    
    productos.add(producto);
    
    return producto;
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


}
