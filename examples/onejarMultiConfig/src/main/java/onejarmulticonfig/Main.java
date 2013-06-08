package onejarmulticonfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public final class Main {

  public static void main(String[] args) {
    System.out.println("Hello, onejar" + StringUtils.repeat("!", 4));
    System.out.println("Temp directory path: " + FileUtils.getTempDirectoryPath());
  }
}
