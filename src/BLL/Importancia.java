package BLL; // Capa de Lógica de Negocio. < BLL.

public enum Importancia {
    // ( Enum de Prioridades de Actividades. )
    // Clavo este grupo cerrado para ordenar las actividades del cronograma según su relevancia.
    // Evito meter un int ( 1, 2, 3. ) o un String libre que después en el 'order by' de MySQL te volves loco.
    
	BAJA,  // Actividades secundarias. ( ej: tiempo libre, recreación, otros. )
	MEDIA, // Bloques comunes. ( ej: almuerzo, desayunos, recepciones. )
	ALTA   // Eventos principales del cronograma que no se pueden pisar con nada. ( ej: charlas, ceremonias, actos. )
}