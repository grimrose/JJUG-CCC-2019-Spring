package ninja.grimrose.sandbox.core

import java.io.BufferedInputStream
import java.util.Properties

import wvlet.log.LogSupport
import wvlet.log.io.{IOUtil, Resource}

trait LoadGitStatus extends LogSupport {

  def handle(): GitStatus =
    Resource
      .find("", "git.properties")
      .map { url =>
        debug(url)

        IOUtil.withResource(new BufferedInputStream(url.openStream())) { in =>
          val p = new Properties()
          p.load(in)
          p
        }
      }
      .map(GitStatus(_))
      .getOrElse(GitStatus.empty)

}
