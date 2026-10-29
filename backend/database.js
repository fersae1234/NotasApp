/*
 * Base de datos de NotasApp.
 *
 * Utilizamos SQLite para almacenar
 * la información de los usuarios y sus notas.
 *
 * La biblioteca better-sqlite3 permite
 * ejecutar consultas SQL desde Node.js.
 */

// Importamos las bibliotecas necesarias.
const Database = require("better-sqlite3");
const path = require("path");

/*
 * Definimos la ubicación de la base de datos.
 *
 * El archivo notasapp.db se creará
 * automáticamente dentro de nuestro backend.
 */
const dbPath = path.join(__dirname, "notasapp.db");

// Abrimos o creamos la base de datos.
const db = new Database(dbPath);

/*
 * Activamos las claves foráneas.
 *
 * Esto permite mantener la relación
 * entre los usuarios y sus notas.
 */
db.pragma("foreign_keys = ON");

/*
 * Creamos la tabla de usuarios.
 *
 * id: identificador único del usuario.
 * name: nombre del usuario.
 * email: correo electrónico único.
 * password: contraseña protegida con hash.
 * created_at: fecha de registro.
 */
db.exec(`
    CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        email TEXT NOT NULL UNIQUE,
        password TEXT NOT NULL,
        created_at TEXT DEFAULT CURRENT_TIMESTAMP
    )
`);

/*
 * Creamos la tabla de notas.
 *
 * id: identificador único de la nota.
 * user_id: usuario propietario de la nota.
 * title: título de la nota.
 * content: contenido de la nota.
 * created_at: fecha de creación.
 * updated_at: fecha de modificación.
 *
 * ON DELETE CASCADE permite eliminar
 * las notas si se elimina su usuario.
 */
db.exec(`
    CREATE TABLE IF NOT EXISTS notes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER NOT NULL,
        title TEXT NOT NULL,
        content TEXT NOT NULL,
        created_at TEXT DEFAULT CURRENT_TIMESTAMP,
        updated_at TEXT DEFAULT CURRENT_TIMESTAMP,

        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
    )
`);

/*
 * Exportamos la conexión.
 *
 * Los demás archivos del backend podrán
 * utilizarla para consultar, insertar,
 * modificar y eliminar información.
 */
module.exports = db;
