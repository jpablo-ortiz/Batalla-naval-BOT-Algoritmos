package co.edu.javeriana.algoritmos.proyecto;

/**
 * Enum que representa los posibles valores de la respuesta a una jugada del rival.
 * @author danilo
 * NO CAMBIE NADA EN ESTE ARCHIVO, NI LO MUEVA, NI LO COPIE.
 */
public enum RespuestaJugada 
{
	AGUA( "agua" ), IMPACTO( "impacto" ), HUNDIDO( "hundido" );
	
	private String letrero;

	private RespuestaJugada( String letrero ) 
	{
		this.letrero = letrero;
	}
	
	public String getLetrero() 
	{
		return letrero;
	}
	
}
