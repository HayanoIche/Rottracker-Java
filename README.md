# RotTracker

## Link da API

[https://rottracker-java.onrender.com](https://rottracker-java.onrender.com)

## Tabela de Endpoints

### Usuário

- **POST** `/api/v1/usuarios` — Criar usuário
- **GET** `/api/v1/usuarios` — Listar usuários
- **GET** `/api/v1/usuarios/{id}` — Buscar por ID
- **GET** `/api/v1/usuarios/email/{email}` — Buscar por e-mail
- **GET** `/api/v1/usuarios/nome?nome=...` — Buscar por nome
- **PUT** `/api/v1/usuarios/{id}` — Atualizar usuário

### Categoria

- **POST** `/api/v1/categorias` — Criar categoria
- **GET** `/api/v1/categorias` — Listar categorias
- **GET** `/api/v1/categorias/{id}` — Buscar por ID
- **GET** `/api/v1/categorias/nome?nome=...` — Buscar por nome
- **PUT** `/api/v1/categorias/{id}` — Atualizar categoria
- **DELETE** `/api/v1/categorias/{id}` — Excluir categoria

### Despensas

- **POST** `/api/v1/despensas` — Criar despensa
- **GET** `/api/v1/despensas` — Listar despensas
- **GET** `/api/v1/despensas/{id}` — Buscar por ID
- **GET** `/api/v1/despensas/usuario/{usuarioId}` — Listar despensas de um usuário
- **PUT** `/api/v1/despensas/{id}` — Atualizar despensa
- **DELETE** `/api/v1/despensas/{id}` — Excluir despensa

### Produto

- **POST** `/api/v1/produtos/despensa/{despensaId}` — Criar produto
- **GET** `/api/v1/produtos` — Listar produtos
- **GET** `/api/v1/produtos/{id}` — Buscar por ID
- **GET** `/api/v1/produtos/despensa/{despensaId}` — Listar produtos por despensa
- **GET** `/api/v1/produtos/nome?nome=...` — Buscar por nome
- **PUT** `/api/v1/produtos/{id}/despensa/{despensaId}` — Atualizar produto
- **DELETE** `/api/v1/produtos/{id}` — Excluir produto

