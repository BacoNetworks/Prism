package com.helion3.prism.listeners;

import com.helion3.prism.Prism;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.PrismEvents;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.text.Text;

public class ChangeSignListener {
    /**
     * Listens to the change sign event
     *
     * @param event ChangeSignEvent
     */
    @Listener(order = Order.POST)
    public void onChangeSign(ChangeSignEvent event) {
        if (!Prism.getInstance().getConfig().getEventCategory().isSignChange()) {
            return;
        }
        Sign tileEntity = event.getTargetTile();
        final SignData finalSignData = event.getText();
        StringBuilder finalText = new StringBuilder().append("\n");
        int cleaner = 0;
        for(Text text : finalSignData.lines()){
            cleaner++;
            finalText.append(" - ").append(text.toPlain());
            if(cleaner != finalSignData.lines().size()){
                finalText.append("\n");
            }
        }
        PrismRecord.create()
                .source(event.getCause())
                .event(PrismEvents.SIGN_EDIT)
                .finalSignData(finalText.toString())
                .location(tileEntity.getLocation())
                .buildAndSave();
    }
}

