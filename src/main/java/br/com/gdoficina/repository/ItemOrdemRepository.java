package br.com.gdoficina.repository;

import br.com.gdoficina.model.ItemOrdem;
import org.springframework.data.jpa.repository.*;
public interface ItemOrdemRepository extends JpaRepository<ItemOrdem, Long> {
    boolean existsByServicoId(Long id);
}
