package com.zhss.dfs.client;

/**
 * 作为文件系统的接口
 */
public interface FileSystem {
    void mkdir(String path) throws Exception;
    void shutdown() throws Exception;
}
