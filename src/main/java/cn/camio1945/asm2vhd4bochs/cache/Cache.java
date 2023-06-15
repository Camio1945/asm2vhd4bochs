package cn.camio1945.asm2vhd4bochs.cache;

import cn.camio1945.asm2vhd4bochs.exception.TodoException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.setting.Setting;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static cn.camio1945.asm2vhd4bochs.constant.SettingConstant.*;
import static cn.camio1945.util.StaticMethodUtil.getCurrentExecutingProjectFolderPath;

/**
 * 缓存，用于存放一些全局变量，例如配置文件
 * <p>
 * Cache, used to store some global variables, such as configuration files
 * @author Camio1945
 */
public class Cache {
  protected static final Log log = Log.get();

  protected static Setting setting;

  public static String nasmExeFilePath;

  public static String asmSourceCodeFilePath;

  public static String binFilePath;

  public static String vhdFilePath;

  public static String vhdLockFilePath;

  protected static Map<String, String> argsMap = new HashMap<>();

  public static void initCache(String[] args) {
    initSetting();
    initArgsMap(args);
    initNasmExeFilePath();
    initAsmSourceCodeFilePath();
  }

  protected static void initNasmExeFilePath() {
    nasmExeFilePath = argsMap.get(NASM_EXE_FILE_PATH);
    if (StrUtil.isBlank(nasmExeFilePath)) {
      nasmExeFilePath = setting.getStr(NASM_EXE_FILE_PATH);
    }
    Assert.notBlank(nasmExeFilePath, "{} is blank", NASM_EXE_FILE_PATH);
    Assert.isTrue(FileUtil.exist(nasmExeFilePath), "{} does not exist : {}", NASM_EXE_FILE_PATH, nasmExeFilePath);
  }

  protected static void initAsmSourceCodeFilePath() {
    asmSourceCodeFilePath = argsMap.get(ASM_SOURCE_CODE_FILE_PATH);
    if (StrUtil.isBlank(asmSourceCodeFilePath)) {
      asmSourceCodeFilePath = setting.getStr(ASM_SOURCE_CODE_FILE_PATH);
    }
    Assert.notBlank(asmSourceCodeFilePath, "{} is blank", ASM_SOURCE_CODE_FILE_PATH);
    Assert.isTrue(FileUtil.exist(asmSourceCodeFilePath), "{} does not exist : {}", ASM_SOURCE_CODE_FILE_PATH, asmSourceCodeFilePath);
    binFilePath = asmSourceCodeFilePath.replace(".asm", ".bin");
    vhdFilePath = asmSourceCodeFilePath.replace(".asm", ".vhd");
    vhdLockFilePath = vhdFilePath + ".lock";
  }

  protected static void initArgsMap(String[] args) {
    for (String arg : args) {
      String[] split = arg.split("=");
      if (split.length == 2) {
        String key = split[0];
        if (SETTING_KEYS.contains(key)) {
          argsMap.put(key, split[1]);
        } else {
          log.warn("Unknown key: " + key);
        }
      }
    }
  }

  protected static void initSetting() {
    String currentExecutingProjectFolderPath = getCurrentExecutingProjectFolderPath();
    String settingFilePath = currentExecutingProjectFolderPath + "/" + "asm2vhd4bochs.setting";
    if (FileUtil.exist(settingFilePath)) {
      setting = new Setting(settingFilePath);
    } else {
      // TODO: 如果配置文件不存在，则从 gitee 下载配置文件
      throw new TodoException();
    }
  }

}
