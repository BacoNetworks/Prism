/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.helion3.prism.listeners;

import com.helion3.prism.Prism;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.PrismEvents;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
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

