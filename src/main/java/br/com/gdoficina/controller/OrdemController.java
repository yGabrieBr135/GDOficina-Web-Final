package br.com.gdoficina.controller;

import br.com.gdoficina.service.*;
import br.com.gdoficina.web.OrdemForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
@Controller
@RequestMapping("/ordens")
public class OrdemController {
    private final OficinaService service;
    public OrdemController(OficinaService service) { this.service=service; }
    @ModelAttribute public void comum(Model m) {m.addAttribute("pagina","ordens");m.addAttribute("titulo","Ordens de serviço");}
    @GetMapping public String lista(@RequestParam(defaultValue="") String q,Model m) {m.addAttribute("registros",service.ordens(q));m.addAttribute("q",q);return "ordens/lista";}
    private String formulario(Long id,Model m) {m.addAttribute("id",id);m.addAttribute("veiculos",service.veiculos(""));m.addAttribute("servicos",service.servicos(""));m.addAttribute("situacoes", br.com.gdoficina.model.StatusOrdem.values());return "ordens/form";}
    @GetMapping("/novo") public String novo(Model m) {m.addAttribute("form",new OrdemForm());return formulario(null,m);}
    @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model m) {m.addAttribute("form",service.ordemForm(id));return formulario(id,m);}
    @PostMapping public String criar(@Valid @ModelAttribute("form") OrdemForm form,BindingResult erros,Model m,RedirectAttributes flash) {return salvar(null,form,erros,m,flash);}
    @PostMapping("/{id}") public String atualizar(@PathVariable Long id,@Valid @ModelAttribute("form") OrdemForm form,BindingResult erros,Model m,RedirectAttributes flash) {return salvar(id,form,erros,m,flash);}
    private String salvar(Long id,OrdemForm form,BindingResult erros,Model m,RedirectAttributes flash) {
        if(erros.hasErrors())return formulario(id,m);
        try{service.salvarOrdem(id,form);}
        catch(RegraNegocioException e){erros.reject("regra",e.getMessage());return formulario(id,m);}
        catch(DataIntegrityViolationException e){erros.reject("vinculo","Não foi possível salvar. Confira duplicidades e registros vinculados.");return formulario(id,m);}
        flash.addFlashAttribute("sucesso","Registro salvo com sucesso.");return "redirect:/ordens";
    }
    @GetMapping("/{id}/excluir") public String confirmar(@PathVariable Long id,Model m) {service.ordem(id);m.addAttribute("id",id);return "excluir";}
    @PostMapping("/{id}/excluir") public String excluir(@PathVariable Long id,RedirectAttributes flash) {
        try{service.excluirOrdem(id);flash.addFlashAttribute("sucesso","Registro excluído.");}
        catch(RegraNegocioException e){flash.addFlashAttribute("erro",e.getMessage());}
        catch(DataIntegrityViolationException e){flash.addFlashAttribute("erro","Registro vinculado a outros dados. A exclusão foi bloqueada.");}
        return "redirect:/ordens";
    }
}
