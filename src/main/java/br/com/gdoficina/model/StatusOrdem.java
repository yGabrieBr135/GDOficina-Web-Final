package br.com.gdoficina.model;

public enum StatusOrdem {
    ABERTA("Aberta"), EM_ANDAMENTO("Em andamento"), CONCLUIDA("Concluída"), CANCELADA("Cancelada");
    private final String descricao;
    StatusOrdem(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
