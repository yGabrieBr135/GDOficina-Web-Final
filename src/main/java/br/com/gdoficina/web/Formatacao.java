package br.com.gdoficina.web;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
@Component("fmt")
public class Formatacao {
    public String moeda(BigDecimal valor) { return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor==null?BigDecimal.ZERO:valor); }
}
