/*
 * Middleware de autenticación de NotasApp.
 *
 * Este archivo verifica que las solicitudes
 * incluyan un token JWT válido.
 *
 * Su función es proteger las rutas privadas
 * para que solo los usuarios autenticados
 * puedan acceder a sus propias notas.
 */

// Importamos la biblioteca para verificar JWT.
const jwt = require("jsonwebtoken");

// Importamos la conexión a SQLite.
const db = require("../database");

/*
 * Función que verifica la autenticación.
 *
 * Recibe:
 * req: solicitud del cliente.
 * res: respuesta del servidor.
 * next: permite continuar a la siguiente función.
 */
function authMiddleware(req, res, next) {
    try {
        /*
         * Obtenemos el encabezado Authorization.
         *
         * El cliente debe enviarlo así:
         * Authorization: Bearer TOKEN
         */
        const authHeader = req.headers.authorization;

        // Verificamos que exista el encabezado.
        if (!authHeader) {
            return res.status(401).json({
                message: "Token de autenticación requerido"
            });
        }

        // Separamos el tipo de autenticación y el token.
        const parts = authHeader.split(" ");

        /*
         * Comprobamos que el formato sea correcto
         * y que el tipo de autenticación sea Bearer.
         */
        if (
            parts.length !== 2 ||
            parts[0] !== "Bearer" ||
            !parts[1]
        ) {
            return res.status(401).json({
                message: "Formato de token incorrecto"
            });
        }

        // Obtenemos el token JWT.
        const token = parts[1];

        // Comprobamos que exista la clave secreta.
        if (!process.env.JWT_SECRET) {
            console.error("JWT_SECRET no configurado");

            return res.status(500).json({
                message: "Error de configuración del servidor"
            });
        }

        /*
         * Verificamos la firma y la fecha
         * de expiración del token JWT.
         */
        const decoded = jwt.verify(
            token,
            process.env.JWT_SECRET
        );

        /*
         * Comprobamos que el usuario del token
         * todavía exista en la base de datos.
         */
        const user = db.prepare(`
            SELECT id, name, email
            FROM users
            WHERE id = ?
        `).get(decoded.id);

        // Rechazamos usuarios inexistentes.
        if (!user) {
            return res.status(401).json({
                message: "Usuario no autorizado"
            });
        }

        /*
         * Guardamos los datos del usuario
         * en la solicitud actual.
         *
         * Las rutas de notas podrán utilizar
         * req.user.id para identificar al dueño.
         */
        req.user = user;

        // Permitimos continuar a la ruta protegida.
        next();

    } catch (error) {
        /*
         * Si el token es inválido o ha expirado,
         * rechazamos la solicitud.
         */
        if (
            error.name === "JsonWebTokenError" ||
            error.name === "TokenExpiredError" ||
            error.name === "NotBeforeError"
        ) {
            return res.status(401).json({
                message: "Token inválido o expirado"
            });
        }

        // Registramos otros errores del servidor.
        console.error("Error de autenticación:", error);

        return res.status(500).json({
            message: "Error interno de autenticación"
        });
    }
}

// Exportamos el middleware.
module.exports = authMiddleware;
