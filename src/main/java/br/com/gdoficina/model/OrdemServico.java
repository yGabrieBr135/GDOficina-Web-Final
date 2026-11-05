package br.com.gdoficina.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="ordens_servico")
public class OrdemServico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="veiculo_id",nullable=false)
    private Veiculo veiculo;
    @Column(name="data_abertura",nullable=false)
    private LocalDate dataAbertura;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)
    private StatusOrdem status;
    @Column(length=2000)
    private String observacoes;
    @OneToMany(mappedBy="ordem", cascade=CascadeType.ALL, orphanRemoval=true) @OrderBy("id asc")
    private List<ItemOrdem> itens = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }

    public StatusOrdem getStatus() { return status; }
    public void setStatus(StatusOrdem status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public List<ItemOrdem> getItens() { return itens; }
    public void setItens(List<ItemOrdem> itens) { this.itens = itens; }

    public BigDecimal getTotal() { return itens.stream().map(ItemOrdem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add); }
}
