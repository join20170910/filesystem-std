gRPC 参考说明
protoc-3.1.0
grpc
https://blog.csdn.net/weixin_33701251/article/details/92600190?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1-92600190-blog-70171331.235^v38^pc_relevant_anti_vip&spm=1001.2101.3001.4242.2&utm_relevant_index=4
https://protobuf.dev/programming-guides/proto3/

win protoc.exe --java_out=./*.proto
win 命令行:
protoc.exe --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java-0.13.2-windows-x86_64.exe --grpc-java_out=./*.proto

