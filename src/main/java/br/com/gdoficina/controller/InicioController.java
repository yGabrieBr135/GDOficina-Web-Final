package br.com.gdoficina.controller;

import br.com.gdoficina.service.OficinaService;
import br.com.gdoficina.model.StatusOrdem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
@Controller
public class InicioController {
    private final OficinaService service;
    public InicioController(OficinaService service) { this.service=service; }
    @GetMapping("/") public String inicio(Model m) {
        var ordens=service.ordens("");m.addAttribute("pagina","inicio");m.addAttribute("titulo","Visão geral");
        m.addAttribute("clientes",service.clientes("").size());m.addAttribute("veiculos",service.veiculos("").size());
        m.addAttribute("abertas",ordens.stream().filter(o->o.getStatus()==StatusOrdem.ABERTA||o.getStatus()==StatusOrdem.EM_ANDAMENTO).count());
        m.addAttribute("valorConcluido",ordens.stream().filter(o->o.getStatus()==StatusOrdem.CONCLUIDA).map(o->o.getTotal()).reduce(BigDecimal.ZERO,BigDecimal::add));
        m.addAttribute("registros",ordens.stream().limit(5).toList());return "inicio";
    }
}
