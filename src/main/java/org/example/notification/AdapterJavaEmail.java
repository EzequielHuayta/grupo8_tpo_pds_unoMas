package org.example.notification;

public class AdapterJavaEmail implements IAdapterEmail {

    @Override
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        // Adapter: aquí se integraría con JavaMail u otra librería de email
        System.out.println("[JavaMail] Enviando email a " + destinatario
                + " | Asunto: " + asunto
                + " | Cuerpo: " + cuerpo);
    }
}
