package com.zhss.dfs.namenode.server;
import java.io.ByteArrayOutputStream;
import java.io.IOException;import java.nio.charset.StandardCharsets;
/**
 * 内存双缓冲
 *
 * @author zhonghuashishan
 */
public class DoubleBuffer {

  public final Integer EDIT_LOG_BUFFER_LIMIT = 512 * 1024;

  /** 是专门用来承载线程写入edits log */
  private EditLogBuffer currentBuffer = new EditLogBuffer();
  /** 专门用来将数据同步到磁盘中去的一块缓冲 */
  private EditLogBuffer syncBuffer = new EditLogBuffer();

  /** 当前写入的最大的txid是谁 */
  private Long maxTxid = 0L;
  /**
   * 将edits log写到内存缓冲里去
   *
   * @param log
   */
  public void write(EditLog log)throws IOException {
    currentBuffer.write(log);
    maxTxid = log.getTxid();
  }

  /** 交换两块缓冲区，为了同步内存数据到磁盘做准备 */
  public void setReadyToSync() {
    EditLogBuffer tmp = currentBuffer;
    currentBuffer = syncBuffer;
    syncBuffer = tmp;
  }

  /** 将syncBuffer缓冲区中的数据刷入磁盘中 */
  public void flush() {
    syncBuffer.flush();
    syncBuffer.clear();
  }

  /** EditsLog 缓冲区 */
  class EditLogBuffer {

    /** 字节数组 IO 流 */
    ByteArrayOutputStream out = new ByteArrayOutputStream(EDIT_LOG_BUFFER_LIMIT);
    /**
     * 将 editslog日志写入缓冲区
     *
     * @param log
     */
    public void write(EditLog log)throws IOException {
      out.write(log.getContent().getBytes());
    }

    /**
     * 获取当前缓冲区已经写入数据字节数量
     *
     * @return
     */
    public Long size() {
      return 0L;
    }

    public void flush() {}
  public void clear() {
}}
  /**
   * 判断一下当前的缓冲区是否写满了需要刷到磁盘上去
   *
   * @return
   */
  public boolean shouldSyncToDisk() {
    if (currentBuffer.size() >= EDIT_LOG_BUFFER_LIMIT) {
      return true;
    }
    return false;
  }
}
