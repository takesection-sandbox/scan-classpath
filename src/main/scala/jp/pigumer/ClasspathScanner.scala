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

  private def listFiles(url: URL, path: String): Array[String] = {
    val file = new File(url.getFile)
    if (file.isDirectory) {
      val files = for (file <- file.listFiles()) yield {
        listFiles(file.toURI.toURL, path + "/" + file.getName)
      }
      files.flatten
    } else {
      Array(path)
    }
  }

  private def listEntries(jarFile: JarFile, path: String): Array[String] = {
    val list = for {
      entry <- jarFile.entries()
      if (!entry.isDirectory)
      if (entry.getName.startsWith(path))
    } yield entry.getName
    list.toArray
  }

  def scan(classLoader: ClassLoader, path: String): java.util.List[String] = {
    val convertedPath = path.replaceAll("\\.", "/")
    val urls = classLoader.getResources(convertedPath)
    val list = for (url <- urls) yield {
      val resourceUrl = getUrl(url)
      val resourceFile = resourceUrl.getFile
      if (resourceFile.endsWith(".jar")) {
        listEntries(getJarFile(resourceUrl), convertedPath + "/")
      } else {
        listFiles(resourceUrl, convertedPath)
      }
    }
    list.flatten.toList
  }
}