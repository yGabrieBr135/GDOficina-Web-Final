package br.com.gdoficina.web;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import br.com.gdoficina.model.StatusOrdem;


public class OrdemForm {
    @NotNull(message="Selecione um veículo.")
    private Long veiculoId;
    @NotNull(message="Informe a data.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate dataAbertura = LocalDate.now();
    @NotNull(message="Selecione a situação.")
    private StatusOrdem status = StatusOrdem.ABERTA;
    @Size(max=2000)
    private String observacoes;
    @Valid @NotEmpty(message="Adicione ao menos um serviço.") @Size(max=100,message="Limite de 100 serviços por ordem.")
    private List<ItemForm> itens = new ArrayList<>();

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }

    public StatusOrdem getStatus() { return status; }
    public void setStatus(StatusOrdem status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public List<ItemForm> getItens() { return itens; }
    public void setItens(List<ItemForm> itens) { this.itens = itens; }


}
