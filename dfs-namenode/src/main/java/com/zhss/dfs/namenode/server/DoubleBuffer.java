package com.zhss.dfs.namenode.server;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;import java.io.IOException;import java.io.RandomAccessFile;import java.nio.ByteBuffer;import java.nio.channels.FileChannel;

/**
 * 内存双缓冲
 *
 * @author zhonghuashishan
 */
public class DoubleBuffer {

  /**
   *  单块 editslog 缓冲区的最大大小:默认为512kb
   */
  public final Integer EDIT_LOG_BUFFER_LIMIT = 25 * 1024;

  /** 是专门用来承载线程写入edits log */
  private EditLogBuffer currentBuffer = new EditLogBuffer();
  /** 专门用来将数据同步到磁盘中去的一块缓冲 */
  private EditLogBuffer syncBuffer = new EditLogBuffer();

  /** 当前这块缓冲区写入的最大的一个txid */
  private Long startTxid = 1L;

  /**
   * 将edits log写到内存缓冲里去
   *
   * @param log
   */
  public void write(EditLog log)throws IOException {
    currentBuffer.write(log);
  }

  /** 交换两块缓冲区，为了同步内存数据到磁盘做准备 */
  public void setReadyToSync() {
    EditLogBuffer tmp = currentBuffer;
    currentBuffer = syncBuffer;
    syncBuffer = tmp;
  }

  /** 将syncBuffer缓冲区中的数据刷入磁盘中 */
  public void flush()throws IOException {
    syncBuffer.flush();
    syncBuffer.clear();
  }

  /** EditsLog 缓冲区 */
  class EditLogBuffer {

    /** 针对内存缓冲区的字节数组 IO 输出流 */
    ByteArrayOutputStream buffer;

    /**
     * 磁盘上的editslog日志文件的fileChannel
     */
    FileChannel editsLogFileChannel;
    /**
     * 上一次flush到磁盘的时候他的最大的txid是多少
     */
    long endTxid = 0L;


    public EditLogBuffer(){
      this.buffer = new ByteArrayOutputStream(EDIT_LOG_BUFFER_LIMIT * 2);
    }

    /**
     * 将 editslog日志写入缓冲区
     *
     * @param log
     */
    public void write(EditLog log)throws IOException {
      endTxid = log.getTxid();
      buffer.write(log.getContent().getBytes());
      buffer.write("\n".getBytes());
      System.out.println("写入一条editslog:"+ log.getContent() +",当前缓冲区的大小是:" + size());
    }

    /**
     * 获取当前缓冲区已经写入数据字节数量
     *
     * @return
     */
    public Integer size() {
      return buffer.size();
    }

    /**
     * 将 sync buffer中的灵气刷入磁盘中
     */
    public void flush()throws IOException {

      byte[] data = buffer.toByteArray();
      ByteBuffer dataBuffer = ByteBuffer.wrap(data);
      String editsLogFilePath = "/Users/apple/IdeaProjects/distributed-filesystem/editslog/" + "edits-"
              + startTxid + "-" + endTxid + ".log";
      RandomAccessFile file = null;
      FileOutputStream out = null;
      FileChannel editsLogFileChannel = null;
      try{
        //读写模式,数据写入缓冲区中
           file = new RandomAccessFile(editsLogFilePath,"rw");
           out =new FileOutputStream(file.getFD());
           editsLogFileChannel = out.getChannel();
         // 定位到文件里的最后一个位置,进行append追加写
          editsLogFileChannel.position(editsLogFileChannel.size());
           editsLogFileChannel.write(dataBuffer);
          //强制把数据刷到磁盘上
          editsLogFileChannel.force(false);
      }catch (Exception e){
        e.printStackTrace();
      }finally{

        if (out != null){
          out.close();
        }
        if (file != null){
          file.close();
        }
        if (editsLogFileChannel != null){
          editsLogFileChannel.close();
        }
      }
      startTxid = endTxid + 1;
    }

    /**
     * 清空内存缓冲里的数据
     */
    public void clear() {
      buffer.reset();
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
