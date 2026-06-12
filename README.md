# Sistema de Licitações Governamentais - Frontend

> 🖥️ Interface Web para o Sistema de Licitações Governamentais (EditaisGOV) desenvolvida com Spring Boot, Thymeleaf e Bootstrap 5.

Esta é a aplicação frontend do Sistema de Licitações, projetada para oferecer uma experiência de usuário segura, responsiva e altamente interativa tanto para Compradores Governamentais (criação e gerenciamento de editais) quanto para Fornecedores (envio de lances). Ela consome uma API REST (Backend) e gerencia estado e segurança por meio de tokens JWT.

## 🚀 Funcionalidades Principais

* **Validação de Formulário em Tempo Real:** Requisições AJAX com debounce para verificar disponibilidade de e-mail e nome instantaneamente durante o cadastro, evitando envios desnecessários.
* **Indicador Dinâmico de Força de Senha:** Feedback visual indicando requisitos não atendidos (maiúsculas, números, caracteres especiais) enquanto o usuário digita.
* **Navegação Baseada em Perfil:** Elementos da interface e botões de ação se adaptam dinamicamente conforme o usuário seja `FORNECEDOR` ou Comprador.
* **Integração com JWT:** Tratamento de autenticação via token. Extrai dados do usuário (como nome e perfil) diretamente do payload do token para personalizar a interface (ex.: saudações na Navbar).
* **Layout Responsivo:** Abordagem mobile-first com Bootstrap 5, padronizando layouts via fragmentos Thymeleaf para cabeçalhos, rodapés e componentes estruturais.

## 🛠️ Tecnologias Utilizadas

* **Java 17+**
* **Spring Boot** (Spring Web, Spring MVC)
* **Thymeleaf** (Renderização server-side e template engine)
* **Bootstrap 5.3** (Estilização UI/UX e Grid)
* **Vanilla JavaScript** (Manipulação do DOM e Fetch API)
* **HTML5 / CSS3**

## ⚙️ Pré-requisitos

Antes de começar, certifique-se de que os seguintes requisitos foram atendidos:
* **Java 17** ou superior instalado.
* **Maven** instalado.
* O **Backend do Sistema de Licitações** (a API) em execução localmente em `http://localhost:8080` (ou atualize a URL base do RestClient conforme necessário).

## 📥 Como Executar a Aplicação

1. **Clone o repositório:**
   ```bash
   git clone <url-do-seu-repositorio>
   cd biddingFrontEnd
   ```

2. **Certifique-se de que o Backend está em execução:**
   Por ser a aplicação cliente, o backend deve estar ativo para lidar com autenticação, criação de editais e processos de licitação.

3. **Build e Execução:**
   Usando o Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   Ou executando o `FrontendApplication.java` diretamente pela sua IDE (Eclipse / IntelliJ IDEA).

4. **Acesse a aplicação:**
   Abra o navegador e acesse:
   ```text
   http://localhost:8081 
   ```
   *(Obs.: Certifique-se de que roda em uma porta diferente do backend — geralmente 8081 se o backend estiver na 8080)*

## 📂 Estrutura do Projeto

```text
src/main/
├── java/com/bidding/system/frontend/
│   ├── controller/      # Controllers Web (roteamento, renderização da UI)
│   ├── model/           # DTOs mapeando dados do backend
│   └── service/         # Camada de integração com a API (RestClient)
└── resources/
    ├── static/          # CSS, JS, Imagens
    └── templates/       # Views HTML Thymeleaf (layout, login, registrar, editais...)
```

## 📝 Licença

Este projeto tem fins educacionais como parte de um Curso de Desenvolvimento Web com Java.
