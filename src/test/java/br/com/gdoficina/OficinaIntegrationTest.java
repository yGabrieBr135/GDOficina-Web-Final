package br.com.gdoficina;

import br.com.gdoficina.model.*;
import br.com.gdoficina.repository.*;
import br.com.gdoficina.service.*;
import br.com.gdoficina.web.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class OficinaIntegrationTest {
    @Autowired OficinaService service;
    @Autowired ClienteRepository clientes;
    @Autowired VeiculoRepository veiculos;
    @Autowired ServicoRepository servicos;
    @Autowired OrdemServicoRepository ordens;
    @Autowired ItemOrdemRepository itens;
    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    @BeforeEach void limpar(){ ordens.deleteAll();veiculos.deleteAll();clientes.deleteAll();servicos.deleteAll(); }
    Cliente cliente(){var f=new ClienteForm();f.setNome("João Teste");f.setTelefone("47999990000");f.setEmail("joao@example.com");return service.salvarCliente(null,f);}
    Veiculo veiculo(Cliente c){var f=new VeiculoForm();f.setClienteId(c.getId());f.setPlaca("ABC1D23");f.setMarca("VW");f.setModelo("Gol");f.setAno(2015);return service.salvarVeiculo(null,f);}
    Servico servico(){var f=new ServicoForm();f.setNome("Balanceamento");f.setDescricao("Por roda");f.setPreco(new BigDecimal("25.00"));return service.salvarServico(null,f);}
    OrdemForm ordemForm(Veiculo v,Servico s){var f=new OrdemForm();f.setVeiculoId(v.getId());var item=new ItemForm();item.setServicoId(s.getId());item.setQuantidade(4);item.setValorUnitario(new BigDecimal("25.00"));f.getItens().add(item);return f;}
    @Test void cincoTabelasEViewsVazias() throws Exception {
        var tabelas=jdbc.queryForList("select table_name from information_schema.tables where table_schema='PUBLIC'",String.class);
        assertTrue(tabelas.containsAll(List.of("CLIENTES","VEICULOS","SERVICOS","ORDENS_SERVICO","ITENS_ORDEM")));
        for(String url:List.of("/","/clientes","/veiculos","/servicos","/ordens","/clientes/novo","/veiculos/novo","/servicos/novo","/ordens/novo"))mvc.perform(get(url)).andExpect(status().isOk());
    }
    @Test void fluxoHttpCrudClientesEValidacao() throws Exception {
        mvc.perform(post("/clientes").with(csrf()).param("nome","Ana").param("telefone","4799999").param("email","ana@example.com")).andExpect(status().is3xxRedirection());
        long id=clientes.findAll().get(0).getId();
        mvc.perform(get("/clientes/"+id+"/editar")).andExpect(status().isOk()).andExpect(content().string(containsString("ana@example.com")));
        mvc.perform(post("/clientes/"+id).with(csrf()).param("nome","Ana Maria").param("telefone","471234").param("email","ana@example.com")).andExpect(status().is3xxRedirection());
        assertEquals("Ana Maria",service.cliente(id).getNome());
        mvc.perform(post("/clientes").with(csrf()).param("nome"," ").param("telefone","123").param("email","invalido")).andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form","nome","email"));
        assertEquals(1,clientes.count());
        mvc.perform(get("/clientes/"+id+"/excluir")).andExpect(status().isOk());assertEquals(1,clientes.count());
        mvc.perform(post("/clientes/"+id+"/excluir").with(csrf())).andExpect(status().is3xxRedirection());assertEquals(0,clientes.count());
    }
    @Test void ordemAtualizaItensTotalEPrecoHistorico() throws Exception {
        var c=cliente();var v=veiculo(c);var s=servico();var f=ordemForm(v,s);var o=service.salvarOrdem(null,f);
        assertEquals(0,new BigDecimal("100.00").compareTo(service.ordem(o.getId()).getTotal()));
        var sf=service.servicoForm(s.getId());sf.setPreco(new BigDecimal("99.00"));service.salvarServico(s.getId(),sf);
        assertEquals(0,new BigDecimal("100.00").compareTo(service.ordem(o.getId()).getTotal()));
        for(String url:List.of("/","/ordens","/veiculos","/servicos","/ordens/"+o.getId()+"/editar","/veiculos/"+v.getId()+"/editar","/servicos/"+s.getId()+"/editar"))mvc.perform(get(url)).andExpect(status().isOk());
        f=service.ordemForm(o.getId());f.getItens().get(0).setQuantidade(2);f.setStatus(StatusOrdem.CONCLUIDA);service.salvarOrdem(o.getId(),f);
        assertEquals(1,itens.count());assertEquals(0,new BigDecimal("50.00").compareTo(service.ordem(o.getId()).getTotal()));
        service.excluirOrdem(o.getId());assertEquals(0,itens.count());assertEquals(0,ordens.count());assertEquals(1,servicos.count());
    }
    @Test void protegeVinculosEPermiteExcluirNaOrdemCorreta(){
        var c=cliente();var v=veiculo(c);var s=servico();var o=service.salvarOrdem(null,ordemForm(v,s));
        assertThrows(RegraNegocioException.class,()->service.excluirCliente(c.getId()));
        assertThrows(RegraNegocioException.class,()->service.excluirVeiculo(v.getId()));
        assertThrows(RegraNegocioException.class,()->service.excluirServico(s.getId()));
        service.excluirOrdem(o.getId());service.excluirVeiculo(v.getId());service.excluirCliente(c.getId());service.excluirServico(s.getId());
        assertEquals(0,clientes.count());assertEquals(0,veiculos.count());assertEquals(0,servicos.count());
    }
    @Test void placaDuplicadaEBuscaSemAcentos(){
        var c=cliente();veiculo(c);assertThrows(RegraNegocioException.class,()->veiculo(c));
        assertEquals(1,service.clientes("joao").size());assertEquals(0,service.clientes("nao existe").size());
    }
    @Test void ordemInvalidaNaoAlteraDadosSalvos(){
        var v=veiculo(cliente());var s=servico();var o=service.salvarOrdem(null,ordemForm(v,s));
        var vazio=service.ordemForm(o.getId());vazio.getItens().clear();assertThrows(RegraNegocioException.class,()->service.salvarOrdem(o.getId(),vazio));
        var duplicado=service.ordemForm(o.getId());duplicado.getItens().add(duplicado.getItens().get(0));assertThrows(RegraNegocioException.class,()->service.salvarOrdem(o.getId(),duplicado));
        var inexistente=service.ordemForm(o.getId());inexistente.getItens().get(0).setServicoId(Long.MAX_VALUE);assertThrows(RegraNegocioException.class,()->service.salvarOrdem(o.getId(),inexistente));
        var negativo=service.ordemForm(o.getId());negativo.getItens().get(0).setValorUnitario(new BigDecimal("-1"));assertThrows(RegraNegocioException.class,()->service.salvarOrdem(o.getId(),negativo));
        assertEquals(1,itens.count());assertEquals(0,new BigDecimal("100.00").compareTo(service.ordem(o.getId()).getTotal()));
    }
    @Test void csrf404EHtmlEscapado() throws Exception {
        mvc.perform(post("/clientes").param("nome","Sem CSRF").param("telefone","123")).andExpect(status().isForbidden());
        mvc.perform(get("/clientes/999999/editar")).andExpect(status().isNotFound());
        var f=new ClienteForm();f.setNome("<script>alert(1)</script>");f.setTelefone("123");service.salvarCliente(null,f);
        mvc.perform(get("/clientes")).andExpect(content().string(containsString("&lt;script&gt;")));
    }
    @Test void cadastroHttpOrdemEQuantidadeInvalida() throws Exception {
        var v=veiculo(cliente());var s=servico();
        mvc.perform(post("/ordens").with(csrf()).param("veiculoId",v.getId().toString()).param("dataAbertura","2026-11-04").param("status","ABERTA").param("itens[0].servicoId",s.getId().toString()).param("itens[0].quantidade","3").param("itens[0].valorUnitario","25.50")).andExpect(status().is3xxRedirection());
        var o=service.ordens("").get(0);assertEquals(0,new BigDecimal("76.50").compareTo(o.getTotal()));
        mvc.perform(post("/ordens/"+o.getId()).with(csrf()).param("veiculoId",v.getId().toString()).param("dataAbertura","2026-11-04").param("status","ABERTA").param("itens[0].servicoId",s.getId().toString()).param("itens[0].quantidade","0").param("itens[0].valorUnitario","25.50")).andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form","itens[0].quantidade"));
        assertEquals(0,new BigDecimal("76.50").compareTo(service.ordem(o.getId()).getTotal()));
    }
}
