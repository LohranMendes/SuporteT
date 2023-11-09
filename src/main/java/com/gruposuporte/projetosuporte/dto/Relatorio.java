package com.gruposuporte.projetosuporte.dto;

public record Relatorio(
        String tecnico,
        int abertas,
        int fechadas,
        int total
) {
}
