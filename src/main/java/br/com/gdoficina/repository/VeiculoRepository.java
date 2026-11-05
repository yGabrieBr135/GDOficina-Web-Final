package br.com.gdoficina.repository;

import br.com.gdoficina.model.Veiculo;
import org.springframework.data.jpa.repository.*;
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    boolean existsByClienteId(Long id);
    boolean existsByPlacaAndIdNot(String placa, Long id);
    boolean existsByPlaca(String placa);
}
