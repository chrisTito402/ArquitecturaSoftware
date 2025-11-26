package buseventos;

public interface IEventSuscriptor {

    void recibirEvento(String eventoJSON);

    String getSuscriptorId();
}
