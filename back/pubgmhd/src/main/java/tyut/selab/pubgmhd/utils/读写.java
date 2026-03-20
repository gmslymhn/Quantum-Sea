package tyut.selab.pubgmhd.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import tyut.selab.common.utils.http.HttpClientUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class 读写 {
    private static String baseUrl;

    public 读写(String ip) {
        this.baseUrl = "http://" + ip + ":8080";
    }

    /**
     * 1. 获取进程PID
     */
    public Integer getPid(String packageName) {
        String url = baseUrl + "/api/get_pid?package=" + packageName;
        String response = HttpClientUtils.get(url);
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            return json.getInteger("pid");
        }
        return null;
    }

    /**
     * 2. 初始化读写
     */
    public boolean initReadWrite(int pid) {
        String url = baseUrl + "/api/init_readwrite?pid=" + pid;
        String response = HttpClientUtils.get(url);
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            return "success".equals(json.getString("status"));
        }
        return false;
    }

    /**
     * 3. 获取模块基址
     */
    public Long getModuleBase(String moduleName) {
        String url = baseUrl + "/api/get_module_base?module=" + moduleName;
        String response = HttpClientUtils.get(url);
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            return json.getLong("base_address_dec");
        }
        return null;
    }

    /**
     * 4. 读取内存数据
     */
    public static byte[] readMemory(long address, int size) {
        String url = baseUrl + "/api/read_memory?addr=0x" + Long.toHexString(address) + "&size=" + size;
        String response = HttpClientUtils.get(url);
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            if (json.getBoolean("success")) {
                String base64Data = json.getString("data_base64");
                return Base64.getDecoder().decode(base64Data);
            }
        }
        return null;
    }

    /**
     * 5. 读取浮点数
     */
    public Float getFloat(long address) {
        String url = baseUrl + "/api/read_float?addr=0x" + Long.toHexString(address);
        String response = HttpClientUtils.get(url);
        if (response != null) {
            try {
                JSONObject json = JSON.parseObject(response);
                return json.getFloat("value");
            } catch (JSONException e) {
                // 处理JSON解析异常
                System.err.println("JSON解析失败: " + e.getMessage());
                return 0.0f;
            } catch (Exception e) {
                // 处理其他异常
                System.err.println("读取float值时发生异常: " + e.getMessage());
                return 0.0f;
            }
        }
        return 0.0f;
    }

    /**
     * 6. 读取整数（DWORD）
     */
    public Integer getDword(long address) {
        String url = baseUrl + "/api/read_dword?addr=0x" + Long.toHexString(address);
        String response = HttpClientUtils.get(url);
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            return json.getInteger("value");
        }
        return null;
    }

    /**
     * 7. 读取64位指针
     */
    public Long getPtr64(long address) {
        String url = baseUrl + "/api/read_ptr64?addr=0x" + Long.toHexString(address);
        String response = HttpClientUtils.get(url);
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            return json.getLong("value_dec");
        }
        return null;
    }

    /**
     * 8. 读取UTF-8字符串
     */
    public String getUtf8(long address) {
        String url = baseUrl + "/api/read_utf8?addr=0x" + Long.toHexString(address);
        String response = HttpClientUtils.get(url);
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            return json.getString("string");
        }
        return null;
    }

    /**
     * 9. 检查驱动状态
     */
    public JSONObject checkDriver() {
        String url = baseUrl + "/api/check_driver";
        String response = HttpClientUtils.get(url);
        if (response != null) {
            return JSON.parseObject(response);
        }
        return null;
    }

    /**
     * 10. 写入内存数据
     */
    public boolean writeMemory(long address, byte[] data, String type) {
        String url = baseUrl + "/api/write_memory";

        JSONObject requestBody = new JSONObject();
        requestBody.put("addr", address);
        requestBody.put("data", Base64.getEncoder().encodeToString(data));
        requestBody.put("type", type);

        String response = HttpClientUtils.postJson(url, requestBody.toJSONString());
        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            return json.getBoolean("success");
        }
        return false;
    }

    /**
     * 写入浮点数
     */
    public boolean writeFloat(long address, float value) {
        byte[] bytes = new byte[4];
        int intBits = Float.floatToIntBits(value);
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((intBits >> (8 * i)) & 0xFF);
        }
        return writeMemory(address, bytes, "float");
    }

    /**
     * 写入整数（DWORD）
     */
    public boolean writeDword(long address, int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((value >> (8 * i)) & 0xFF);
        }
        return writeMemory(address, bytes, "dword");
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        读写 kernelApi = new 读写("192.168.51.64");

        // 1. 获取进程PID
        Integer pid = kernelApi.getPid("com.tencent.tmgp.pubgmhd");
        System.out.println("PID: " + pid);

        if (pid != null) {
            // 2. 初始化读写
            boolean initSuccess = kernelApi.initReadWrite(pid);
            System.out.println("初始化结果: " + initSuccess);

            // 3. 获取模块基址
            Long baseAddress = kernelApi.getModuleBase("libUE4.so");
            System.out.println("模块基址: " + baseAddress);

            if (baseAddress != null) {
                // 4. 读取内存数据
                byte[] data = kernelApi.readMemory(baseAddress, 16);
                System.out.println("读取到 " + (data != null ? data.length : 0) + " 字节数据");

                // 5. 读取浮点数
                Float floatValue = kernelApi.getFloat(baseAddress);
                System.out.println("浮点数值: " + floatValue);

                // 6. 读取整数
                Integer intValue = kernelApi.getDword(baseAddress);
                System.out.println("整数值: " + intValue);

                // 7. 读取指针
                Long ptrValue = kernelApi.getPtr64(baseAddress);
                System.out.println("指针值: " + ptrValue);

                // 8. 读取字符串
                String strValue = kernelApi.getUtf8(baseAddress);
                System.out.println("字符串: " + strValue);

                // 9. 检查驱动状态
                JSONObject driverStatus = kernelApi.checkDriver();
                System.out.println("驱动状态: " + driverStatus);
            }
        }
    }
}
