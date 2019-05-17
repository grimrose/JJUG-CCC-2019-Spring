package ninja.grimrose.sandbox.core

import java.util.Properties

case class GitStatus(value: Map[String, String]) {

  def getCommitId: String = value.getOrElse("git.commit.id", "")

}

object GitStatus {

  import scala.collection.JavaConverters._

  def apply(properties: Properties): GitStatus = new GitStatus(properties.asScala.toMap)

  def empty: GitStatus = GitStatus(Map.empty[String, String])

}
