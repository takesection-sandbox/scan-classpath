# scan-classpath

ClassLoader::getResources(path)で返されるURLは何通りかある。

* file: の場合
* jar:file: の場合は、!/ を区切りとして、その前の部分がファイル名等をあらわしている。

参考にしたソースコード:
- [DefaultResourceLoader.java](https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/io/DefaultResourceLoader.java)
- [ResourceUtils.java](https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/util/ResourceUtils.java)


