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

package com.helion3.prism.commands;

import com.google.common.base.Preconditions;
import com.helion3.prism.Prism;
import com.helion3.prism.configuration.Configuration;
import com.helion3.prism.storage.h2.H2StorageAdapter;
import com.helion3.prism.storage.mongodb.MongoStorageAdapter;
import com.helion3.prism.storage.mysql.MySQLStorageAdapter;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class ReloadCommand {
    private ReloadCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .permission("prism.reload")
                .executor((source, args) -> {
                    Prism prism = Prism.getInstance();
                    prism.setConfiguration(new Configuration(prism.getPath()));
                    prism.getConfiguration().loadConfiguration();
                    String engine = prism.getConfig().getStorageCategory().getEngine();
                    if (StringUtils.equalsIgnoreCase(engine, "h2")) {
                        prism.setStorageAdapter(new H2StorageAdapter());
                    } else if (StringUtils.equalsAnyIgnoreCase(engine, "mongo", "mongodb")) {
                        prism.setStorageAdapter(new MongoStorageAdapter());
                    } else if (StringUtils.equalsIgnoreCase(engine, "mysql")) {
                        prism.setStorageAdapter(new MySQLStorageAdapter());
                    } else {
                        source.sendMessage(Text.of("Incorrect storage provider set!"));
                    }
                    try {
                        Preconditions.checkState(prism.getStorageAdapter().connect());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    source.sendMessage(Text.of("Config successfully reloaded"));
                    return CommandResult.success();
                })
                .build();
    }
}
