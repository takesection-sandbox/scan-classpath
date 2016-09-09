# scan-classpath

ClassLoader::getResources(path)で返されるURLは何通りかある。

* file: の場合
* jar:file: の場合は、!/ を区切りとして、その前の部分がファイル名等をあらわしている。

