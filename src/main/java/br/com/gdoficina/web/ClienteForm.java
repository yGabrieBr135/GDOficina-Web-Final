package br.com.gdoficina.web;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import br.com.gdoficina.model.StatusOrdem;


public class ClienteForm {
    @NotBlank(message="Preencha este campo.") @Size(max=100, message="Use até 100 caracteres.")
    private String nome;
    @NotBlank(message="Preencha este campo.") @Size(max=25,message="Use até 25 caracteres.")
    private String telefone;
    @Email(message="Informe um e-mail válido.") @Size(max=254)
    private String email;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


}
