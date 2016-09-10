package jp.pigumer

import java.net.{MalformedURLException, URL}
import java.util.jar.JarFile
import java.util.zip.ZipEntry

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

  def scan(classLoader: ClassLoader, path: String): java.util.List[ZipEntry] = {
    val convertedPath = path.replaceAll("\\.", "/")
    val urls = classLoader.getResources(convertedPath)
    val jarFiles = for (url <- urls) yield {
      getJarFile(url)
    }
    val entries = for {
      jarFile <- jarFiles
      entry <- jarFile.entries()
      if (entry.getName().startsWith(convertedPath))
    } yield entry
    entries.toList
  }
}