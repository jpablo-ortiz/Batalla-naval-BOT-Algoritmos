package jugadorbatallanaval;

import Estructuras.Grafo.GrafoAbstracto;
import Estructuras.Grafo.GrafoListaAdyacencia;
import Estructuras.Grafo.GrafoListaAristas;
import Estructuras.Grafo.GrafoMatrizAdyacencia;
import Estructuras.Vertice;
import co.edu.javeriana.algoritmos.proyecto.Casilla;
import co.edu.javeriana.algoritmos.proyecto.RespuestaJugada;
import co.edu.javeriana.algoritmos.proyecto.ResumenTablero;
import co.edu.javeriana.algoritmos.proyecto.Tablero;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableroJugador implements Tablero
{
    //===============================//
    //========== Atributos ==========//
    //===============================//

    private GrafoAbstracto tableroGrafo; //con Nodos Casilla de barco, Barcos y Nodos Posicion
    private int[] casillasOcupadasFila, casillasOcupadasColumna;
    private ArrayList<Vertice<Barco>> barcosEnTablero;

    //===============================//
    //======== Constructores ========//
    //===============================//
    /**
     *
     * @param dimension
     * @param barcos
     * @param tipoGrafo ListaAdyacencia = 1,ListaArista = 2,MatrizAdyacencia = 3
     */
    public TableroJugador(int dimension, int[] barcos, int tipoGrafo)
    {
        this.barcosEnTablero = new ArrayList<>();
        //3 implementaciones de grafo para probar la mejor
        switch(tipoGrafo)
        {
            case 1:
                tableroGrafo = new GrafoListaAdyacencia();
                break;
            case 2:
                tableroGrafo = new GrafoListaAristas();
                break;
            case 3:
                tableroGrafo = new GrafoMatrizAdyacencia();
                break;
        }
        crearGrafo(dimension, barcos);
    }

    //===============================//
    //=========== Métodos ===========//
    //===============================//
    public void crearGrafo(int dimension, int[] barcos)
    {
        //Agregar Vertices
        for(int i = 0; i < dimension; i++)
        {
            tableroGrafo.agregarVertice(new Vertice(new Posicion(i, 0, true), tableroGrafo.vertices.size(), 0, "Circulo"));
        }

        //Inicializacion Aristas
        for(int i = 0; i < dimension - 1; i++)
        {
            tableroGrafo.agregarArista(tableroGrafo.vertices.get(i), tableroGrafo.vertices.get(i + 1), 1.0, false);
        }

        //agregar Barcos
        //agregarBarcos(barcos);
        agregarBarcosReal(barcos, dimension);

        //Crear tableroResumen
        crearTableroResumen(dimension);

        //Graficar
        //mostrarGrafo();
    }

    public void crearTableroResumen(int dimension)
    {
        casillasOcupadasFila = new int[dimension];
        casillasOcupadasColumna = new int[dimension];

        int acumFila, acumColumna;
        for(int i = 0; i < dimension; i++)
        {
            acumColumna = 0;
            acumFila = 0;
            Vertice<Posicion> numeroPos = (Vertice<Posicion>) tableroGrafo.vertices.get(i);
            for(Vertice<Posicion> casilla : tableroGrafo.vecinos(tableroGrafo.vertices.get(i)))
            {
                if(!casilla.getValor().esCasillaPosicion())
                {
                    if(casilla.getValor().getFila() == numeroPos.getValor().getFila())
                    {
                        acumFila++;
                    }
                    if(casilla.getValor().getColumna() == numeroPos.getValor().getFila())
                    {
                        acumColumna++;
                    }
                }
            }
            casillasOcupadasFila[i] = acumFila;
            casillasOcupadasColumna[i] = acumColumna;
        }
    }

    //Metodos para ingresar Barco
    public void ponerAristasPorDefectoParaBarcoArrayList(ArrayList<Vertice<Posicion>> temporal)
    {
        //Ciclo que pone las aristas que relacionan cada casilla del barco
        for(int i = tableroGrafo.vertices.size() - temporal.size() - 1; i < tableroGrafo.vertices.size() - 1; i++)
        {
            Vertice<Posicion> posActual = tableroGrafo.vertices.get(i);

            if(i < tableroGrafo.vertices.size() - 2)
            {
                Vertice<Posicion> posSiguiente = tableroGrafo.vertices.get(i + 1);
                tableroGrafo.agregarArista(posActual, posSiguiente, 1.0, true);
            }
            //Relacionar con Nodos posicion en  tablero
            tableroGrafo.agregarArista(tableroGrafo.vertices.get(posActual.getValor().getFila()), tableroGrafo.vertices.get(i), posActual.getValor().getFila() + 1, true);
            tableroGrafo.agregarArista(tableroGrafo.vertices.get(posActual.getValor().getColumna()), tableroGrafo.vertices.get(i), posActual.getValor().getColumna() + 1, true);

            //Relacionar con nodo Barco
            tableroGrafo.agregarArista(tableroGrafo.vertices.get(tableroGrafo.vertices.size() - 1), tableroGrafo.vertices.get(i), 0.0, true);

        }
    }

    public int rando(int n)
    {
        // create instance of Random class
        Random rand = new Random();

        // Generate random integers in range 0 to n
        int rand_int1 = rand.nextInt(n);

        return rand_int1;

    }

    public boolean verificarEstadoPosicion(int fila, int columna, int dimension)
    {
        if(fila < 0 || fila >= dimension || columna < 0 || columna >= dimension)
        {
            return true;
        }

        for(Vertice vertice : tableroGrafo.vecinos(tableroGrafo.vertices.get(fila)))
        {
            //System.out.println(vertice.getValor());
            if(vertice.getValor() instanceof Posicion)
            {
                Posicion posicionVertice = (Posicion) vertice.getValor();
                if(!posicionVertice.esCasillaPosicion() && posicionVertice.getFila() == fila && posicionVertice.getColumna() == columna)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean verificarCabeBarco(int fila, int columna, int orientacion, int tam, int dimension)
    {

        //verificar que el barco quepa en el tablero
        if(orientacion == 0 && columna + tam >= dimension)
        {
            return false;
        }
        if(orientacion == 1 && fila + tam >= dimension)
        {
            return false;
        }
        if(orientacion == 2 && (columna + tam >= dimension || fila + tam >= dimension))
        {
            return false;
        }

        //verificar que ninguna de las posiciones que ocupará el barco esté ocupada
        boolean hayCasillaOcupada = false;
        int[][] casillasBarco = calcularCasillasBarco(fila, columna, orientacion, tam);
        for(int i = 0; i < tam && !hayCasillaOcupada; i++)
        {
            int filabarco = casillasBarco[i][0];
            int columnabarco = casillasBarco[i][1];

            for(int j = -1; j <= 1; j++)
            {
                for(int k = -1; k <= 1; k++)
                {
                    if(verificarEstadoPosicion(filabarco + j, columnabarco + k, dimension))
                    {
                        hayCasillaOcupada = true;
                        break;
                    }
                }
                if(hayCasillaOcupada)
                {
                    break;
                }
            }

        }
        return !hayCasillaOcupada;

    }

    public int[][] calcularCasillasBarco(int fila, int columna, int orientacion, int tam)
    {
        int deltafilas = 0, deltacolumnas = 0;
        //filas
        if(orientacion == 0)
        {
            deltafilas = 0;
        }
        else if(orientacion == 1 || orientacion == 2)
        {
            deltafilas = 1;
        }
        //columnas
        if(orientacion == 0 || orientacion == 2)
        {
            deltacolumnas = 1;
        }
        else
        {
            deltacolumnas = 0;
        }

        int[][] casillasOcupadas = new int[tam][2];
        for(int i = 0; i < tam; i++)
        {
            casillasOcupadas[i][0] = fila + i * deltafilas;
            casillasOcupadas[i][1] = columna + i * deltacolumnas;
        }
        return casillasOcupadas;

    }

    public void agregarBarcosReal(int[] barcos, int dimension)
    {
        int orientacion = 0, fila = 0, columna = 0, tam;
        
        int intento = 0;
        boolean tiempoAgotado = false;
        long inicio = System.currentTimeMillis();
        long fin; 
        double tiempo = 0;
        for(int i = 0; i < barcos.length; i++)
        {
            //ArrayList contenedor de los vertices de la posicion en la que van a estar las casillas del barco
            ArrayList<Vertice<Posicion>> temporalVerticesBarco = new ArrayList<>();
            boolean posicionEncontrada = false;
            tam = barcos[i];

            while(!posicionEncontrada)
            {
                //escoje una orientacion
                orientacion = rando(3);
                //escojer una fila y una columna
                fila = rando(dimension);
                columna = rando(dimension);

                //verificar si el barco puede ubicarse en esa posición
                posicionEncontrada = verificarCabeBarco(fila, columna, orientacion, tam, dimension);
                
                //Medir tiempos
                fin = System.currentTimeMillis();
                tiempo = (double) ((fin - inicio) / 1);
                if(tiempo > 1 * 500)//Que no se pase de 500ms
                {
                    tiempoAgotado = true;
                    break;
                }
            }
            if(tiempoAgotado)
            {
                break;
            }
            int[][] casillasBarco = calcularCasillasBarco(fila, columna, orientacion, tam);

            for(int j = 0; j < tam; j++)
            {
                int filabarco = casillasBarco[j][0];
                int columnabarco = casillasBarco[j][1];
                Vertice vTemp = new Vertice(new Posicion(filabarco, columnabarco, false), tableroGrafo.vertices.size(), 0, "Cuadrado");
                temporalVerticesBarco.add(vTemp);
                tableroGrafo.agregarVertice(vTemp);
            }

            //Agregar el nuevo barco a el arreglo barcosEnTablero (variable global en esta clase) con parametro de temporal que contiene las casillas
            Vertice<Barco> barcoTemp = new Vertice(new Barco(temporalVerticesBarco), tableroGrafo.vertices.size(), 0, "Rectangulo");
            barcosEnTablero.add(barcoTemp);
            tableroGrafo.agregarVertice(barcoTemp);

            //Proceso para poner relaciones por defecto al ingresar un barco
            ponerAristasPorDefectoParaBarcoArrayList(temporalVerticesBarco);
        }
        System.out.println(tiempo + " Milisegundos");
        if(tiempoAgotado)
        {
            System.out.println("REPETIR");
            volverAPonerBarcos(barcos, dimension);
        }
    }

    public void volverAPonerBarcos(int[] barcos, int dimension)
    {
        tableroGrafo = new GrafoListaAdyacencia();
        barcosEnTablero = new ArrayList<>();

        //Agregar Vertices
        for(int i = 0; i < dimension; i++)
        {
            tableroGrafo.agregarVertice(new Vertice(new Posicion(i, 0, true), tableroGrafo.vertices.size(), 0, "Circulo"));
        }

        //Inicializacion Aristas
        for(int i = 0; i < dimension - 1; i++)
        {
            tableroGrafo.agregarArista(tableroGrafo.vertices.get(i), tableroGrafo.vertices.get(i + 1), 1.0, false);
        }

        //agregar Barcos
        //agregarBarcos(barcos);
        agregarBarcosReal(barcos, dimension);
    }

    //Quitar para el proyecto final (solo grafica para entender)
    public void mostrarGrafo()
    {
        boolean dirigido = false;
        tableroGrafo.graficar(dirigido);
    }

    public void imprimir()
    {
        List<Casilla> barcoImprimir = obtenerCasillasOcupadasPorBarco(0);
        System.out.println(barcoImprimir);
        barcoImprimir = obtenerCasillasOcupadasPorBarco(1);
        System.out.println(barcoImprimir);
    }

    public Vertice<Barco> esDelBarco(Vertice<Posicion> casillaActual)
    {
        for(Vertice vertice : tableroGrafo.vecinos(casillaActual))
        {
            if(vertice.getValor() instanceof Barco)
            {
                return vertice;
            }
        }
        return null;
    }

    //===============================//
    //======= Métodos Override ======//
    //===============================//
    @Override
    public List<Casilla> obtenerCasillasOcupadasPorBarco(int numeroBarco)
    {
        List<Casilla> res = new ArrayList<>();

        for(Vertice<Posicion> vertice : barcosEnTablero.get(numeroBarco).getValor().getPosicionCasillasBarco())
        {
            res.add(vertice.getValor());
        }
        return res;
    }

    @Override
    public RespuestaJugada dispararACasilla(Casilla casilla)
    {
        ArrayList<Vertice> casillasFilaColumna = tableroGrafo.vecinos(tableroGrafo.vertices.get(casilla.getFila()));
        for(Vertice<Posicion> casillaActual : casillasFilaColumna)
        {
            if(casillaActual.getValor().getFila() == casilla.getFila() && casillaActual.getValor().getColumna() == casilla.getColumna() && !casillaActual.getValor().esCasillaPosicion())
            {
                if(casillasOcupadasFila[casilla.getFila()] > 0)
                {
                    casillasOcupadasFila[casilla.getFila()]--;
                }
                if(casillasOcupadasColumna[casilla.getColumna()] > 0)
                {
                    casillasOcupadasColumna[casilla.getColumna()]--;
                }
                casillaActual.getValor().setDestruido(true);
                if(esDelBarco(casillaActual).getValor().actualizarEstado())
                {
                    //System.out.println("FUERA BARCOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                    return RespuestaJugada.HUNDIDO;
                }
                else
                {
                    return RespuestaJugada.IMPACTO;
                }
            }
        }
        return RespuestaJugada.AGUA;
    }

    @Override
    public int numeroBarcosNoHundidos()
    {
        int noHundidos = 0;
        for(Vertice<Barco> verticeBarco : barcosEnTablero)
        {
            if(!verticeBarco.getValor().esDestruido())
            {
                noHundidos++;
            }
        }
        return noHundidos;
    }

    @Override
    public ResumenTablero obtenerResumen()
    {
        return new ResumenTablero(casillasOcupadasFila, casillasOcupadasColumna);
    }

    //===============================//
    //====== Getters y Setters ======//
    //===============================//
    public GrafoAbstracto getTableroGrafo()
    {
        return tableroGrafo;
    }

    public void setTableroGrafo(GrafoAbstracto tableroGrafo)
    {
        this.tableroGrafo = tableroGrafo;
    }

    public int[] getCasillasOcupadasFila()
    {
        return casillasOcupadasFila;
    }

    public void setCasillasOcupadasFila(int[] casillasOcupadasFila)
    {
        this.casillasOcupadasFila = casillasOcupadasFila;
    }

    public int[] getCasillasOcupadasColumna()
    {
        return casillasOcupadasColumna;
    }

    public void setCasillasOcupadasColumna(int[] casillasOcupadasColumna)
    {
        this.casillasOcupadasColumna = casillasOcupadasColumna;
    }

    public ArrayList<Vertice<Barco>> getBarcosEnTablero()
    {
        return barcosEnTablero;
    }

    public void setBarcosEnTablero(ArrayList<Vertice<Barco>> barcosEnTablero)
    {
        this.barcosEnTablero = barcosEnTablero;
    }

}
