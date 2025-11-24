CREATE TABLE comentario (
    id UUID PRIMARY KEY,
    tecnico_id UUID NOT NULL,
    tecnico_nome VARCHAR(255) NOT NULL,
    conteudo VARCHAR(1000) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    chamado_id UUID NOT NULL,
    CONSTRAINT fk_comentario_chamado FOREIGN KEY (chamado_id) REFERENCES chamado (id) ON DELETE CASCADE
);