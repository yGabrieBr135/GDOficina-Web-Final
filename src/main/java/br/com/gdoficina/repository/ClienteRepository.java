package br.com.gdoficina.repository;

import br.com.gdoficina.model.Cliente;
import org.springframework.data.jpa.repository.*;
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
