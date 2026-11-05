package br.com.gdoficina.service;

import br.com.gdoficina.model.*;
import br.com.gdoficina.repository.*;
import br.com.gdoficina.web.*;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.math.BigDecimal;
import java.text.Normalizer;

@Service
@Transactional(readOnly=true)
public class OficinaService {
    private final ClienteRepository clientes;
    private final VeiculoRepository veiculos;
    private final ServicoRepository servicos;
    private final OrdemServicoRepository ordens;
    private final ItemOrdemRepository itens;
    private final Validator validator;
    public OficinaService(ClienteRepository c, VeiculoRepository v, ServicoRepository s,
                          OrdemServicoRepository o, ItemOrdemRepository i, Validator validator) {
        clientes=c; veiculos=v; servicos=s; ordens=o; itens=i; this.validator=validator;
    }
    private void validar(Object form) {
        var erros=validator.validate(form);
        if (!erros.isEmpty()) throw new RegraNegocioException(erros.iterator().next().getMessage());
    }
    private String normalizar(String s) { return Normalizer.normalize(s==null?"":s, Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT); }
    private boolean contem(String texto,String q) { return normalizar(texto).contains(normalizar(q).trim()); }
    public List<Cliente> clientes(String q) { return clientes.findAll().stream().filter(c->contem(c.getNome()+" "+c.getTelefone()+" "+c.getEmail(),q)).sorted(Comparator.comparing(Cliente::getNome)).toList(); }
    public List<Veiculo> veiculos(String q) { return veiculos.findAll().stream().filter(v->contem(v.getPlaca()+" "+v.getMarca()+" "+v.getModelo()+" "+v.getCliente().getNome(),q)).sorted(Comparator.comparing(Veiculo::getPlaca)).toList(); }
    public List<Servico> servicos(String q) { return servicos.findAll().stream().filter(s->contem(s.getNome()+" "+s.getDescricao(),q)).sorted(Comparator.comparing(Servico::getNome)).toList(); }
    public List<OrdemServico> ordens(String q) { return ordens.findAll().stream().filter(o->contem(o.getId()+" "+o.getVeiculo().getPlaca()+" "+o.getVeiculo().getCliente().getNome()+" "+o.getStatus().getDescricao(),q)).sorted(Comparator.comparing(OrdemServico::getId).reversed()).toList(); }
    public Cliente cliente(Long id) { return clientes.findById(id).orElseThrow(NaoEncontradoException::new); }
    public Veiculo veiculo(Long id) { return veiculos.findById(id).orElseThrow(NaoEncontradoException::new); }
    public Servico servico(Long id) { return servicos.findById(id).orElseThrow(NaoEncontradoException::new); }
    public OrdemServico ordem(Long id) { return ordens.findById(id).orElseThrow(NaoEncontradoException::new); }
    private String texto(String s) { return s==null?"":s.trim(); }
    @Transactional public Cliente salvarCliente(Long id, ClienteForm f) {
        validar(f); Cliente c=id==null?new Cliente():cliente(id);
        c.setNome(texto(f.getNome())); c.setTelefone(texto(f.getTelefone())); c.setEmail(texto(f.getEmail()));
        return clientes.saveAndFlush(c);
    }
    @Transactional public Veiculo salvarVeiculo(Long id, VeiculoForm f) {
        validar(f); String placa=f.getPlaca().toUpperCase(Locale.ROOT);
        if (id==null?veiculos.existsByPlaca(placa):veiculos.existsByPlacaAndIdNot(placa,id))
            throw new RegraNegocioException("Esta placa já está cadastrada.");
        Cliente c=clientes.findById(f.getClienteId()).orElseThrow(()->new RegraNegocioException("Selecione um cliente existente."));
        Veiculo v=id==null?new Veiculo():veiculo(id); v.setCliente(c);v.setPlaca(placa);v.setMarca(texto(f.getMarca()));v.setModelo(texto(f.getModelo()));v.setAno(f.getAno());
        return veiculos.saveAndFlush(v);
    }
    @Transactional public Servico salvarServico(Long id, ServicoForm f) {
        validar(f); Servico s=id==null?new Servico():servico(id);
        s.setNome(texto(f.getNome()));s.setDescricao(texto(f.getDescricao()));s.setPreco(f.getPreco());return servicos.saveAndFlush(s);
    }
    @Transactional public OrdemServico salvarOrdem(Long id, OrdemForm f) {
        validar(f);
        var veiculo=veiculos.findById(f.getVeiculoId()).orElseThrow(()->new RegraNegocioException("Selecione um veículo existente."));
        Set<Long> usados=new HashSet<>(); List<ItemOrdem> novos=new ArrayList<>();
        for (ItemForm item:f.getItens()) {
            if (!usados.add(item.getServicoId())) throw new RegraNegocioException("Serviço repetido: ajuste a quantidade na mesma linha.");
            Servico servico=servicos.findById(item.getServicoId()).orElseThrow(()->new RegraNegocioException("Um serviço foi excluído. Selecione outro."));
            ItemOrdem novo=new ItemOrdem();novo.setServico(servico);novo.setQuantidade(item.getQuantidade());novo.setValorUnitario(item.getValorUnitario());novos.add(novo);
        }
        OrdemServico o=id==null?new OrdemServico():ordem(id);
        o.setVeiculo(veiculo);o.setDataAbertura(f.getDataAbertura());o.setStatus(f.getStatus());o.setObservacoes(texto(f.getObservacoes()));
        // Remove os itens antigos antes dos novos para respeitar UNIQUE(ordem_id, servico_id).
        // Toda a operação pertence à mesma transação: qualquer falha desfaz as alterações.
        if (id!=null) { o.getItens().clear(); ordens.flush(); }
        for(ItemOrdem novo:novos) { novo.setOrdem(o);o.getItens().add(novo); }
        return ordens.saveAndFlush(o);
    }
    @Transactional public void excluirCliente(Long id) { cliente(id);if(veiculos.existsByClienteId(id))throw new RegraNegocioException("Cliente vinculado a veículo. Exclua ou transfira os veículos antes.");clientes.deleteById(id);clientes.flush(); }
    @Transactional public void excluirVeiculo(Long id) { veiculo(id);if(ordens.existsByVeiculoId(id))throw new RegraNegocioException("Veículo vinculado a ordem de serviço. Resolva as ordens antes.");veiculos.deleteById(id);veiculos.flush(); }
    @Transactional public void excluirServico(Long id) { servico(id);if(itens.existsByServicoId(id))throw new RegraNegocioException("Serviço usado em uma ordem. Resolva os vínculos antes.");servicos.deleteById(id);servicos.flush(); }
    @Transactional public void excluirOrdem(Long id) { ordens.delete(ordem(id));ordens.flush(); }
    public ClienteForm clienteForm(Long id) {var c=cliente(id);var f=new ClienteForm();f.setNome(c.getNome());f.setTelefone(c.getTelefone());f.setEmail(c.getEmail());return f;}
    public VeiculoForm veiculoForm(Long id) {var v=veiculo(id);var f=new VeiculoForm();f.setClienteId(v.getCliente().getId());f.setPlaca(v.getPlaca());f.setMarca(v.getMarca());f.setModelo(v.getModelo());f.setAno(v.getAno());return f;}
    public ServicoForm servicoForm(Long id) {var s=servico(id);var f=new ServicoForm();f.setNome(s.getNome());f.setDescricao(s.getDescricao());f.setPreco(s.getPreco());return f;}
    public OrdemForm ordemForm(Long id) {var o=ordem(id);var f=new OrdemForm();f.setVeiculoId(o.getVeiculo().getId());f.setDataAbertura(o.getDataAbertura());f.setStatus(o.getStatus());f.setObservacoes(o.getObservacoes());
        for(var i:o.getItens()){var item=new ItemForm();item.setServicoId(i.getServico().getId());item.setQuantidade(i.getQuantidade());item.setValorUnitario(i.getValorUnitario());f.getItens().add(item);}return f;}
}
