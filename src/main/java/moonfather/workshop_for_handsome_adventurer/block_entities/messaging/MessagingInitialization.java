package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MessagingInitialization
{
    public static void register(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar(Constants.MODID + "_v3");
        registrar.playToServer(ChestRenameMessage.TYPE, ChestRenameMessage.STREAM_CODEC, PayloadHandler::handleMessage);
        registrar.playToServer(ClientRequestMessage.TYPE, ClientRequestMessage.STREAM_CODEC, PayloadHandler::handleMessage);
        registrar.playToServer(GridChangeMessage.TYPE, GridChangeMessage.STREAM_CODEC, PayloadHandler::handleMessage);
        registrar.playToServer(TabChangeMessage.TYPE, TabChangeMessage.STREAM_CODEC, PayloadHandler::handleMessage);
        registrar.playToServer(CraftingUpdateRequestMessage.TYPE, CraftingUpdateRequestMessage.STREAM_CODEC, PayloadHandler::handleMessage);
    }
}
