package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.minecraft.network.FriendlyByteBuf;

public class CraftingUpdateRequestMessage
{
    private int value = -1;  // slotIndex
    public CraftingUpdateRequestMessage(int value)
    {
        this.value = value;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(value);
    }

    public static CraftingUpdateRequestMessage decode(FriendlyByteBuf buffer)
    {
        CraftingUpdateRequestMessage result = new CraftingUpdateRequestMessage(-1);
        result.value = buffer.readInt();
        return result;
    }

    public int getValue() {
        return this.value;
    }
}