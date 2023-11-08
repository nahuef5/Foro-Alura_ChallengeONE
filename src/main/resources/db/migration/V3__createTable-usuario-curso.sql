CREATE TABLE usuario_curso(
    usuario_id BIGINT,
    curso_id BIGINT,
    PRIMARY KEY (usuario_id,curso_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    FOREIGN KEY (curso_id) REFERENCES cursos (id)
);