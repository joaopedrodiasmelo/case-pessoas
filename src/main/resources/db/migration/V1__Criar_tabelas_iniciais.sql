
--================================================================
-- TABELA 1: Colaboradores
--================================================================
CREATE TABLE colaboradores (
    matricula VARCHAR(9) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_admissao DATE NOT NULL,
    cargo VARCHAR(50) NOT NULL
);

-- Index para otimizar buscas por nome
CREATE INDEX idx_colaborador_nome ON colaboradores(nome);

COMMENT ON TABLE colaboradores IS 'Armazena os dados cadastrais dos colaboradores.';
COMMENT ON COLUMN colaboradores.matricula IS 'Chave Primária: Matrícula única do colaborador (9 dígitos).';

--================================================================
-- TABELA 2: Avaliações Comportamentais
-- (Chave Primária Composta: {colaborador_matricula, numero})
--================================================================
CREATE TABLE avaliacoes_comportamentais (

    colaborador_matricula VARCHAR(9) NOT NULL,
    numero INT NOT NULL,
    nota_pergunta_1 INT NOT NULL,
    nota_pergunta_2 INT NOT NULL,
    nota_pergunta_3 INT NOT NULL,
    nota_pergunta_4 INT NOT NULL,

    CONSTRAINT pk_avaliacao_comportamental PRIMARY KEY (colaborador_matricula, numero),

    CONSTRAINT fk_comp_colaborador
        FOREIGN KEY (colaborador_matricula)
        REFERENCES colaboradores(matricula)
        ON DELETE CASCADE,

    -- Restrições de Domínio (Notas 1-5)
    CONSTRAINT chk_comp_nota_1 CHECK (nota_pergunta_1 BETWEEN 1 AND 5),
    CONSTRAINT chk_comp_nota_2 CHECK (nota_pergunta_2 BETWEEN 1 AND 5),
    CONSTRAINT chk_comp_nota_3 CHECK (nota_pergunta_3 BETWEEN 1 AND 5),
    CONSTRAINT chk_comp_nota_4 CHECK (nota_pergunta_4 BETWEEN 1 AND 5)
);

COMMENT ON TABLE avaliacoes_comportamentais IS 'Armazena a parte comportamental das avaliações.';
COMMENT ON COLUMN avaliacoes_comportamentais.numero IS 'Identificador da instância da avaliação para o colaborador (ex: ano).';

--================================================================
-- TABELA 3: Avaliações de Entrega (Cabeçalho dos Desafios)
-- (Chave Primária Composta: {colaborador_matricula, numero})
--================================================================
CREATE TABLE avaliacoes_entregas (
    colaborador_matricula VARCHAR(9) NOT NULL,
    numero INT NOT NULL,

    CONSTRAINT pk_avaliacao_entrega PRIMARY KEY (colaborador_matricula, numero),

    CONSTRAINT fk_entrega_colaborador
        FOREIGN KEY (colaborador_matricula)
        REFERENCES colaboradores(matricula)
        ON DELETE CASCADE
);

COMMENT ON TABLE avaliacoes_entregas IS 'Tabela "ponte" que representa uma instância de avaliação de entrega.';

--================================================================
-- TABELA 4: Desafios
-- (Chave Primária própria 'id', liga-se a AvaliacaoEntrega)
--================================================================
CREATE TABLE desafios (
    id BIGSERIAL PRIMARY KEY,
    colaborador_matricula VARCHAR(9) NOT NULL,
    numero INT NOT NULL,
    descricao_desafio VARCHAR(255) NOT NULL,
    nota_desafio INT NOT NULL,

    CONSTRAINT fk_desafio_avaliacao_entrega
        FOREIGN KEY (colaborador_matricula, numero)
        REFERENCES avaliacoes_entregas(colaborador_matricula, numero)
        ON DELETE CASCADE, -- Se deletar a 'avaliacao_entrega', deleta seus desafios

    -- Restrição de Domínio (Nota 1-5)
    CONSTRAINT chk_desafio_nota CHECK (nota_desafio BETWEEN 1 AND 5)
);

-- Index para otimizar a chave estrangeira composta
CREATE INDEX idx_desafios_fk ON desafios(colaborador_matricula, numero);

COMMENT ON TABLE desafios IS 'Armazena os desafios individuais (entregas) de uma avaliação.';