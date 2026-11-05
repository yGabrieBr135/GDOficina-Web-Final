package br.com.gdoficina.web;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import br.com.gdoficina.model.StatusOrdem;


public class ItemForm {
    @NotNull(message="Selecione um serviço.")
    private Long servicoId;
    @NotNull(message="Informe a quantidade.") @Min(1) @Max(9999)
    private Integer quantidade = 1;
    @NotNull(message="Informe o valor unitário.") @DecimalMin("0.00") @Digits(integer=10,fraction=2)
    private BigDecimal valorUnitario;

    public Long getServicoId() { return servicoId; }
    public void setServicoId(Long servicoId) { this.servicoId = servicoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }


}
