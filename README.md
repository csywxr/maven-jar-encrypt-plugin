# maven-jar-encrypt-plugin是一款class加密maven插件，通过在工程目录中引入maven-jar-encrypt-plugin即可实现class文件的加密.

maven-jar-encrypt-plugin是采用jvmti的agent，实现对class文件的加密和解密

使用方式如下：

<plugin>
  
                <groupId>com.encrypt.plugin</groupId>
  
                <artifactId>maven-jar-encrypt-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <jars>
                        <jar>
                           <name>../../bootstrap/target/bootstrap.jar</name>
                            <includes>
                                <include>com.error.*</include>
                            </includes>
                        </jar>
                    </jars>
                </configuration>
            </plugin>
 jar：定义需要加密的目标jar包：
 include: 定义需要包含的类，支持正则表达式
 exclude: 定义需要排除的类
