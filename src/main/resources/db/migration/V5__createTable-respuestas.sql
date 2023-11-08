CREATE TABLE respuestas(
    id BIGINT NOT NULL AUTO_INCREMENT,
    mensaje TEXT NOT NULL,
    usuarioAutor_id BIGINT,
    topico_id BIGINT,
    fechaCreacion DATETIME,
    solucion BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuarioAutor_id) REFERENCES usuarios (id),
    FOREIGN KEY (topico_id) REFERENCES topicos (id),
    PRIMARY KEY (id)
);