package com.wiredi.jpa;

import com.wiredi.compiler.processor.ProcessorProperties;
import com.wiredi.logging.Logging;
import com.wiredi.runtime.properties.Key;
import com.wiredi.runtime.values.Value;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HashChecker {

    private static final Logging logger = Logging.getInstance(HashChecker.class);
    private static final MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Unable to retrieve SHA-256 message digestion", e);
        }

        System.getProperty("user.home");
    }

    private final ProcessorProperties properties;
    private final Value<Path> cacheDir = Value.empty();

    public HashChecker(ProcessorProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void postConstruct() {
        if (properties.isEnabled(Key.just("jpa.local-cache.enabled"), true)) {
            String rawPath = properties.getName(Key.just("jpa.local-cache.folder"), ".cache");
            Path path = Paths.get(rawPath);
            Path finalFolder;
            if (!path.isAbsolute()) {
                finalFolder = Paths.get(System.getProperty("user.home"), rawPath, "hibernate-processor");
            } else {
                finalFolder = path.resolve("hibernate-processor");
            }

            if (Files.notExists(finalFolder)) {
                try {
                    Files.createDirectories(finalFolder);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            if (!Files.isDirectory(finalFolder)) {
                throw new UncheckedIOException(new IOException("Invalid cache folder: " + finalFolder + " (not a directory)"));
            }
            if (!Files.isWritable(finalFolder)) {
                throw new UncheckedIOException(new IOException("Invalid cache folder: " + finalFolder + " (not writable)"));
            }
            cacheDir.set(finalFolder);
        }
    }

    public boolean hasChanged(String key, byte[] content) {
        if (cacheDir.isEmpty()) {
            return false;
        }
        Path cachePath = cacheDir.get();
        try {
            Path hashFile = cachePath.resolve(key + ".hash");
            byte[] newHash = messageDigest.digest(content);
            if (Files.exists(hashFile)) {
                byte[] oldHash = Files.readAllBytes(hashFile);
                if (Arrays.equals(oldHash, newHash)) return false;
            }
            Files.write(hashFile, newHash);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to check hash for key " + key, ex);
            return false;
        }
    }
}
