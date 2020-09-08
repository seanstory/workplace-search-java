package com.sstory.workplace.search.sdk.api;

import com.sstory.workplace.search.client.Client;
import com.sstory.workplace.search.client.ClientKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlConfig {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String ACCESS_TOKEN_CONFIG_KEY = "access_token";
    public static final String CONTENT_SOURCE_KEY_CONFIG_KEY = "content_source_key";
    public static final String ENDPOINT_CONFIG_KEY = "endpoint";
    public static final String SECURITY_CONFIG_KEY = "security";
    public final String accessToken;
    public final String contentSourceKey;
    public final String endpoint;
    public final String security;
    private final Map<String, Object> configMap;

    public YamlConfig(String yamlPath) throws FileNotFoundException {
        File file = new File(yamlPath);
        log.debug("Attempting to parse yaml config from: {}", file.getAbsolutePath());
        Yaml yaml = new Yaml();
        InputStream stream = new FileInputStream(file);
        Map<String, Object> config;
        try{
            config = yaml.load(stream);
        } catch (ClassCastException e){
            throw new IllegalArgumentException("The content of "+file.getAbsolutePath()+" was not of the expected format. This must be a YAML file");
        }

        if(config==null){
            throw new IllegalArgumentException("No yaml could be read from: "+file.getAbsolutePath());
        }

        this.accessToken = (String) config.get(ACCESS_TOKEN_CONFIG_KEY);
        this.contentSourceKey = (String) config.get(CONTENT_SOURCE_KEY_CONFIG_KEY);
        this.endpoint = config.containsKey(ENDPOINT_CONFIG_KEY) ? (String) config.get(ENDPOINT_CONFIG_KEY) : ClientKt.DEFAULT_ENDPOINT;
        if(this.accessToken == null || this.contentSourceKey == null){
            throw new ConfigurationException("Both "+ACCESS_TOKEN_CONFIG_KEY+" and "+CONTENT_SOURCE_KEY_CONFIG_KEY+" must be configured in "+yamlPath);
        }
        this.security = config.containsKey(SECURITY_CONFIG_KEY) ? (String) config.get(SECURITY_CONFIG_KEY) : ClientKt.DEFAULT_SECURITY;
        if(!ClientKt.getSECURITY_OPTIONS().contains(this.security)){
            throw new ConfigurationException(SECURITY_CONFIG_KEY+" must be one of: "+ ClientKt.getSECURITY_OPTIONS() +", but was: '"+this.security+"'");
        }
        this.configMap = config;
        //TODO, extra validations?
    }

    public static class ConfigurationException extends RuntimeException{
        ConfigurationException(String msg){
            super(msg);
        }
    }

    public boolean hasSourceConfig(String sourceName){
        return getNormalizedPairs().stream().anyMatch(it -> it.left.equals(sourceName));
    }

    public List<String> getArgsForSourceName(String sourceName){
        String key = getNormalizedPairs().stream().filter(it -> it.left.equals(sourceName)).map(it -> it.right).findAny().get();
        return (List<String>) configMap.get(key);
    }

    private List<StringPair> getNormalizedPairs(){
        return configMap.keySet().stream().map( it -> {
            String lower = it.toLowerCase();
            String normalized = lower.endsWith("source") ? lower : lower+"source";
            return new StringPair(normalized, it);
        }).collect(Collectors.toList());
    }

    static class StringPair{
        private String left;
        private String right;

        StringPair(String left, String right){
            this.left = left;
            this.right = right;
        }

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }
    }
}
