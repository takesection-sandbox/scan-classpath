package jp.pigumer

import java.io.File
import java.net.{MalformedURLException, URL}
import java.util.jar.JarFile

import collection.JavaConversions._

class ClasspathScanner {

  private def getUrl(url: URL): URL = {
    val file: String = url.getFile
    val index = file.indexOf("!/")
    if (index != -1) {
      val jarFile = file.substring(0, index)
      try {
        new URL(jarFile)
      } catch {
        case e: MalformedURLException ⇒
          if (jarFile.startsWith("/")) {
            new URL("file:" + jarFile)
          } else {
            new URL("file:/" + jarFile)
          }
      }
    } else {
      url
    }
  }

  private def getJarFile(url: URL): JarFile = {
    val uri = getUrl(url).toURI
    new JarFile(uri.getSchemeSpecificPart)
  }

  private def files(f: File, path: String): Seq[String] = {
    if (f.isFile) {
      List(path)
    }
    files(f, path + "/" + f.getName)
  }

  private def listFiles(url: URL, path: String): Seq[String] = {
    val root = new File(url.getFile)
    files(root, path)
  }

  private def listEntries(jarFile: JarFile, path: String): Seq[String] = {
    val entries = jarFile.entries()
    val filtered = entries.filter(e ⇒ !e.isDirectory && e.getName.startsWith(path))
    val list = filtered.map {
      entry ⇒ entry.getName
    }
    list.toList
  }

  def scan(classLoader: ClassLoader, path: String): java.util.List[String] = {
    val convertedPath = path.replaceAll("\\.", "/")
    val urls = classLoader.getResources(convertedPath)
    val seq = urls.flatMap {
      url ⇒ {
        val resourceUrl = getUrl(url)
        val resourceFile = resourceUrl.getFile
        if (resourceFile.endsWith(".jar")) {
          listEntries(getJarFile(resourceUrl), convertedPath + "/")
        } else {
          listFiles(resourceUrl, convertedPath)
        }
      }
    }
    seq.toList
  }
}