package br.com.gdoficina.repository;

import br.com.gdoficina.model.Servico;
import org.springframework.data.jpa.repository.*;
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    
}
