CREATE TABLE chamado (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    status VARCHAR(255) NOT NULL,
    criador_id UUID NOT NULL,
    criador_nome VARCHAR(255) NOT NULL,
    tecnico_id UUID,
    tecnico_nome VARCHAR(255),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP
);
