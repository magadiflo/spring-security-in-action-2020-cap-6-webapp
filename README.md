# [Pág. 135] Práctico: una pequeña aplicación web segura

![img.png](./assets/authentication-flow-in-this-app.png)

![main-page-after-a-successful-login.png](./assets/main-page-after-a-successful-login.png)

![entities-relationship-diagram.png](./assets/entities-relationship-diagram.png)

## [Pág. 141] Implementación de la gestión de usuarios

En esta sección, analizamos la implementación de la parte de administración de usuarios de la aplicación. **El
componente representativo de la gestión de usuarios con respecto a Spring Security es UserDetailsService.** Debe
implementar al menos este contrato para indicarle a Spring Security cómo recuperar los detalles de sus usuarios.

## [Pág. 146] Implementación de la lógica de autenticación personalizada

Habiendo completado la administración de usuarios y contraseñas, podemos comenzar a escribir una lógica de autenticación
personalizada. Para hacer esto, debemos implementar un AuthenticationProvider (listado 6.12) y registrarlo en la
arquitectura de autenticación de Spring Security. Las dependencias necesarias para escribir la lógica de autenticación
son la implementación de UserDetailsService y los dos codificadores de contraseña. Además de autoconectarlos, también
anulamos los métodos authenticate() y support().

**Implementamos el método supports() para especificar que el tipo de implementación de autenticación admitida es
UsernamePasswordAuthenticationToken.**

En nuestra clase personalizada **AuthenticationProviderService** que implementa el **AuthenticationProvider**,
agregamos como parte del fragmento de código, la versión moderna del **switch(...)**:

````java

@Service
public class AuthenticationProviderService implements AuthenticationProvider {
    /* more code */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        CustomUserDetails customUserDetails = this.userDetailsService.loadUserByUsername(username);

        return switch (customUserDetails.getUser().getAlgorithm()) {
            case BCRYPT -> this.checkPassword(customUserDetails, password, this.bCryptPasswordEncoder);
            case SCRYPT -> this.checkPassword(customUserDetails, password, this.sCryptPasswordEncoder);
            //default -> throw new  BadCredentialsException("Bad credentials");
        };

        //throw new  BadCredentialsException("Bad credentials");
    }
    /* more code */
}
````

En el código anterior comentamos el código que lanza los BadCredentialsException, eso es porque con la moderna
implementación del switch, el flujo no llegará hasta esas líneas. **¿Por qué?**, porque en el switch(), estamos
usando el **Enum EncryptionAlgorithm** y ese enum, tiene exactamente dos posibilidades: **BCRYPT o SCRYPT**.
Lo que significa que si en la BD se cambia el tipo de texto, por ejemplo de un BCRYPT a un ENCRYPT, o cualquier
otro valor que no tengamos definido en el enum EncryptionAlgorithm, al momento de que el Entity User haga el
mapeo con la tabla de la base de datos, ocurrirá un error en tiempo de ejecución.

En nuestra implementación del **AuthenticationProvider**, elegimos el PasswordEncoder que usamos para validar la
contraseña en función del valor del atributo del algoritmo del usuario. En el listado 6.14, encontrará la definición del
método checkPassword(). Este método utiliza el codificador de contraseña enviado como parámetro para validar que la
contraseña sin procesar recibida de la entrada del usuario coincida con la codificación en la base de datos. Si la
contraseña es válida, devuelve una instancia de una implementación del contrato de autenticación. La clase
UsernamePasswordAuthenticationToken es una implementación de la interfaz de autenticación. El constructor al que llamo
en el listado 6.14 también establece el valor autenticado en verdadero. **Este detalle es importante porque sabe que el
método authenticate() de AuthenticationProvider tiene que devolver una instancia autenticada.**

Registrando el AuthenticationProvider dentro de la clase de configuración

````java

@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationProviderService authenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(this.authenticationProvider);
    }
    /* more code */
}

````

En la clase de configuración, queremos establecer tanto la implementación de la autenticación en el método formLogin
como la ruta /main como la URL de éxito predeterminada, como se muestra en la siguiente lista. Queremos implementar esta
ruta como la página principal de la aplicación web.

````java

@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {
    /* more code */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .defaultSuccessUrl("/main", true);
        http.authorizeRequests().anyRequest().authenticated();
    }
    /* more code */
}

````

## [Pág. 148] Implementando la página principal

Finalmente, ahora que tenemos la parte de seguridad en su lugar, podemos implementar la página principal de la
aplicación. Es una página simple que muestra todos los registros de la tabla de productos. Solo se puede acceder a esta
página después de que el usuario inicie sesión. Para obtener los registros del producto de la base de datos, debemos
agregar una clase de entidad Producto y una interfaz ProductRepository a nuestro proyecto. ``Recordar que hasta
este punto ya tengo implementado cierta parte de la página main con su controlador, su entity y su repository.``

Como ahora ya tenemos la funcionalidad de la autenticación, podemos acceder al **SecurityContext** para obtener
el username autenticado y mostrarlo en el main.html. Esto lo haremos a través del Authentication agregado como parámetro
del método main(...), recordar que Spring hace automáticamente la inyección de dependencia a dicho parámetro del método:

````java

@Controller
public class MainPageController {

    @Autowired
    private ProductService productService;

    @GetMapping(path = "/main")
    public String main(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("products", this.productService.findAll());
        return "main.html";
    }

}
````

**NOTA**
> Tuve que colocar el @Bean de BCryptPasswordEncoder y del SCryptPasswordEncoder en una clase de configuración aparte,
> porque mostraba un error de dependencia cíclica al estar estos beans dentro de la clase de configuración principal
> de Spring Security ya que se está incluyendo el AuthenticationProviderService que también hace uso de dichos beans.

## TAREA: Implementar un DelegatingPasswordEncoder como se vio en el capítulo 4

Para verificar que la implementación que hagamos sea correcta, aparte del BCryptPasswordEncoder, necesitamos usar otra
forma de codificación, en nuestro caso usaremos además el SCryptPasswordEncoder.

### Agregando dependencia en el pom.xml

Resulta que al codificar una contraseña con SCryptPasswordEncoder, me arrojaba la siguiente excepción:

````
java.lang.NoClassDefFoundError: org/bouncycastle/crypto/generators/SCrypt
````

Según investigué: **Todo esto sucede porque la técnica de codificación utilizada se basa en la función de derivación
de clave.SCryptPasswordEncoderscrypt**

**SOLUCIÓN**

Al agregar la siguiente dependencia en el archivo de compilación de su proyecto, se resolverá este problema.

````xml

<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
````

Ahora, crearemos un usuario que tenga este tipo de codificación y los agregamos al **import.sql**.

````
INSERT INTO users(id, username, password, algorithm) VALUES(3, 'usuario', '$e0801$V5/rB1qLny3sy2mplgkMoiarWK1gjDAx7I7zfu1Q16WNTSERBoo9y0NDNfTdUxfJ39u182z4laSu9khUMTLvzA==$B7H1w7h8+HfCB6RJ6PsUc8M/5XRIhSylAFlhxNTQSNU=', 'SCRYPT');
INSERT INTO authorities(name, user_id) VALUES('READ', 3);
````

Listo, hasta ahora tenemos en el **import.sql** usuarios con tipo de codificación BCRYPT y SCRYPT.

### Configurando el EncryptionTypesConfig

Nuestra clase de configuración que tiene los @Bean de BCryptPasswordEncoder y el SCryptPasswordEncoder, serán
reemplazadas por el **DelegatingPasswordEncoder**.

Como se vio en el capítulo 4, si tenemos varias formas de codificar, el DelegatingPasswordEncoder nos será de mucha
ayuda.

Podríamos usar solo siguiente @Bean que a continuación se muestra, pero si sabemos de antemano que **solo usaremos
dos tipos de codificación: bcrypt y el scrypt**, el código de abajo no es muy eficiente, puesto que el
**PasswordEncoderFactories** tiene definido todas las implementaciones estándar proporcionadas de PasswordEncoder:

````java

@Configuration
public class EncryptionTypesConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
````

En cambio, usaremos el siguiente código donde definimos exactamente los tipos de codificación que usaremos:

````java

@Configuration
public class EncryptionTypesConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }
}
````

### Modificando el enum EncryptionAlgorithm

Modificamos el enum EncryptionAlgorithm para que cada codificador tenga su identificador:

````java
public enum EncryptionAlgorithm {
    BCRYPT("bcrypt"), SCRYPT("scrypt");

    private final String idForEncode;

    EncryptionAlgorithm(String idForEncode) {
        this.idForEncode = idForEncode;
    }

    public final String getIdForEncode() {
        return this.idForEncode;
    }
}
````

### Modificando el AuthenticationProviderService

Modificaremos ahora nuestra personalización del AuthenticationProvider, que como recordaremos contiene la lógica de
autenticación.

Lo primero será modificar el @Autowired para poder inyectar el Bean del PasswordEncoder definido en la clase
EncryptionTypesConfig. En este caso, se tenía inyectado el BCryptPasswordEncoder y el SCryptPasswordEncoder, pero
como ahora tenemos un solo bean, será ese el que se inyecte.

Eliminamos el método que teníamos de verificación y modificamos el método authenticate(...), finalmente
toda la clase quedaría de la siguiente manera:

````java

@Service
public class AuthenticationProviderService implements AuthenticationProvider {
    @Autowired
    private JpaUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        CustomUserDetails customUserDetails = this.userDetailsService.loadUserByUsername(username);
        String passwordEncryptedWithEncodeType = String.format("{%s}%s", customUserDetails.getUser().getAlgorithm().getIdForEncode(), customUserDetails.getPassword());

        if (this.passwordEncoder.matches(password, passwordEncryptedWithEncodeType)) {
            return new UsernamePasswordAuthenticationToken(customUserDetails.getUsername(), customUserDetails.getPassword(), customUserDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Credenciales incorrectos! (el password no hace match)");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
````

Del código anterior, podemos ver que en el método authenticate() definimos la lógica para hacer la autenticación, además
algo muy importante y la razón de esta tarea es que ahora necesitamos construir algo similar a esto:

````
{bcrypt}$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG
````

o también

````
{scrypt}$e0801$V5/rB1qLny3sy2mplgkMoiarWK1gjDAx7I7zfu1Q16WNTSERBoo9y0NDNfTdUxfJ39u182z4laSu9khUMTLvzA==$B7H1w7h8+HfCB6RJ6PsUc8M/5XRIhSylAFlhxNTQSNU=
````

Y eso lo logramos con el siguiente código:

````
String passwordEncryptedWithEncodeType = String.format("{%s}%s", customUserDetails.getUser().getAlgorithm().getIdForEncode(), customUserDetails.getPassword());
````

De esta forma, si un usuario ya sea que tenga el tipo de codificación **bcrypt** o **scrypt**, con la definición del
bean donde retornamos un **DelegatingPasswordEncoder** haremos la magia, en automático este determinará el tipo de
codificación que usará para determinar si la contraseña es válida o no.