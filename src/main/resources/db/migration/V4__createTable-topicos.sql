CREATE TABLE topicos(
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL,
    mensaje TEXT NOT NULL,
    fechaCreacion DATETIME,
    status VARCHAR(50) NOT NULL,
    usuarioAutor_id BIGINT,
    curso_id BIGINT,
    FOREIGN KEY (usuarioAutor_id) REFERENCES usuarios (id),
    FOREIGN KEY (curso_id) REFERENCES cursos (id),
    PRIMARY KEY (id)
);