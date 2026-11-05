package br.com.gdoficina.web;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import br.com.gdoficina.model.StatusOrdem;


public class ServicoForm {
    @NotBlank(message="Preencha este campo.") @Size(max=100, message="Use até 100 caracteres.")
    private String nome;
    @Size(max=1000)
    private String descricao;
    @NotNull(message="Informe o preço.") @DecimalMin(value="0.00",message="O preço não pode ser negativo.") @Digits(integer=10,fraction=2,message="Use até 10 dígitos inteiros e 2 casas decimais.")
    private BigDecimal preco;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }


}
