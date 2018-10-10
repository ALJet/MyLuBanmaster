package indi.aljet.myluban_master.luban;

import java.io.IOException;
import java.io.InputStream;

/**
 * 通过此接口获取输入流，以兼容文件、FileProvider方式获取到的图片
 */
public interface InputStreamProvider {
    InputStream open() throws IOException;

    String getPath();
}
