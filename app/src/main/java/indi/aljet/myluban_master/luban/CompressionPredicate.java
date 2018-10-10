package indi.aljet.myluban_master.luban;

/**
 * 压缩图片接口
 */
public interface CompressionPredicate {

    /**
     * 压缩图片（通过路径获取压缩文件，成功与否 返回）
     * @param path
     * @return
     */
    boolean apply(String path);
}
