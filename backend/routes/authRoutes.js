/*
 * Rutas de autenticación de NotasApp.
 *
 * Este archivo permite registrar usuarios
 * e iniciar sesión mediante el backend.
 *
 * Utilizamos bcryptjs para proteger contraseñas
 * y jsonwebtoken para generar tokens JWT.
 */

// Importamos las bibliotecas necesarias.
const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");

// Importamos nuestra conexión SQLite.
const db = require("../database");

// Creamos un enrutador de Express.
const router = express.Router();

/*
 * Endpoint: POST /api/auth/register
 *
 * Recibe el nombre, correo y contraseña
 * de un usuario nuevo.
 *
 * Valida los datos, comprueba que el correo
 * no esté registrado y guarda el usuario.
 */
router.post("/register", async (req, res) => {
    try {
        // Obtenemos los datos enviados por el cliente.
        const { name, email, password } = req.body || {};

        // Comprobamos que todos los campos sean textos.
        if (
            typeof name !== "string" ||
            typeof email !== "string" ||
            typeof password !== "string"
        ) {
            return res.status(400).json({
                message: "Todos los campos son obligatorios"
            });
        }

        // Eliminamos espacios innecesarios.
        const cleanName = name.trim();
        const cleanEmail = email.trim().toLowerCase();

        // Validamos que los campos no estén vacíos.
        if (!cleanName || !cleanEmail || !password.trim()) {
            return res.status(400).json({
                message: "Todos los campos son obligatorios"
            });
        }

        // Validamos el formato básico del correo.
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(cleanEmail)) {
            return res.status(400).json({
                message: "El correo electrónico no es válido"
            });
        }

        // Exigimos una contraseña de al menos 8 caracteres.
        if (password.length < 8) {
            return res.status(400).json({
                message: "La contraseña debe tener al menos 8 caracteres"
            });
        }

        // Verificamos si el correo ya está registrado.
        const existingUser = db.prepare(
            "SELECT id FROM users WHERE email = ?"
        ).get(cleanEmail);

        if (existingUser) {
            return res.status(409).json({
                message: "Este correo ya está registrado"
            });
        }

        /*
         * Protegemos la contraseña con bcrypt.
         *
         * El número 12 representa el factor de costo.
         * Nunca guardamos la contraseña original.
         */
        const hashedPassword = await bcrypt.hash(password, 12);

        /*
         * Insertamos el usuario en SQLite.
         *
         * Los parámetros ? evitan concatenar
         * directamente los datos en SQL.
         */
        const result = db.prepare(`
            INSERT INTO users (name, email, password)
            VALUES (?, ?, ?)
        `).run(cleanName, cleanEmail, hashedPassword);

        // Respondemos sin revelar la contraseña.
        return res.status(201).json({
            message: "Usuario registrado correctamente",
            user: {
                id: Number(result.lastInsertRowid),
                name: cleanName,
                email: cleanEmail
            }
        });

    } catch (error) {
        // Registramos el error en el servidor.
        console.error("Error al registrar usuario:", error);

        // No exponemos detalles internos al cliente.
        return res.status(500).json({
            message: "Error interno al registrar el usuario"
        });
    }
});

/*
 * Endpoint: POST /api/auth/login
 *
 * Permite iniciar sesión utilizando
 * el correo electrónico y la contraseña.
 *
 * Si las credenciales son correctas,
 * genera un token JWT válido por 24 horas.
 */
router.post("/login", async (req, res) => {
    try {
        // Obtenemos las credenciales enviadas.
        const { email, password } = req.body || {};

        /*
         * Validamos que ambos campos existan
         * y contengan información de tipo texto.
         */
        if (
            typeof email !== "string" ||
            typeof password !== "string" ||
            !email.trim() ||
            !password
        ) {
            return res.status(400).json({
                message: "Correo y contraseña son obligatorios"
            });
        }

        // Normalizamos el correo electrónico.
        const cleanEmail = email.trim().toLowerCase();

        /*
         * Buscamos al usuario en SQLite.
         *
         * Utilizamos una consulta parametrizada
         * para evitar inyección SQL.
         */
        const user = db.prepare(`
            SELECT id, name, email, password
            FROM users
            WHERE email = ?
        `).get(cleanEmail);

        /*
         * Si el usuario no existe,
         * devolvemos un mensaje genérico.
         */
        if (!user) {
            return res.status(401).json({
                message: "Correo o contraseña incorrectos"
            });
        }

        /*
         * Comparamos la contraseña ingresada
         * con el hash almacenado en SQLite.
         */
        const validPassword = await bcrypt.compare(
            password,
            user.password
        );

        // Rechazamos las contraseñas incorrectas.
        if (!validPassword) {
            return res.status(401).json({
                message: "Correo o contraseña incorrectos"
            });
        }

        /*
         * Verificamos que exista una clave secreta
         * válida en nuestro archivo .env.
         */
        if (
            !process.env.JWT_SECRET ||
            process.env.JWT_SECRET ===
                "REEMPLAZAR_CON_UNA_CLAVE_ALEATORIA_SEGURA"
        ) {
            console.error("JWT_SECRET no configurado");

            return res.status(500).json({
                message: "Error de configuración del servidor"
            });
        }

        /*
         * Generamos el token JWT.
         *
         * Incluimos el ID del usuario para
         * identificarlo en futuras peticiones.
         *
         * El token tendrá una duración de 24 horas.
         */
        const token = jwt.sign(
            { id: user.id },
            process.env.JWT_SECRET,
            { expiresIn: "24h" }
        );

        /*
         * Devolvemos el token y los datos
         * básicos del usuario autenticado.
         *
         * Nunca devolvemos la contraseña
         * ni su hash.
         */
        return res.status(200).json({
            message: "Inicio de sesión exitoso",
            token: token,
            user: {
                id: user.id,
                name: user.name,
                email: user.email
            }
        });

    } catch (error) {
        // Registramos el error en el servidor.
        console.error("Error al iniciar sesión:", error);

        // No exponemos detalles internos al cliente.
        return res.status(500).json({
            message: "Error interno al iniciar sesión"
        });
    }
});

/*
 * Exportamos las rutas de autenticación.
 *
 * server.js utiliza este enrutador para
 * habilitar los endpoints bajo /api/auth.
 */
module.exports = router;
