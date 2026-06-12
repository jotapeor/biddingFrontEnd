package com.bidding.system.frontend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EditalDTO {

    private Long id;

    /**
     * Título ou nome resumido do edital. Tem validação de tamanho mínimo.
     */
    @NotBlank(message = "O título é obrigatório")
    @Size(min = 5, max = 150, message = "O título deve ter entre 5 e 150 caracteres")
    private String titulo;
    /**
     * Descrição completa detalhando o que será licitado.
     */
    @NotBlank(message = "A descrição não pode estar vazia")
    private String descricao;
    /**
     * Data e hora do término da concorrência e recebimento de lances.
     * Mapeado em JSON com a chave "data_fechamento" para conversar corretamente com o backend.
     */
    @JsonProperty("data_fechamento")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "A data de fechamento é obrigatória")
    private LocalDateTime dataFechamento;
    private String status;
    private Long vencedor;
    private boolean encerrando;

    public EditalDTO() {
    }

    public EditalDTO(Long id, String titulo, String descricao, LocalDateTime dataFechamento, String status) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataFechamento = dataFechamento;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVencedor() {
        return vencedor;
    }

    public void setVencedor(Long vencedor) {
        this.vencedor = vencedor;
    }

    public boolean isEncerrando() {
        return encerrando;
    }

    public void setEncerrando(boolean encerrando) {
        this.encerrando = encerrando;
    }

    /**
     * Entrega a data de fechamento formatada de maneira agradável para leitura (visualização em HTML).
     *
     * @return Data em formato "dd/MM/yyyy HH:mm", ou "-" se vazia.
     */
    public String getDataFechamentoFormatada() {
        if (dataFechamento == null) return "-";
        return dataFechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}