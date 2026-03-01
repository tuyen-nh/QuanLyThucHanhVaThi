package com.example.agent;

import com.example.agent.Security.PEFeatureExtractor;
import com.example.agent.Security.MalwareScanner;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ScanRunnerTest {

    @Test
    public void testScanUniKey() throws Exception {
        File f = new File("C:\\Temp\\UniKeyNT.exe");
        assertTrue(f.exists(), "Target file must exist for scan");

        PEFeatureExtractor ext = new PEFeatureExtractor(f);
        float[] features = ext.getFeatureArray();
        assertNotNull(features);
        assertEquals(12, features.length);

        MalwareScanner scanner = new MalwareScanner();
        boolean isMalware = scanner.isMalware(features);
        System.out.println("Scan result for UniKeyNT.exe: isMalware=" + isMalware);
    }
}
