package com.miempresa.pm2e3359.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.miempresa.pm2e3359.R
import com.miempresa.pm2e3359.models.Herramienta
import com.miempresa.pm2e3359.utils.DateUtils
import java.util.*

class HerramientaAdapter(
    private var tools: List<Herramienta>,
    private val onItemClick: (Herramienta) -> Unit
) : RecyclerView.Adapter<HerramientaAdapter.ToolViewHolder>() {

    class ToolViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgTool: ImageView = view.findViewById(R.id.imgTool)
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
        val txtAsignado: TextView = view.findViewById(R.id.txtAsignado)
        val txtFechaFin: TextView = view.findViewById(R.id.txtFechaFin)
        val indicator: View = view.findViewById(R.id.indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_herramienta, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val tool = tools[position]
        holder.txtNombre.text = tool.nombre
        holder.txtEstado.text = tool.estado

        if (tool.foto_uri != null) {
            holder.imgTool.setImageURI(Uri.parse(tool.foto_uri))
        } else {
            holder.imgTool.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        if (tool.estado == "ASIGNADA") {
            holder.txtAsignado.visibility = View.VISIBLE
            holder.txtAsignado.text = "Asignado a: ${tool.tecnicoAsignado ?: "N/A"}"
            holder.txtFechaFin.visibility = View.VISIBLE
            holder.txtFechaFin.text = "Entrega: ${tool.fechaFin ?: "N/A"}"
        } else {
            holder.txtAsignado.visibility = View.GONE
            holder.txtFechaFin.visibility = View.GONE
        }

        val color = getColorForTool(tool, holder.itemView.context)
        holder.indicator.background.setTint(color)
        holder.txtEstado.setTextColor(color)

        holder.itemView.setOnClickListener { onItemClick(tool) }
    }

    override fun getItemCount() = tools.size

    fun updateList(newList: List<Herramienta>) {
        tools = newList
        notifyDataSetChanged()
    }

    private fun getColorForTool(tool: Herramienta, context: Context): Int {
        // Verde: Devuelta (tiene fecha_devolucion)
        if (tool.fechaDevolucion != null && tool.fechaDevolucion!!.isNotEmpty()) {
            return ContextCompat.getColor(context, R.color.status_green)
        }

        // Gris: Disponible (sin asignación activa)
        if (tool.estado == "DISPONIBLE") {
            return ContextCompat.getColor(context, R.color.status_grey)
        }
        
        // Si está ASIGNADA, verificamos fechas
        val dateFin = DateUtils.parseDate(tool.fechaFin) ?: return ContextCompat.getColor(context, R.color.status_grey)
        val hoy = Date()
        
        // Rojo: Asignada y vencida (hoy > fecha_fin, sin fecha_devolucion)
        if (hoy.after(dateFin)) {
            return ContextCompat.getColor(context, R.color.status_red)
        }
        
        // Ámbar: Asignada, faltan <= 48 horas para fecha_fin
        val diffHours = DateUtils.getHoursDifference(dateFin, hoy)
        if (diffHours >= 0 && diffHours <= 48) {
            return ContextCompat.getColor(context, R.color.status_amber)
        }
        
        // Por defecto si está asignada pero falta más de 48h, podemos usar un azul o dejarlo Gris
        return ContextCompat.getColor(context, R.color.status_grey)
    }
}
