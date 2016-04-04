protoc --java_out=../bin/ Result.proto
javac -d ../bin/ -cp ../bin:../bin/protobuf-java-2.5.0.jar *.java
cd ../bin
rmic DataNode
rmic NameNode
java -cp ../bin:../bin/protobuf-java-2.5.0.jar Client
