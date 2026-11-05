package br.com.gdoficina.controller;

import br.com.gdoficina.service.*;
import br.com.gdoficina.web.ClienteForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final OficinaService service;
    public ClienteController(OficinaService service) { this.service=service; }
    @ModelAttribute public void comum(Model m) {m.addAttribute("pagina","clientes");m.addAttribute("titulo","Clientes");}
    @GetMapping public String lista(@RequestParam(defaultValue="") String q,Model m) {m.addAttribute("registros",service.clientes(q));m.addAttribute("q",q);return "clientes/lista";}
    private String formulario(Long id,Model m) {m.addAttribute("id",id);return "clientes/form";}
    @GetMapping("/novo") public String novo(Model m) {m.addAttribute("form",new ClienteForm());return formulario(null,m);}
    @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model m) {m.addAttribute("form",service.clienteForm(id));return formulario(id,m);}
    @PostMapping public String criar(@Valid @ModelAttribute("form") ClienteForm form,BindingResult erros,Model m,RedirectAttributes flash) {return salvar(null,form,erros,m,flash);}
    @PostMapping("/{id}") public String atualizar(@PathVariable Long id,@Valid @ModelAttribute("form") ClienteForm form,BindingResult erros,Model m,RedirectAttributes flash) {return salvar(id,form,erros,m,flash);}
    private String salvar(Long id,ClienteForm form,BindingResult erros,Model m,RedirectAttributes flash) {
        if(erros.hasErrors())return formulario(id,m);
        try{service.salvarCliente(id,form);}
        catch(RegraNegocioException e){erros.reject("regra",e.getMessage());return formulario(id,m);}
        catch(DataIntegrityViolationException e){erros.reject("vinculo","Não foi possível salvar. Confira duplicidades e registros vinculados.");return formulario(id,m);}
        flash.addFlashAttribute("sucesso","Registro salvo com sucesso.");return "redirect:/clientes";
    }
    @GetMapping("/{id}/excluir") public String confirmar(@PathVariable Long id,Model m) {service.cliente(id);m.addAttribute("id",id);return "excluir";}
    @PostMapping("/{id}/excluir") public String excluir(@PathVariable Long id,RedirectAttributes flash) {
        try{service.excluirCliente(id);flash.addFlashAttribute("sucesso","Registro excluído.");}
        catch(RegraNegocioException e){flash.addFlashAttribute("erro",e.getMessage());}
        catch(DataIntegrityViolationException e){flash.addFlashAttribute("erro","Registro vinculado a outros dados. A exclusão foi bloqueada.");}
        return "redirect:/clientes";
    }
}
