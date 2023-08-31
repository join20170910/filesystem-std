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

018