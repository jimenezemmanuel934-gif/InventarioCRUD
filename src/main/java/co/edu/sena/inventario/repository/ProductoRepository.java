package co.edu.sena.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.sena.inventario.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{
    
}

