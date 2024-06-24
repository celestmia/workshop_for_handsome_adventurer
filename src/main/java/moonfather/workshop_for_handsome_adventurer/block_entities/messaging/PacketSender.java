package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.neoforged.neoforge.network.PacketDistributor;

public class PacketSender
{
    public static void sendTabChangeToServer(int newTab)
    {
        TabChangeMessage message = new TabChangeMessage(newTab);
        PacketDistributor.sendToServer(message);
    }

    public static void sendDestinationGridChangeToServer(int newDestination)
    {
        GridChangeMessage message = new GridChangeMessage(newDestination);
        PacketDistributor.sendToServer(message);
    }

    public static void sendRemoteUpdateRequestToServer()
    {
        ClientRequestMessage message = new ClientRequestMessage(ClientRequestMessage.REQUEST_REMOTE_UPDATE);
        PacketDistributor.sendToServer(message);
    }

    public static void sendRenameRequestToServer(String newName)
    {
        ChestRenameMessage message = new ChestRenameMessage(newName);
        PacketDistributor.sendToServer(message);
    }
}