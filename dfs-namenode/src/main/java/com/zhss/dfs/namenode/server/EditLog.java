package com.zhss.dfs.namenode.server;
	/**
	 * 代表了一条edits log
	 * @author zhonghuashishan
	 *
	 */
	public class EditLog {
	
		private long txid;
		private String content;
		
		public EditLog(long txid, String content) {
			this.txid = txid;
			this.content = content;
		}

  public long getTxid() {
    return txid;
  }

  public void setTxid(long txid) {
    this.txid = txid;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
