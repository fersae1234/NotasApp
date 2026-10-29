/*
 * Servidor principal de NotasApp.
 *
 * Este archivo configura nuestro backend
 * desarrollado con Node.js y Express.
 *
 * Se encarga de inicializar SQLite,
 * configurar los middlewares y conectar
 * las rutas de nuestra API REST.
 */

// Importamos las bibliotecas necesarias.
const express = require("express");
const cors = require("cors");
require("dotenv").config();

/*
 * Importamos la conexión a SQLite.
 *
 * Al importar database.js se abre o crea
 * automáticamente el archivo notasapp.db.
 *
 * También se crean las tablas users y notes
 * si todavía no existen.
 */
const db = require("./database");

/*
 * Importamos las rutas de autenticación.
 *
 * Estas rutas permiten registrar usuarios
 * e iniciar sesión mediante tokens JWT.
 */
const authRoutes = require("./routes/authRoutes");

/*
 * Importamos las rutas de notas.
 *
 * Estas rutas permiten crear, consultar,
 * actualizar y eliminar notas.
 *
 * Todas están protegidas mediante
 * nuestro middleware de autenticación.
 */
const notesRoutes = require("./routes/notesRoutes");

// Creamos nuestra aplicación Express.
const app = express();

/*
 * Configuramos los middlewares.
 *
 * cors permite controlar solicitudes
 * provenientes de otros orígenes.
 *
 * express.json permite procesar los datos
 * enviados en formato JSON.
 */
app.use(cors());
app.use(express.json());

/*
 * Registramos las rutas de autenticación.
 *
 * Todas las rutas definidas en authRoutes.js
 * comenzarán con /api/auth.
 *
 * Ejemplos:
 * POST /api/auth/register
 * POST /api/auth/login
 */
app.use("/api/auth", authRoutes);

/*
 * Registramos las rutas de notas.
 *
 * Todas las rutas definidas en notesRoutes.js
 * comenzarán con /api/notes.
 *
 * Ejemplos:
 * GET    /api/notes
 * POST   /api/notes
 * PUT    /api/notes/:id
 * DELETE /api/notes/:id
 *
 * Estas operaciones requieren un token JWT.
 */
app.use("/api/notes", notesRoutes);

/*
 * Definimos el puerto del servidor.
 *
 * Utilizamos el puerto configurado en .env
 * o el puerto 3000 como alternativa.
 */
const PORT = process.env.PORT || 3000;

/*
 * Endpoint principal.
 *
 * Permite comprobar que nuestro backend
 * está funcionando correctamente.
 */
app.get("/", (req, res) => {
    res.json({
        message: "Bienvenido al backend de NotasApp",
        status: "Servidor funcionando correctamente"
    });
});

/*
 * Endpoint para verificar SQLite.
 *
 * Ejecuta una consulta sencilla para
 * comprobar que la base de datos responde.
 */
app.get("/api/status", (req, res) => {
    try {
        const result = db.prepare(
            "SELECT 1 AS connected"
        ).get();

        res.json({
            server: "NotasApp Backend",
            database: "SQLite",
            connected: result.connected === 1,
            message: "Base de datos conectada correctamente"
        });

    } catch (error) {
        console.error("Error al consultar SQLite:", error);

        res.status(500).json({
            connected: false,
            message: "Error al conectar con la base de datos"
        });
    }
});

/*
 * Iniciamos el servidor.
 *
 * Express escuchará las solicitudes HTTP
 * en el puerto configurado.
 */
app.listen(PORT, () => {
    console.log(
        `Servidor de NotasApp ejecutándose en http://localhost:${PORT}`
    );

    console.log(
        "Base de datos SQLite inicializada correctamente"
    );

    console.log(
        "Rutas de autenticación y notas disponibles"
    );
});
