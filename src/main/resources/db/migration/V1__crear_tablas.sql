CREATE TABLE pedidos (
     id         BIGSERIAL    PRIMARY KEY,
     cliente    VARCHAR(100) NOT NULL,
     producto   VARCHAR(100) NOT NULL,
     cantidad   INTEGER      NOT NULL CHECK (cantidad > 0),
     creado_en  TIMESTAMP    NOT NULL DEFAULT now()
);

COMMENT ON TABLE  pedidos          IS 'Pedido de productos en una bodega';
COMMENT ON COLUMN pedidos.cliente  IS 'Identificador de cliente';
COMMENT ON COLUMN pedidos.producto IS 'Identificador de producto';
