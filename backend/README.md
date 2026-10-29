
# NotasApp - Backend

Backend REST desarrollado con Node.js, Express y SQLite para una aplicación Android de gestión de notas personales.

## Tecnologías utilizadas

- Node.js
- Express
- SQLite
- JSON Web Token (JWT)
- bcryptjs

## Funcionalidades

- Registro de usuarios.
- Inicio de sesión con autenticación JWT.
- Consulta de notas personales.
- Creación de notas.
- Actualización de notas.
- Eliminación de notas.
- Protección de endpoints mediante autenticación.

## Requisitos

- Node.js y npm instalados.
- Puerto 3000 disponible.

## Instalación

1. Abrir una terminal en la carpeta del backend.
2. Instalar las dependencias:

```bash
npm install
```

3. Crear un archivo `.env` a partir de `.env.example`.
4. Configurar una clave privada y segura en `JWT_SECRET`.
5. Iniciar el servidor:

```bash
node server.js
```

## Base de datos

Se utiliza SQLite.

El archivo `database.js` crea automáticamente la base de datos y las tablas necesarias cuando se inicia el backend.

No es necesario importar manualmente una base de datos.

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/auth/register | Registrar usuario |
| POST | /api/auth/login | Iniciar sesión |
| GET | /api/notes | Consultar notas |
| POST | /api/notes | Crear nota |
| PUT | /api/notes/:id | Actualizar nota |
| DELETE | /api/notes/:id | Eliminar nota |
| GET | /api/status | Verificar el servidor y SQLite |

Las operaciones de notas requieren el encabezado:

```text
Authorization: Bearer TOKEN_JWT
```

## Conexión con Android

Durante el desarrollo se utilizó un emulador de Android Studio y la dirección:

```text
http://127.0.0.1:3000/
```

Para conectar el emulador con el backend local, ejecutar:

```bash
adb reverse tcp:3000 tcp:3000
```

El emulador debe estar iniciado y reconocido por ADB.

## Consideraciones

- El archivo `.env` no se incluye por seguridad.
- La base de datos local no se publica en GitHub.
- Es necesario registrar un usuario antes de iniciar sesión.
- La configuración HTTP y ADB Reverse corresponde al entorno de desarrollo local.
