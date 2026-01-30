package com.example.agent.Security;

import com.kichik.pecoff4j.PE;
import com.kichik.pecoff4j.COFFHeader;
import com.kichik.pecoff4j.OptionalHeader;
import com.kichik.pecoff4j.SectionTable;
import com.kichik.pecoff4j.SectionHeader;
import com.kichik.pecoff4j.SectionData;
import com.kichik.pecoff4j.ImageData;
import com.kichik.pecoff4j.ResourceDirectory;
import com.kichik.pecoff4j.io.DataReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PEFeatureExtractor {
    private final byte[] data;

    public PEFeatureExtractor(File file) throws IOException {
        this.data = Files.readAllBytes(file.toPath());
    }

    // Allow constructing directly from bytes so other extractors can delegate
    public PEFeatureExtractor(byte[] data) {
        this.data = data;
    }

    public float[] getFeatureArray() throws IOException {
        return computeFeatures();
    }

    // Return features keyed by name in the same order as getFeatureArray
    public Map<String, Double> getFeatureMap() throws IOException {
        float[] f = computeFeatures();
        Map<String, Double> features = new LinkedHashMap<>();
        // Map features in the requested order:
        // ['ResourcesMaxEntropy','ResourcesMinEntropy','Characteristics','ResourcesMaxSize','ImageBase',
        // 'SectionsMaxEntropy','ResourcesMeanSize','SectionsMinVirtualsize','SectionsMinEntropy',
        // 'VersionInformationSize','AddressOfEntryPoint','ResourcesMinSize']
        features.put("ResourcesMaxEntropy", (double) f[0]);
        features.put("ResourcesMinEntropy", (double) f[1]);
        features.put("Characteristics", (double) f[2]);
        features.put("ResourcesMaxSize", (double) f[3]);
        features.put("ImageBase", (double) f[4]);
        features.put("SectionsMaxEntropy", (double) f[5]);
        features.put("ResourcesMeanSize", (double) f[6]);
        features.put("SectionsMinVirtualsize", (double) f[7]);
        features.put("SectionsMinEntropy", (double) f[8]);
        features.put("VersionInformationSize", (double) f[9]);
        features.put("AddressOfEntryPoint", (double) f[10]);
        features.put("ResourcesMinSize", (double) f[11]);
        return features;
    }

    // Core feature computation used by both
    private float[] computeFeatures() throws IOException {
        float[] out = new float[12];

        try (DataReader reader = new DataReader(data)) {
            PE pe = PE.read(reader); // đọc cấu trúc file PE

            COFFHeader coff = pe.getCoffHeader();
            OptionalHeader opt = pe.getOptionalHeader(); // chứa thông tin quan trọng để windows chạy file . Điểm bắt
                                                         // đầu của chương trình
            SectionTable st = pe.getSectionTable(); // bảng danh sach chứa thông tin về các section
            ImageData img = pe.getImageData(); // chứa dữ liệu của các section

            int characteristics = coff != null ? coff.getCharacteristics() : 0;
            int addressOfEntryPoint = opt != null ? opt.getAddressOfEntryPoint() : 0;

            // 12 đặc trưng mà models cần để dự đoán
            // ['ResourcesMaxEntropy','ResourcesMinEntropy','Characteristics','ResourcesMaxSize','ImageBase',
            // 'SectionsMaxEntropy','ResourcesMeanSize','SectionsMinVirtualsize','SectionsMinEntropy',
            // 'VersionInformationSize','AddressOfEntryPoint','ResourcesMinSize']

            List<Double> sectionEntropies = new ArrayList<>();
            List<Long> virtualSizes = new ArrayList<>();
            // quyets từng section của file exe để thu thập virtual size và entropy của từng
            // section
            if (st != null) {
                int n = st.getNumberOfSections();
                for (int i = 0; i < n; i++) {
                    SectionHeader hdr = st.getHeader(i);
                    SectionData sdata = st.getSection(i);
                    if (hdr != null) {
                        virtualSizes.add((long) hdr.getVirtualSize());
                    }
                    if (sdata != null && sdata.getData() != null) {
                        byte[] sec = sdata.getData();
                        sectionEntropies.add(calculateShannonEntropy(sec)); // đo độ ngẫu nhiên của section tương ứng
                    }
                }
            }

            List<Double> resEntropies = new ArrayList<>();
            List<Long> resSizes = new ArrayList<>();
            long versionInfoSize = 0L;
            if (img != null && img.getResourceTable() != null) {
                ResourceDirectory rd = img.getResourceTable();
                try {
                    if (rd.getEntries() != null) {
                        for (com.kichik.pecoff4j.ResourceEntry re : rd.getEntries()) {
                            byte[] d = re.getData();
                            if (d != null && d.length > 0) {
                                resEntropies.add(calculateShannonEntropy(d)); // Tính độ hỗn loạn để phát hiện dữ liệu
                                                                              // bị mã hóa/nén
                                resSizes.add((long) d.length); // Lưu kích thước tài nguyên để phát hiện bất thường (ví
                                                               // dụ: payload lớn)

                                // Phát hiện tài nguyên chứa thông tin phiên bản (Version Info) bằng kỹ thuật
                                // reflection
                                // Do thư viện pecoff4j có thể không hỗ trợ trực tiếp, ta kiểm tra Type ID (16
                                // là RT_VERSION) hoặc tên có chứa "VERSION"
                                boolean isVersion = false;
                                try {
                                    java.lang.reflect.Method mType = re.getClass().getMethod("getType");
                                    Object typeObj = mType.invoke(re);
                                    if (typeObj instanceof Number) {
                                        int t = ((Number) typeObj).intValue();
                                        if (t == 16)
                                            isVersion = true; // 16 là định danh chuẩn cho RT_VERSION trong Windows
                                    }
                                } catch (Throwable ignore) {
                                }
                                try {
                                    java.lang.reflect.Method mName = re.getClass().getMethod("getName");
                                    Object nameObj = mName.invoke(re);
                                    if (nameObj != null && nameObj.toString().toUpperCase().contains("VERSION"))
                                        isVersion = true;
                                } catch (Throwable ignore) {
                                }
                                if (isVersion)
                                    versionInfoSize += d.length; // Tổng hợp kích thước của thông tin phiên bản (Mã độc
                                                                 // thường có Version Info giả hoặc kích thước bất
                                                                 // thường)
                            }
                        }
                    }
                } catch (Throwable t) {
                    // fallback: ignore resource parsing issues
                }
            }
            // tính toán Độ hỗn loạn lớn nhất (Max Entropy) và nhỏ nhất (Min Entropy) trong
            // số tất cả các phân khu (sections) tìm thấy trong file.
            double secMaxEntropy = 0, secMinEntropy = 0;
            if (!sectionEntropies.isEmpty()) {
                secMaxEntropy = sectionEntropies.stream().mapToDouble(d -> d).max().orElse(0);
                secMinEntropy = sectionEntropies.stream().mapToDouble(d -> d).min().orElse(0);
            }
            // tìm Kích thước ảo nhỏ nhất (Min Virtual Size) trong các phân khu.

            long secMinVirtual = 0;
            if (!virtualSizes.isEmpty()) {
                secMinVirtual = virtualSizes.stream().mapToLong(Long::longValue).min().orElse(0);
            }
            // tính Độ hỗn loạn lớn nhất (Max Entropy) và nhỏ nhất (Min Entropy) cho các Tài
            // nguyên (Resources). ví dụ hình ảnh, icon, chuỗi ..
            double resMaxEntropy = 0, resMinEntropy = 0;
            if (!resEntropies.isEmpty()) {
                resMaxEntropy = resEntropies.stream().mapToDouble(d -> d).max().orElse(0);
                resMinEntropy = resEntropies.stream().mapToDouble(d -> d).min().orElse(0);
            }
            // tính toán thống kê về Kích thước của các Tài nguyên
            long resMaxSize = 0;
            long resMinSize = 0;
            double resMeanSize = 0;
            if (!resSizes.isEmpty()) {
                resMaxSize = resSizes.stream().mapToLong(Long::longValue).max().orElse(0); // kích thước lớn nhất
                resMinSize = resSizes.stream().mapToLong(Long::longValue).min().orElse(0); // kích thước nhỏ nhất
                resMeanSize = resSizes.stream().mapToLong(Long::longValue).average().orElse(0); // trung bình
            }

            // ;ấy imagebase
            long imageBase = 0L;
            if (opt != null) {
                try {
                    java.lang.reflect.Method mImg = opt.getClass().getMethod("getImageBase");
                    Object imgObj = mImg.invoke(opt);
                    if (imgObj instanceof Number)
                        imageBase = ((Number) imgObj).longValue();
                } catch (Throwable ignore) {
                }
            }

            // Fill the output array in the exact order requested
            out[0] = (float) resMaxEntropy; // ResourcesMaxEntropy
            out[1] = (float) resMinEntropy; // ResourcesMinEntropy
            out[2] = (float) characteristics; // Characteristics
            out[3] = (float) resMaxSize; // ResourcesMaxSize
            out[4] = (float) imageBase; // ImageBase
            out[5] = (float) secMaxEntropy; // SectionsMaxEntropy
            out[6] = (float) resMeanSize; // ResourcesMeanSize
            out[7] = (float) secMinVirtual; // SectionsMinVirtualsize
            out[8] = (float) secMinEntropy; // SectionsMinEntropy
            out[9] = (float) versionInfoSize; // VersionInformationSize
            out[10] = (float) addressOfEntryPoint; // AddressOfEntryPoint
            out[11] = (float) resMinSize; // ResourcesMinSize
        } catch (IOException e) {
            throw e;
        } catch (Exception ex) {
        }

        return out;
    }

    private static double calculateShannonEntropy(byte[] data) {
        if (data == null || data.length == 0)
            return 0;
        int[] freqs = new int[256];
        for (byte b : data)
            freqs[b & 0xFF]++;
        double entropy = 0;
        for (int f : freqs) {
            if (f > 0) {
                double p = (double) f / data.length;
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return entropy;
    }
}
