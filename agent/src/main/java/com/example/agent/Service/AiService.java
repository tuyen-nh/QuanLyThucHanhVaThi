package com.example.agent.Service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.agent.Security.MalwareScanner;
import com.example.agent.Security.PEFeatureExtractor;

@Service
public class AiService {
    @Autowired
    private MalwareScanner malwareScanner;

    public boolean scanFile(File file) {
        // Check if file exists and can be read
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        // 0. Pre-check: Check for MZ header (DOS header) to avoid processing non-PE
        // files
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] magic = new byte[2];
            if (fis.read(magic) < 2 || magic[0] != 'M' || magic[1] != 'Z') {
                return false; // Not a PE file, ignore silently
            }
        } catch (java.io.IOException e) {
            // File might be locked or inaccessible
            System.err.println("Could not check file header: " + file.getName() + " (" + e.getMessage() + ")");
            return false;
        }

        try {
            // 1. Trích xuất đặc trưng
            PEFeatureExtractor extractor = new PEFeatureExtractor(file);
            float[] features = extractor.getFeatureArray();

            // 2. Hỏi AI
            boolean isVirus = malwareScanner.isMalware(features);

            return isVirus;
        } catch (Exception e) {
            System.err.println("Error scanning file " + file.getName() + ": " + e.getMessage());
            if (!(e instanceof java.io.IOException)) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
