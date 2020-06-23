package co.edu.javeriana.algoritmos.proyecto;

/**
 * Clase que representa una casilla dentro del tablero de Batalla Naval.  
 * NO CAMBIE NADA EN ESTE ARCHIVO, NI LO MUEVA, NI LO COPIE.
 * @author danilo
 *
 */
public class Casilla 
{
	private int fila, columna;

	public Casilla( int fila, int columna ) 
	{
		super();
		this.fila = fila;
		this.columna = columna;
	}

	public int getFila() 
	{
            return fila;
	}

	public int getColumna() 
	{
            return columna;
	}
	
	public boolean validaParaDimension( int dimension ) 
	{
	    return fila >= 0 && fila < dimension && columna >= 0 && columna < dimension;
	}

    @Override
    public int hashCode() 
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + columna;
        result = prime * result + fila;
        return result;
    }

    @Override
    public boolean equals( Object obj ) 
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Casilla other = ( Casilla ) obj;
        if ( columna != other.columna )
            return false;
        if ( fila != other.fila )
            return false;
        return true;
    }
    
    public String toString() 
    {
        return "(" + fila + ", " + columna + ")";
    }
	
}
