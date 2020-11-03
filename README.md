#helptask back-end
Utilize a ide intellij para rodar o projeto.
Clone o projeto do git ou baixe via rar.
Abra a ide Intellij e va em File-> open e abra a pasta do projeto, escolha o arquivo pom.xml e clique duas vezes nele, na tela que aparecer a seguir clique em open as project e depois em this new window.
Foi utilizado para banco de dados o mongodb, banco de dados não relacional.
instale o mongodb-windows-x86_64-4.4.1-signed sem alterar nada, apenas de next, após instalado abra ele e clique na opção connect para verificar os dados.
Após instalado va novamente a ide Intellij e de um Run na Application que se chama HelptaskApplication para subir a api back end.
Para testar a Api utilize o endereço http://localhost:8080/swagger-ui/index.html?configUrl=/api-docs/swagger-config
Após acessar o endereço vá ao endponint de autenticação feito com Spring Security e JWT(token type:ApiKey) a seguir.
POST ​/api​/auth endpoint que gera o token.
Clique no endpoint, vai abir a demonstração do endpoint.
Clique na opção try out.
Informe o seguinte json no body: {
  "email": "admin@admin.com",
  "password": "123456"
}
Após isso clique em execute.
No requeste body copie o valor sem as aspas do atributo token.
Vá ao início da pagina e clique na opção authorize.
Vai abrir uma janela(Pop-up).
Cole o valor copiado no campo Value e clique em authorize, pronto você esta autenticado.
Após isso vc pode utilizar os endpois para fazer as operações disponíveis na interface do swagger para a api.
Ao terminar suba a aplicação front-end que esta em angular como descrito no READMI do git da mesma para testar o fluxo da aplicação com front-end e back-end se conversando.
