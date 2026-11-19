# RotTracker
## Link Api
https://rottracker-java.onrender.com
## Tabela Endpoints
### Usuário

POST	/api/v1/usuarios		Criar usuário
GET	/api/v1/usuarios		Listar usuários
GET	/api/v1/usuarios/{id}		Buscar por ID
GET	/api/v1/usuarios/email/{email}	Buscar por email
GET	/api/v1/usuarios/nome?nome=...	Buscar por nome
PUT	/api/v1/usuarios/{id}		Atualizar usuário

### Categoria

POST	/api/v1/categorias		Criar categoria
GET	/api/v1/categorias		Listar categorias
GET	/api/v1/categorias/{id}		Buscar por ID
GET	/api/v1/categorias/nome?nome=...Buscar por nome
PUT	/api/v1/categorias/{id}		Atualizar categoria
DELETE	/api/v1/categorias/{id}		Excluir categoria

### Despensas

POST	/api/v1/despensas			Criar despensa
GET	/api/v1/despensas			Listar despensas
GET	/api/v1/despensas/{id}			Buscar por ID
GET	/api/v1/despensas/usuario/{usuarioId}	Despensas de um usuário
PUT	/api/v1/despensas/{id}			Atualizar despensa
DELETE	/api/v1/despensas/{id}			Excluir despensa


### Produto

POST	/api/v1/produtos/despensa/{despensaId}	Criar produto
GET	/api/v1/produtos	Listar produtos
GET	/api/v1/produtos/{id}	Buscar por ID
GET	/api/v1/produtos/despensa/{despensaId}	Listar por despensa
GET	/api/v1/produtos/nome?nome=...	Buscar por nome
PUT	/api/v1/produtos/{id}/despensa/{despensaId}	Atualizar produto
DELETE	/api/v1/produtos/{id}	Excluir produto

### Membro

POST	/api/v1/membros	Adicionar membro
GET	/api/v1/membros	Listar membros
GET	/api/v1/membros/{id}	Buscar por ID
GET	/api/v1/membros/despensa/{despensaId}	Listar membros da despensa
GET	/api/v1/membros/usuario/{usuarioId}	Listar participações do usuário
PUT	/api/v1/membros/{id}	Atualizar membro
DELETE	/api/v1/membros/{id}	Remover membro

### Convite

POST	/api/v1/convites	Criar convite
GET	/api/v1/convites	Listar convites
GET	/api/v1/convites/{id}	Buscar por ID
GET	/api/v1/convites/token/{token}	Buscar por token
GET	/api/v1/convites/despensa/{despensaId}	Listar por despensa
GET	/api/v1/convites/email?email=...	Buscar por email
GET	/api/v1/convites/status/{status}	Buscar por status
PUT	/api/v1/convites/{id}	Atualizar convite
POST	/api/v1/convites/token/{token}/aceitar	Aceitar convite
PATCH	/api/v1/convites/{id}/cancelar	Cancelar convite
DELETE	/api/v1/convites/{id}	Excluir convite
