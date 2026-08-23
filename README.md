# Sistema de Gestión de Inventario Multisucursal (SIM) #

Como solución se propuso crear un sistema de inventario donde se llevan las entradas de productos por sucursal, para que de esta manera se pueda acceder por medio de informes sobre el stock de la sucursal a buscar. De igual manera, en la entidad Productos se encuentra un campo llamado stock donde se puede visualizar el stock general del producto. Esto permitirá llevar un mejor control y organización en la empresa.

Para este proyecto web se utilizó la arquitectura cliente - servidor y tecnologías como Spring Boot con Java 17+ para el backend y Angular para el frontend, además de librerías como Tailwind CSS con un diseño sencillo y práctico para que la experiencia de usuario fuera cómoda en las operaciones principales.

> **Nota:** este repositorio contiene únicamente la **API REST (backend)**. El frontend Angular se desarrolla en el repositorio independiente **`sistema-inventario-api`**, el cual cuenta con su propio README. La API ya tiene configurado CORS para permitir orígenes como `http://localhost:4200` y los dominios de despliegue en Vercel.

# Organización del sistema #

El sistema está dividido entre:

1. **Sucursales:** registro de las sucursales de la empresa con nombre, dirección física y estado para habilitarlas o deshabilitarlas. Un código las diferencia cuando en una misma dirección operan varias.

2. **Productos:** registro de productos con SKU único como código, nombre descriptivo, stock general, marca (catálogo obligatorio) y estado para deshabilitarlos.

3. **Entradas:** registro histórico de productos que ingresan en cada punto de venta, con cantidad y precio de entrada; al guardar o eliminar una entrada se actualiza automáticamente el stock correspondiente.

4. **Marcas:** catálogo administrable asociado a los productos, con código único, nombre y estado.

# Tecnologías #

- Java 17
- Spring Boot 4.0.5 (Web MVC, Data JPA / Hibernate, Validation)
- Maven (incluido vía wrapper `mvnw`)
- Base de datos: SQL Server (desarrollo) / PostgreSQL (producción)
- Lombok y ModelMapper

# Estructura del proyecto #

```
src/main/java/com/sginventario/inventarioWS/
├── controller/    # Controladores REST
├── service/
│   ├── interfaces/   # Contratos de servicio
│   └── imp/          # Implementaciones
├── repository/    # Repositorios Spring Data JPA
├── entity/        # Entidades JPA
├── dto/           # Objetos de transferencia de datos
├── exception/     # ApiResponse y manejo global de errores
├── config/        # Configuración de CORS
└── utility/       # Configuración de ModelMapper
```

# Endpoints #

| Módulo | Ruta base | Operaciones |
|---|---|---|
| Productos | `/api/productos` | GET (listar), GET `/activos`, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |
| Sucursales | `/api/sucursales` | GET (listar), GET `/activos`, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |
| Marcas | `/api/marcas` | GET (listar), GET `/activos`, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |
| Entradas | `/api/entradas` | POST (registrar entrada), GET (listar activas), DELETE `/{id}` (baja lógica) |

Todas las respuestas utilizan el formato común `ApiResponse` (`success`, `message`, `data`).

# Ejecución del proyecto localmente #

## Requerimientos básicos ##

1. Instalar JDK 17 o superior
2. SQL Server en ejecución con la base de datos `InventarioDB`
3. Clonar el repositorio

## Servicio API ##

4. En consola (Windows/bash):
    - Ejecutar `.\mvnw.cmd clean spring-boot:run`

   Linux/Mac:
    - Ejecutar `./mvnw clean spring-boot:run`

La API estará disponible en: `http://localhost:8080/api`

# Configuración #

El perfil activo se define en `src/main/resources/application.properties` (`spring.profiles.active`):

| Perfil | Archivo | Base de datos | Credenciales |
|---|---|---|---|
| `dev` (actual) | `application-dev.properties` | SQL Server local (`InventarioDB`) | Definidas en el archivo |
| `prod` | `application-prod.properties` | PostgreSQL | Variables de entorno: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` |

Puerto del servidor: `8080` (en producción configurable mediante `PORT`).

# Base de datos #

El esquema se gestiona con Hibernate mediante `spring.jpa.hibernate.ddl-auto=update`. Tablas generadas: `producto`, `sucursal`, `entrada`, `detalle_entrada` y `marca`.

> **Pendiente de limpieza:** la columna legado `producto.marca` (varchar) puede eliminarse una vez confirmada la migración a la nueva relación `producto.marca_id`.

# Reglas de negocio y validaciones #

- **Productos:** SKU único (normalizado a mayúsculas); unicidad por combinación nombre + marca; la marca es obligatoria y debe existir en el catálogo.
- **Sucursales y Marcas:** código único global y nombre único entre registros activos (sin distinguir mayúsculas/minúsculas).
- **Eliminación:** física para productos, sucursales y marcas (una marca no puede eliminarse si tiene productos asociados); lógica para entradas, revirtiendo el stock de los productos involucrados.
- **Entradas:** al registrar una entrada se incrementa el stock de cada producto; al eliminarla se descuenta.

# Docker #

El `Dockerfile` incluido compila el proyecto con Maven (imagen `maven:3.8.5-eclipse-temurin-17`) y genera una imagen final basada en `eclipse-temurin:17-jre` que expone el puerto `8080`.

```bash
docker build -t inventario-ws .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=... \
  -e SPRING_DATASOURCE_USERNAME=... \
  -e SPRING_DATASOURCE_PASSWORD=... \
  inventario-ws
```
