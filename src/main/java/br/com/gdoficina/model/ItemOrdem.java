package br.com.gdoficina.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="itens_ordem", uniqueConstraints=@UniqueConstraint(columnNames={"ordem_id","servico_id"}))
public class ItemOrdem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="ordem_id",nullable=false)
    private OrdemServico ordem;
    @ManyToOne(optional=false) @JoinColumn(name="servico_id",nullable=false)
    private Servico servico;
    @Column(nullable=false)
    private Integer quantidade;
    @Column(name="valor_unitario",nullable=false,precision=12,scale=2)
    private BigDecimal valorUnitario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrdemServico getOrdem() { return ordem; }
    public void setOrdem(OrdemServico ordem) { this.ordem = ordem; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public BigDecimal getSubtotal() { return valorUnitario.multiply(BigDecimal.valueOf(quantidade)); }
}
