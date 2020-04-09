package com.sstory.workplace.search.sdk.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SourcesUtils {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final Map<String, Class<? extends Source<? extends DocumentBase>>> enabledSources;

    public SourcesUtils() throws IOException, ClassNotFoundException, ClassCastException {
        this(SourcesUtils.class.getClassLoader().getResourceAsStream("sources"));
    }

    private SourcesUtils(InputStream stream) throws IOException, ClassNotFoundException, ClassCastException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Map<String, Class<? extends Source<? extends DocumentBase>>> sources = new HashMap<>();
        while(reader.ready()) {
            String line = reader.readLine().trim();
            try {
                Class<? extends Source<? extends DocumentBase>> klass = (Class<? extends Source<? extends DocumentBase>>) Class.forName(line);
                sources.put(klass.getSimpleName().toLowerCase(), klass);
            } catch (ClassNotFoundException | ClassCastException e) {
                log.error("Failed to find class with name {} from the 'sources' resource", line);
                throw e;
            }

        }
        this.enabledSources = sources;
    }

    public Set<String> getEnabledSources(){
        return this.enabledSources.keySet();
    }

    public boolean isEnabled(String sourceName){
        String lowerSourceName = sourceName.trim().toLowerCase();
        String withSourceName = lowerSourceName.endsWith("source") ? lowerSourceName : lowerSourceName+"source";
        return getEnabledSources().contains(lowerSourceName) || getEnabledSources().contains(withSourceName);
    }

    public Class<? extends Source<? extends DocumentBase>> getSource(String sourceName){
        if (!isEnabled(sourceName)){
            return null;
        } else {
            String lowerSourceName = sourceName.trim().toLowerCase();
            String withSourceName = lowerSourceName.endsWith("source") ? lowerSourceName : lowerSourceName+"source";
            if(enabledSources.containsKey(lowerSourceName)){
                return enabledSources.get(lowerSourceName);
            } else {
                return enabledSources.get(withSourceName);
            }
        }
    }

    public Source<? extends DocumentBase> initialize(Class<? extends Source<? extends DocumentBase>> klass, YamlConfig config){
        String sourceName = klass.getSimpleName().toLowerCase();
        if(!isEnabled(sourceName)){
            throw new IllegalStateException("You must enable "+klass.getCanonicalName()+" in order to initialize it via config");
        } else {
            String[] args = new String[0];
            if(config.hasSourceConfig(sourceName)){
                args = config.getArgsForSourceName(sourceName).toArray(args);
            }
            return initialize(klass, args);
        }
    }

    public Source<? extends DocumentBase> initialize(Class<? extends Source<? extends DocumentBase>> klass, String[] args){
        final Class<?>[] paramTypes = new Class[args.length];
        Arrays.fill(paramTypes, String.class);
        try {
            return klass.getConstructor(paramTypes).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not instantiate class: "+klass.getCanonicalName()+" from arguments: "+args, e);
        }
    }
}
