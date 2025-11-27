# PetFriends - Sistema Reativo de Gestao de Entregas

Sistema de gestao de pedidos e entregas desenvolvido com arquitetura reativa, Event Sourcing e CQRS, utilizando Domain-Driven Design (DDD).

## Indice

1. [Como Subir a Aplicacao](#1-como-subir-a-aplicacao)
   - [1.1 Pre-requisitos](#11-pre-requisitos)
   - [1.2 Executar com Docker Compose](#12-executar-com-docker-compose)
   - [1.3 Executar com Kubernetes](#13-executar-com-kubernetes)
2. [Testar via Swagger](#2-testar-via-swagger)
   - [2.1 Acessar Swagger UI](#21-acessar-swagger-ui)
   - [2.2 Fluxo Completo de Teste](#22-fluxo-completo-de-teste)
   - [2.3 Cenarios Alternativos](#23-cenarios-alternativos)
3. [Health Checks](#3-health-checks)
4. [Observabilidade](#4-observabilidade)
   - [4.1 Zipkin - Distributed Tracing](#41-zipkin---distributed-tracing)
   - [4.2 Micrometer Tracing](#42-micrometer-tracing)
   - [4.3 ELK Stack](#43-elk-stack)

---

## 1. Como Subir a Aplicacao

### 1.1 Pre-requisitos

#### Software Necessario

Instale os seguintes softwares:

| Software | Versao Minima | Download |
|----------|---------------|----------|
| **Java JDK** | 17+ | https://adoptium.net/ |
| **Maven** | 3.8+ | https://maven.apache.org/download.cgi |
| **Docker Desktop** | Latest | https://www.docker.com/products/docker-desktop |
| **Git** | Latest | https://git-scm.com/downloads |

#### Verificar Instalacoes

```bash
# Verificar Java
java -version
# Deve mostrar: openjdk version "17.0.x" ou superior

# Verificar Maven
mvn -version
# Deve mostrar: Apache Maven 3.8.x ou superior

# Verificar Docker
docker --version
# Deve mostrar: Docker version 20.x ou superior

docker-compose --version
# Deve mostrar: docker-compose version 1.29.x ou superior

# Verificar Git
git --version
# Deve mostrar: git version 2.x ou superior
```

#### Configurar Memoria do Docker

**Windows/Mac (Docker Desktop)**:
- Abra Docker Desktop
- Settings -> Resources
- **Memory**: Minimo 8GB (recomendado 12GB)
- **CPUs**: Minimo 4 cores
- **Disk**: Minimo 20GB livre
- Clique em "Apply & Restart"

#### Clonar o Repositorio

```bash
# Clone o projeto
git clone <URL_DO_REPOSITORIO>
cd petfriends
```

---

### 1.2 Executar com Docker Compose

#### 1.2.1 Build do Projeto

```bash
# Na raiz do projeto (petfriends/)
mvn clean install -DskipTests

# Este comando:
# - Limpa builds anteriores
# - Compila todos os modulos
# - Gera os JARs
# - Pula os testes (para build mais rapido)
```

**Tempo esperado**: 2-5 minutos

**Saida esperada**:
```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] petfriends 1.0.0-SNAPSHOT .......................... SUCCESS
[INFO] petfriends-almoxarifado 1.0.0-SNAPSHOT ............. SUCCESS
[INFO] petfriends-transporte 1.0.0-SNAPSHOT ............... SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

#### 1.2.2 Verificar JARs Gerados

```bash
# Verificar se os JARs foram criados
ls -lh petfriends-almoxarifado/target/*.jar
ls -lh petfriends-transporte/target/*.jar

# Deve mostrar arquivos como:
# petfriends-almoxarifado-1.0.0-SNAPSHOT.jar
# petfriends-transporte-1.0.0-SNAPSHOT.jar
```

#### 1.2.3 Iniciar Todos os Servicos

```bash
# Na raiz do projeto
docker-compose up -d

# -d = detached mode (roda em background)
```

**Este comando iniciara**:
- PostgreSQL (banco de dados)
- Zookeeper + Kafka (mensageria)
- Almoxarifado (microsservico)
- Transporte (microsservico)
- Zipkin (tracing)
- Elasticsearch (armazenamento de logs)
- Logstash (processamento de logs)
- Kibana (visualizacao de logs)

**Tempo esperado**: 2-3 minutos para todos os servicos estarem prontos

#### 1.2.4 Acompanhar os Logs

```bash
# Ver logs de todos os servicos
docker-compose logs -f

# Ver logs de um servico especifico
docker-compose logs -f almoxarifado
docker-compose logs -f transporte

# Para sair dos logs: Ctrl+C
```

#### 1.2.5 Aguardar Inicializacao

**Importante**: Aguarde ate ver estas mensagens nos logs:

**Almoxarifado**:
```
Started PetfriendsAlmoxarifadoApplication in X.XXX seconds
```

**Transporte**:
```
Started PetfriendsTransporteApplication in X.XXX seconds
```

#### 1.2.6 Verificar Status dos Containers

```bash
docker-compose ps
```

**Saida esperada** (todos com status "Up"):
```
NAME                        STATUS              PORTS
petfriends-almoxarifado     Up (healthy)        0.0.0.0:8082->8082/tcp
petfriends-transporte       Up (healthy)        0.0.0.0:8083->8083/tcp
petfriends-postgres         Up (healthy)        0.0.0.0:5432->5432/tcp
petfriends-kafka            Up (healthy)        0.0.0.0:9092-9093->9092-9093/tcp
petfriends-zipkin           Up                  0.0.0.0:9411->9411/tcp
petfriends-elasticsearch    Up (healthy)        0.0.0.0:9200->9200/tcp
petfriends-logstash         Up                  0.0.0.0:5000->5000/tcp
petfriends-kibana           Up                  0.0.0.0:5601->5601/tcp
```

#### 1.2.7 Parar a Execucao

```bash
# Parar todos os containers (mas mantem volumes/dados)
docker-compose stop

# Parar e remover containers (mantem volumes/dados)
docker-compose down

# Parar e limpar TUDO (Remove dados)
# ATENCAO: Isto apagara TODOS os dados (banco de dados, logs, etc.)
docker-compose down -v
```

---

### 1.3 Executar com Kubernetes

#### 1.3.1 Pre-requisitos Kubernetes

Alem dos pre-requisitos basicos, voce precisara de:

| Software | Versao Minima | Descricao |
|----------|---------------|-----------|
| **kubectl** | 1.25+ | CLI para interagir com cluster Kubernetes |
| **Cluster Kubernetes** | 1.25+ | Cluster Kubernetes configurado e acessivel |

#### 1.3.2 Verificar Cluster Kubernetes

```bash
# Verificar kubectl
kubectl version --client

# Verificar conexao com o cluster
kubectl cluster-info

# Verificar nodes disponiveis
kubectl get nodes

# Deve mostrar os nodes do cluster como Ready
```

**Saida esperada:**
```
NAME           STATUS   ROLES           AGE   VERSION
node-1         Ready    control-plane   10d   v1.28.0
node-2         Ready    <none>          10d   v1.28.0
```

#### 1.3.3 Build das Imagens Docker

Antes de fazer o deploy, e necessario buildar as imagens Docker dos microsservicos:

```bash
# Na raiz do projeto, compile o codigo
mvn clean install -DskipTests

# Build das imagens Docker
docker-compose build almoxarifado transporte
```

**Importante**: As imagens precisam estar acessiveis ao cluster Kubernetes. Voce tem algumas opcoes:

**Opcao 1: Docker Hub ou Registry Privado (RECOMENDADO)**

```bash
# Login no registry
docker login

# Tag com seu usuario/registry
docker tag petfriends-almoxarifado:latest <seu-usuario>/petfriends-almoxarifado:latest
docker tag petfriends-transporte:latest <seu-usuario>/petfriends-transporte:latest

# Push
docker push <seu-usuario>/petfriends-almoxarifado:latest
docker push <seu-usuario>/petfriends-transporte:latest
```

**IMPORTANTE**: Se voce usar Docker Hub/Registry privado, voce **DEVE** atualizar os arquivos de deployment:

1. **k8s/almoxarifado/almoxarifado-deployment.yaml** (linha 18):
```yaml
      containers:
      - name: almoxarifado
        image: <seu-usuario>/petfriends-almoxarifado:latest
        imagePullPolicy: Always
```

2. **k8s/transporte/transporte-deployment.yaml** (linha 18):
```yaml
      containers:
      - name: transporte
        image: <seu-usuario>/petfriends-transporte:latest
        imagePullPolicy: Always
```

Substitua `<seu-usuario>` pelo seu usuario do Docker Hub.

#### 1.3.4 Deploy no Kubernetes

```bash
# Navegar para o diretorio k8s
cd k8s

# Executar o script de deploy
bash deploy.sh
```

**O que o script faz:**

1. Cria o namespace `petfriends`
2. Deploy do PostgreSQL (banco de dados)
3. Deploy do Zookeeper + Kafka (mensageria)
4. Deploy do Zipkin (tracing distribuido)
5. Deploy do Elasticsearch (armazenamento de logs)
6. Deploy do Logstash (processamento de logs)
7. Deploy do Kibana (visualizacao de logs)
8. Deploy do Almoxarifado (microsservico)
9. Deploy do Transporte (microsservico)
10. Aguarda cada servico estar pronto antes de prosseguir

**Tempo esperado**: 5-10 minutos (dependendo do cluster)

**Acompanhar o deploy:**
```bash
# Em outro terminal, acompanhe os pods sendo criados
watch kubectl get pods -n petfriends
```

#### 1.3.5 Verificar Status do Deploy

```bash
# Ver todos os pods
kubectl get pods -n petfriends

# Ver todos os services
kubectl get services -n petfriends

# Ver deployments/statefulsets
kubectl get deployments -n petfriends
kubectl get statefulsets -n petfriends

# Ver todos os recursos de uma vez
kubectl get all -n petfriends
```

**Saida esperada** (todos os pods devem estar Running):

```
NAME                            READY   STATUS    RESTARTS   AGE
almoxarifado-xxx                1/1     Running   0          2m
transporte-xxx                  1/1     Running   0          2m
postgres-0                      1/1     Running   0          8m
kafka-0                         1/1     Running   0          6m
zookeeper-xxx                   1/1     Running   0          7m
zipkin-xxx                      1/1     Running   0          5m
elasticsearch-0                 1/1     Running   0          5m
logstash-xxx                    1/1     Running   0          4m
kibana-xxx                      1/1     Running   0          3m
```

**Se algum pod nao estiver Running:**
```bash
# Ver detalhes do pod
kubectl describe pod <pod-name> -n petfriends

# Ver logs do pod
kubectl logs <pod-name> -n petfriends

# Ver logs anteriores (se o pod reiniciou)
kubectl logs <pod-name> -n petfriends --previous
```

#### 1.3.6 Acessar os Servicos

**IMPORTANTE: Docker Desktop + Windows/WSL2**

Se voce esta usando **Docker Desktop no Windows com WSL2**:

- NAO tente acessar os IPs internos (172.x.x.x) - eles nao sao acessiveis do Windows
- NAO use NodePort diretamente - pode nao funcionar corretamente
- USE `kubectl port-forward` - e a forma **correta e universal**

Para acessar os servicos do Kubernetes localmente, faca **port-forward**:

**Microsservicos:**

```bash
# Almoxarifado (porta 8082)
kubectl port-forward svc/almoxarifado-service 8082:80 -n petfriends

# Transporte (porta 8083) - em outro terminal
kubectl port-forward svc/transporte-service 8083:80 -n petfriends
```

**Observabilidade:**

```bash
# Zipkin (porta 9411) - em outro terminal
kubectl port-forward svc/zipkin-service 9411:9411 -n petfriends

# Kibana (porta 5601) - em outro terminal
kubectl port-forward svc/kibana-service 5601:5601 -n petfriends
```

Apos executar os port-forwards, os servicos estarao acessiveis no navegador atraves das mesmas URLs do Docker Compose.

#### 1.3.7 Parar a Execucao

```bash
# Deletar todos os recursos do namespace
kubectl delete namespace petfriends

# Ou usar o script de limpeza
cd k8s
bash cleanup.sh
```

---

## 2. Testar via Swagger

### 2.1 Acessar Swagger UI

Abra no navegador:

**Almoxarifado**:
```
http://localhost:8082/swagger-ui.html
```

**Transporte**:
```
http://localhost:8083/swagger-ui.html
```

### 2.2 Fluxo Completo de Teste

#### PASSO 1: Criar uma Reserva de Estoque

1. No Swagger do **Almoxarifado**: http://localhost:8082/swagger-ui.html
2. Expandir `reserva-estoque-command-controller`
3. Clicar em **POST** `/almoxarifado/reservas`
4. Clicar em "Try it out"
5. Colar o JSON:

```json
{
  "pedidoId": "PED-001",
  "endereco": {
    "rua": "Av Paulista",
    "numero": "1000",
    "complemento": "Conj 42",
    "bairro": "Bela Vista",
    "cidade": "Sao Paulo",
    "estado": "SP",
    "cep": "01310-100"
  },
  "itens": [
    {
      "produtoId": "PROD-001",
      "quantidade": 10
    },
    {
      "produtoId": "PROD-002",
      "quantidade": 5
    }
  ]
}
```

6. Clicar em "Execute"
7. **Copiar** o `reservaId` da resposta (exemplo: `RES-abc123`)

**Resposta esperada** (Status 201 Created):
```json
{
  "reservaId": "RES-abc123",
  "message": "Estoque reservado com sucesso"
}
```

---

#### PASSO 2: Consultar a Reserva

1. Ainda no Swagger do **Almoxarifado**
2. Expandir `reserva-estoque-query-controller`
3. Clicar em **GET** `/almoxarifado/reservas/{id}`
4. Clicar em "Try it out"
5. Inserir o `reservaId` copiado (exemplo: `RES-abc123`)
6. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "id": "RES-abc123",
  "pedidoId": "PED-001",
  "status": "PENDENTE",
  "itens": [
    {
      "produtoId": "PROD-001",
      "quantidade": 10
    },
    {
      "produtoId": "PROD-002",
      "quantidade": 5
    }
  ]
}
```

---

#### PASSO 3: Confirmar a Reserva

1. Expandir `reserva-estoque-command-controller`
2. Clicar em **PUT** `/almoxarifado/reservas/{id}/confirmar`
3. Clicar em "Try it out"
4. Inserir o `reservaId` (exemplo: `RES-abc123`)
5. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "reservaId": "RES-abc123",
  "message": "Reserva confirmada com sucesso"
}
```

---

#### PASSO 4: Consultar Reserva (verificar status CONFIRMADA)

1. Repetir **PASSO 2**
2. Verificar que `status` mudou para `"CONFIRMADA"`

---

#### PASSO 5: Separar Itens (SAGA AUTOMATICA)

1. Expandir `reserva-estoque-command-controller`
2. Clicar em **PUT** `/almoxarifado/reservas/separar`
3. Clicar em "Try it out"
4. Colar o JSON:

```json
{
  "id": "RES-abc123",
  "operadorId": "OP-001"
}
```

5. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "reservaId": "RES-abc123",
  "message": "Itens separados com sucesso"
}
```

**SAGA AUTOMATICA ATIVADA:**

Ao separar os itens:
1. Evento `ItensSeparados` e publicado no Kafka (com o endereco)
2. Servico de Transporte **recebe automaticamente** o evento
3. Entrega e **CRIADA AUTOMATICAMENTE** com status `AGENDADA`
4. Nao e mais necessario chamar `POST /transporte/entregas` manualmente

---

#### PASSO 6: Verificar Entrega Criada Automaticamente

1. Abrir Swagger do **Transporte**: http://localhost:8083/swagger-ui.html
2. Expandir `entrega-query-controller`
3. Clicar em **GET** `/transporte/entregas`
4. Clicar em "Try it out"
5. No campo `pedidoId`, inserir: `PED-001`
6. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
[
  {
    "id": "ENT-auto-generated",
    "pedidoId": "PED-001",
    "reservaId": "RES-abc123",
    "status": "AGENDADA",
    "enderecoCompleto": "Av Paulista, 1000 Conj 42 - Bela Vista, Sao Paulo/SP - CEP: 01310-100",
    "dataPrevisaoEntrega": "2025-11-21"
  }
]
```

**Copiar o `id` da entrega para os proximos passos**

---

#### PASSO 7: Consultar Detalhes da Entrega

1. Ainda em `entrega-query-controller`
2. Clicar em **GET** `/transporte/entregas/{id}`
3. Clicar em "Try it out"
4. Inserir o `entregaId` copiado do passo anterior
5. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "id": "ENT-auto-generated",
  "pedidoId": "PED-001",
  "reservaId": "RES-abc123",
  "status": "AGENDADA",
  "enderecoCompleto": "Av Paulista, 1000 Conj 42 - Bela Vista, Sao Paulo/SP - CEP: 01310-100",
  "dataPrevisaoEntrega": "2025-11-21"
}
```

---

#### PASSO 8: Iniciar Transporte

1. Expandir `entrega-command-controller`
2. Clicar em **PUT** `/transporte/entregas/{id}/iniciar`
3. Clicar em "Try it out"
4. Inserir o `entregaId`
5. No Request Body, colar:

```json
{
  "motoristaId": "MOT-001",
  "veiculoId": "VEI-001"
}
```

6. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "entregaId": "ENT-xyz789",
  "message": "Transporte iniciado com sucesso"
}
```

---

#### PASSO 9: Concluir Entrega

1. Expandir `entrega-command-controller`
2. Clicar em **PUT** `/transporte/entregas/{id}/concluir`
3. Clicar em "Try it out"
4. Inserir o `entregaId`
5. No Request Body, colar:

```json
{
  "recebedor": "Joao Silva",
  "dataRecebimento": "2025-11-20T14:30:00",
  "observacoes": "Entregue com sucesso, recebido pelo porteiro"
}
```

6. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "entregaId": "ENT-xyz789",
  "message": "Entrega concluida com sucesso"
}
```

---

#### PASSO 10: Verificar Status Final da Entrega

1. Repetir **PASSO 7**
2. Verificar que `status` mudou para `"CONCLUIDA"`
3. Verificar que apareceram os campos: `recebedor`, `dataRecebimento`, `observacoes`

---

### 2.3 Cenarios Alternativos

#### Cenario A: Devolver Entrega

**Pre-requisito**: A entrega deve estar com status `CONCLUIDA` (PASSO 9 executado)

1. Expandir `entrega-command-controller`
2. Clicar em **PUT** `/transporte/entregas/{id}/devolver`
3. Clicar em "Try it out"
4. Inserir o `entregaId`
5. No Request Body, colar:

```json
{
  "motivo": "Cliente recusou o recebimento",
  "dataDevolucao": "2025-11-20T16:00:00",
  "responsavel": "MOT-001"
}
```

6. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "entregaId": "ENT-xyz789",
  "message": "Entrega devolvida com sucesso"
}
```

**Validacao**:
- Consultar a entrega novamente (PASSO 7)
- Verificar que `status` mudou para `"DEVOLVIDA"`
- Verificar que apareceram: `motivoDevolucao`, `dataDevolucao`, `responsavelDevolucao`

---

#### Cenario B: Marcar Entrega como Extraviada

**Pre-requisito**: A entrega deve estar com status `EM_TRANSITO` (PASSO 8 executado, mas NAO executar PASSO 9)

**IMPORTANTE**: Para testar este cenario, crie uma NOVA entrega:
- Repetir PASSOS 1-8 com um novo `pedidoId` (ex: `PED-002`)
- NAO executar o PASSO 9 (concluir entrega)
- Entao executar este cenario

1. Expandir `entrega-command-controller`
2. Clicar em **PUT** `/transporte/entregas/{id}/marcar-extraviada`
3. Clicar em "Try it out"
4. Inserir o `entregaId` da nova entrega em transito
5. No Request Body, colar:

```json
{
  "motivo": "Veiculo sofreu acidente, carga perdida",
  "dataExtravio": "2025-11-20T18:00:00",
  "localUltimoRegistro": "Rodovia Anhanguera, KM 45"
}
```

6. Clicar em "Execute"

**Resposta esperada** (Status 200 OK):
```json
{
  "entregaId": "ENT-abc456",
  "message": "Entrega marcada como extraviada"
}
```

**Validacao**:
- Consultar a entrega novamente (PASSO 7)
- Verificar que `status` mudou para `"EXTRAVIADA"`
- Verificar que apareceram: `motivoExtravio`, `dataExtravio`, `localUltimoRegistro`

---

## 3. Health Checks

### 3.1 Verificacao de Saude dos Servicos

#### Microsservicos (Docker Compose ou Kubernetes com port-forward)

```bash
# Almoxarifado
curl http://localhost:8082/actuator/health

# Transporte
curl http://localhost:8083/actuator/health
```

**Resposta esperada** (todos com `"status":"UP"`):
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

#### Infraestrutura

```bash
# PostgreSQL (apenas Docker Compose)
# Conectar ao banco e verificar status
docker exec -it petfriends-postgres psql -U postgres -c "SELECT version();"

# Kafka (apenas Docker Compose)
# Listar topics
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Zipkin
curl http://localhost:9411/health

# Elasticsearch (apenas Docker Compose)
curl http://localhost:9200/_cluster/health?pretty

# Kibana (apenas Docker Compose)
curl http://localhost:5601/api/status
```

### 3.2 URLs de Referencia Rapida

| Servico | URL | Descricao |
|---------|-----|-----------|
| **Almoxarifado - Swagger** | http://localhost:8082/swagger-ui.html | API Documentation |
| **Transporte - Swagger** | http://localhost:8083/swagger-ui.html | API Documentation |
| **Almoxarifado - Health** | http://localhost:8082/actuator/health | Health Check |
| **Transporte - Health** | http://localhost:8083/actuator/health | Health Check |
| **Almoxarifado - Metrics** | http://localhost:8082/actuator/prometheus | Prometheus Metrics |
| **Transporte - Metrics** | http://localhost:8083/actuator/prometheus | Prometheus Metrics |
| **Zipkin** | http://localhost:9411 | Distributed Tracing |
| **Kibana** | http://localhost:5601 | Log Visualization |
| **Elasticsearch** | http://localhost:9200 | Search Engine API |

### 3.3 Verificar Status no Docker Compose

```bash
# Ver status de todos os containers
docker-compose ps

# Ver logs de saude
docker-compose logs -f | grep -i "health"

# Verificar health check de um container especifico
docker inspect petfriends-almoxarifado | grep -A 10 Health
```

### 3.4 Verificar Status no Kubernetes

```bash
# Ver status dos pods
kubectl get pods -n petfriends

# Ver eventos do namespace
kubectl get events -n petfriends --sort-by='.lastTimestamp'

# Verificar readiness/liveness probes
kubectl describe pod <pod-name> -n petfriends | grep -A 10 Liveness
kubectl describe pod <pod-name> -n petfriends | grep -A 10 Readiness

# Ver logs de um pod especifico
kubectl logs -f <pod-name> -n petfriends
```

### 3.5 Comandos de Diagnostico

```bash
# Verificar conectividade entre servicos (Docker Compose)
docker-compose exec almoxarifado curl http://transporte:8083/actuator/health

# Verificar conectividade com PostgreSQL (Docker Compose)
docker-compose exec almoxarifado nc -zv postgres 5432

# Verificar conectividade com Kafka (Docker Compose)
docker-compose exec almoxarifado nc -zv kafka 9092

# Verificar uso de memoria dos containers
docker stats --no-stream

# Verificar uso de recursos no Kubernetes
kubectl top pods -n petfriends
kubectl top nodes
```

---

## 4. Observabilidade

### 4.1 Zipkin - Distributed Tracing

O Zipkin permite rastrear requisicoes atraves de multiplos microsservicos, mostrando latencia, dependencias e gargalos de performance.

#### 4.1.1 Acessar o Zipkin UI

Abra no navegador: **http://localhost:9411**

#### 4.1.2 Buscar Traces

**Opcao 1: Busca Simples**
1. Na pagina inicial, clique em **"RUN QUERY"** (sem filtros)
2. Isso mostrara os ultimos traces capturados

**Opcao 2: Busca por Servico**
1. No campo **"Service Name"**, selecione:
   - `petfriends-almoxarifado` - para ver traces do almoxarifado
   - `petfriends-transporte` - para ver traces do transporte
2. Clique em **"RUN QUERY"**

**Opcao 3: Busca por Operacao (Span Name)**
1. Selecione um servico
2. No campo **"Span Name"**, escolha operacoes especificas como:
   - `GET /almoxarifado/reservas/{id}`
   - `POST /almoxarifado/reservas`
   - `PUT /transporte/entregas/{id}/iniciar`
3. Clique em **"RUN QUERY"**

**Opcao 4: Busca com Filtros de Tempo**
1. Ajuste o periodo usando os botoes rapidos:
   - **"Last 15m"** - Ultimos 15 minutos
   - **"Last Hour"** - Ultima hora
   - **"Last Day"** - Ultimo dia
2. Ou defina um periodo customizado nos campos de data/hora
3. Clique em **"RUN QUERY"**

#### 4.1.3 Analisar um Trace Especifico

Apos executar uma busca, voce vera uma lista de traces:

1. **Entender a Lista de Traces:**
   ```
   [Tempo Total]  [Nome do Servico]  [Operacao]  [Numero de Spans]
   142ms  petfriends-transporte  PUT /transporte/entregas/{id}/concluir  4 spans
   ```

2. **Clicar em um trace** para abrir os detalhes

3. **Visualizacao em Timeline (padrao):**
   ```
   |-- petfriends-transporte: PUT /transporte/entregas/{id}/concluir (142ms)
   |  |-- r2dbc: SELECT FROM entrega_view (15ms)
   |  |-- kafka: publish to transporte-events (8ms)
   |  |-- r2dbc: UPDATE entrega_view (12ms)
   ```

4. **Informacoes de cada Span:**
   - **Duracao** - Quanto tempo levou
   - **Inicio relativo** - Quando comecou em relacao ao trace
   - **Tags** - Metadados (HTTP status, metodo, URL, etc.)
   - **Annotations** - Eventos pontuais (Client Send, Server Receive, etc.)

#### 4.1.4 Analisar Trace da SAGA Completa

Execute o fluxo completo (PASSOS 1-10 da secao 2.2) e entao visualize a SAGA no Zipkin:

1. No Zipkin, busque por **"Service Name: petfriends-transporte"**
2. Procure por traces que mostrem **multiplos servicos**
3. Clique em um trace que tenha passado por ambos os servicos

**O que observar:**
- Latencia total da SAGA (tempo do evento ate processamento completo)
- Tempo de publicacao no Kafka
- Tempo de consumo e processamento de eventos
- Operacoes de banco de dados (R2DBC)
- Gargalos de performance

#### 4.1.5 Identificar Problemas de Performance

**Buscar por Traces Lentos:**
1. Na busca principal, adicione filtro:
   - **"Min Duration"**: ex: `500ms` (traces com mais de 500ms)
2. Clique em **"RUN QUERY"**
3. Analise os traces mais lentos

**Buscar por Erros:**
1. No campo **"Tags"**, adicione:
   - `error=true`
2. Clique em **"RUN QUERY"**
3. Voce vera apenas traces que tiveram erros

**Analisar Latencia por Componente:**
1. Abra um trace lento
2. Ordene os spans por duracao
3. Identifique qual operacao esta causando lentidao:
   - Queries de banco lentas?
   - Tempo de rede com Kafka alto?
   - Processamento de evento demorado?

---

### 4.2 Micrometer Tracing

O Micrometer Tracing e o substituto moderno do Spring Cloud Sleuth. Ele adiciona automaticamente **Trace ID** e **Span ID** a todas as requisicoes, permitindo rastrear uma transacao atraves de multiplos servicos.

#### 4.2.1 Conceitos Fundamentais

- **Trace ID**: Identificador unico de uma transacao completa atraves de todos os servicos
- **Span ID**: Identificador de uma operacao individual dentro de um trace
- **Propagacao de Contexto**: Os IDs sao automaticamente propagados via HTTP headers e mensagens Kafka

#### 4.2.2 Visualizar Trace/Span IDs nos Logs

**Formato dos Logs:**
```
INFO [petfriends-transporte,a1b2c3d4e5f6g7h8,1234567890abcdef] Handling event...
      |                      |                |
   service name           Trace ID         Span ID
```
#### 4.2.3 Correlacionar Logs com Traces do Zipkin

**Passo a Passo:**

1. **Encontre o Trace ID nos logs:**
```bash
docker-compose logs transporte | grep "Creating delivery"

# Saida:
DEBUG [petfriends-transporte,f1e2d3c4b5a6,span007] Creating delivery
```

2. **Copie o Trace ID:** `f1e2d3c4b5a6`

3. **No Zipkin UI (http://localhost:9411):**
   - Cole o Trace ID no campo de busca
   - Clique em "RUN QUERY"
   - Voce vera o trace visual correspondente aos logs

4. **Correlacao Completa:**
```
Logs:        [petfriends-transporte,f1e2d3c4b5a6,span007]
              |
Zipkin UI:   Trace ID: f1e2d3c4b5a6
              |-- Span: span005 (publish event)
              |-- Span: span006 (kafka consumer)
              |-- Span: span007 (create delivery)
```

---

### 4.3 ELK Stack

O ELK Stack (Elasticsearch, Logstash, Kibana) centraliza e visualiza logs de todos os microsservicos em um unico lugar.

#### 4.3.1 Componentes

- **Elasticsearch**: Armazena e indexa logs
- **Logstash**: Coleta e processa logs (porta 5000)
- **Kibana**: Interface web para visualizacao (porta 5601)

#### 4.3.2 Acessar o Kibana

1. Abra no navegador: **http://localhost:5601**
2. Aguarde inicializacao (1-2 minutos)

#### 4.3.3 Configurar (Primeira Vez)

**Criar Data View:**

1. Menu hamburguer → **Management → Stack Management**
2. **Kibana → Data Views**
3. Clicar em **"Create data view"**
4. Preencher:
   - **Name**: `PetFriends Logs`
   - **Index pattern**: `petfriends-logs-*`
   - **Timestamp field**: `@timestamp`
5. Clicar em **"Create data view"**

**Abrir Discover:**

1. Menu hamburguer → **Analytics → Discover**
2. Logs aparecerao em tempo real

#### 4.3.4 Pesquisar Logs

**Busca Simples:**
```
# Buscar por palavra
"reserva"

# Buscar por servico
service:"petfriends-almoxarifado"

# Buscar por nivel
level:"ERROR"

# Buscar por Trace ID
trace_id:"abc123def456"
```

**Busca Avancada (KQL):**
```
# Erros no transporte
service:"petfriends-transporte" AND level:"ERROR"

# Logs de pedido especifico
message:*PED-001*

# Logs de eventos Kafka
message:*ItensSeparados* OR message:*EntregaAgendada*
```

#### 4.3.5 Correlacionar ELK com Zipkin

**Workflow:**

1. **No Kibana**: Encontre um log interessante
2. **Copie o Trace ID** do campo `trace_id`
3. **No Zipkin** (http://localhost:9411): Cole o Trace ID na busca
4. **Analise Completa**:
   - **Kibana**: detalhes de cada log (texto completo)
   - **Zipkin**: visao visual de latencia e spans

**Quando Usar:**

| Necessidade | Ferramenta | Por que |
|-------------|------------|---------|
| Latencia de requisicoes | Zipkin | Timeline visual |
| Mensagens de log detalhadas | Kibana | Busca de texto completo |
| Analise de erros | Kibana | Filtros avancados |
| Debugging de fluxo | Ambos | Correlacao via Trace ID |

#### 4.3.6 Comandos Uteis

**Verificar Status:**
```bash
# Elasticsearch
curl http://localhost:9200/_cluster/health?pretty

# Kibana
curl http://localhost:5601/api/status
```

**Ver Logs dos Componentes:**
```bash
# Elasticsearch
docker-compose logs -f elasticsearch

# Logstash
docker-compose logs -f logstash

# Kibana
docker-compose logs -f kibana
```

---

**Desenvolvido com Domain-Driven Design, Event Sourcing e CQRS**