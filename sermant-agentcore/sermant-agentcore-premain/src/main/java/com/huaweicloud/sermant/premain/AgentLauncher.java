/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.huaweicloud.sermant.premain;

import com.huaweicloud.sermant.god.common.SermantClassLoader;
import com.huaweicloud.sermant.god.common.SermantManager;
import com.huaweicloud.sermant.premain.common.BootArgsBuilder;
import com.huaweicloud.sermant.premain.common.PathDeclarer;
import com.huaweicloud.sermant.premain.common.SermantConstant;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Agent Premain方法
 *
 * @author luanwenfei
 * @since 2022-03-26
 */
public class AgentLauncher {
    private static final Logger LOGGER = getLogger();

    private static boolean installFlag = false;

    private AgentLauncher() {}

    /**
     * premain
     *
     * @param agentArgs agentArgs
     * @param instrumentation instrumentation
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        launchAgent(agentArgs, instrumentation, false);
    }

    /**
     * agentmain
     *
     * @param agentArgs agentArgs
     * @param instrumentation instrumentation
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        launchAgent(agentArgs, instrumentation, true);
    }

    private static void launchAgent(String agentArgs, Instrumentation instrumentation, Boolean dynamicInstall) {
        try {
            if (!installFlag) {
                // 添加引导库
                LOGGER.info("Loading god library into BootstrapClassLoader.");
                loadGodLib(instrumentation);
                installFlag = true;
            }

            // 初始化启动参数
            LOGGER.info("Building argument map by agent arguments.");
            final Map<String, Object> argsMap = BootArgsBuilder.build(agentArgs);
            String artifact = (String)argsMap.get(SermantConstant.ARTIFACT_NAME_KEY);
            
            if (SermantManager.checkSermantStatus(artifact)) {
                LOGGER.info("Sermant for artifact is running， artifact is: " + artifact);
                return;
            }
            // 添加核心库
            LOGGER.info("Loading core library into SermantClassLoader.");
            SermantClassLoader sermantClassLoader = SermantManager.createSermant(artifact, loadCoreLib());

            // agent core入口
            LOGGER.info("Loading sermant agent, artifact is: " + artifact);
            sermantClassLoader.loadClass("com.huaweicloud.sermant.core.AgentCoreEntrance")
                .getDeclaredMethod("install", String.class, Map.class, Instrumentation.class, boolean.class)
                .invoke(null, artifact, argsMap, instrumentation, dynamicInstall);
            LOGGER.info("Load sermant done， artifact is: " + artifact);
            SermantManager.updateSermantStatus(artifact,true);
        } catch (OutOfMemoryError | StackOverflowError | Exception e) {
            LOGGER.log(Level.SEVERE, "Loading sermant agent failed.", e);
            e.printStackTrace();
        }
    }

    private static URL[] loadCoreLib() throws IOException {
        final File coreDir = new File(PathDeclarer.getCorePath());
        if (!coreDir.exists() || !coreDir.isDirectory()) {
            throw new RuntimeException("core directory is not exist or is not directory.");
        }
        final File[] jars = coreDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        if (jars == null || jars.length == 0) {
            throw new RuntimeException("core directory is empty");
        }
        List<URL> list = new ArrayList<>();
        for (File jar : jars) {
            list.add(jar.toURI().toURL());
        }
        return list.toArray(new URL[] {});
    }

    private static void loadGodLib(Instrumentation instrumentation) throws IOException {
        final File bootstrapDir = new File(PathDeclarer.getGodLibPath());
        if (!bootstrapDir.exists() || !bootstrapDir.isDirectory()) {
            throw new RuntimeException("God directory is not exist or is not directory.");
        }
        final File[] jars = bootstrapDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        if (jars == null || jars.length == 0) {
            throw new RuntimeException("God directory is empty");
        }
        for (File jar : jars) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(jar);
                instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
            } finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (IOException ignored) {
                        LOGGER.severe(ignored.getMessage());
                    }
                }
            }
        }
    }

    public static Logger getLogger() {
        if (LOGGER != null) {
            return LOGGER;
        }
        final Logger logger = Logger.getLogger("sermant.agent");
        final ConsoleHandler handler = new ConsoleHandler();
        final String lineSeparator = System.getProperty("line.separator");
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String time = format.format(new Date(record.getMillis()));
                return "[" + time + "] " + "[" + record.getLevel() + "] " + record.getMessage() + lineSeparator;
            }
        });
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        return logger;
    }
}