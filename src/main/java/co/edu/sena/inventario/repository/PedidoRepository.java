package co.edu.sena.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.sena.inventario.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long>{
    
}
