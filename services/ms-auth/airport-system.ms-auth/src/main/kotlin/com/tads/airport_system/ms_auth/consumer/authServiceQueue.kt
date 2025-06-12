



@Component

public class authServiceQueue{

    @RabbitListener(queues = "auth-service-queue")
    public void receiveMessage(String msg){
        system.out.println(msg)
    }


}