package jugadorbatallanaval;

public class BarcoEnemigo
{
    private int tam;
    private boolean destruido;

    public BarcoEnemigo(int tam)
    {
        this.tam = tam;
        this.destruido = false;
    }

    public int getTam()
    {
        return tam;
    }

    public void setTam(int tam)
    {
        this.tam = tam;
    }

    public void setDestruido(boolean destruido)
    {
        this.destruido = destruido;
    }

    public boolean isDestruido()
    {
        return destruido;
    }
    
    
}
