/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IotTerminal;

import java.awt.Color;

/**
 *
 * @author liptak
 */
public class IotTerminalPrefs {
  public IotTerminalPrefs()
  {
  }
  static public String get(String path, String key, String defVal)
  {
    if (path.isEmpty())
      path = root;
    else
      path = root + "/" + path;
    return java.util.prefs.Preferences.userRoot().node(path).get(key, defVal);
  }
  static public String get(String key, String defVal)
  {
    return get("", key, defVal);
  }
  static int get(String path, String key, int defVal)
  {
    return Integer.parseInt(get(path, key, "" + defVal));
  }
  public static int get(String key, int defVal)
  {
    return Integer.parseInt(get(key, "" + defVal));
  }
  static double get(String path, String key, double defVal)
  {
    return Double.parseDouble(get(path, key, "" + defVal));
  }
  static double get(String key, double defVal)
  {
    return Double.parseDouble(get(key, "" + defVal));
  }

  static java.awt.Color get(String key, java.awt.Color defVal)
  {
    return new java.awt.Color(get(key, defVal.getRGB()));
  }

  static public void put(String path, String key, String val)
  {
    if (path.isEmpty())
      path = root;
    else
      path = root + "/" + path;
    java.util.prefs.Preferences.userRoot().node(path).put(key, val);
  }
  static public void put(String key, String val)
  {
    put("", key, val);
  }
  static public void put(String path, String key, int val)
  {
    put(path, key, "" + val);
  }
  static public void put(String key, int val)
  {
    put(key, "" + val);
  }
  static public void put(String path, String key, double val)
  {
    put(path, key, "" + val);
  }
  static public void put(String key, double val)
  {
    put(key, "" + val);
  }

  static public void put(String key, java.awt.Color val)
  {
    put(key, val.getRGB());
  }

  static public String getRecentFile(int idx, String defVal)
  {
    return get("RecentFiles", "RecentFile" + idx, defVal);
  }
  static public void putRecentFile(int idx, String val)
  {
    put("RecentFiles", "RecentFile" + idx, val);
  }

  static final String backgroundColorPrefsStr = "Background color";
  public static Color getBackgroundColor(Color color)
  {
      return get(backgroundColorPrefsStr, color);
  }
  public static void putBackgroundColor(Color backgroundColor)
  {
      put(backgroundColorPrefsStr, backgroundColor);
  }

  static final String dataCursorMaxChannelPrefsStr = "DataCursorMaxChannel";
  static int dataCursorMaxChannel = -1;
  public static int getDataCursorMaxChannel() {
      if (dataCursorMaxChannel < 0)
          dataCursorMaxChannel = get(dataCursorMaxChannelPrefsStr, 2);
      return dataCursorMaxChannel;
  }
  public static void putDataCursorMaxChannel(int val) {
      dataCursorMaxChannel = val;
      put(dataCursorMaxChannelPrefsStr, val);
  }
  public static double getZoomOutLimit() {
      return 0.5;
  }

  static final String root = "IotTerminal";
}
