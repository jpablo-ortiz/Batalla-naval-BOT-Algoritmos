package jugadorbatallanaval;

import Estructuras.Vertice;
import java.util.ArrayList;

public class Barco
{
    //===============================//
    //========== Atributos ==========//
    //===============================//
    
    private int tam;
    private ArrayList<Vertice<Posicion>> posicionCasillasBarco;
    private boolean destruido;
    
    //===============================//
    //======== Constructores ========//
    //===============================//
    
    public Barco(int tam, ArrayList<Vertice<Posicion>> posicionCasillasBarco, boolean estado)
    {
        this.tam = tam;
        this.posicionCasillasBarco = posicionCasillasBarco;
        this.destruido = estado;
    }
    
    public Barco(ArrayList<Vertice<Posicion>> posicionCasillasBarco, boolean estado)
    {
        this.tam = posicionCasillasBarco.size();
        this.posicionCasillasBarco = posicionCasillasBarco;
        this.destruido = estado;
    }
    
    public Barco(ArrayList<Vertice<Posicion>> posicionCasillasBarco)
    {
        this.tam = posicionCasillasBarco.size();
        this.posicionCasillasBarco = posicionCasillasBarco;
        this.destruido = false;
    }
    
    public boolean actualizarEstado()
    {
        for (Vertice<Posicion> vertice : posicionCasillasBarco)
        {
            if(!vertice.getValor().esDestruido())
            {
                destruido = false;
                return false;
            }      
        }
        destruido = true;
        return true;
    }
    
    //===============================//
    //====== Getters y Setters ======//
    //===============================//

    public int getTam()
    {
        return tam;
    }

    public void setTam(int tam)
    {
        this.tam = tam;
    }

    public ArrayList<Vertice<Posicion>> getPosicionCasillasBarco()
    {
        return posicionCasillasBarco;
    }

    public void setPosicionCasillasBarco(ArrayList<Vertice<Posicion>> posicionCasillasBarco)
    {
        this.posicionCasillasBarco = posicionCasillasBarco;
    }

    public boolean esDestruido()
    {
        return destruido;
    }

    public void setDestruido(boolean estado)
    {
        this.destruido = estado;
    }
}
