package BLL; // < Capa de Lógica de Negocio.

public enum CategoriaActividad {
	// ( Enum Fixeado. )
	// Clavo una lista fija de palabras clave. Son constantes obligatorias.
	// Uso esto a propósito para que nadie pueda meter un texto como "charliiis" o "tallerzito" en la base de datos.
	CHARLA, TALLER, CONFERENCIA, RECREATIVA, COMIDA, CEREMONIA, OTRO
	// Hecho de otra manera, después cuando quiera hacer un SELECT con un WHERE en MySQL para filtrar las actividades, no me iba a encontrar los datos por culpa de esos tipeos raros.
}
