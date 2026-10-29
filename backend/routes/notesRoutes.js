/*
 * Rutas de administración de notas de NotasApp.
 *
 * Este archivo implementa las operaciones CRUD:
 *
 * CREATE: Crear notas.
 * READ: Consultar notas.
 * UPDATE: Actualizar notas.
 * DELETE: Eliminar notas.
 *
 * Todas las operaciones requieren autenticación
 * mediante un token JWT válido.
 */

// Importamos Express.
const express = require("express");

// Importamos nuestra conexión SQLite.
const db = require("../database");

// Importamos el middleware de autenticación.
const authMiddleware = require("../middleware/authMiddleware");

// Creamos el enrutador.
const router = express.Router();

/*
 * Aplicamos el middleware a todas las rutas.
 *
 * Esto impide que usuarios sin autenticación
 * puedan acceder a las notas.
 */
router.use(authMiddleware);

/*
 * Endpoint: GET /api/notes
 *
 * Consulta todas las notas pertenecientes
 * al usuario que inició sesión.
 */
router.get("/", (req, res) => {
    try {
        // Obtenemos el ID del usuario autenticado.
        const userId = req.user.id;

        /*
         * Consultamos únicamente las notas
         * que pertenecen a este usuario.
         *
         * Mostramos primero las más recientes.
         */
        const notes = db.prepare(`
            SELECT
                id,
                title,
                content,
                created_at,
                updated_at
            FROM notes
            WHERE user_id = ?
            ORDER BY created_at DESC, id DESC
        `).all(userId);

        // Devolvemos las notas en formato JSON.
        return res.status(200).json({
            message: "Notas obtenidas correctamente",
            notes: notes
        });

    } catch (error) {
        console.error("Error al consultar notas:", error);

        return res.status(500).json({
            message: "Error interno al consultar las notas"
        });
    }
});

/*
 * Endpoint: POST /api/notes
 *
 * Permite crear una nota nueva.
 *
 * Recibe:
 * title: Título de la nota.
 * content: Contenido de la nota.
 */
router.post("/", (req, res) => {
    try {
        // Obtenemos los datos enviados.
        const { title, content } = req.body || {};

        // Validamos los tipos de datos.
        if (
            typeof title !== "string" ||
            typeof content !== "string"
        ) {
            return res.status(400).json({
                message: "El título y el contenido son obligatorios"
            });
        }

        // Eliminamos espacios innecesarios.
        const cleanTitle = title.trim();
        const cleanContent = content.trim();

        // Comprobamos que los campos no estén vacíos.
        if (!cleanTitle || !cleanContent) {
            return res.status(400).json({
                message: "El título y el contenido son obligatorios"
            });
        }

        /*
         * Guardamos la nota en SQLite.
         *
         * Asociamos automáticamente la nota
         * con el usuario que inició sesión.
         */
        const result = db.prepare(`
            INSERT INTO notes (user_id, title, content)
            VALUES (?, ?, ?)
        `).run(req.user.id, cleanTitle, cleanContent);

        // Consultamos la nota recién creada.
        const newNote = db.prepare(`
            SELECT id, title, content, created_at, updated_at
            FROM notes
            WHERE id = ? AND user_id = ?
        `).get(result.lastInsertRowid, req.user.id);

        return res.status(201).json({
            message: "Nota creada correctamente",
            note: newNote
        });

    } catch (error) {
        console.error("Error al crear nota:", error);

        return res.status(500).json({
            message: "Error interno al crear la nota"
        });
    }
});

/*
 * Endpoint: PUT /api/notes/:id
 *
 * Permite modificar el título y contenido
 * de una nota existente.
 *
 * Solo el propietario puede modificarla.
 */
router.put("/:id", (req, res) => {
    try {
        // Obtenemos el ID de la nota.
        const noteId = Number(req.params.id);

        // Validamos que sea un ID válido.
        if (!Number.isSafeInteger(noteId) || noteId <= 0) {
            return res.status(400).json({
                message: "ID de nota inválido"
            });
        }

        // Obtenemos los nuevos datos.
        const { title, content } = req.body || {};

        // Validamos los tipos de datos.
        if (
            typeof title !== "string" ||
            typeof content !== "string"
        ) {
            return res.status(400).json({
                message: "El título y el contenido son obligatorios"
            });
        }

        // Limpiamos los campos.
        const cleanTitle = title.trim();
        const cleanContent = content.trim();

        if (!cleanTitle || !cleanContent) {
            return res.status(400).json({
                message: "El título y el contenido son obligatorios"
            });
        }

        /*
         * Actualizamos únicamente una nota
         * perteneciente al usuario autenticado.
         *
         * El filtro user_id impide modificar
         * notas de otros usuarios.
         */
        const result = db.prepare(`
            UPDATE notes
            SET
                title = ?,
                content = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND user_id = ?
        `).run(
            cleanTitle,
            cleanContent,
            noteId,
            req.user.id
        );

        // Verificamos si se actualizó alguna nota.
        if (result.changes === 0) {
            return res.status(404).json({
                message: "Nota no encontrada"
            });
        }

        // Consultamos la nota actualizada.
        const updatedNote = db.prepare(`
            SELECT id, title, content, created_at, updated_at
            FROM notes
            WHERE id = ? AND user_id = ?
        `).get(noteId, req.user.id);

        return res.status(200).json({
            message: "Nota actualizada correctamente",
            note: updatedNote
        });

    } catch (error) {
        console.error("Error al actualizar nota:", error);

        return res.status(500).json({
            message: "Error interno al actualizar la nota"
        });
    }
});

/*
 * Endpoint: DELETE /api/notes/:id
 *
 * Permite eliminar una nota existente.
 *
 * Solo el propietario puede eliminarla.
 */
router.delete("/:id", (req, res) => {
    try {
        // Obtenemos el ID de la nota.
        const noteId = Number(req.params.id);

        // Validamos que el ID sea correcto.
        if (!Number.isSafeInteger(noteId) || noteId <= 0) {
            return res.status(400).json({
                message: "ID de nota inválido"
            });
        }

        /*
         * Eliminamos la nota únicamente
         * si pertenece al usuario autenticado.
         */
        const result = db.prepare(`
            DELETE FROM notes
            WHERE id = ? AND user_id = ?
        `).run(noteId, req.user.id);

        // Comprobamos si la nota existía.
        if (result.changes === 0) {
            return res.status(404).json({
                message: "Nota no encontrada"
            });
        }

        return res.status(200).json({
            message: "Nota eliminada correctamente"
        });

    } catch (error) {
        console.error("Error al eliminar nota:", error);

        return res.status(500).json({
            message: "Error interno al eliminar la nota"
        });
    }
});

/*
 * Exportamos las rutas de notas.
 *
 * Posteriormente las conectaremos
 * al servidor principal mediante server.js.
 */
module.exports = router;
