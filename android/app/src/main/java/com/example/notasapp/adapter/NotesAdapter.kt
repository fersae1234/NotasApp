package com.example.notasapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notasapp.R
import com.example.notasapp.model.Note

/*
 * Adaptador encargado de mostrar las notas
 * del usuario dentro de un RecyclerView.
 *
 * Cada elemento utiliza el diseño item_note.xml
 * que creamos anteriormente.
 *
 * También permite detectar cuándo el usuario
 * presiona los botones Editar o Eliminar.
 */
class NotesAdapter(
    private val onEditClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // Lista de notas que se mostrarán en pantalla.
    private var notes: List<Note> = emptyList()

    /*
     * Representa los elementos visuales
     * de cada tarjeta de nota.
     */
    class NoteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView =
            itemView.findViewById(R.id.txtNoteTitle)

        val content: TextView =
            itemView.findViewById(R.id.txtNoteContent)

        val date: TextView =
            itemView.findViewById(R.id.txtNoteDate)

        val editButton: Button =
            itemView.findViewById(R.id.btnEditNote)

        val deleteButton: Button =
            itemView.findViewById(R.id.btnDeleteNote)
    }

    /*
     * Crear una tarjeta utilizando item_note.xml.
     *
     * RecyclerView llama a esta función cuando
     * necesita mostrar un nuevo elemento.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)

        return NoteViewHolder(view)
    }

    /*
     * Colocar los datos de cada nota
     * en los elementos de su tarjeta.
     */
    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {

        val note = notes[position]

        // Mostrar el título y contenido.
        holder.title.text = note.title
        holder.content.text = note.content

        // Mostrar la fecha de actualización.
        holder.date.text =
            "Actualizada: ${note.updated_at}"

        /*
         * Enviar la nota seleccionada cuando
         * el usuario presione Editar.
         */
        holder.editButton.setOnClickListener {
            onEditClick(note)
        }

        /*
         * Enviar la nota seleccionada cuando
         * el usuario presione Eliminar.
         */
        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }
    }

    /*
     * Indicar cuántas notas existen
     * para que RecyclerView pueda mostrarlas.
     */
    override fun getItemCount(): Int {
        return notes.size
    }

    /*
     * Actualizar las notas que aparecen
     * en la pantalla.
     *
     * Se llama cuando recibimos una lista
     * nueva desde nuestro backend.
     */
    fun updateNotes(newNotes: List<Note>) {

        notes = newNotes

        // Solicitar que RecyclerView actualice la lista.
        notifyDataSetChanged()
    }
}
