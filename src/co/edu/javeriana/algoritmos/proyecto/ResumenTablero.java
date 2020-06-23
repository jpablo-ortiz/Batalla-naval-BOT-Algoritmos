package co.edu.javeriana.algoritmos.proyecto;

/**
 * Clase que representa el resumen del tablero que se le pasa al jugador rival
 * al inicio del juego.
 *
 * @author danilo NO CAMBIE NADA EN ESTE ARCHIVO, NI LO MUEVA, NI LO COPIE.
 */
public class ResumenTablero
{
    private int[] casillasOcupadasFila, casillasOcupadasColumna;

    public ResumenTablero(int[] casillasOcupadasFila, int[] casillasOcupadasColumna)
    {
        super();
        this.casillasOcupadasFila = new int[casillasOcupadasFila.length];
        System.arraycopy(casillasOcupadasFila, 0, this.casillasOcupadasFila, 0, casillasOcupadasFila.length);
        
        this.casillasOcupadasColumna = new int[casillasOcupadasColumna.length];
        System.arraycopy(casillasOcupadasColumna, 0, this.casillasOcupadasColumna, 0, casillasOcupadasColumna.length);
    }

    public int getCasillasOcupadasEnFila(int fila)
    {
        return casillasOcupadasFila[fila];
    }

    public int getCasillasOcupadasEnColumna(int columna)
    {
        return casillasOcupadasColumna[columna];
    }

}
