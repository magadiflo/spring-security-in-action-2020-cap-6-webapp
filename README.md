# [Pág. 135] Práctico: una pequeña aplicación web segura

![img.png](./assets/authentication-flow-in-this-app.png)

![main-page-after-a-successful-login.png](./assets/main-page-after-a-successful-login.png)

![entities-relationship-diagram.png](./assets/entities-relationship-diagram.png)

## [Pág. 141] Implementación de la gestión de usuarios

En esta sección, analizamos la implementación de la parte de administración de usuarios de la aplicación. **El
componente representativo de la gestión de usuarios con respecto a Spring Security es UserDetailsService.** Debe
implementar al menos este contrato para indicarle a Spring Security cómo recuperar los detalles de sus usuarios.