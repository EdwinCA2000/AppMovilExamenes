import java.sql.Date

data class Examen(
    val IdExamen: Int=-1,
    val TituloExamen: String,
    val DescripcionExamen: String,
    val FechaInicio: Date,
    val FechaFinal: Date,
    val TiempoExamen: Int,
    val EstadoExamen: Int
)
