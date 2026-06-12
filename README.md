# 🏛️ EditaisGOV — Frontend

> 🖥️ Interface Web do Sistema de Licitações Governamentais, desenvolvida com Spring Boot, Thymeleaf e Bootstrap 5. Consome a API REST do backend e gerencia autenticação via JWT.

Este repositório é a **camada de interface do sistema**. Para a API que alimenta esta aplicação, consulte o repositório do **[Backend →](https://github.com/jotapeor/biddingBackEnd)**.

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Descrição |
|---|---|
| Java 17+ | Linguagem principal |
| Spring Boot | Framework base (Spring Web, Spring MVC) |
| Thymeleaf | Renderização server-side e template engine |
| Bootstrap 5.3 | Estilização UI/UX e Grid responsivo |
| Vanilla JavaScript | Manipulação do DOM e Fetch API |
| Maven | Gerenciador de dependências |

---

## ✨ Funcionalidades

- **Validação em Tempo Real:** Requisições AJAX com debounce para verificar disponibilidade de e-mail e nome durante o cadastro.
- **Indicador de Força de Senha:** Feedback visual com requisitos (maiúsculas, números, caracteres especiais) enquanto o usuário digita.
- **Navegação Baseada em Perfil:** Interface e botões se adaptam conforme o perfil do usuário (`FORNECEDOR` ou Comprador).
- **Integração com JWT:** Extrai dados do usuário diretamente do payload do token para personalizar a interface.
- **Layout Responsivo:** Abordagem mobile-first com fragmentos Thymeleaf reutilizáveis.

---

## 🛠️ Pré-requisitos

- [JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/)
- **[Backend do sistema](https://github.com/jotapeor/biddingBackEnd)** em execução em `http://localhost:8080`

---

## ⚙️ Como Executar Localmente

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/jotapeor/bidding-frontend.git
   cd bidding-frontend
   ```

2. **Certifique-se de que o Backend está em execução:**
   Esta aplicação consome a API REST. Siga as instruções no **[repositório do Backend](https://github.com/jotapeor/biddingBackEnd)** antes de prosseguir.

3. **Inicie a aplicação:**
   ```bash
   # Com Maven Wrapper
   ./mvnw spring-boot:run
   ```
   Ou execute `FrontendApplication.java` diretamente pela sua IDE (Eclipse / IntelliJ IDEA).

4. **Acesse a aplicação:**
   ```
   http://localhost:8081
   ```

> ⚠️ Certifique-se de que o frontend rode em uma porta diferente do backend (padrão: `8081`).

---

## 📂 Estrutura de Pacotes

```text
src/main/
├── java/com/bidding/system/frontend/
│   ├── controller/    # Controllers Web (roteamento e renderização da UI)
│   ├── model/         # DTOs mapeando dados do backend
│   └── service/       # Camada de integração com a API (RestClient)
└── resources/
    ├── static/        # CSS, JS, Imagens
    └── templates/     # Views HTML Thymeleaf (layout, login, registrar, editais...)
```

---

## 🤝 Contribuindo

Sinta-se à vontade para abrir uma *issue* antes de enviar um *pull request*, especialmente para mudanças maiores.

---

## 📝 Licença

Este projeto tem fins educacionais como parte de um curso de Desenvolvimento Web com Java.
