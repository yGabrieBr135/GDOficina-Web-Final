package br.com.gdoficina.web;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import br.com.gdoficina.model.StatusOrdem;


public class VeiculoForm {
    @NotNull(message="Selecione um cliente.")
    private Long clienteId;
    @NotBlank(message="Preencha este campo.") @Pattern(regexp="[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}",message="Informe uma placa válida, sem hífen (ABC1234 ou ABC1D23).")
    private String placa;
    @NotBlank(message="Preencha este campo.") @Size(max=100, message="Use até 100 caracteres.")
    private String marca;
    @NotBlank(message="Preencha este campo.") @Size(max=100, message="Use até 100 caracteres.")
    private String modelo;
    @NotNull(message="Informe o ano.") @Min(1900) @Max(2100)
    private Integer ano;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }


}
