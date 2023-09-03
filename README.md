gRPC 参考说明
protoc-3.1.0
grpc
https://blog.csdn.net/weixin_33701251/article/details/92600190?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1-92600190-blog-70171331.235^v38^pc_relevant_anti_vip&spm=1001.2101.3001.4242.2&utm_relevant_index=4
https://protobuf.dev/programming-guides/proto3/

win protoc.exe --java_out=./*.proto
win 命令行:
protoc.exe --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java-0.13.2-windows-x86_64.exe --grpc-java_out=./*.proto

基于内存的buffer 做一个 写缓冲
editslog 本次对元数据做了什么修改
先写缓冲区，设置阈值 触发阈值 在写到磁盘
双缓冲区机制  缓冲区写磁盘，一个写缓冲区

文件目录树 每隔一段时间写到 fsimage文件中 [checkpoint]
作用: 8:00:00 -8:10:00之间 checkpoint 写入 fsimage文件中
此时 NameNode 需要重启，它直接读取fsimage文件加载到内存里变成文件
目录树，接着把 edit log 文件里的几十条 editslog读取出来，在内存的
文件 目录 树 里回放一遍
NameNode 所在的机器做磁盘IO操作太多了
NameNode 进程也需要 额外分配 一个线程，后台线程定时去做
磁盘IO操作, 加锁 
增加 BackupNode 节点  同步 IO  fsimage  每隔一段时间 checkpoint ,文件写入 fsimage

引入 BackupNode 冷备份的解决方案 保障集群数据不丢失

![img.png](img.png)

基于字节数组IO流构建内存中的数据缓冲区
ByteArrayOutputStream
内部维护一个内存缓冲区,然后可以从里面获取一个字节数组，
把字节数组通过NIO的方式,Buffer +FileChannel的方式,
写入磁盘文件中。

读写模式,数据写入缓冲区中
RandomAccessFile file = new RandomAccessFile(name,"rw)
FileOutputStream out =new FileOutputStream(file.getFD());
FileChannel channel = out.getChannel();
定位到文件里的最后一个位置,进行append追加写
channel.position(channel.size());
byte[] data = ByteArrayOutputStream.toCharArray();
channel.force(false);
ByteArrayOutputStream.reset();

日志文件进行分段存储机制设计
可以拆分为多个日志文件
每次刷新磁盘都是刷一个新的 editslog日志文件 日志文件的名字就是txid~txid.log格式
是最好的。如果你checkpoint 之后要删除一些editslog日志文件,完全可以把之前的一些
文件给物理删除即可。
每次在落地磁盘的时候，都是把上一次落地磁盘的最大一个txid+1
每次落地磁盘都记录好本次落地的时候最大的txid是多少，下次在落地磁盘就可以取出来
进行全名就可以了,每次落地磁盘都是一个新的日志文件 



