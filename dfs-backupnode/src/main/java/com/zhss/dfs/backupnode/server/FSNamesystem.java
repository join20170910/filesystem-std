package com.zhss.dfs.backupnode.server;

/**
 * 负责管理元数据的核心组件
 * @author zhonghuashishan
 *
 */
public class FSNamesystem {

	/**
	 * 负责管理内存文件目录树的组件
	 */
	private FSDirectory directory;

	public FSNamesystem() {
		this.directory = new FSDirectory();
	}
	/**
	 * 创建目录
	 * @param path 目录路径
	 * @return 是否成功
	 */
	public Boolean mkdir(String path) throws Exception {
		// 基于FSDirectory组件管理文件目录树
		this.directory.mkdir(path);
		return true;
	}
}
