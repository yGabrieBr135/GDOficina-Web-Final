package br.com.gdoficina.repository;

import br.com.gdoficina.model.OrdemServico;
import org.springframework.data.jpa.repository.*;
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    boolean existsByVeiculoId(Long id);
    @Override @EntityGraph(attributePaths={"veiculo.cliente","itens.servico"})
    java.util.List<OrdemServico> findAll();
    @Override @EntityGraph(attributePaths={"veiculo.cliente","itens.servico"})
    java.util.Optional<OrdemServico> findById(Long id);
}
